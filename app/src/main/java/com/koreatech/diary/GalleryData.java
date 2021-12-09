package com.koreatech.diary;

import android.net.Uri;

import java.net.URI;

public class GalleryData {


    public GalleryData(String imaguri, String imagename, String time) {
        this.imaguri = imaguri;
        this.imagename = imagename;
        this.time = time;
    }

    public String getImaguri() {
        return imaguri;
    }

    public void setImaguri(String imaguri) {
        this.imaguri = imaguri;
    }

    String imaguri = "";
    String imagename="";

    public String getImagename() {
        return imagename;
    }

    public void setImagename(String imagename) {
        this.imagename = imagename;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    String time="";

    public GalleryData(){

    }




}
