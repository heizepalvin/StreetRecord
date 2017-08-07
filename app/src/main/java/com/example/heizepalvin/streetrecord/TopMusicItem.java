package com.example.heizepalvin.streetrecord;

/**
 * Created by soyounguensoo on 2017-06-21.
 */

public class TopMusicItem {

    private String ranking;
    private String title;
    private String artist;
    private int image;
    private String albumImg;
    private String musicURL;
    private String lyrics;
    private String albumName;
    private String date;
    private String genre;

    public String getAlbumImg() {
        return albumImg;
    }

    public void setAlbumImg(String albumImg) {
        this.albumImg = albumImg;
    }

    public TopMusicItem(String ranking, String title, String artist){
        this.ranking = ranking;
        this.title = title;
        this.artist = artist;
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

    public TopMusicItem(String ranking, String title, String artist, String albumImg, String musicURL, String lyrics, String albumName, String date, String genre){
        this.ranking = ranking;
        this.title = title;
        this.artist = artist;

        this.albumImg = albumImg;
        this.musicURL = musicURL;
        this.lyrics = lyrics;
        this.albumName = albumName;
        this.date = date;
        this.genre = genre;
    }

    public TopMusicItem(String ranking,String title,String artist, int image){
        this.ranking = ranking;
        this.title = title;
        this.artist = artist;
        this.image = image;
    }

    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getMusicURL() {
        return musicURL;
    }

    public void setMusicURL(String musicURL) {
        this.musicURL = musicURL;
    }
}
