package com.unitapplications.reelsaver.API;



import com.unitapplications.reelsaver.Models.Root;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiSets {

    @GET("?")
    Call<Root> getAll(
            @Query("url") String url
    );

}