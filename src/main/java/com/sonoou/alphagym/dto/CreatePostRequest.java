package com.sonoou.alphagym.dto;

public class CreatePostRequest {

    private String caption;
    private String imageUrl;

    public CreatePostRequest() {}

    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
