package com.example.onosystems;

import android.os.AsyncTask;

import java.net.MalformedURLException;
import java.net.URL;

public class DeliveryInfoAPI1 extends AsyncTask<String, String, String>{

    DeliveryInfoAPI1() {
    }

    interface Callback {
        void callbackMethod1(String result);
    }
    private Callback reference;
    public void setReference(Callback ref) {
        this.reference = ref;
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
        reference.callbackMethod1(result);
    }
}
