package com.example.heizepalvin.streetrecord;

import android.app.Activity;
import android.app.Application;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.kakao.auth.KakaoSDK;

/**
 * Created by soyounguensoo on 2017-07-05.
 */

public class GlobalApplication extends Application {

    private static volatile GlobalApplication obj = null;
    private static volatile Activity currentActivity = null;

    private static GlobalApplication instance;
    private AudioServiceInterface mInterface;

    @Override
    public void onCreate() {
        super.onCreate();
        obj = this;
        KakaoSDK.init(new KakaoSDKAdapter());

        instance = this;
        mInterface = new AudioServiceInterface(getApplicationContext());
    }

    public static GlobalApplication getGlobalApplicationContext(){
        return obj;
    }

    public static Activity getCurrentActivity(){
        return currentActivity;
    }

    public static void setCurrentActivity(Activity currentActivity){
        GlobalApplication.currentActivity = currentActivity;
    }

    //음악 서비스

    public static GlobalApplication getInstance(){
        return instance;
    }

    public AudioServiceInterface getServiceInterface(){
        return mInterface;
    }
}
