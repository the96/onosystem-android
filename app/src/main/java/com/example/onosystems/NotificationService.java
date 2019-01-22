package com.example.onosystems;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.UUID;

public class NotificationService extends FirebaseMessagingService implements TextToSpeech.OnInitListener {
    private static final String NOTIFICATION_CHANNEL_ID = "ONOSYSTEMS_NOTIFICATION";
    private static final String NOTIFICATION_CHANNEL_NAME = "ONOSYSTEMS_NOTIFICATION";
    private static final String CUSTOMER_USER = "customer";
    private static final String DRIVER_USER = "driver";
    private static final int NOTICE_RECEIVABLE = 1;
    private static final int NOTICE_NOT_RECEIVABLE = 2;
    private static final int NOTICE_CHANGE_DATE = 3;
    private static final int NOTICE_NEAR_DRIVER = 1;
    private static TextToSpeech textToSpeech;
    private static NoticeData data;
    private boolean initialized = false;


    class NoticeData {
        private String title, body, userType;
        private int type;
        NoticeData(RemoteMessage.Notification notification, Map<String, String> payload) {
            this.title = notification.getTitle();
            this.body = notification.getBody();
            userType = payload.get("user_type");
            if (userType == null) {
//            userTypeがなかった場合
//            このコードではテストのため毎回読み上げる
                userType = DRIVER_USER;
            }
            try {
                this.type = Integer.parseInt(payload.get("notice_type"));
            } catch (NumberFormatException e) {
//            parseIntに失敗した場合、debug用に毎回読み上げる値をセット
//            このコードではテストのため毎回読み上げる
                this.type = NOTICE_NOT_RECEIVABLE;
            }
        }

        public int getType() {
            return type;
        }

        public String getBody() {
            return body;
        }

        public String getTitle() {
            return title;
        }

        public String getUserType() {
            return userType;
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        data = new NoticeData(remoteMessage.getNotification(), remoteMessage.getData());
        if (needSpeech(data)) {
            if (!initialized) {
                textToSpeech = new TextToSpeech(this, this);
                textToSpeech.setSpeechRate(1.0f);
                textToSpeech.setPitch(1.0f);
//            引数sは与えられたUUID
//            textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
//                @Override
//                public void onStart(String s) {
//                    System.out.println("読み上げ開始: " + s);
//                }
//
//                @Override
//                public void onDone(String s) {
//                    System.out.println("読み上げ終了: " + s);
//                }
//
//                @Override
//                public void onError(String s) {
//                    System.out.println("読み上げ中にエラーが発生しました。");
//                    System.out.println(s);
//                }
//            });
            } else {
                speechText(data.getBody());
            }
        }

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
                    .setContentTitle(data.getTitle())
                    .setContentText(data.getBody());
        } else {
            mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.onosystem_logo_mini)
                    .setContentTitle(data.getTitle())
                    .setContentText(data.getBody());
        }

        Intent openIntent = new Intent(this,  LoginActivity.class);
//        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
//        taskStackBuilder.addParentStack(LoginActivity.class);
//        taskStackBuilder.addNextIntent(openIntent);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, openIntent, PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(pendingIntent);
        notificationManager.notify(0, mBuilder.build());

    }

    @Override
    public void onInit(int i) {
        if (i != TextToSpeech.SUCCESS) {
            System.out.println("TextToSpeech initializing failed");
        } else {
            initialized = true;
            speechText(data.getBody());
        }
    }

    private boolean needSpeech(NoticeData notice) {
        return notice.getUserType().equals(DRIVER_USER) && notice.getType()!= NOTICE_RECEIVABLE;
    }

    private void speechText(String text) {
        if (initialized && text.length() > 0) {
            textToSpeech.speak(text,  TextToSpeech.QUEUE_ADD, null, UUID.randomUUID().toString());
        }
    }
}