package com.example.heizepalvin.streetrecord;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;

/**
 * Created by soyounguensoo on 2017-08-01.
 */

public class NotificationPlayer {

    private final static int NOTIFICATION_PLAYER_ID = 0x342;
    private AudioService audioService;
    private NotificationManager mNotificationManager;
    private NotificationManagerBuilder notificationManagerBuilder;
    private boolean isForeground;

    public NotificationPlayer(AudioService service){
        audioService = service;
        mNotificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
    }
    public void updateNotificationPlayer(){
        cancel();
        notificationManagerBuilder = new NotificationManagerBuilder();
        notificationManagerBuilder.execute();
    }

    public void removeNotificationPlayer(){
        cancel();
        audioService.stopForeground(true);
        isForeground = false;
    }

    private void cancel(){
        if(notificationManagerBuilder != null){
            notificationManagerBuilder.cancel(true);
            notificationManagerBuilder = null;
        }
    }

    private class NotificationManagerBuilder extends AsyncTask<Void, Void, Notification>{

        private RemoteViews mRemoteViews;
        private NotificationCompat.Builder mNotificationBuilder;
        private PendingIntent mMainPendingIntent;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Intent mainActivity = new Intent(audioService, MainActivity.class);
            mainActivity.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            mMainPendingIntent = PendingIntent.getActivity(audioService, 0 , mainActivity,0);
            mRemoteViews = createRemoteView(R.layout.notification_player);
            mNotificationBuilder = new NotificationCompat.Builder(audioService);
            mNotificationBuilder.setSmallIcon(R.drawable.logos)
                    .setOngoing(true)
                    .setContentIntent(mMainPendingIntent)
                    .setContent(mRemoteViews);

            Notification notification = mNotificationBuilder.build();
            notification.priority = Notification.PRIORITY_MAX;
            notification.contentIntent = mMainPendingIntent;
            if(!isForeground){
                isForeground = true;
                audioService.startForeground(NOTIFICATION_PLAYER_ID,notification);
            }
        }

        @Override
        protected Notification doInBackground(Void... params) {

            mNotificationBuilder.setContent(mRemoteViews);
            mNotificationBuilder.setContentIntent(mMainPendingIntent);
            mNotificationBuilder.setPriority(Notification.PRIORITY_MAX);
            Notification notification = mNotificationBuilder.build();

            return notification;
        }

        @Override
        protected void onPostExecute(Notification notification) {
            super.onPostExecute(notification);
            try{
                updateRemoteView(mRemoteViews, notification);
                mNotificationManager.notify(NOTIFICATION_PLAYER_ID, notification);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        private RemoteViews createRemoteView(int layoutId){
            RemoteViews remoteView = new RemoteViews(audioService.getPackageName(), layoutId);
            Intent actionTogglePlay = new Intent(CommandActions.TOGGLE_PLAY);
            Intent actionForward = new Intent(CommandActions.FORWARD);
            Intent actionRewind = new Intent(CommandActions.REWIND);
            Intent actionClose = new Intent(CommandActions.CLOSE);
            PendingIntent togglePlay = PendingIntent.getService(audioService, 0 , actionTogglePlay, 0);
            PendingIntent forward = PendingIntent.getService(audioService, 0 , actionForward, 0);
            PendingIntent rewind = PendingIntent.getService(audioService, 0 , actionRewind, 0);
            PendingIntent close = PendingIntent.getService(audioService, 0 , actionClose, 0);

            remoteView.setOnClickPendingIntent(R.id.notificationPlay, togglePlay);
            remoteView.setOnClickPendingIntent(R.id.notificationNext, forward);
            remoteView.setOnClickPendingIntent(R.id.notificationPre, rewind);
            remoteView.setOnClickPendingIntent(R.id.notificationClose, close);

            return remoteView;
        }

        private void updateRemoteView(RemoteViews remoteViews, Notification notification){



            remoteViews.setImageViewResource(R.id.notificationPre, R.drawable.pre);
            remoteViews.setImageViewResource(R.id.notificationNext, R.drawable.next);
            remoteViews.setImageViewResource(R.id.notificationClose, R.drawable.close);

            String title = audioService.getMusicItem().getTitle();
            String artist = audioService.getMusicItem().getArtist();
            String albumImg = audioService.getMusicItem().getImgPath();
            remoteViews.setTextViewText(R.id.notificationTitle, title);
            remoteViews.setTextViewText(R.id.notificationArtist, artist);
            Picasso.with(audioService).load(albumImg).error(R.drawable.logoface).into(remoteViews,R.id.notificationImg,NOTIFICATION_PLAYER_ID,notification);

            if(audioService.isPlaying()){
                remoteViews.setImageViewResource(R.id.notificationPlay, R.drawable.pause);
            } else {
                remoteViews.setImageViewResource(R.id.notificationPlay, R.drawable.play);
            }

        }
    }

}
