package com.example.heizepalvin.streetrecord;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;



/**
 * Created by soyounguensoo on 2017-07-11.
 */

public class PlayActivity extends AppCompatActivity {

    private TextView musicTitle;
    private TextView musicArtist;
    private ImageView musicAlbumImg;
    private ImageView playlist;
    private ImageView finishBtn;
    private SeekBar seekBar;

    private MediaPlayer mediaPlayer;
    private boolean isPlayling = true;

    private ImageView playButton;
    private ImageView pauseButton;
    private ImageView preButton;
    private ImageView nextButton;
    private ImageView infoButton;

    //한곡반복, 반복재생, 랜덤재생 버튼
    private ImageView repeatOn;
    private ImageView repeatOff;
    private ImageView repeatOne;
    private ImageView randomOn;
    private ImageView randomOff;

    private ProgressUpdate update;

    private String repeatMode;


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals("NEXT")) {
                Log.e("playActivity", "next에 들어옴");
                updateUI();
            } else if(intent.getAction().equals("TRUE")){
                updateProgress();
                updateUI();
            } else if(intent.getAction().equals("REPEATOFF")){
                updateProgress();
                updateUI();
            } else {
                updateUI();
                Log.e("playActivity","나머지에 들어옴");
            }

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_activity);

        musicTitle = (TextView) findViewById(R.id.playActTitle);
        musicArtist = (TextView) findViewById(R.id.playActArtist);
        musicAlbumImg = (ImageView) findViewById(R.id.playActImg);
        seekBar = (SeekBar) findViewById(R.id.playActSeek);
        playButton = (ImageView) findViewById(R.id.playActPlay);
        pauseButton = (ImageView) findViewById(R.id.playActPause);
        preButton = (ImageView) findViewById(R.id.playActPre);
        nextButton = (ImageView) findViewById(R.id.playActNext);
        infoButton = (ImageView) findViewById(R.id.playActInfo);

        playlist = (ImageView) findViewById(R.id.playActMenu);
        finishBtn = (ImageView) findViewById(R.id.playActFinish);

        mediaPlayer = new MediaPlayer();

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayActivity.this,MusicInfoActivity.class);
                PlaylistItem item = GlobalApplication.getInstance().getServiceInterface().getMusicItem();
                String title = item.getTitle();
                String artist = item.getArtist();
                String albumImg = item.getImgPath();
                String albumName = item.getAlbumName();
                String date = item.getDate();
                String genre = item.getGenre();
                String lyrics = item.getLyrics();

                intent.putExtra("title",title);
                intent.putExtra("artist",artist);
                intent.putExtra("albumImg",albumImg);
                intent.putExtra("albumName",albumName);
                intent.putExtra("date", date);
                intent.putExtra("genre", genre);
                intent.putExtra("lyrics", lyrics);

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayActivity.this,PlaylistActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
//                finish();
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playButton.setVisibility(View.GONE);
                pauseButton.setVisibility(View.VISIBLE);
                GlobalApplication.getInstance().getServiceInterface().togglePlay();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseButton.setVisibility(View.GONE);
                playButton.setVisibility(View.VISIBLE);
                GlobalApplication.getInstance().getServiceInterface().togglePlay();
            }
        });

        preButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalApplication.getInstance().getServiceInterface().rewind();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalApplication.getInstance().getServiceInterface().forwardClick();
            }
        });


        seekBar.setMax(GlobalApplication.getInstance().getServiceInterface().getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                GlobalApplication.getInstance().getServiceInterface().togglePlay();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                GlobalApplication.getInstance().getServiceInterface().seek(seekBar.getProgress());
                GlobalApplication.getInstance().getServiceInterface().play();
            }
        });

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        musicTitle.setSelected(true);

        registerBroadcast();
        updateUI();

        //한곡반복, 반복재생, 랜덤재생

        repeatOn = (ImageView) findViewById(R.id.playActRepeat);
        repeatOff = (ImageView) findViewById(R.id.playActRepeatOff);
        repeatOne = (ImageView) findViewById(R.id.playActRepeatOne);
        randomOn = (ImageView) findViewById(R.id.playActRandomOn);
        randomOff = (ImageView) findViewById(R.id.playActRandomOff);

        Log.e("repeatMode","repeatMode = " + repeatMode);

        repeatOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeatOff.setVisibility(View.GONE);
                repeatOn.setVisibility(View.VISIBLE);
                Toast.makeText(PlayActivity.this, "반복 재생 됩니다.", Toast.LENGTH_SHORT).show();
                GlobalApplication.getInstance().getServiceInterface().repeatAll();
                SharedPreferences preferences = getSharedPreferences("repeat",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.putString("repeat","on");
                editor.commit();
            }
        });

        repeatOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeatOn.setVisibility(View.GONE);
                repeatOne.setVisibility(View.VISIBLE);
                Toast.makeText(PlayActivity.this, "한 곡만 반복 재생 됩니다.", Toast.LENGTH_SHORT).show();
                GlobalApplication.getInstance().getServiceInterface().repeatOne();
                SharedPreferences preferences = getSharedPreferences("repeat",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.putString("repeat","one");
                editor.commit();
            }
        });

        repeatOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeatOne.setVisibility(View.GONE);
                repeatOff.setVisibility(View.VISIBLE);
                GlobalApplication.getInstance().getServiceInterface().repeatOff();
                SharedPreferences preferences = getSharedPreferences("repeat",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();
            }
        });

        SharedPreferences preferences = getSharedPreferences("repeat",MODE_PRIVATE);
        String repeat = preferences.getString("repeat","off");

        if(repeat.equals("on")){
            repeatOn.setVisibility(View.VISIBLE);
            repeatOff.setVisibility(View.GONE);
            repeatOne.setVisibility(View.GONE);
        } else if(repeat.equals("one")){
            repeatOne.setVisibility(View.VISIBLE);
            repeatOff.setVisibility(View.GONE);
            repeatOn.setVisibility(View.GONE);
        } else {
            repeatOff.setVisibility(View.VISIBLE);
            repeatOn.setVisibility(View.GONE);
            repeatOne.setVisibility(View.GONE);
        }

        randomOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomOff.setVisibility(View.GONE);
                randomOn.setVisibility(View.VISIBLE);
                Toast.makeText(PlayActivity.this, "랜덤재생 됩니다.", Toast.LENGTH_SHORT).show();
                SharedPreferences preferences = getSharedPreferences("random",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("random",true);
                editor.commit();
            }
        });

        randomOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomOn.setVisibility(View.GONE);
                randomOff.setVisibility(View.VISIBLE);
                SharedPreferences preferences = getSharedPreferences("random",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();
            }
        });

        SharedPreferences randomPlayBoolean = getSharedPreferences("random",MODE_PRIVATE);
        Boolean randomPlayUI = randomPlayBoolean.getBoolean("random",false);
        if(randomPlayUI){
            randomOn.setVisibility(View.VISIBLE);
            randomOff.setVisibility(View.GONE);
        } else {
            randomOff.setVisibility(View.VISIBLE);
            randomOn.setVisibility(View.GONE);
        }
    }

    private class ProgressUpdate extends Thread{
        @Override
        public void run() {
            super.run();
            Log.e("progressupdate",GlobalApplication.getInstance().getServiceInterface().isPlaying() + " ??");
            while (GlobalApplication.getInstance().getServiceInterface().isPlaying()){
                try{
                    seekBar.setProgress(GlobalApplication.getInstance().getServiceInterface().getMusicPosition());
                    Thread.sleep(1000);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
    }

    public void registerBroadcast(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastActions.PLAY_STATE_CHANGED);
        filter.addAction(BroadcastActions.NEXT);
        filter.addAction(BroadcastActions.PLAY_START);
        filter.addAction(BroadcastActions.isPlaying);
        filter.addAction(BroadcastActions.repeatOne);
        filter.addAction(BroadcastActions.repeatOff);
        registerReceiver(receiver, filter);
    }

    public void unregisterBroadcast(){
        unregisterReceiver(receiver);
    }

    private  void updateUI(){

        update = new ProgressUpdate();
        update.start();

        if(GlobalApplication.getInstance().getServiceInterface().isPlaying()){
            playButton.setVisibility(View.GONE);
            pauseButton.setVisibility(View.VISIBLE);
        } else {
            playButton.setVisibility(View.VISIBLE);
            pauseButton.setVisibility(View.GONE);
        }

        PlaylistItem musicItem = GlobalApplication.getInstance().getServiceInterface().getMusicItem();
        if(musicItem != null){
            musicTitle.setText(musicItem.getTitle());
            musicArtist.setText(musicItem.getArtist());
            Glide.with(this).load(musicItem.getImgPath()).into(musicAlbumImg);
        }
    }

    private void updateProgress(){
        seekBar.setProgress(0);
        seekBar.setMax(GlobalApplication.getInstance().getServiceInterface().getDuration());
    }
}
