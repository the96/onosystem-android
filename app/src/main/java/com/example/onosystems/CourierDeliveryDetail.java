package com.example.onosystems;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

//import java.sql.Time;

public class CourierDeliveryDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.courier_delivery_detail);

        Intent intent = getIntent();
        //MainActivityから値を受け取る,初期値を設定
        int position = intent.getIntExtra("DATA", 0);

        // TextView のインスタンスを作成

        TextView textView = findViewById(R.id.text_view);

        // テキストビューのテキストを設定
        textView.setText(String.valueOf(position));

        //表示
        //setContentView(textView);

        Button time_change_Button = findViewById(R.id.rescheduling_Button);
        time_change_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), TimeChange.class);
                //日時変更画面に遷移
                startActivity(intent);
            }
        });
    }
}