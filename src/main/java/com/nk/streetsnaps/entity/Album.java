package com.nk.streetsnaps.entity;

public class Album {

    private String id;
    private String content;
    private String imageUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageurl) {
        this.imageUrl = imageurl;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
