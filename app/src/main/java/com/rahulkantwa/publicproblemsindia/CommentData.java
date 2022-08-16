package com.rahulkantwa.publicproblemsindia;

public class CommentData {
    private String name,comment,url;

    public CommentData(String name, String comment, String url) {
        this.name = name;
        this.comment = comment;
        this.url = url;
    }

    public CommentData()
    {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
