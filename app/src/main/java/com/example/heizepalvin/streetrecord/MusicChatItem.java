package com.example.heizepalvin.streetrecord;

import java.util.Date;

/**
 * Created by soyounguensoo on 2017-08-22.
 */

public class MusicChatItem {

    String title;
    String genre;
    String memberNumber;
    Date time;
    int memberCount;
    String image;

    public MusicChatItem (String title, String genre, int memberCount, Date time){
        this.title = title;
        this.genre = genre;
        this.memberCount = memberCount;
        this.time = time;
    }

    public MusicChatItem (String title, String genre, int memberCount, Date time, String image){
        this.title = title;
        this.genre = genre;
        this.memberCount = memberCount;
        this.time = time;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getMemberNumber() {
        return memberNumber;
    }


    public Date getTime() {
        return time;
    }

    public void setMemberNumber(String memberNumber) {
        this.memberNumber = memberNumber;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
