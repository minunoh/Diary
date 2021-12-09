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

    public String getFilename() {
        return imagename;
    }

    public void setFilename(String imagename) {
        this.imagename =  imagename;;
    }


    public DiaryData() { }
    //Diary-
    // ID-
    //    날짜,
    //        테마,공개여부,작성글(DiaryData)
    public DiaryData(boolean open, String theme,String content,String day, String time, String imagename){
        this.open = open;
        this.theme = theme;
        this.content= content;
        this.day = day;
        this.time =time;
        this.imagename=imagename;
    }


    public boolean getOpen(){return open;}
    public void setOpen(Boolean open){this.open = open;}

    public String getTheme(){return theme;}
    public void setTheme(String theme){this.theme = theme;}

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

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


}



