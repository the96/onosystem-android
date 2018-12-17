package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class SubActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        //MainActivityから値を受け取る
        int position = intent.getIntExtra("DATA", 0);

        // TextView のインスタンスを作成
        TextView textView = new TextView(getApplicationContext());
        //TextView textView = (TextView) findViewById(R.id.subtext);

        // テキストビューのテキストを設定
        textView.setText(String.valueOf(position));

        //画面に表示
        setContentView(textView);
    }
}