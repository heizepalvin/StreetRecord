package com.example.heizepalvin.streetrecord;

/**
 * Created by soyounguensoo on 2017-06-25.
 */

public class MusicGenreActivityItem {

    String title;
    String artist;
    String albumImg;
    String musicURL;
    String lyrics;
    String albumName;
    String date;
    String genre;

    public MusicGenreActivityItem (String title, String artist, String albumImg,String musicURL, String lyrics, String albumName, String date, String genre){
        this.title = title;
        this.artist = artist;
        this.albumImg = albumImg;
        this.musicURL = musicURL;
        this.lyrics = lyrics;
        this.albumName = albumName;
        this.date = date;
        this.genre = genre;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
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

    public String getAlbumImg() {
        return albumImg;
    }

    public void setAlbumImg(String albumImg) {
        this.albumImg = albumImg;
    }

    public String getMusicURL() {
        return musicURL;
    }

    public void setMusicURL(String musicURL) {
        this.musicURL = musicURL;
    }
}
