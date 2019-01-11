package com.example.onosystems;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class CallAPI {
    public static String post(URL url, String body) {
        String json = "";
        try {
            // urlの生成とhttps接続の準備
            System.out.println(url);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
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
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
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