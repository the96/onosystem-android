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
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

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
    //private static
    private boolean initialized = false;

    class Delivery {
        private String name, address, ship_to, ship_from, ;
        private long slip_number, time;
        private int delivery_time, delivered_status, receivable_status;
    }
    class NoticeData {
        private String title, body, userType;
        private int type;
        private Delivery delivery;

        NoticeData(RemoteMessage.Notification notification, Map<String, String> payload) {
            this.title = notification.getTitle();
            this.body = notification.getBody();
            userType = payload.get("user_type");
            if (userType == null) {
//            userTypeがなかった場合
//            このコードではテストのため毎回読み上げる
                userType = DRIVER_USER;
            }
            type = getAndParseInt("notice_type", payload);
        }

        private int getAndParseInt(String key, Map<String, String> payload) {
            try {
                return Integer.parseInt(payload.get("notice_type"));
            } catch (NumberFormatException e) {
                System.out.println(e);
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
    public void onCreate() {
        System.out.println("Notification Service is created!!");
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        data = new NoticeData(remoteMessage.getNotification(), remoteMessage.getData());
        if (needSpeech(data)) {
            if (!initialized) {
                textToSpeech = new TextToSpeech(getApplicationContext(), this);
                textToSpeech.setSpeechRate(1.0f);
                textToSpeech.setPitch(1.0f);
//            引数sは与えられたUUID
//                textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
//                    @Override
//                    public void onStart(String s) {
//                        System.out.println("読み上げ開始: " + s);
//                    }
//
//                    @Override
//                    public void onDone(String s) {
//                        System.out.println("読み上げ終了: " + s);
//                        textToSpeechDestroy();
//                    }
//
//                    @Override
//                    public void onError(String s) {
//                        System.out.println("読み上げ中にエラーが発生しました。");
//                        System.out.println(s);
//                    }
//                });
            } else {
                speechText(data.getBody());
            }
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            channel.setSound(null,null);
            notificationManager.createNotificationChannel(channel);
            mBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.onosystem_logo_mini)
                    .setContentTitle(data.getTitle())
                    .setContentText(data.getBody())
                    .setDefaults(0);
        } else {
            mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.onosystem_logo_mini)
                    .setContentTitle(data.getTitle())
                    .setContentText(data.getBody());
        }

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        if (data.getUserType().equals(DRIVER_USER)) {
            Intent openIntent = new Intent(this, CourierDeliveryDetail.class);
            taskStackBuilder.addParentStack(CourierHomeActivity.class);
            taskStackBuilder.addNextIntent(openIntent);
        } else if (data.getUserType().equals(CUSTOMER_USER)) {
            Intent openIntent = new Intent(this, CustomerDeliveryDetail.class);
            taskStackBuilder.addParentStack(CustomerHomeActivity.class);
            taskStackBuilder.addNextIntent(openIntent);
        }
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, openIntent, PendingIntent.FLAG_ONE_SHOT);
        PendingIntent pendingIntent =
                taskStackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );

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

    private void speechText(final String text) {
        if (initialized && text.length() > 0) {
            new Thread( new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        textToSpeech.speak(text,  TextToSpeech.QUEUE_ADD, null, UUID.randomUUID().toString());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }


    public void textToSpeechDestroy() {
        if (textToSpeech != null) {
            textToSpeech.setOnUtteranceProgressListener(null);
            textToSpeech.shutdown();
        }
        textToSpeech = null;
        initialized = false;
    }
}