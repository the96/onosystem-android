package com.example.onosystems;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CourierHomeActivity extends HomeActivity implements View.OnFocusChangeListener {
    @Override
    public void setUserOptions() {
        toolBarLayout = R.menu.tool_options_courier;
        detailActivity = CourierDeliveryDetail.class;
        drawerLayout = R.id.courier_layout;
        homeLayout = R.layout.courier_home_layout;

        Intent i = getIntent();
        int userId = i.getIntExtra("driver_id", 0);
        try {
            JSONObject json = new JSONObject();
            json.put("driver_id", userId);
            String id = json.toString();
            User.setUserId(id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String password = i.getStringExtra("password");
        User.setPassword(password);
        String url = "https://www.onosystems.work/aws/TopCourier";
        User.setUrl(url);
        String profileURL = "https://www.onosystems.work/aws/InformationCourier";
        User.setProfileURL(profileURL);
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
        Intent intent = new Intent(getApplication(), CourierMapActivity.class);  // 遷移先指定
        intent.putExtra("deliveryInfo", list);
        startActivity(intent);// CourierMapActivityに遷移
    }

    @Override
    public void parseDeliveries(String json) {

        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject deliveryData = jsonArray.getJSONObject(i);

                if (deliveryCheck.get(deliveryData.getLong("slip_number")) == null) {
                    deliveryInfo.add(new Delivery(deliveryData.getString("name"),
                            deliveryData.getLong("slip_number"),
                            deliveryData.getString("address"),
                            deliveryData.getString("ship_from"),
                            deliveryData.getInt("time"),
                            deliveryData.getInt("delivery_time"),
                            deliveryData.getInt("delivered_status"),
                            deliveryData.getInt("receivable_status"),
                            i, // item_number
                            Delivery.VISIBLE,
                            Delivery.READ_FLAG,
                            deliveryData.getBoolean("customer_updated")));
                    deliveryCheck.put(deliveryData.getLong("slip_number"), true);
                } else {
                    for (int j = 0; j < jsonArray.length(); j++) {
                        if (deliveryData.getBoolean("customer_updated") && deliveryData.getLong("slip_number") == deliveryInfo.get(j).slipNumber) {
                            deliveryInfo.set(j, new Delivery(deliveryData.getString("name"),
                                    deliveryData.getLong("slip_number"),
                                    deliveryData.getString("address"),
                                    deliveryData.getString("ship_from"),
                                    deliveryData.getInt("time"),
                                    deliveryData.getInt("delivery_time"),
                                    deliveryData.getInt("delivered_status"),
                                    deliveryData.getInt("receivable_status"),
                                    i, // item_number
                                    Delivery.VISIBLE,
                                    Delivery.READ_FLAG,
                                    deliveryData.getBoolean("customer_updated")));
                        }
                    }
                }
            }

            sortTime(); //時間順にソート
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void parseProfile(String json) {
        try {
            JSONObject profileData = new JSONObject(json);
            profileInfo = new Courier(profileData.getString("name"),
                    profileData.getString("mail"),
                    profileData.getLong("tel"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setProfile() {
        profileName = findViewById(R.id.edit_name);
        profileMail = findViewById(R.id.edit_mail);
        profileTel = findViewById(R.id.edit_tel);
        profilePassword = findViewById(R.id.password);
        profileRePassword = findViewById(R.id.edit_rePassword);

        profileName.setText(((Courier) profileInfo).getName(), TextView.BufferType.NORMAL);
        profileMail.setText(((Courier) profileInfo).getMail(), TextView.BufferType.NORMAL);
        profileTel.setText(String.valueOf(((Courier) profileInfo).getTel()), TextView.BufferType.NORMAL);
        profilePassword.setText(User.getPassword(), TextView.BufferType.NORMAL);
        profileRePassword.setText("");

        profileName.setOnFocusChangeListener(this);
        profileMail.setOnFocusChangeListener(this);
        profileTel.setOnFocusChangeListener(this);
        profileRePassword.setOnFocusChangeListener(this);

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
        String newProfilePassword = profilePassword.getText().toString();
        String newProfileRePassword = profileRePassword.getText().toString();

        if(newProfilePassword.equals(newProfileRePassword)) {
            //更新する
            try {
                JSONObject json = new JSONObject();
                json.put("driver_id", "1");
                json.put("name", newProfileName);
                json.put("mail", newProfileMail);
                json.put("tel", newProfileTel);
                json.put("password", newProfilePassword);

                String newJson = json.toString();

                PostAsync postAsync = new PostAsync();
                postAsync.setRef(new PostAsync.Callback() {
                    @Override
                    public void callback(String result) {
                        profUpdAlert(result);
                    }
                });
                postAsync.execute("http://54.92.85.232/aws/SettingCourier", newJson);

            } catch (JSONException e) {
                e.printStackTrace();
            }


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
    public Courier(String name, String mail, long tel) {
        super.name = name;
        super.mail = mail;
        super.tel = tel;
    }
}