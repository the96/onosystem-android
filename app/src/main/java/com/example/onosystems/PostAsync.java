package com.example.onosystems;

import android.os.AsyncTask;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.MalformedURLException;
import java.net.URL;

public class PostAsync  extends AsyncTask<String, String, String> {
    interface Callback {
        void callback(String result);
    }
    private Callback ref;

    // 初回の通信の際に呼び出す
    public static void initializeCallAPI() {
        // Cookieが発行される通信の前(要するにLoginの際)にCookieManagerを生成してセットする
        CookieManager manager = new CookieManager();
        manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CallAPI.setCookieManager(manager);
    }

    public void setRef(Callback ref) {
        this.ref = ref;
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
        ref.callback(result);
    }
}
