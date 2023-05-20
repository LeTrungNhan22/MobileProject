package com.example.mobileproject.models;

import java.util.Date;

public class User {
    private String id;
    private String username;
    private String fullName;
    private String imageURL;
    private String bio;
    private Date createdAt;
    private Date updatedAt;
    private String statusUser;

    private String statusNetwork;

    public User(String id, String username, String fullName, String imageURL, String bio, Date createdAt, Date updatedAt, String statusNetwork, String statusUser) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.imageURL = imageURL;
        this.bio = bio;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.statusNetwork = statusNetwork;
        this.statusUser = statusUser;
    }

    public String getStatusUser() {
        return statusUser;
    }

    public void setStatusUser(String statusUser) {
        this.statusUser = statusUser;
    }

    public String getStatusNetwork() {
        return statusNetwork;
    }

    public void setStatusNetwork(String statusNetwork) {
        this.statusNetwork = statusNetwork;
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

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
