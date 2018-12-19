package com.example.test;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class TimeChange extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_time_change);

        Button time_change_Button = findViewById(R.id.change_complete_button);
        time_change_Button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplication(), HomeActivity.class);
            startActivity(intent);
            //ホーム画面に遷移
        }
    });



    }
}
