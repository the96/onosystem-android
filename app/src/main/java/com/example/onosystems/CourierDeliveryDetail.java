package com.example.onosystems;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

//import java.sql.Time;

public class CourierDeliveryDetail extends AppCompatActivity {
    public HashMap<String, String> status;
    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日"); //日付フォーマット
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.courier_delivery_detail);

        Intent intent = getIntent();
        //MainActivityから値を受け取る,初期値を設定
        status = (HashMap<String, String>) intent.getSerializableExtra("itemInfo");
        String name = status.get("name");
        String slip_number = status.get("slipNumber");
        String address = status.get("address");
        int unixtime = Integer.valueOf(status.get("unixTime"));
        Date date = new Date(unixtime * 1000L);

        String time = sdf.format(date);

        // TextView のインスタンスを作成
        TextView Customer_name = findViewById(R.id.name);
        TextView Slip_number = findViewById(R.id.slip_number);
        TextView Address = findViewById(R.id.address);
        TextView Time = findViewById(R.id.delivery_date);

        // テキストビューのテキストを設定
        Customer_name.setText(name);
        Slip_number.setText(slip_number);
        Address.setText(address);
        Time.setText(time);
        //表示
        //setContentView(textView);




        Button time_change_Button = findViewById(R.id.rescheduling_Button);
        time_change_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), CourierTimeChange.class);
                //日時変更画面に遷移
                intent.putExtra("itemInfo", status);
                startActivity(intent);
            }
        });

        Button delivery_complete_Button = findViewById(R.id.delivery_complete_Button);
        delivery_complete_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), CourierHomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);

            }
        });

    }

}










