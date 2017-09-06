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

    public MusicChatItem (String title, String genre, String memberNumber, Date time){
        this.title = title;
        this.genre = genre;
        this.memberNumber = memberNumber;
        this.time = time;
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

    public void setMemberNumber(String memberNumber) {
        this.memberNumber = memberNumber;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
