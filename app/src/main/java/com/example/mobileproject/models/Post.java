package com.example.mobileproject.models;

import java.util.Date;

public class Post {
    private String postId;
    private String postImage;
    private String description;
    private String publisher;

    private Date createdAt;
    private Date updatedAt;

    public Post(String postId, String postImage, String description, String publisher, Date createdAt, Date updatedAt) {
        this.postId = postId;
        this.postImage = postImage;
        this.description = description;
        this.publisher = publisher;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Post() {
    }


    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
