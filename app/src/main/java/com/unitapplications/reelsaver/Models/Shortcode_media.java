package com.unitapplications.reelsaver.Models;

public class Shortcode_media {
    private String display_url;

    private boolean is_video;

    private String title;

    private String product_type;

    private String video_url;

    private Owner owner;

    public void setDisplay_url(String display_url){
        this.display_url = display_url;
    }
    public String getDisplay_url(){
        return this.display_url;
    }
    public void setIs_video(boolean is_video){
        this.is_video = is_video;
    }
    public boolean getIs_video(){
        return this.is_video;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getTitle(){
        return this.title;
    }
    public void setProduct_type(String product_type){
        this.product_type = product_type;
    }
    public String getProduct_type(){
        return this.product_type;
    }
    public void setVideo_url(String video_url){
        this.video_url = video_url;
    }
    public String getVideo_url(){
        return this.video_url;
    }
    public void setOwner(Owner owner){
        this.owner = owner;
    }
    public Owner getOwner(){
        return this.owner;
    }
}