package com.example.onosystems;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;

public class CourierHomeActivity extends HomeActivity {
    public Object profileInfo;
    public EditText profileName, profileMail, profileTel, profileStoreCode, profilePassword, profileRePassword;

    AlertDialog alertDialog;



    @Override
    public void setUserOptions() {
        toolBarLayout = R.menu.tool_options_courier;
        detailActivity = CourierDeliveryDetail.class;
        drawerLayout = R.id.courier_layout;
        homeLayout = R.layout.courier_home_layout;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.mapView:
                showMapActivity();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showMapActivity() {
        //Intent intent = new Intent(getApplication(), CourierMapActivity.class);  // 遷移先指定
        //intent.putExtra("itemInfo", list.toString());
        //intent.putStringArrayListExtra("name", list);
        //startActivity(intent);// CourierMapActivityに遷移
    }

    public void getProfileCourier() {
        //本来はサーバからデータ受け取る
        try {
            JSONObject profileData = new JSONObject("{\"name\":\"driver\", \"mail\":\"driver@gmail.com\", \"tel\":\"1000000001\", \"store_code\":\"1001\"}");

            profileInfo = new Courier(profileData.getString("name"),
                                      profileData.getString("mail"),
                                      profileData.getLong("tel"),
                                      profileData.getInt("store_code"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setProfile() {
        getProfileCourier();

        profileName = findViewById(R.id.edit_name);
        profileMail = findViewById(R.id.edit_mail);
        profileTel = findViewById(R.id.edit_tel);
        profileStoreCode = findViewById(R.id.edit_store_code);
        profilePassword = findViewById(R.id.edit_password);
        profileRePassword = findViewById(R.id.edit_rePassword);

        profileName.setText(((Courier) profileInfo).getName(), TextView.BufferType.NORMAL);
        profileMail.setText(((Courier) profileInfo).getMail(), TextView.BufferType.NORMAL);
        profileTel.setText(String.valueOf(((Courier) profileInfo).getTel()), TextView.BufferType.NORMAL);
        profileStoreCode.setText(String.valueOf(((Courier) profileInfo).getStore_code()), TextView.BufferType.NORMAL);
        profilePassword.setText(((Courier) profileInfo).getPassword(), TextView.BufferType.NORMAL);
        profileRePassword.setText("");

        Button editButton = findViewById(R.id.edit_profile_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                alertDialog = new AlertDialog.Builder(CourierHomeActivity.this)
                        .setTitle("確認")
                        .setMessage("プロフィールの更新をしますか？")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                updateProfile(); //プロフィールを更新する
                                drawer.closeDrawer(GravityCompat.START);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).show();
            }
        });
    }

    @Override
    public void updateProfile() {
        //変更されたprofileデータを渡す
        String newProfileName = profileName.getText().toString();
        String newProfileMail = profileMail.getText().toString();
        long newProfileTel = Long.valueOf(profileTel.getText().toString());
        int newProfileStoreCode = Integer.valueOf(profileStoreCode.getText().toString());
        String newProfilePassword = profilePassword.getText().toString();
        String newProfileRePassword = profileRePassword.getText().toString();

        if(newProfilePassword.equals(newProfileRePassword)) {
            //更新する

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("パスワードが一致しません").setPositiveButton("やり直す", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    drawer.openDrawer(GravityCompat.START);
                }
            });
            builder.show();
        }
    }

}

class Courier extends User{
    int driver_id;
    int store_code;

    public int getDriver_id() { return driver_id; }

    public int getStore_code() { return store_code; }

    public Courier(String name, String mail, long tel, int store_code) {
        super.name = name;
        super.mail = mail;
        super.tel = tel;
        this.store_code = store_code;
    }
}