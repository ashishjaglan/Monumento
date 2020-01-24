package com.example.blogapp.Models;

public class Post {

    private String title;
    private String description;
    private String picture;
    private String userid;
    private String userPhoto;
    private Object timeStamp;

    public Post(String title, String description, String picture, String userid, String userPhoto, Object timeStamp) {
        this.title = title;
        this.description = description;
        this.picture = picture;
        this.userid = userid;
        this.userPhoto = userPhoto;
        this.timeStamp = timeStamp;
    }

    
}
