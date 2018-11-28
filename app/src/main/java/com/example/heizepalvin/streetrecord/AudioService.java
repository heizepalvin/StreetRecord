package com.example.heizepalvin.streetrecord;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ProviderInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Random;

import bolts.Bolts;

/**
 * Created by soyounguensoo on 2017-07-27.
 */

public class AudioService extends Service implements AudioManager.OnAudioFocusChangeListener{

    private final IBinder mbinder = new AudioServiceBinder();
    private ArrayList<PlaylistItem> items;
    private MediaPlayer mediaPlayer;
    private boolean isPrepared;
    private int currentPosition;

    private PlaylistItem playItem;

    private NotificationPlayer mNotificationPlayer;

//    private Boolean allRepeat = false;
//    private Boolean oneRepeat = false;
    private Boolean firstPlaying = false;

    private Boolean lossTransient = false;
    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange){

            case AudioManager.AUDIOFOCUS_GAIN:
                if(lossTransient){
                    if(!mediaPlayer.isPlaying()){
                        mediaPlayer.start();
                        sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED));
                        updateNotificationPlayer();
                        lossTransient = false;
                    }
                }
                mediaPlayer.setVolume(1.0f,1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
            
                pause();
                sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED));
//                updateNotificationPlayer();
                removeNotificationPlayer();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if(mediaPlayer.isPlaying()){
                    pause();
                    updateNotificationPlayer();
                    sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED));
                    lossTransient = true;
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.setVolume(0.1f,0.1f);
                }
                break;
        }
    }


    public class AudioServiceBinder extends Binder {
        AudioService getService(){
            return AudioService.this;
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)){
                int state = intent.getIntExtra("state",-1);
                switch (state){
                    case 0:
                        GlobalApplication.getInstance().getServiceInterface().pause();
                        removeNotificationPlayer();
                        break;
                    case 1:
                        break;
                }
            }
        }
    };

    public void registerBroadcast(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(mBroadcastReceiver,filter);
    }

    public void unregisterBroadcast(){
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        registerBroadcast();

        items = new ArrayList<>();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                isPrepared = true;
                mp.start();
                SharedPreferences repeatOne = getSharedPreferences("repeat",MODE_PRIVATE);
                String repeat = repeatOne.getString("repeat","off");
                if(repeat.equals("one")){
                    mp.setLooping(true);
                } else {
                    mp.setLooping(false);
                }
                if(firstPlaying){
                    mp.pause();
                    firstPlaying = false;
                    sendBroadcast(new Intent(BroadcastActions.repeatOff));
                    removeNotificationPlayer();
                } else {
                    sendBroadcast(new Intent(BroadcastActions.PREPARED));
                    sendBroadcast(new Intent(BroadcastActions.isPlaying));
                    updateNotificationPlayer();
                }
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isPrepared = false;
                sendBroadcast(new Intent(BroadcastActions.NEXT));
                SharedPreferences preferences = getSharedPreferences("repeat",MODE_PRIVATE);
                String repeat = preferences.getString("repeat","off");
//                if(items.size()-1 == currentPosition && repeat.equals("off")){
//                    forward();
//                }
                SharedPreferences preference = getSharedPreferences("destroy",MODE_PRIVATE);
                Boolean destroy = preference.getBoolean("destroy",false);
                SharedPreferences randomPlay = getSharedPreferences("random",MODE_PRIVATE);
                Boolean random = randomPlay.getBoolean("random",false);
                if(items.size()-1 == currentPosition && repeat.equals("off") && destroy){
                    if(random){
                        forwardRandom();
                        } else {
                            forward();
                        }
                    } else if (items.size()-1 == currentPosition && repeat.equals("on") && destroy){
                        if (random){
                            forwardRandom();
                        } else {
                            forward();
                        }
                    } else if(destroy){
                        if(random){
                            forwardRandom();
                        } else {
                            forward();
                        }
                    } else if(random){
                        forwardRandom();
                    }

                updateNotificationPlayer();
            }
        });

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                isPrepared = false;
                mp.reset();
                rewind();
                sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED));
                updateNotificationPlayer();
                return false;
            }
        });


        mNotificationPlayer = new NotificationPlayer(this);

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this,AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
        } else if(result == AudioManager.AUDIOFOCUS_REQUEST_FAILED){
        }

//        mediaPlayer.setLooping(false);
        SharedPreferences preferences = getSharedPreferences("repeat",MODE_PRIVATE);
        String repeat = preferences.getString("repeat","off");
        if(repeat.equals("one")){
            mediaPlayer.setLooping(true);
        }


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            String action = intent.getAction();
            if(CommandActions.TOGGLE_PLAY.equals(action)){
                if (isPlaying()){
                    pause();
                } else {
                    play();
                }
            } else if (CommandActions.REWIND.equals(action)){
                rewind();
            } else if (CommandActions.FORWARD.equals(action)){
                forward();
            } else if(CommandActions.CLOSE.equals(action)){
                pause();
                removeNotificationPlayer();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void updateNotificationPlayer(){
        if(mNotificationPlayer != null){
            mNotificationPlayer.updateNotificationPlayer();
        }
    }

    public void removeNotificationPlayer(){
        if(mNotificationPlayer != null){
            mNotificationPlayer.removeNotificationPlayer();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mbinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            unregisterBroadcast();
        }
    }

    public PlaylistItem getMusicItem(){

        return playItem;
    }

    private PlaylistItem queryAudioItem (int position){
        currentPosition = position;
        String title = items.get(currentPosition).getTitle();
        String artist = items.get(currentPosition).getArtist();
        String imagePath = items.get(currentPosition).getImgPath();
        String musicURL = items.get(currentPosition).getMusicURL();
        String lyrics = items.get(currentPosition).getLyrics();
        String albumName = items.get(currentPosition).getAlbumName();
        String genre = items.get(currentPosition).getGenre();
        String date = items.get(currentPosition).getDate();


        playItem = new PlaylistItem(title,artist,imagePath,musicURL,lyrics,albumName,genre,date);

        return playItem;
    }

    private void prepare(PlaylistItem item){
        try{
            String url = item.getMusicURL();
            String urlEncoder = URLEncoder.encode(url,"UTF-8");
            String urlDecoder = URLDecoder.decode(urlEncoder,"UTF-8");
            mediaPlayer.setDataSource(urlDecoder);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepareAsync();
        }catch (Exception e){
            e.printStackTrace();
            mediaPlayer.release();
        }
    }

    public void stop(){
        mediaPlayer.pause();
//        mediaPlayer.stop();
    }

    public void setPlaylist(ArrayList<PlaylistItem> item){
        if(!items.equals(item)){
            items.clear();
            items.addAll(item);
        }
    }

    public void setPlaylist(ArrayList<PlaylistItem> item , int position){
        if(!items.equals(item)){
            items.clear();
            items.addAll(item);
            currentPosition = position;
        }
    }

    public void seek (int sec){
        mediaPlayer.seekTo(sec);
    }

    public int getMusicPostion(){
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration(){
        return mediaPlayer.getDuration();
    }

    public int getCurrentPosition(){
        return currentPosition;
    }

    public void play(){
        if(isPrepared){
            mediaPlayer.start();
            sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED));
            updateNotificationPlayer();
        }
    }

    public void play(int position){

        mediaPlayer.reset();
        PlaylistItem getItem  = queryAudioItem(position);
        prepare(getItem);
        sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED));
        sendBroadcast(new Intent(BroadcastActions.PLAY_START));
        updateNotificationPlayer();
    }

    public void pause(){
        if(isPrepared){
            mediaPlayer.pause();
            sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED));
            updateNotificationPlayer();
        }
    }

    public void playReady(int position){
        mediaPlayer.reset();
        PlaylistItem getItem = queryAudioItem(position);
        try{
            String url = getItem.getMusicURL();
            String urlEncoder = URLEncoder.encode(url,"UTF-8");
            String urlDecoder = URLDecoder.decode(urlEncoder,"UTF-8");
            mediaPlayer.setDataSource(urlDecoder);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepareAsync();
            firstPlaying = true;
        } catch (Exception e){
            e.printStackTrace();
            mediaPlayer.release();
        }
    }

    public void forward(){
        SharedPreferences preferences = getSharedPreferences("repeat",MODE_PRIVATE);
        String repeat = preferences.getString("repeat","off");
        if(repeat.equals("on")){
            if(items.size() -1 > currentPosition){
                currentPosition++;
                play(currentPosition);
            } else {
                currentPosition = 0;
                play(currentPosition);
            }
        }else {
            if(items.size() -1 > currentPosition){
                currentPosition++;
                play(currentPosition);
            } else {
                currentPosition = 0;
                playReady(currentPosition);
            }
        }

//        mediaPlayer.reset();
    }

    public void forwardClick(){
        SharedPreferences randomPlay = getSharedPreferences("random",MODE_PRIVATE);
        Boolean random = randomPlay.getBoolean("random",false);
        if(random){
           forwardRandom();
        } else {
            if(items.size() -1 > currentPosition){
                currentPosition++;
                play(currentPosition);
            } else {
                currentPosition = 0;
                play(currentPosition);
            }
        }

    }

    public void forwardRandom(){
        Random random = new Random();
        int randomPlayPosition = random.nextInt(items.size());
        if(randomPlayPosition == currentPosition){
            int randomRePlayPosition = random.nextInt(items.size());
            play(randomRePlayPosition);
        } else {
            play(randomPlayPosition);
        }
    }

    public void rewind(){
        SharedPreferences randomPlay = getSharedPreferences("random",MODE_PRIVATE);
        Boolean random = randomPlay.getBoolean("random",false);
        if(random){
            forwardRandom();
        } else {
            if(currentPosition > 0){
                currentPosition--;
            } else {
                currentPosition = items.size() - 1;
            }
            play(currentPosition);
        }

    }

    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }

    //한곡 반복

    public void repeatOne(){
        if(mediaPlayer != null){
           mediaPlayer.setLooping(true);
//            allRepeat = false;
//            oneRepeat = true;
        }
    }

    public void repeatOff(){
        if(mediaPlayer != null){
            mediaPlayer.setLooping(false);
//            allRepeat = false;
//            oneRepeat = false;
        }
    }

    public void repeatAll(){
        if(mediaPlayer!=null){
            mediaPlayer.setLooping(false);
//            allRepeat = true;
//            oneRepeat = false;
        }
    }

    public void getRemoveNotificationPlayer(){
        removeNotificationPlayer();
    }

}
