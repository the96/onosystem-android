package com.example.onosystems;

import android.os.AsyncTask;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.MalformedURLException;
import java.net.URL;

public class SampleLogin extends AsyncTask<String, String, String> {
    CookieManager manager;
    SampleLogin() {
        // Cookieが発行される通信の前(要するにLoginの際)にCookieManagerを生成してセットする
        manager = new CookieManager();
        manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CallAPI.setCookieManager(manager);
    }

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
        return CallAPI.post(url, body);
    }
    @Override
    protected void onPostExecute(String result) {
        System.out.println(result);
        // デバッグ用にセットされているCookieを全て出力している
        for (HttpCookie cookie:CallAPI.getCookies()) {
            System.out.println(cookie.getName() + ": " + cookie.getValue());
        }
    }
}
