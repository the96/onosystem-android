package com.example.onosystems;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.LocationResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CourierHomeActivity extends HomeActivity implements View.OnFocusChangeListener, LocationUpdater.LocationResultListener {
    // NOTICE_THRESHOLD回位置情報取得で連続して基準距離を下回ったとき通知する
    private static final int NOTICE_THRESHOLD = 6;
    private static final int DISTANCE_THRESHOLD = 1500;
    LocationUpdater locationUpdater;
    Location location;
    HashMap<Long, Integer> noticedMap;
    public static final boolean LOCATION_DEBUG_MODE = false;
    int id;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        locationUpdater = new LocationUpdater(this, this);
        location = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        locationUpdater.run();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationUpdater.stopUpdateLocation();
    }

    @Override
    public void setUserOptions() {
        toolBarLayout = R.menu.tool_options_courier;
        detailActivity = CourierDeliveryDetail.class;
        drawerLayout = R.id.courier_layout;
        homeLayout = R.layout.courier_home_layout;

        Intent i = getIntent();
        id = i.getIntExtra("driver_id", -1);
        try {
            JSONObject json = new JSONObject();
            json.put("driver_id", id);
            String userId = json.toString();
            User.setUserId(userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String password = i.getStringExtra("password");
        User.setPassword(password);
        User.setUrl(PostURL.getTopCourierURL());
        User.setProfileURL(PostURL.getInformationCourierURL());
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
        Intent intent = new Intent(this, CourierMapActivity.class);  // 遷移先指定
        intent.putExtra("deliveryInfo", list);;
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
                                                  deliveryData.getBoolean("customer_updated"),
                                                  geocoder));
                    deliveryCheck.put(deliveryData.getLong("slip_number"), true);
                } else if (deliveryData.getBoolean("customer_updated")) {
                    for (int j = 0; j < jsonArray.length(); j++) {
                        if (deliveryData.getLong("slip_number") == deliveryInfo.get(j).slipNumber) {
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
                                                            deliveryData.getBoolean("customer_updated"),
                                                            geocoder));
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
                json.put("driver_id", id);
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
                postAsync.execute(PostURL.getSettingCourierURL(), newJson);

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

    @Override
    public void locationResult(LocationResult location) {
        this.location = location.getLastLocation();
        approachNotice(this.location);
    }

    private void approachNotice(Location location) {
        JSONArray slipNumbers = new JSONArray();
        if (noticedMap == null) {
            noticedMap = new HashMap<>();
        }
        for (Delivery delivery: deliveryInfo) {
            // DISTANCE_THRESHOLD m 以内にNOTICE_THRESHOLD回連続で接近した場合通知を送る
            if (calcDistance(location, delivery) <= DISTANCE_THRESHOLD) {
                Integer count = noticedMap.get(delivery.slipNumber);
                if (count == null) {
                    count = 0;
                }
                if (count == NOTICE_THRESHOLD) {
                    slipNumbers.put(delivery.slipNumber);
                }
                noticedMap.put(delivery.slipNumber, count + 1);
            } else {
                noticedMap.put(delivery.slipNumber, 0);
            }
        }
        if (slipNumbers.length() <= 0) return;
        try {
            JSONObject json = new JSONObject().putOpt("slip_number", slipNumbers);
            PostAsync post = new PostAsync();
            post.setRef(new PostAsync.Callback() {
                @Override
                public void callback(String result) {
                    try {
                        JSONObject res = new JSONObject(result);
                        String str = res.getString("result");
                        if (str == null || str.isEmpty() || !"ok".equals(str)) {
                            System.out.println("approachNotice() is failed");
                        }
                        System.out.println(result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
            post.execute(PostURL.getNotificationURL(), json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private float calcDistance(Location location, Delivery delivery) {
        float[] results = new float[3];
        Location.distanceBetween(location.getLatitude(),location.getLongitude(),
                delivery.getLatitude(), delivery.getLongitude(), results);
        return results[0];
    }
}

class Courier extends User{
    public Courier(String name, String mail, long tel) {
        super.name = name;
        super.mail = mail;
        super.tel = tel;
    }
}