package com.example.heizepalvin.streetrecord;

import java.io.Serializable;

/**
 * Created by soyounguensoo on 2017-07-18.
 */

public class PlaylistItem implements Serializable{

    private String title;
    private String artist;
    private String imgPath;
    private String musicURL;
    private String lyrics;
    private String albumName;
    private String genre;
    private String date;


    public PlaylistItem (String title, String artist, String imgPath){
        this.title = title;
        this.artist = artist;
        this.imgPath = imgPath;
    }

    public PlaylistItem (String title, String artist, String imgPath, String musicURL, String lyrics, String albumName, String genre, String date){
        this.title = title;
        this.artist = artist;
        this.imgPath = imgPath;
        this.musicURL = musicURL;
        this.lyrics = lyrics;
        this.albumName = albumName;
        this.genre = genre;
        this.date = date;
    }

    public PlaylistItem(){

    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getMusicURL() {
        return musicURL;
    }

    public void setMusicURL(String musicURL) {
        this.musicURL = musicURL;
    }
}
