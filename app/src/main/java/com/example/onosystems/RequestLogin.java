package com.example.onosystems;

import java.net.CookieManager;
import java.net.CookiePolicy;

/*
 *  ログイン時にサーバとの通信で使用する
 */

public class RequestLogin extends Request {
    CookieManager manager;
    RequestLogin() {
        // Cookieが発行される通信の前(要するにLoginの際)にCookieManagerを生成してセットする
        manager = new CookieManager();
        manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CallAPI.setCookieManager(manager);
    }
}
