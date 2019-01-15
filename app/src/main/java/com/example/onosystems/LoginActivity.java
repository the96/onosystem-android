package com.example.onosystems;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;


/**
 * A login screen that offers login via email/password.
 */

public class LoginActivity extends AppCompatActivity {

    private String loginEmail;
    private String loginPassword;
    private int customer_id = 0;
    private int driver_id = 0;

    String customerId = "customerId";
    String driverId = "driverId";

    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailView = findViewById(R.id.loginId);
        mPasswordView = (EditText) findViewById(R.id.password);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        // ログインボタン
        ImageButton loginButton = findViewById(R.id.email_sign_in_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginEmail = mEmailView.getText().toString();
                loginPassword = mPasswordView.getText().toString();

                login(loginEmail, loginPassword);
            }
        });

        // アカウント作成ボタン
        Button createAccountButton = findViewById(R.id.newAccountButton);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewAccountActivity();
            }
        });
    }

    public void sendToken() {
        MyFirebaseMessagingService myFirebaseMessagingService = new MyFirebaseMessagingService();
        myFirebaseMessagingService.onTokenRefresh();

        Toast toast = Toast.makeText(LoginActivity.this, "test", Toast.LENGTH_SHORT);
        toast.show();
    }

    // 自動ログイン
    public void  autoLogin() {
//        login(id, password);
    }

    // ログイン処理
    public void login(String id, String password) {
        try {
            JSONObject loginJson = new JSONObject();
            loginJson.put("id", id);
            loginJson.put("password", password);

            URL url = null;
            try {
                url = new URL("http://sample.jp");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            String result = doInBackground();
            JSONObject jsonObject = new JSONObject(result);
//            JSONObject jsonObject = Request.sendRequest(url, loginJson);
            customer_id = jsonObject.getInt("customer_id");
            driver_id = jsonObject.getInt("driver_id");

            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            CallAPI.setCookieManager(cookieManager);

            transitionActivity();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // ログイン成功時にホーム画面へ遷移する
    public void transitionActivity() {
        FirebaseMessaging.getInstance().subscribeToTopic("test");
        sendToken();

        if (customer_id != 0) {
            // 消費者側
            Intent intent = new Intent(getApplication(), CustomerHomeActivity.class);
            intent.putExtra(customerId, customer_id);
            startActivity(intent);
        } else if (driver_id != 0){
            // 配達員側
            Intent intent = new Intent(getApplication(), CourierHomeActivity.class);
            intent.putExtra(driverId, driver_id);
            startActivity(intent);
        } else {
            mPasswordView.setError("Error");
        }
    }

    public void createNewAccountActivity() {
        Intent intent = new Intent(getApplication(), NewAccountActivity.class);
        startActivity(intent);
    }

//    @Override
    protected String doInBackground(String... params) {
        URL url = null;
        /*try {
            // Simulate network access.
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            return null;
        }*/

        try {
            url = new URL(params[0]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String body = params[1];
        return CallAPI.post(url, body);
    }
}

