package com.example.onosystems;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class CourierHomeActivity extends HomeActivity {
    public Object profileInfo;
    public EditText profileName, profileMail, profileTel, profileStoreCode, profilePassword, profileRePassword;

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
            case R.id.sortTime:
                sortTime();
                break;
            case R.id.sortDistance:
                sortDistance();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sortTime() {
        //BubbleSort
        for (int i = 0; i < deliveryInfo.size()-1; i++) {
            for (int j = 1; j < deliveryInfo.size(); j++) {
                if (deliveryInfo.get(i).getTime() > deliveryInfo.get(j).getTime()) {
                    Delivery tmp = deliveryInfo.get(i);
                    deliveryInfo.set(i, deliveryInfo.get(j));
                    deliveryInfo.set(j, tmp);
                }
            }
        }

        reloadDeliveries();
    }

    public void sortDistance() {

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
            JSONObject profileData = new JSONObject("{\"name\":\"001\", \"mail\":\"1546239600\", \"tel\":\"1000000001\", \"store_code\":\"1001\"}");

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

        Button editButton = (Button)findViewById(R.id.edit_profile_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                updateProfile();
                drawer.closeDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public void updateProfile() {
        //変更されたprofileデータを渡す
        SpannableStringBuilder newProfileName = (SpannableStringBuilder)profileName.getText();
        SpannableStringBuilder newProfileMail = (SpannableStringBuilder)profileMail.getText();
        SpannableStringBuilder newProfileTel = (SpannableStringBuilder)profileTel.getText();
        SpannableStringBuilder newProfileStoreCode = (SpannableStringBuilder)profileStoreCode.getText();
        SpannableStringBuilder newProfilePassword = (SpannableStringBuilder)profilePassword.getText();
        SpannableStringBuilder newProfileRePassword = (SpannableStringBuilder)profileName.getText();
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
