package com.example.onosystems;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
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

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        data = new NoticeData(remoteMessage.getNotification(), remoteMessage.getData());
        if (needSpeech(data)) {
            if (!initialized) {
                textToSpeech = new TextToSpeech(getApplicationContext(), this);
                textToSpeech.setSpeechRate(1.0f);
                textToSpeech.setPitch(1.0f);
//            引数sは与えられたUUID
                textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String s) {
//                        System.out.println("読み上げ開始: " + s);
                    }

                    @Override
                    public void onDone(String s) {
//                        System.out.println("読み上げ終了: " + s);
                        textToSpeechDestroy();
                    }

                    @Override
                    public void onError(String s) {
                        System.out.println("読み上げ中にエラーが発生しました。");
                        System.out.println(s);
                    }
                });
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
        System.out.println("LOGINED_ID================================-");
        System.out.println(LoginActivity.logined_id);
        if (data.getUserType().equals(DRIVER_USER) && LoginActivity.usertype == LoginActivity.DRIVER_USER) {
            Intent homeIntent = new Intent(getApplication(), CourierHomeActivity.class);
            Intent openIntent = new Intent(this, CourierDeliveryDetail.class);
            homeIntent.putExtra("driver_id", LoginActivity.logined_id);
            homeIntent.putExtra("password", LoginActivity.loginPassword);
            openIntent.putExtra("itemInfo", data.getDelivery());
            taskStackBuilder.addParentStack(CourierHomeActivity.class);
            taskStackBuilder.addNextIntent(homeIntent);
            taskStackBuilder.addNextIntent(openIntent);
        } else if (data.getUserType().equals(CUSTOMER_USER) && LoginActivity.usertype == LoginActivity.CUSTOMER_USER) {
            Intent homeIntent = new Intent(getApplication(), CustomerHomeActivity.class);
            Intent openIntent = new Intent(this, CustomerDeliveryDetail.class);
            homeIntent.putExtra("customer_id", LoginActivity.logined_id);
            homeIntent.putExtra("password", LoginActivity.loginPassword);
            openIntent.putExtra("itemInfo", data.getDelivery());
            taskStackBuilder.addParentStack(CustomerHomeActivity.class);
            taskStackBuilder.addNextIntent(homeIntent);
            taskStackBuilder.addNextIntent(openIntent);
        } else {
            Intent openIntent = new Intent(getApplication(), LoginActivity.class);
            taskStackBuilder.addParentStack(LoginActivity.class);
            taskStackBuilder.addNextIntent(openIntent);
        }
        System.out.println("TaskStackBuilder.getIntents()");
        for (Intent intent: taskStackBuilder.getIntents()) {
            System.out.println(intent.toString());
        }
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, openIntent, PendingIntent.FLAG_ONE_SHOT);
        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);

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

class NoticeData {
    private String title, body, userType;
    private int type;
    private HashMap<String, String> delivery;

    NoticeData(RemoteMessage.Notification notification, Map<String, String> payload) {
        this.title = notification.getTitle();
        this.body = notification.getBody();
        this.userType = getString("user_type", payload);
        this.type = getAndParseInt("notice_type", payload);
        payload.remove("user_type");
        payload.remove("notice_type");
        this.delivery = new HashMap<>();
        this.delivery.put("name", getString("name", payload));
        this.delivery.put("slipNumber", getString("slip_number", payload));
        this.delivery.put("address", getString("address", payload));
        this.delivery.put("unixTime", getString("time", payload));
        this.delivery.put("deliveryTime", getString("delivery_time", payload));
    }

    private String getString(String key, Map<String, String> payload) {
        String str = payload.get(key);
        if (str != null) {
            return str;
        } else {
            return "no string";
        }
    }

    private int getAndParseInt(String key, Map<String, String> payload) {
        String str = getString(key,payload);
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            System.out.println("cannot get number from string:" + str);
            return -1;
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

    public HashMap<String, String> getDelivery() {
        return delivery;
    }
}
