package com.koreatech.diary;


import android.os.Parcel;
import android.os.Parcelable;

public class ScheduleData  {
    private String content;
    private String title;
    private String day;

    public ScheduleData() {

    }

    public ScheduleData(String day, String title, String content){
        this.day = day;
        this.title = title;
        this.content = content;
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
