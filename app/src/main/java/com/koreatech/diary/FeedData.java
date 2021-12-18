package com.koreatech.diary;

public class FeedData {
    private String content; // 내용
    private String theme;// 테마
    private String userid; // ID
    private String day; // 날짜
    private String time;
    private boolean open ;
    private String imageurl;
    private String imagename;
    private String like;
    private String comment;

    public FeedData() { }

    public FeedData(boolean open, String userid,String theme,String content,String day, String time, String imagename,String imageurl, String like, String comment){
        this.open = open;
        this.userid = userid;
        this.theme = theme;
        this.content= content;
        this.day = day;
        this.time =time;
        this.imagename = imagename;
        this.imageurl = imageurl;
        this.like = like;
        this.comment = comment;
    }

    public boolean getOpen(){return open;}
    public void setOpen(Boolean open){this.open = open;}

    public String getUserid() {
        return userid;
    }
    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getTheme(){return theme;}
    public void setTheme(String theme){this.theme = theme;}

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

    public String getDay() {
        return day;
    }
    public void setDay(String day) {
        this.day = day;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) { this.content = content; }

    public String getImagename() { return imagename; }
    public void setImagename(String imagename) {
        this.imagename = imagename;
    }

    public String getImageurl() {
        return imageurl;
    }
    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getLike() {
        return like;
    }
    public void setLike(String like) {
        this.like = like;
    }

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
}
