package com.example.onosystems;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class CourierTimeChange extends AppCompatActivity {
    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日"); //日付フォーマット



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_time_change);

        Intent intent = getIntent();


        //MainActivityから値を受け取る,初期値を設定
        HashMap<String, String> status = (HashMap<String, String>) intent.getSerializableExtra("itemInfo");
        String name = status.get("name");
        String slip_number = status.get("slipNumber");
        String address = status.get("address");
        int unixtime = Integer.valueOf(status.get("unixTime"));
        Date date = new Date(unixtime * 1000L);

        String time = sdf.format(date);

        // TextView のインスタンスを作成
        TextView Customer_name = findViewById(R.id.name1);
        TextView Slip_number = findViewById(R.id.slip_number1);
        TextView Address = findViewById(R.id.address1);
        TextView Time = findViewById(R.id.dialog_button1);

        // テキストビューのテキストを設定
        Customer_name.setText(name);
        Slip_number.setText(slip_number);
        Address.setText(address);
        Time.setText(time);
        //表示
        //setContentView(textView);

        intent.putExtra("itemInfo",name);

        Spinner spinner = findViewById(R.id.spinner);

        // ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item,getResources().getStringArray(R.array.time_list));

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        // spinner に adapter をセット
        spinner.setAdapter(adapter);

        // スピナーのアイテムが選択された時の動作を設定
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                //スピナー内のアイテムが選択された場合の処理をここに記載
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                //スピナーでは呼ばれない模様。ただし消せないので「おまじない」として残す。
            }
        });




        Button time_change_Button = findViewById(R.id.change_complete_button);
        time_change_Button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplication(), CourierHomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            //ホーム画面に遷移
        }
    });

        // idがdialogButtonのButtonを取得
        Button dialogButton =  findViewById(R.id.dialog_button1);
        // clickイベント追加
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            // クリックしたらダイアログを表示する処理
            public void onClick(View v) {
                // ダイアログクラスをインスタンス化
                DialogFlagment dialog = new DialogFlagment();
                // 表示  getFragmentManager()は固定、sampleは識別タグ
                dialog.show(getSupportFragmentManager(), "sample");
            }
        });

    }
    // ダイアログで入力した値をtextViewに入れる - ダイアログから呼び出される
    public void setTextView(String value){
        TextView textView = findViewById(R.id.dialog_button1);
        textView.setText(value);
    }


}
