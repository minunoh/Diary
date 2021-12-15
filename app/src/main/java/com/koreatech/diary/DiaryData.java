package com.koreatech.diary;

public class DiaryData {
    private String content; // 내용
    private String title; // 다이어리 타이틀
    private String theme;// 테마
    private String name; // ID
    private String day; // 날짜
    private String time;
    private boolean open ;
    private String imagename;
    private String imageurl;

    public DiaryData(String content, String theme, String day, String time, boolean open, String imagename, String imageurl) {
        this.content = content;
        this.theme = theme;
        this.day = day;
        this.time = time;
        this.open = open;
        this.imagename = imagename;
        this.imageurl = imageurl;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean getOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getImagename() {
        return imagename;
    }

    public void setImagename(String imagename) {
        this.imagename = imagename;
    }

    public DiaryData() { }
    //Diary-
    // ID-
    //    날짜,
    //        테마,공개여부,작성글(DiaryData)





}



