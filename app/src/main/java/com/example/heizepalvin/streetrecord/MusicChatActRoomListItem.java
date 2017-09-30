package com.example.heizepalvin.streetrecord;

/**
 * Created by soyounguensoo on 2017-09-15.
 */

public class MusicChatActRoomListItem {

    String title;
    String image;
    String lastMessage;
    String time;
    int token;
    int count;

    public MusicChatActRoomListItem(String title, String image, String lastMessage, String time, int token, int count){
        this.title = title;
        this.image = image;
        this.lastMessage = lastMessage;
        this.time = time;
        this.token = token;
        this.count = count;
    }

    public MusicChatActRoomListItem(){

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
