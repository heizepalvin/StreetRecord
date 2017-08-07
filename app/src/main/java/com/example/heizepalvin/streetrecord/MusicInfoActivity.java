package com.example.heizepalvin.streetrecord;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by soyounguensoo on 2017-08-04.
 */

public class MusicInfoActivity extends Activity {

    private TextView musicTitle;
    private TextView musicArtist;
    private TextView musicAlbumName;
    private TextView musicDate;
    private TextView musicGenre;
    private TextView musicLyrics;
    private ImageView musicAlbumImg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.musicinfo_activity);

        musicTitle = (TextView) findViewById(R.id.musicInfoTitle);
        musicArtist = (TextView) findViewById(R.id.musicInfoArtist);
        musicAlbumName = (TextView) findViewById(R.id.musicInfoAlbumName);
        musicDate = (TextView) findViewById(R.id.musicInfoDate);
        musicGenre = (TextView) findViewById(R.id.musicInfoGenre);
        musicLyrics = (TextView) findViewById(R.id.musicInfoLyrics);
        musicAlbumImg = (ImageView) findViewById(R.id.musicInfoAlbumImg);

        Intent intent = getIntent();

        String title = intent.getStringExtra("title");
        String artist = intent.getStringExtra("artist");
        String albumName = intent.getStringExtra("albumName");
        String albumImg = intent.getStringExtra("albumImg");
        String date = intent.getStringExtra("date");
        String genre = intent.getStringExtra("genre");
        String lyrics = intent.getStringExtra("lyrics");

        musicTitle.setText(title);
        musicTitle.setSelected(true);
        musicArtist.setText(artist);
        musicArtist.setSelected(true);
        musicAlbumName.setText(albumName);
        musicAlbumName.setSelected(true);
        Glide.with(this).load(albumImg).into(musicAlbumImg);
        musicDate.setText(date);
        musicGenre.setText(genre);
        musicLyrics.setText(lyrics);


    }
}
