package com.example.onosystems;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

//import java.sql.Time;

public class CourierDeliveryDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.courier_delivery_detail);

//        Intent intent = getIntent();
//        //MainActivityから値を受け取る,初期値を設定
//        HashMap<String, String> status = (HashMap<String, String>) intent.getSerializableExtra("itemInfo");
//        String name = status.get("name");
//
//        // TextView のインスタンスを作成
//        TextView customer_name = findViewById(R.id.name);
//
//        // テキストビューのテキストを設定
//        customer_name.setText(String.valueOf(name));
//
//        //表示
//        //setContentView(textView);

        Button time_change_Button = findViewById(R.id.rescheduling_Button);
        time_change_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), CourierTimeChange.class);
                //日時変更画面に遷移
                startActivity(intent);
            }
        });

        Button delivery_complete_Button = findViewById(R.id.delivery_complete_Button);
        delivery_complete_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), CourierHomeActivity.class);
                //ホーム画面に遷移
                startActivity(intent);

            }
        });

    }

}










