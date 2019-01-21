package com.example.onosystems;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationService extends FirebaseMessagingService {
    private static final String NOTIFICATION_CHANNEL_ID = "ONOSYSTEMS_NOTIFICATION";
    private static final String NOTIFICATION_CHANNEL_NAME = "ONOSYSTEMS_NOTIFICATION";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(channel);
            mBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.onosystem_logo_mini)
                    .setContentTitle(title)
                    .setContentText(body);
        } else {
            mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.onosystem_logo_mini)
                    .setContentTitle(title)
                    .setContentText(body);
        }

        Intent openIntent = new Intent(this,  LoginActivity.class);
//        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
//        taskStackBuilder.addParentStack(LoginActivity.class);
//        taskStackBuilder.addNextIntent(openIntent);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, openIntent, PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(pendingIntent);
        notificationManager.notify(0, mBuilder.build());
    }
}
