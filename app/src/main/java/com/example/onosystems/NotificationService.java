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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NotificationService extends FirebaseMessagingService implements TextToSpeech.OnInitListener {
    private static final String NOTIFICATION_CHANNEL_ID = "ONOSYSTEMS_NOTIFICATION";
    private static final String NOTIFICATION_CHANNEL_NAME = "ONOSYSTEMS_NOTIFICATION";
    private static final String CUSTOMER_USER = "customer";
    private static final String DRIVER_USER = "driver";
    private static final int NOT_INITIALIZED = -1;
    private static final int LIST_HAS_NOT_DELIVERY = -2;
    private static final int INVALID_INPUT = -3;
    private static final int NOT_FOUND = -4;
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
        int index = getIndexInDeliveries(data.getSlipNumber());
        boolean valid_index = index != NOT_INITIALIZED && index != LIST_HAS_NOT_DELIVERY && index != NOT_FOUND;
        ArrayList<HashMap<String, String>> list;
        Intent homeIntent = null;
        Intent openIntent = null;

        if (LoginActivity.usertype != LoginActivity.OTHER_USER && equalTwoUserType(Integer.parseInt(data.getUserType()), LoginActivity.usertype)) {
            if (data.getUserType().equals(DRIVER_USER) && LoginActivity.usertype == LoginActivity.DRIVER_USER) {
                homeIntent = new Intent(getApplication(), CourierHomeActivity.class);
                if (valid_index)
                    openIntent = new Intent(this, CourierDeliveryDetail.class);
                taskStackBuilder.addParentStack(CourierHomeActivity.class);
            } else if (data.getUserType().equals(CUSTOMER_USER) && LoginActivity.usertype == LoginActivity.CUSTOMER_USER) {
                homeIntent = new Intent(getApplication(), CustomerHomeActivity.class);
                if (valid_index)
                    openIntent = new Intent(this, CustomerDeliveryDetail.class);
                taskStackBuilder.addParentStack(CustomerHomeActivity.class);
            }
            homeIntent.putExtra("customer_id", LoginActivity.logined_id);
            homeIntent.putExtra("password", LoginActivity.loginPassword);
            taskStackBuilder.addNextIntent(homeIntent);
            if (valid_index) {
                openIntent.putExtra("itemNumber", index);
                openIntent.putExtra("deliveryInfo", HomeActivity.list);
                taskStackBuilder.addNextIntent(openIntent);
            }
        } else {
            openIntent = new Intent(getApplication(), LoginActivity.class);
            taskStackBuilder.addParentStack(LoginActivity.class);
            taskStackBuilder.addNextIntent(openIntent);
        }
        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);

        mBuilder.setContentIntent(pendingIntent);

        notificationManager.notify(0, mBuilder.build());

    }

    private boolean equalTwoUserType(int usertype, int usertype_) {
        return usertype == usertype_;
    }

    public int getIndexInDeliveries(long ship_number) {
        if (HomeActivity.list == null) {
            Log.e("NotificationService", "HomeActivity or Deliveries List is not initialized object");
            return NOT_INITIALIZED;
        }
        if (HomeActivity.list.isEmpty()) {
            Log.e("NotificationService", "HomeActivity's Deliveries list has not Delivery.");
            return LIST_HAS_NOT_DELIVERY;
        }
        if (ship_number <= 0) {
            Log.e("NotificationService", "input ship number is invalid number: " + ship_number);
            return INVALID_INPUT;
        }
        int i = 0;
        for (HashMap<String, String> map: HomeActivity.list) {
            if (Long.parseLong(map.get("slipNumber")) == ship_number)
                return i;
            else
                i++;
        }
        return NOT_FOUND;
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

    public long getSlipNumber() {
        return Long.parseLong(delivery.get("slipNumber"));
    }
}
