package com.unitapplications.reelsaver;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.unitapplications.reelsaver.API.ApiClient;
import com.unitapplications.reelsaver.API.ApiSets;
import com.unitapplications.reelsaver.Models.Root;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    EditText link;
    Button download,play,share;
    ApiSets apiSet;
    DownloadManager manager;
    AsyncTask mMyTask;
    Intent intent;
    String sharedText,title;
    long downloadID;
    VideoView videoView;
    MediaController mediaController;
    WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        link = findViewById(R.id.et_link);
        share = findViewById(R.id.share);
        play = findViewById(R.id.play);
        download = findViewById(R.id.button);
   videoView  = findViewById(R.id.videoView1);

        Retrofit retrofit = ApiClient.getClient();
        apiSet = retrofit.create(ApiSets.class);

        //Creating MediaController
     mediaController   = new MediaController(this);
        mediaController.setAnchorView(videoView);


        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        if (Build.VERSION.SDK_INT >= 30){
            if (!Environment.isExternalStorageManager()){
                Intent getpermission = new Intent();
                getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(getpermission);
            }
        }


        download.setOnClickListener(view -> {
    //   makeApiCall("https://www.instagram.com/reel/ClYKdkpr_gr/?utm_source=ig_web_copy_link");
            String link_txt = link.getText().toString();
            if(link_txt.startsWith("https://www.instagram.com/")){
             makeApiCall(link_txt);

            }
            else Toast.makeText(this, "Invalid Link", Toast.LENGTH_SHORT).show();


        });
        play.setOnClickListener(view -> {
            //Setting MediaController and URI, then starting the videoView
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),title); // Set Your File Name
            if (file.exists()) {
            videoView.setMediaController(mediaController);
            videoView.setVideoURI(Uri.parse(file.getAbsolutePath()));
            videoView.requestFocus();
            videoView.start();
            }
        });
        share.setOnClickListener(view -> {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),title); // Set Your File Name
            if (file.exists()) {
                shareVideo(Uri.parse(file.getAbsolutePath()));
            }
        });

        intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String got_link = handleSendText(intent);
                link.setText(got_link);
                makeApiCall(got_link);
            }}

        registerReceiver(onDownloadComplete,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }//onCreate
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onDownloadComplete);
    }
    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {
                Toast.makeText(MainActivity.this, "Download Completed", Toast.LENGTH_SHORT).show();
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),title); // Set Your File Name
                if (file.exists()) {
                    videoView.setMediaController(mediaController);
                    videoView.setVideoURI(Uri.parse(file.getAbsolutePath()));
                    videoView.requestFocus();
                    videoView.start();
                }
            }
        }
    };

    private void shareVideo(Uri uri) {
        //this to share video
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("video/mp4");
        startActivity(Intent.createChooser(intent, "Share"));
    }


    //public void download(){
    public void download(String url_video,String username){
        Uri url =null;
        manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (url_video!=null){
       url= Uri.parse(url_video);}
        title = username+"_"+System.currentTimeMillis()+".mp4";
        //Uri uri = Uri.parse("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf");
        DownloadManager.Request request = new DownloadManager.Request(url);
            request.setTitle(title)
                   .setMimeType("video/mp4")
                   .setDescription("Downloading Video Please wait...!")
                   .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title);
                manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadID = manager.enqueue(request);
                
        }
      private void makeApiCall(String url) {
        apiSet.getAll(url).enqueue(new Callback<Root>() {
            @Override
            public void onResponse(Call<Root> call, Response<Root> response) {
                String url_got = null;
                if (response.body() != null) {
                    url_got = response.body().getGraphql().getShortcode_media().getVideo_url();
                    String u =response.body().getGraphql().getShortcode_media().getOwner().getUsername();
                    Log.d("TAG3", "User: "+u);
                    download(url_got,u);
                }
                Log.d("TAG3", "onResponse: "+url_got);
            }

            @Override
            public void onFailure(Call<Root> call, Throwable t) {
                Log.d("TAG3", "onFailure: "+t.getLocalizedMessage());

            }
        });

    }
    public String handleSendText(Intent intent) {
        sharedText  = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
        }
        return sharedText;
    }
}