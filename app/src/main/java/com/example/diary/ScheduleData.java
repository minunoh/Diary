package com.example.diary;


import android.os.Parcel;
import android.os.Parcelable;

public class ScheduleData  {
    private String content;
    private String title;

    private String day;

    public ScheduleData() {

    }




    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
