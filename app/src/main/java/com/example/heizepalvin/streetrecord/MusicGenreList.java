package com.example.heizepalvin.streetrecord;

/**
 * Created by soyounguensoo on 2017-06-24.
 */

public class MusicGenreList {

    String img;
    String genre;

    public MusicGenreList(String img, String genre){
        this.img = img;
        this.genre = genre;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}
