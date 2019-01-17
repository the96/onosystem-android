package com.example.onosystems;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class NewAccountActivity extends AppCompatActivity {

    EditText editName, editPassword1, editPassword2, editMail, editAddress, editTel;
    String name, mail, password1, password2, address;
    long tel;
    private String url = "http://54.92.85.232/aws/RegisterAccount";
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        editName = findViewById(R.id.name);
        editMail = findViewById(R.id.mail);
        editPassword1 = findViewById(R.id.password1);
        editPassword2 = findViewById(R.id.password2);
        editTel = findViewById(R.id.tel);
        editAddress = findViewById(R.id.address);



        Button createAccountButton2 = findViewById(R.id.createAccountButton2);
        createAccountButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name = editName.getText().toString();
                mail = editMail.getText().toString();
                password1 = editPassword1.getText().toString();
                password2 = editPassword2.getText().toString();
                String phone = editTel.getText().toString();
                address = editAddress.getText().toString();

                if (isEmpty(name) && isEmpty(mail) && isEmpty(password1) && isEmpty(password2)
                        && isEmpty(phone) && isEmpty(address)) {

                    if (isTelValid(phone)) {
                        tel = Long.parseLong(phone);

                        if (password1.equals(password2)) {
                            createNewAccount(name, mail, tel, address, password1);

                        } else {
                            alertDialog = new AlertDialog.Builder(NewAccountActivity.this)
                                    .setMessage("パスワードが一致しません")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    }).show();
                        }
                    } else {
                        alertDialog = new AlertDialog.Builder(NewAccountActivity.this)
                                .setMessage("tel length = 11")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                }).show();
                    }
                } else {
                    alertDialog = new AlertDialog.Builder(NewAccountActivity.this)
                            .setMessage("input all form")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }).show();
                }

            }
        });
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

    private boolean isTelValid(String phoneLen) {
        int lengthTel = 11;
        return phoneLen.length() == lengthTel;
    }

    // 新規アカウントの作成
    public void createNewAccount(String name, String mail, long tel, String address, String password) {
        try {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("tel", tel);
        json.put("password", password);

        SampleLogin sampleLogin = new SampleLogin();
        String send = json.toString();
        sampleLogin.execute(url, send);

//            String result = jsonObject.getString("result");
//
//            if (result.equals("ok")) {
////                Intent newAccountIntent = new Intent(getApplication(), LoginActivity.class);
////                newAccountIntent.putExtra()
////                startActivity(newAccountIntent);
//                // ログイン画面に戻る
                finish();
//            } else {
//                Toast toast = Toast.makeText(NewAccountActivity.this, "error", Toast.LENGTH_SHORT);
//                toast.show();
//            }
    } catch (JSONException e) {
        e.printStackTrace();
    }
}

}
