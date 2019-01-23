package com.example.onosystems;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

//import java.sql.Time;

public class CourierDeliveryDetail extends AppCompatActivity {
    public HashMap<String, String> status;
    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日"); //日付フォーマット
    public int toolBarLayout;
    AlertDialog mAlertDlg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.courier_delivery_detail);

        // 1. AlertDialog.Builder クラスのインスタンスを生成
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. ダイアログタイトル、表示メッセージ、ボタンを設定
        builder.setTitle(R.string.dlg_title);
        builder.setMessage(R.string.dlg_msg1);
        builder.setPositiveButton("完了", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // OK ボタンクリック処理
                Toast.makeText(CourierDeliveryDetail.this,
                        "配達完了しました", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Cancel ボタンクリック処理

            }
        });

        // 3. ダイアログを生成
        mAlertDlg = builder.create();

        // 4. ボタンクリック時にダイアログを表示
        Button btnExe = findViewById(R.id.delivery_complete_Button);
        btnExe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // ダイアログ表示
                mAlertDlg.show();
            }
        });




        Intent intent = getIntent();
        //MainActivityから値を受け取る,初期値を設定
        status = (HashMap<String, String>) intent.getSerializableExtra("itemInfo");
        String name = status.get("name");
        String slip_number = status.get("slipNumber");
        String address = status.get("address");
        int unixtime = Integer.valueOf(status.get("unixTime"));
        int deliveryTime = Integer.valueOf(status.get("deliveryTime"));
        Date date = new Date(unixtime * 1000L);
        String time = sdf.format(date);


        // TextView のインスタンスを作成
        TextView Customer_name = findViewById(R.id.name);
        TextView Slip_number = findViewById(R.id.slip_number);
        TextView Address = findViewById(R.id.address);
        TextView Time = findViewById(R.id.delivery_date);
        TextView delivery_time = findViewById(R.id.delivery_time);
        String[] time_id = getResources().getStringArray(R.array.time_list);
        System.out.println(time_id[deliveryTime]);
        // テキストビューのテキストを設定
        Customer_name.setText(name);
        Slip_number.setText(slip_number);
        Address.setText(address);
        Time.setText(time);
        delivery_time.setText(time_id[deliveryTime]);

        //time_id.setText(time_id(delivery_time));


        //表示
        //setContentView(textView);




        Button time_change_Button = findViewById(R.id.rescheduling_Button);
        time_change_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), CourierTimeChange.class);
                //日時変更画面に遷移
                ///////後で消す
//                SampleLogin loginTask = new SampleLogin();
//                String body = "{\n" +
//                        "  id: \"driver@gmail.com\",\n" +
//                        "  password: \"driver\"\n" +
//                        "}";
//                // 第一引数がURL、第二引数がPOSTするbody
//                loginTask.execute("http://www.onosystems.work/aws/CourierDeliveryDetail", body);
                ///////
                intent.putExtra("itemInfo", status);
                startActivity(intent);
            }
        });

        Toolbar toolbar =  findViewById(R.id.detail_toolbar); //R.id.toolbarは各自で設定したidを入れる
        toolbar.inflateMenu(R.menu.tool_options_detail);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.mapView) {
                    //Toast.makeText(CourierDeliveryDetail.this,"", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplication(), CourierMapActivity.class);
                    intent.putExtra("itemInfo", status);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

    }

}










