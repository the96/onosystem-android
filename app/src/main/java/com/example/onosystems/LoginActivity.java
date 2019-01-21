package com.example.onosystems;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.sql.Driver;


/**
 * A login screen that offers login via email/password.
 */

public class LoginActivity extends AppCompatActivity {

    private String loginEmail;
    private String loginPassword;
    private String token;
    private int customer_id = 0;
    private int driver_id = 0;
    private SharedPreferences sharedPreferences;
    private String url = "http://54.92.85.232/aws/Login";

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

//        sharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE);
//        autoLogin();

        mEmailView = findViewById(R.id.loginId);
        mPasswordView = (EditText) findViewById(R.id.password);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        // ログインボタン
        final ImageButton loginButton = findViewById(R.id.email_sign_in_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginEmail = mEmailView.getText().toString();
                loginPassword = mPasswordView.getText().toString();
                login(loginEmail, loginPassword);

                Intent intent = new Intent(LoginActivity.this, CourierHomeActivity.class);
                startActivity(intent);




//                FirebaseMessaging.getInstance().subscribeToTopic("test");

//                Toast toast = Toast.makeText(LoginActivity.this, "test", Toast.LENGTH_SHORT);
//                toast.show();
/*
                if (isEmpty(loginEmail)) {
                    if (isEmpty(loginPassword)) {
//                        login(loginEmail, loginPassword);

                    } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this)
                        .setMessage("パスワードを入力してください")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).show();
                    }
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this)
                            .setMessage("メールアドレスを入力してください")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }).show();
                }*/

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
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("LoginActivity", "getInstanceId failed", task.getException());
                            return;
                        }
                        token = task.getResult().getToken();
                        System.out.println("TOKEN: " + token);
                    }
                });
    }

//    public void sendToken() {
//        MyFirebaseMessagingService myFirebaseMessagingService = new MyFirebaseMessagingService();
//        myFirebaseMessagingService.onTokenRefresh();
//    }

    // 自動ログイン
    public void autoLogin() {
        // 端末からデータを取得
        String account = sharedPreferences.getString("account", "");
        String pass = sharedPreferences.getString("pass", "");
        if (isEmpty(account) && isEmpty(pass)) {
            login(account, pass);
        }
    }

    // ログイン処理
    public void login(final String id, final String password) {
        // urlとbodyは仮置き
        JSONObject body = new JSONObject();
        try {
            body.put("id", id);
            body.put("password", password);
            body.put("token", token);
            System.out.println(body.toString());
            
            new PostFirebaseTokenToServer().execute("url", body.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /*try {
            JSONObject loginJson = new JSONObject();
            loginJson.put("id", id);
            loginJson.put("password", password);

            String loginInfo = loginJson.toString();
            SampleLogin sampleLogin = new SampleLogin();
            sampleLogin.execute(url, loginInfo);
            String result = sampleLogin.doInBackground(url, loginInfo);

            JSONObject jsonObject = new JSONObject(result);
            customer_id = jsonObject.getInt("customer_id");
            driver_id = jsonObject.getInt("driver_id");

            transitionActivity();

        } catch (JSONException e) {
            e.printStackTrace();
        }*/

    }

    // ログイン成功時にホーム画面へ遷移する
    /*public void transitionActivity() {
        FirebaseMessaging.getInstance().subscribeToTopic("test");
        sendToken();

        // ログイン情報を端末に保存
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("account", loginEmail);
        editor.putString("pass", loginPassword);
        editor.apply();

        Toast toast = Toast.makeText(LoginActivity.this, "trance", Toast.LENGTH_SHORT);
                toast.show();

        Intent intent2 = new Intent(getApplication(), CustomerHomeActivity.class);
        startActivity(intent2);


        if (customer_id != 0) {
            // 消費者側
            Intent intent = new Intent(getApplication(), CustomerHomeActivity.class);
            intent.putExtra("customer_id", customer_id);
            intent.putExtra("password", loginPassword);
            startActivity(intent);
        } else if (driver_id != 0) {
            // 配達員側
            Intent intent = new Intent(getApplication(), CourierHomeActivity.class);
            intent.putExtra("driver_id", driver_id);
            intent.putExtra("password", loginPassword);
            startActivity(intent);
        } else {
            mPasswordView.setError("Error");
        }
    }*/

    public void createNewAccountActivity() {
        Intent intent = new Intent(getApplication(), NewAccountActivity.class);
        startActivity(intent);
    }

    public boolean isEmpty(String text) {
        if (!text.equals("") && !text.equals(null)) {
            return true;
        }
        return false;
    }

}

