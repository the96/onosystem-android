package com.example.onosystems;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * メールアドレスとパスワードでログインするログイン画面
 */

public class LoginActivity extends AppCompatActivity{

    static String loginEmail;
    static String loginPassword;
    static int usertype;
    static int logined_id;
    static final int OTHER_USER = 0;
    static final int DRIVER_USER = 1;
    static final int CUSTOMER_USER = 2;
    private String token;
    public static final String URL_ORIGIN = "https://www.onosystems.work/aws/";
    private SharedPreferences sharedPreferences;

    int customer_id = 0;
    int driver_id = 0;
    int manager_id = 0;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PostAsync.initializeCallAPI();
        setContentView(R.layout.activity_login);
        usertype = OTHER_USER;
        // ログイン情報を保存するためのもの
        sharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE);

        mEmailView = findViewById(R.id.loginId);
        mPasswordView = findViewById(R.id.password);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        // ログインボタン
        final ImageButton loginButton = findViewById(R.id.email_sign_in_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginEmail = mEmailView.getText().toString();
                loginPassword = mPasswordView.getText().toString();

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

        // トークンの取得
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
                        autoLogin(true);
                    }
                });
    }

    // 自動ログイン
    public void autoLogin(boolean auto) {
        if (auto) {
            // 端末からデータを取得
            loginEmail = sharedPreferences.getString("account", "");
            loginPassword = sharedPreferences.getString("pass", "");
            if (isEmpty(loginEmail) && isEmpty(loginPassword)) {
                login(loginEmail, loginPassword);
            }
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
            PostAsync postAsync = new PostAsync();
            postAsync.setRef(new PostAsync.Callback() {
                @Override
                public void callback(String result) {
                    showProgress(false);
                    try {
                        JSONObject json = new JSONObject(result);
                        int TmpCustomerId = json.optInt("customer_id");
                        int TmpDriverId = json.optInt("driver_id");
                        int TmpManagerId = json.optInt("manager_id");
                        if (TmpCustomerId != 0) {
                            customer_id = TmpCustomerId;
                            logined_id = customer_id;
                            usertype = CUSTOMER_USER;
                        } else if (TmpDriverId != 0) {
                            driver_id = TmpDriverId;
                            logined_id = driver_id;
                            usertype = DRIVER_USER;
                        } else if (TmpManagerId != 0) {
                            manager_id = TmpManagerId;
                            logined_id = manager_id;
                            usertype = OTHER_USER;
                        } else {
                            customer_id = 0;
                            driver_id = 0;
                            manager_id = 0;
                            usertype = OTHER_USER;
                            new AlertDialog.Builder(LoginActivity.this)
                                .setTitle("エラー")
                                .setMessage("ログインに失敗しました")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        mPasswordView.setText("");
                                    }
                                }).show();
                        }
                        transitionActivity();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            showProgress(true);
            postAsync.execute(URL_ORIGIN + "Login", body.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // ログイン成功時にホーム画面へ遷移する
    public void transitionActivity() {
        // ログイン情報を端末に保存
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("account", loginEmail);
        editor.putString("pass", loginPassword);
        editor.apply();

        if (this.customer_id != 0) {
            // 消費者側
            Intent intent = new Intent(getApplication(), CustomerHomeActivity.class);
            intent.putExtra("customer_id", this.customer_id);
            intent.putExtra("password", loginPassword);
            startActivity(intent);
        } else if (this.driver_id != 0) {
            // 配達員側
            Intent intent = new Intent(getApplication(), CourierHomeActivity.class);
            intent.putExtra("driver_id", this.driver_id);
            intent.putExtra("password", loginPassword);
            startActivity(intent);
        } else if (this.manager_id != 0) {
            Toast toast = Toast.makeText(LoginActivity.this, "管理者ユーザーです。", Toast.LENGTH_SHORT);
            toast.show();
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
    }
}

