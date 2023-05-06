package com.example.mobileproject.models;

import java.util.Date;

public class Comment {
    private String comment;
    private String publisher;
    private String commentId;

    private Date createdAt;
    private Date updatedAt;

    public Comment(String comment, String publisher, String commentId, Date createdAt, Date updatedAt) {
        this.comment = comment;
        this.publisher = publisher;
        this.commentId = commentId;
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

    public Comment() {
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
