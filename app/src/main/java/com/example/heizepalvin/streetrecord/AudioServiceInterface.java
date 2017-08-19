package com.example.heizepalvin.streetrecord;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.provider.MediaStore;

import java.util.ArrayList;

/**
 * Created by soyounguensoo on 2017-07-27.
 */

public class AudioServiceInterface {

    private ServiceConnection serviceConnection;
    private AudioService mService;

    public AudioServiceInterface(Context context){
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = ((AudioService.AudioServiceBinder) service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
                serviceConnection = null;
            }
        };
        context.bindService(new Intent(context, AudioService.class).setPackage(context.getPackageName()),serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void setPlaylist(ArrayList<PlaylistItem> item){
        if(mService != null){
            mService.setPlaylist(item);
        }
    }

    public void setPlaylist(ArrayList<PlaylistItem> item, int position){
        if(mService != null){
            mService.setPlaylist(item,position);
        }
    }

    public PlaylistItem getMusicItem(){
        if(mService != null){
            return mService.getMusicItem();
        }
        return null;
    }

    public void togglePlay(){
        if(isPlaying()){
            mService.pause();
        } else {
            mService.play();
        }
    }

    public void seek(int sec){
        if(mService != null){
            mService.seek(sec);
        }
    }

    public int getMusicPosition(){
        if(mService != null){
            mService.getMusicPostion();
        }
        return mService.getMusicPostion();
    }

    public int getDuration(){
        if(mService != null){
            mService.getDuration();
        }
        return mService.getDuration();
    }

    public boolean isPlaying() {
        if (mService != null) {
            return mService.isPlaying();
        }
        return false;
    }

    public void play(int position){
        if(mService != null){
            mService.play(position);
        }
    }

    public void play(){
        if(mService != null){
            mService.play();
        }
    }

    public void pause(){
        if(mService != null){
            mService.pause();
        }
    }

    public void forward(){
        if(mService != null){
            mService.forward();
        }
    }

    public void forwardClick(){
        if(mService != null){
            mService.forwardClick();
        }
    }

    public void rewind(){
        if(mService != null){
            mService.rewind();
        }
    }

    //한곡 반복

    public void repeatOne(){
        if(mService != null){
            mService.repeatOne();
        }
    }

    public void repeatOff(){
        if(mService != null){
            mService.repeatOff();
        }
    }

    public void repeatAll(){
        if(mService != null){
            mService.repeatAll();
        }
    }

    public void getRemoveNotificationPlayer(){
        if(mService != null){
            mService.getRemoveNotificationPlayer();
        }
    }

    public int getCurrentPosition(){
        return mService.getCurrentPosition();
    }

    public void getUpdateNotificationPlayer(){
        if(mService != null){
            mService.updateNotificationPlayer();
        }
    }

    public void stop(){
        if(mService != null){
            mService.stop();
        }
    }

}
