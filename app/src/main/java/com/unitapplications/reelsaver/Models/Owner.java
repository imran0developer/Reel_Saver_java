package com.unitapplications.reelsaver.Models;

public class Owner {
    private String id;

    private String profile_pic_url;

    private String username;

    public void setId(String id){
        this.id = id;
    }
    public String getId(){
        return this.id;
    }
    public void setProfile_pic_url(String profile_pic_url){
        this.profile_pic_url = profile_pic_url;
    }
    public String getProfile_pic_url(){
        return this.profile_pic_url;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public String getUsername(){
        return this.username;
    }
}