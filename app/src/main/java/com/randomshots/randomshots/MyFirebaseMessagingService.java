package com.randomshots.randomshots;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    MediaPlayer music=new MediaPlayer();

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Intent intent=new Intent(this,MainActivity.class);

        System.out.println("Notification received");
        System.out.println("Remote message:"+remoteMessage);
        System.out.println("Notification from:"+remoteMessage.getFrom());
        System.out.println("Notification title:"+remoteMessage.getNotification().getTitle());
        System.out.println("Notification body:"+remoteMessage.getNotification().getBody());

        String type=null;
        if (remoteMessage.getData()!=null) {
            for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                try {
                    if (key.equals("type")) {
                        type = value;
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (remoteMessage.getData()!=null) {
            try {
                if (type.equals("version")) {
                    intent = new Intent(android.content.Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.randomshots.randomshots"));
                    System.out.println("Type is version");
                }
                else if(type.equals("newVideo")) {
                    intent=new Intent(this,Videos.class);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        PendingIntent pendingIntent=PendingIntent.getActivity(this,1,intent,0);

        Notification notification=new Notification.Builder(this)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pendingIntent)
                .build();

        int resourceId = getResources().getIdentifier("tap", "raw", "com.randomshots.randomshots");
        music = MediaPlayer.create(this, resourceId);
        music.start();

        NotificationManager notificationManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1,notification);
    }
}
