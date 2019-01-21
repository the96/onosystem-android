package com.example.onosystems;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
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

public class LoginActivity extends AppCompatActivity implements PostAsync.Callback{

    private String loginEmail;
    private String loginPassword;
    int customer_id = 0;
    int driver_id = 0;
    int manager_id = 0;
    String loginResult = "";
    private SharedPreferences sharedPreferences;
    private String url = "http://www.onosystems.work/aws/Login";

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // ログイン情報を保存するためのもの
        this.sharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE);
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

    // 自動ログイン
    public void autoLogin() {
        // 端末からデータを取得
        String account = this.sharedPreferences.getString("account", "");
        String pass = this.sharedPreferences.getString("pass", "");
        if (isEmpty(account) && isEmpty(pass)) {
            login(account, pass);
        }
    }

    // ログイン処理
    public void login(String id, String password) {
        try {
            JSONObject loginJson = new JSONObject();
            loginJson.put("password", password);
            loginJson.put("id", id);
            String token = FirebaseInstanceId.getInstance().getToken();
            loginJson.put("token", token);

            String loginInfo = loginJson.toString();
            sendRequest(loginInfo);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (this.loginResult.equals("no")) {
            AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this)
                    .setTitle("エラー")
                    .setMessage("ログインに失敗しました")
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
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("account", this.loginEmail);

        editor.putString("pass", this.loginPassword);
        editor.apply();

        if (this.customer_id != 0) {
            // 消費者側
            Intent intent = new Intent(getApplication(), CustomerHomeActivity.class);
            intent.putExtra("customer_id", this.customer_id);
            intent.putExtra("password", this.loginPassword);
            startActivity(intent);
        } else if (this.driver_id != 0) {
            // 配達員側
            Intent intent = new Intent(getApplication(), CourierHomeActivity.class);
            intent.putExtra("driver_id", this.driver_id);
            intent.putExtra("password", this.loginPassword);
            startActivity(intent);
        } else if (this.manager_id != 0) {
            Toast toast = Toast.makeText(LoginActivity.this, "管理者ユーザーです。", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this)
                    .setMessage("ログインしますか？")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            login(loginEmail, loginPassword);
                        }
                    }).show();
//            Toast toast = Toast.makeText(LoginActivity.this, "もう一度ログインボタンを押してください", Toast.LENGTH_SHORT);
//            toast.show();
        }
    }

    public void sendRequest(String json) {
        showProgress(true);

        PostAsync.initializeCallAPI();
        PostAsync postAsync = new PostAsync();
        postAsync.setRef(this);
        postAsync.execute(url, json);
    }

    @Override
    public void callback(String result) {
        showProgress(false);
        try {
            JSONObject jsonObject = new JSONObject(result);
            this.customer_id = jsonObject.optInt("customer_id");
            this.driver_id = jsonObject.optInt("driver_id");
            this.manager_id = jsonObject.optInt("manager_id");
            this.loginResult = jsonObject.optString("result");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void createNewAccountActivity() {
        Intent intent = new Intent(getApplication(), NewAccountActivity.class);
        startActivity(intent);
    }

    public boolean isEmpty(String text) {
        return !text.equals("");
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
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

