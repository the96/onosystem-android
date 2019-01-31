package com.example.onosystems;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CustomerHomeActivity extends HomeActivity implements View.OnFocusChangeListener {
    public EditText profileAddress;
    int id;

    @Override
    public void setUserOptions() {
        toolBarLayout = R.menu.tool_options_customer;
        detailActivity = CustomerDeliveryDetail.class;
        drawerLayout = R.id.customer_layout;
        homeLayout = R.layout.customer_home_layout;

        Intent i = getIntent();
        id = i.getIntExtra("customer_id", -1);
        try {
            JSONObject json = new JSONObject();
            json.put("customer_id", id);
            String userId = json.toString();
            User.setUserId(userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String password = i.getStringExtra("password");
        User.setPassword(password);
        User.setUrl(PostURL.getTopCustomerURL());
        User.setProfileURL(PostURL.getInformationCustomerURL());
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
                                                  deliveryData.getString("ship_to"),
                                                  deliveryData.getString("ship_from"),
                                                  deliveryData.getInt("time"),
                                                  deliveryData.getInt("delivery_time"),
                                                  deliveryData.getInt("delivered_status"),
                                                  deliveryData.getInt("receivable_status"),
                                                  i, // item_number
                                                  deliveryData.getInt("delivered_status")==0?Delivery.VISIBLE:Delivery.NOT_VISIBLE,
                                                  Delivery.READ_FLAG,
                                                  deliveryData.getBoolean("driver_updated"),
                                                  geocoder));
                    deliveryCheck.put(deliveryData.getLong("slip_number"), true);
                } else if (deliveryData.getBoolean("driver_updated")) {
                    for (int j = 0; j < jsonArray.length(); j++) {
                        if (deliveryData.getLong("slip_number") == deliveryInfo.get(j).slipNumber) {
                            deliveryInfo.set(j, new Delivery(deliveryData.getString("name"),
                                                             deliveryData.getLong("slip_number"),
                                                             deliveryData.getString("address"),
                                                             deliveryData.getString("ship_to"),
                                                             deliveryData.getString("ship_from"),
                                                             deliveryData.getInt("time"),
                                                             deliveryData.getInt("delivery_time"),
                                                             deliveryData.getInt("delivered_status"),
                                                             deliveryData.getInt("receivable_status"),
                                                             i, // item_number
                                                             deliveryData.getInt("delivered_status")==0?Delivery.VISIBLE:Delivery.NOT_VISIBLE,
                                                             Delivery.READ_FLAG,
                                                             deliveryData.getBoolean("driver_updated"),
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
            profileInfo = new Customer(profileData.getString("name"),
                    profileData.getString("mail"),
                    profileData.getLong("tel"),
                    profileData.getString("address"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setProfile() {
        profileName = findViewById(R.id.edit_name);
        profileMail = findViewById(R.id.edit_mail);
        profileTel = findViewById(R.id.edit_tel);
        profileAddress = findViewById(R.id.edit_address);
        profilePassword = findViewById(R.id.password);
        profileRePassword = findViewById(R.id.edit_rePassword);

        profileName.setText(((Customer) profileInfo).getName(), TextView.BufferType.NORMAL);
        profileMail.setText(((Customer) profileInfo).getMail(), TextView.BufferType.NORMAL);
        profileTel.setText(String.valueOf(((Customer) profileInfo).getTel()), TextView.BufferType.NORMAL);
        profileAddress.setText(((Customer) profileInfo).getAddress(), TextView.BufferType.NORMAL);
        profilePassword.setText(User.getPassword(), TextView.BufferType.NORMAL);
        profileRePassword.setText("");

        profileName.setOnFocusChangeListener(this);
        profileMail.setOnFocusChangeListener(this);
        profileTel.setOnFocusChangeListener(this);
        profileAddress.setOnFocusChangeListener(this);
        profileRePassword.setOnFocusChangeListener(this);

        Button editButton = findViewById(R.id.edit_profile_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                alertDialog = new AlertDialog.Builder(CustomerHomeActivity.this)
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
        String newProfileAddress = profileAddress.getText().toString();
        String newProfilePassword = profilePassword.getText().toString();
        String newProfileRePassword = profileRePassword.getText().toString();

        if(newProfilePassword.equals(newProfileRePassword)) {
            //更新する
            try {
                JSONObject json = new JSONObject();
                json.put("customer_id", id);
                json.put("name", newProfileName);
                json.put("address", newProfileAddress);
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

}

class Customer extends User {
    String address;

    public String getAddress() { return address; }

    public Customer(String name, String mail, long tel, String address) {
        super.name = name;
        super.mail = mail;
        super.tel = tel;
        this.address = address;
    }

}