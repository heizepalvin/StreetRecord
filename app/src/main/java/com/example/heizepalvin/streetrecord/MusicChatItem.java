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
    int num;

    public MusicChatItem (String title, String genre, int memberCount, Date time, int num){
        this.title = title;
        this.genre = genre;
        this.memberCount = memberCount;
        this.time = time;
        this.num = num;
    }

    public MusicChatItem (String title, String genre, int memberCount, Date time, String image, int num){
        this.title = title;
        this.genre = genre;
        this.memberCount = memberCount;
        this.time = time;
        this.image = image;
        this.num = num;

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

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
