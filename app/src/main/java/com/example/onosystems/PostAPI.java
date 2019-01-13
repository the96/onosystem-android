package com.example.onosystems;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PostAPI extends AsyncTask<String,String,String> {
    URL url;
    String body;
    String json;
    interface PostedCallback {
        public void postedCallback(String json);
    }
    private PostedCallback ref;

    PostAPI(String url, String body) {
        super();
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        this.body = body;
        CookieManager manager = new CookieManager();
        manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(manager);
    }

    public void setUrl(String url) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setReference(PostedCallback ref) {
        this.ref = ref;
    }

    @Override
    protected String doInBackground(String... strings) {
        this.json = post(url,body);
        return this.json;
    }

    @Override
    protected void onPostExecute(String result) {
        ref.postedCallback(result);
    }

    private static String post(URL url, String body) {
        String json = "";
        try {
            // urlの生成とhttps接続の準備
            System.out.println(url);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            con.setDoInput(true);
            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
            out.write(body);
            out.close();

            // httpsリクエスト
            con.connect();
            int status = con.getResponseCode();
            // 接続できたら中身を確認
            if (status == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = con.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder strBuilder_Json = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    strBuilder_Json.append(line + "\r\n");
                }
                json = strBuilder_Json.toString();
                System.out.println("*****\r\n" + json + "*****");
                con.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static String get(URL url) {
        String json = "";
        try {
            // urlの生成とhttps接続の準備
            System.out.println(url);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(false);
            con.setDoInput(true);

            // httpsリクエスト
            con.connect();
            int status = con.getResponseCode();
            // 接続できたら中身を確認
            if (status == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = con.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder strBuilder_Json = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    strBuilder_Json.append(line + "\r\n");
                }
                json = strBuilder_Json.toString();
                System.out.println("*****\r\n" + json + "*****");
                con.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }
}