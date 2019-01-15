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
                //Intent newAccountButton = new Intent(getApplication(), .class);
                //startActivity(newAccountButton);

                name = editName.getText().toString();
                mail = editMail.getText().toString();
                password1 = editPassword1.getText().toString();
                password2 = editPassword2.getText().toString();
                tel = Long.parseLong(editTel.getText().toString());
                address = editAddress.getText().toString();

                if (password1.equals(password2)) {
                    createNewAccount(name, mail, tel, address, password1);

                } else {
                    AlertDialog.Builder builderPassword;
                    builderPassword = new AlertDialog.Builder(getApplicationContext());
                    builderPassword.setMessage("Password is difference").
                            setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // OK Button click password null
//                                    editPassword.setText("");
//                                    editPassword2.setText("");
                                    }
                                });
                    builderPassword.show();
                }


//                createNewAccount(name, mail, tel, address, password1);

                Toast toast = Toast.makeText(NewAccountActivity.this, "test", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    // 新規アカウントの作成
    public void createNewAccount(String name, String mail, long tel, String address, String password) {
        try {
            JSONObject json = new JSONObject();
            json.put("name", name);
            json.put("tel", tel);
            json.put("password", password);

            JSONObject jsonObject = Request.sendRequest(null, json);
            String result = jsonObject.getString("result");

            if (result.equals("ok")) {
//                Intent newAccountIntent = new Intent(getApplication(), LoginActivity.class);
//                newAccountIntent.putExtra()
//                startActivity(newAccountIntent);
                // ログイン画面に戻る
//                finish();
            } else {
                Toast toast = Toast.makeText(NewAccountActivity.this, "error", Toast.LENGTH_SHORT);
                toast.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
