package com.example.onosystems;

import android.os.AsyncTask;

import java.net.MalformedURLException;
import java.net.URL;

public class DeliveryInfoAPI extends AsyncTask<String, String, String>{

    DeliveryInfoAPI() {
    }

    interface Callback {
        void callbackMethod(String result);
    }
    private DeliveryInfoAPI.Callback reference;
    public void setReference(DeliveryInfoAPI.Callback ref) {
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
        reference.callbackMethod(result);
    }
}
