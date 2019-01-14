package com.example.onosystems;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class CallAPI {
    static CookieManager manager;
    static void setCookieManager(CookieManager manager) {
        CallAPI.manager = manager;
        CookieHandler.setDefault(manager);
    }
    static List<HttpCookie> getCookies() {
        return manager.getCookieStore().getCookies();
    }
    public static String post(URL url, String body) {
        String resultJson = "";
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
                resultJson = strBuilder_Json.toString();
                System.out.println("*****\r\n" + resultJson + "*****");
                con.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultJson;
    }

}
