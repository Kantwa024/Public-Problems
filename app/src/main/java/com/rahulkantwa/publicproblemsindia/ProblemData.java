package com.rahulkantwa.publicproblemsindia;

public class ProblemData {

    private String imageurl,problem,username,userurl,time,likes,comments,type,name,place;

    public ProblemData(String imageurl, String problem, String username, String userurl, String time, String likes, String comments,String type,String name,String place) {
        this.imageurl = imageurl;
        this.problem = problem;
        this.username = username;
        this.userurl = userurl;
        this.time = time;
        this.likes = likes;
        this.comments = comments;
        this.type = type;
        this.name = name;
        this.place = place;
    }

    public ProblemData()
    {

    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserurl() {
        return userurl;
    }

    public void setUserurl(String userurl) {
        this.userurl = userurl;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
