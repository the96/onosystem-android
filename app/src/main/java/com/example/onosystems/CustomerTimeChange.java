package com.example.onosystems;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class CustomerTimeChange extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_time_change);

        Button time_change_Button = findViewById(R.id.change_complete_button);
        time_change_Button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplication(), CourierHomeActivity.class);
            startActivity(intent);
            //ホーム画面に遷移
        }
    });

//// idがdialogButtonのButtonを取得
//        Button dialogButton =  findViewById(R.id.dialog_button1);
//        // clickイベント追加
//        dialogButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            // クリックしたらダイアログを表示する処理
//            public void onClick(View v) {
//                // ダイアログクラスをインスタンス化
//                DialogFlagment dialog = new DialogFlagment();
//                // 表示  getFragmentManager()は固定、sampleは識別タグ
//                dialog.show(getSupportFragmentManager(), "sample");
//            }
//        });
//
//    }
//    // ダイアログで入力した値をtextViewに入れる - ダイアログから呼び出される
//    public void setTextView(String value){
//        TextView textView =  findViewById(R.id.dialog_button1);
//        textView.setText(value);
    }


}
