package com.example.onosystems;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AlertDialog;
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
 * メールアドレスとパスワードでログインするログイン画面
 */

public class LoginActivity extends AppCompatActivity implements RequestLogin.CallBack{

    private String loginEmail;
    private String loginPassword;
    public int customer_id = 0;
    public int driver_id = 0;
    private SharedPreferences sharedPreferences;
    private String url = "http://54.92.85.232/aws/Login";

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

//                FirebaseMessaging.getInstance().subscribeToTopic("test");

                if (!isEmpty(loginEmail)) {
                    mEmailView.setError("メールアドレスを入力してください");
                } else if (!isEmailValid(loginEmail)) {
                    mEmailView.setError("@が含まれていません");
                } else if (!isEmpty(loginPassword)) {
                    mPasswordView.setError("パスワードを入力してください");
                } else {
                    login(loginEmail, loginPassword);
                }


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
//        MyFirebaseMessagingService myFirebaseMessagingService = new MyFirebaseMessagingService();
//        myFirebaseMessagingService.onTokenRefresh();
    }

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
    public void login(String id, String password) {
        try {
            JSONObject loginJson = new JSONObject();
            loginJson.put("id", id);
            loginJson.put("password", password);

            String loginInfo = loginJson.toString();
            sendRequest(loginInfo);

            // テスト用
            JSONObject test = new JSONObject();
            for (String credential : DUMMY_CUSTOMER) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(loginEmail) && pieces[1].equals(loginPassword)) {
                    test.put("customer_id",1);
                    this.customer_id = test.getInt("customer_id");
                    this.driver_id = test.getInt("driver_id");
                    String s = String.valueOf(customer_id);
                }
            }
            for (String credential : DUMMY_DRIVER) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(loginEmail) && pieces[1].equals(loginPassword)) {
                    test.put("driver_id", 1);
//                            this.customer_id = test.getInt("customer_id");
                    this.driver_id = test.getInt("driver_id");
                    String s = String.valueOf(driver_id);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (customer_id == 0 && driver_id == 0) {
            AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this)
                    .setMessage("ログインに失敗しました" + customer_id)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mPasswordView.setText("");
                        }
                    }).show();
        } else {
            transitionActivity();
        }

    }

    // ログイン成功時にホーム画面へ遷移する
    public void transitionActivity() {
//        FirebaseMessaging.getInstance().subscribeToTopic("test");
//        sendToken();

        // ログイン情報を端末に保存
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("account", loginEmail);
//        editor.putString("pass", loginPassword);
//        editor.apply();

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
        }
    }

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

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    public void sendRequest(String json) {
        showProgress(true);

        RequestLogin requestLogin = new RequestLogin();
        requestLogin.setCallBack(this);
        requestLogin.execute(url, json);
    }

    // テスト用
    public static final String[] DUMMY_CUSTOMER = new String[]{
            "@1:1","kut@gmail.com:onosystems", "kut2@gmail.com:onosystems2", "kut3@gmail.com:onosystems3"
    };
    public static final String[] DUMMY_DRIVER = new String[]{
            "driver@gmail.com:driver","@2:2"
    };

    //    @Override
    public void fetchResult(String result) {
        try {
            // テスト用
            // Simulate network access.
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }

        showProgress(false);

    }

    // progressの表示とログインフォームの非表示.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}

