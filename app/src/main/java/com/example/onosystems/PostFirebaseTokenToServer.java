package com.example.onosystems;

import android.os.AsyncTask;

import java.net.MalformedURLException;
import java.net.URL;

public class PostFirebaseTokenToServer extends AsyncTask<String,String,String> {

    // 第一引数がURL、第二引数がbody
    @Override
    protected String doInBackground(String... strings) {
        URL url = null;
        try {
            url = new URL(strings[0]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String body = strings[1];
//        return CallAPI.post(url, body);
        return null;
    }
}
