//package com.example.onosystems;
//
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.HashMap;
//
////import java.sql.Time;
//
//public class CustomerDeliveryDetail extends AppCompatActivity {
//    public HashMap<String, String> status;
//    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日"); //日付フォーマット
//    AlertDialog mAlertDlg;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.courier_delivery_detail);
//
//        // 1. AlertDialog.Builder クラスのインスタンスを生成
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//        // 2. ダイアログタイトル、表示メッセージ、ボタンを設定
//        builder.setTitle(R.string.dlg_title);
//        builder.setMessage(R.string.dlg_msg1);
//        builder.setPositiveButton("完了", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                // OK ボタンクリック処理
//                Toast.makeText(CustomerDeliveryDetail.this,
//                        "配達完了しました", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(getApplication(), CourierHomeActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                startActivity(intent);
//            }
//        });
//
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                // Cancel ボタンクリック処理
//
//            }
//        });
//
//        // 3. ダイアログを生成
//        mAlertDlg = builder.create();
//
//        // 4. ボタンクリック時にダイアログを表示
//        Button btnExe = findViewById(R.id.delivery_complete_Button);
//        btnExe.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                // ダイアログ表示
//                mAlertDlg.show();
//            }
//        });
//
//
//
//
//        Intent intent = getIntent();
//        //MainActivityから値を受け取る,初期値を設定
//        status = (HashMap<String, String>) intent.getSerializableExtra("itemInfo");
//        String name = status.get("name");
//        String slip_number = status.get("slipNumber");
//        String address = status.get("address");
//        int unixtime = Integer.valueOf(status.get("unixTime"));
//        Date date = new Date(unixtime * 1000L);
//
//        String time = sdf.format(date);
//
//        // TextView のインスタンスを作成
//        TextView Customer_name = findViewById(R.id.name);
//        TextView Slip_number = findViewById(R.id.slip_number);
//        TextView Address = findViewById(R.id.address);
//        TextView Time = findViewById(R.id.delivery_date);
//
//        // テキストビューのテキストを設定
//        Customer_name.setText(name);
//        Slip_number.setText(slip_number);
//        Address.setText(address);
//        Time.setText(time);
//        //表示
//        //setContentView(textView);
//
//
//
//
//        Button time_change_Button = findViewById(R.id.rescheduling_Button);
//        time_change_Button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplication(), CourierTimeChange.class);
//                //日時変更画面に遷移
//
//                intent.putExtra("itemInfo", status);
//                startActivity(intent);
//            }
//        });
//
//
//    }
//
//}

package com.example.onosystems;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

//import java.sql.Time;

public class CustomerDeliveryDetail extends AppCompatActivity {
    public ArrayList<HashMap<String, String>> status;
    int index;
    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日"); //日付フォーマット
    public int UNRECEIVABLE = 1;
    public int RECEIVABLE = 2;
    private String slip_number;
    AlertDialog mAlertDlg1;
    AlertDialog mAlertDlg2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_delivery_detail);

        // 1. AlertDialog.Builder クラスのインスタンスを生成
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);


        // 2. ダイアログタイトル、表示メッセージ、ボタンを設定
        builder1.setTitle(R.string.dlg_title);
        builder1.setMessage(R.string.dlg_msg3);
        builder1.setPositiveButton("受領不可", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                //サーバにデータ送信
                PostAsync postAsync = new PostAsync();
                postAsync.setRef(new PostAsync.Callback() {
                    @Override
                    public void callback(String result) {
                        // 処理内容を書く
                        System.out.println("result:\r\n" + result);
                    }
                });
                // 伝票(slip_number), receivable_status
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("slip_number", Long.parseLong(slip_number));
                    jsonObject.put("receivable_status", UNRECEIVABLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                postAsync.execute(PostURL.getReceiveCustomerURL(), jsonObject.toString());
                System.out.println(PostURL.getReceiveCustomerURL());


                // OK ボタンクリック処理
                Toast.makeText(CustomerDeliveryDetail.this,
                        "受領不可にしました", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Cancel ボタンクリック処理

            }
        });

        // 3. ダイアログを生成
        mAlertDlg1 = builder1.create();

        // 4. ボタンクリック時にダイアログを表示
        Button btnExe = findViewById(R.id.not_receive_Button);
        btnExe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // ダイアログ表示
                mAlertDlg1.show();
            }
        });



        //ここから受領可ボタンを押したときの動作


        // 1. AlertDialog.Builder クラスのインスタンスを生成
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);

        // 2. ダイアログタイトル、表示メッセージ、ボタンを設定
        builder2.setTitle(R.string.dlg_title);
        builder2.setMessage(R.string.dlg_msg4);
        builder2.setPositiveButton("受領可", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                //サーバにデータ送信
                PostAsync postAsync = new PostAsync();
                postAsync.setRef(new PostAsync.Callback() {
                    @Override
                    public void callback(String result) {
                        // 処理内容を書く
                        System.out.println("result:\r\n" + result);
                    }
                });
                // 伝票(slip_number), receivable_status
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("slip_number", Long.parseLong(slip_number));
                    jsonObject.put("receivable_status", RECEIVABLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                postAsync.execute(PostURL.getReceiveCustomerURL(), jsonObject.toString());
                System.out.println(PostURL.getReceiveCustomerURL());


                // OK ボタンクリック処理
                Toast.makeText(CustomerDeliveryDetail.this,
                        "受領可にしました", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Cancel ボタンクリック処理

            }
        });

        // 3. ダイアログを生成
        mAlertDlg2 = builder2.create();

        // 4. ボタンクリック時にダイアログを表示
        Button btnExe1 = findViewById(R.id.receive_Button);
        btnExe1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // ダイアログ表示
                mAlertDlg2.show();
            }
        });




        Intent intent = getIntent();
        //MainActivityから値を受け取る,初期値を設定
        status = (ArrayList<HashMap<String, String>>) intent.getSerializableExtra("deliveryInfo");
        index = intent.getIntExtra("itemNumber", -1);
        HashMap<String, String> deliveryData = status.get(index);
        String name = deliveryData.get("name");
        slip_number = deliveryData.get("slipNumber");
        String address = deliveryData.get("address");
        int unixtime = Integer.valueOf(deliveryData.get("unixTime"));
        int deliveryTime = Integer.valueOf(deliveryData.get("deliveryTime"));
        Date date = new Date(unixtime * 1000L);
        String time = sdf.format(date);


        // TextView のインスタンスを作成
        TextView Customer_name = findViewById(R.id.name);
        TextView Slip_number = findViewById(R.id.slip_number);
        TextView Address = findViewById(R.id.address);
        TextView Time = findViewById(R.id.delivery_date);
        TextView delivery_time = findViewById(R.id.delivery_time);
        String[] time_id = getResources().getStringArray(R.array.time_list);
        // テキストビューのテキストを設定
        Customer_name.setText(name);
        Slip_number.setText(slip_number);
        Address.setText(address);
        Time.setText(time);
        delivery_time.setText(time_id[deliveryTime]);

        //time_id.setText(time_id(delivery_time));


        //表示
        //setContentView(textView);




        Button time_change_Button = findViewById(R.id.rescheduling_Button1);
        time_change_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), CustomerTimeChange.class);
                //日時変更画面に遷移
                intent.putExtra("deliveryInfo", status);
                intent.putExtra("itemNumber", index);
                startActivity(intent);
            }

        });

    }

}










