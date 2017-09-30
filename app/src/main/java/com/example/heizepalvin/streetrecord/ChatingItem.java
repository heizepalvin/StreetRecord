package com.example.heizepalvin.streetrecord;

/**
 * Created by soyounguensoo on 2017-09-07.
 */

public class ChatingItem {

    String userChat;
    String type;
    String id;
    int token;
    String time;
    String date;


    public ChatingItem(String userChat, String type, String time, String id, String date){

        this.userChat = userChat;
        this.type = type;
        this.time = time;
        this.id = id;
        this.date = date;
    }

    public ChatingItem(){

    }

    public String getUserChat() {
        return userChat;
    }

    public void setUserChat(String userChat) {
        this.userChat = userChat;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
}
