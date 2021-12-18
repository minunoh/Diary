package com.koreatech.diary;

public class LikeData {
    private String userid; // ID

    public LikeData() { }

    public LikeData(String userid){
        this.userid = userid;
    }

    public String getUserid() {
        return userid;
    }
    public void setUserid(String userid) {
        this.userid = userid;
    }

}
