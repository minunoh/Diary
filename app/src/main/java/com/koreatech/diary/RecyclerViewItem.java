package com.koreatech.diary;

public class RecyclerViewItem {
    private String date;
    private String Theme;
    private String Content;
    private String UserName;

    public RecyclerViewItem(){}
    public RecyclerViewItem(String userName, String date, String theme, String content){
        UserName =userName;
        this.date = date;
        Theme =theme;
        Content =content;
    }

    public String getUserName(){return UserName;}
    public void setUserName(String userName) {UserName=userName;}
    public String getDate(){
        return date;
    }
    public void setDate(String date){
        this.date = date;
    }
    public String getTheme(){
        return Theme;
    }
    public void setTheme(String Theme){
        this.Theme = Theme;
    }
    public String getContent(){
        return Content;
    }
    public  void setContent(String Content){
        this.Content = Content;
    }
}


