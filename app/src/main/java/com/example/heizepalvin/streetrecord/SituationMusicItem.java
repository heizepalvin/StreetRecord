package com.example.heizepalvin.streetrecord;

/**
 * Created by soyounguensoo on 2017-06-21.
 */

public class SituationMusicItem {
    private int image;
    private String mainImg;
    private String title;
    private String genre;



    public SituationMusicItem(String title, String genre, String mainImg){

        this.title = title;
        this.genre = genre;
        this.mainImg = mainImg;

    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
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

    public String getMainImg() {
        return mainImg;
    }

    public void setMainImg(String mainImg) {
        this.mainImg = mainImg;
    }
}
