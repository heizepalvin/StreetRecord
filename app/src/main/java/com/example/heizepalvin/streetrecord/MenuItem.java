package com.example.heizepalvin.streetrecord;

import android.graphics.drawable.Drawable;

/**
 * Created by soyounguensoo on 2017-06-20.
 */

public class MenuItem {

    private String title;
    private int image;

    public MenuItem(String title, int image){
        this.title = title;
        this.image = image;
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
}
