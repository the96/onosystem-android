package com.example.onosystems;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class CourierTimeChange extends AppCompatActivity implements TimeChangeAPI.Callback, DatePickerFragment.Callback {
    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日"); //日付フォーマット
    public SimpleDateFormat sdfy = new SimpleDateFormat("yyyy"); //日付フォーマット
    public SimpleDateFormat sdfm = new SimpleDateFormat("MM"); //日付フォーマット
    public SimpleDateFormat sdfd = new SimpleDateFormat("dd"); //日付フォーマット
    AlertDialog mAlertDlg;
    Date date;
    int year, month, day;
    String timeOfMillis;
    String slip_number;
    String delivery_time;
    //private int index = 0;//0:時間指定なし、1:9-12、2:12-15、3:15-18、4:18-21
    public String url = "http://www.onosystems.work/aws/ChangeTimeCourier";
    private Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_time_change);


        Intent intent = getIntent();

     //ここで変更決定時のダイアログを作成

        // 1. AlertDialog.Builder クラスのインスタンスを生成
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. ダイアログタイトル、表示メッセージ、ボタンを設定
        builder.setTitle(R.string.dlg_title);
        builder.setMessage(R.string.dlg_msg2);
        builder.setPositiveButton("変更", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // OK ボタンクリック処理
                callTCAPI();
                Toast.makeText(CourierTimeChange.this,
                        "変更完了しました", Toast.LENGTH_SHORT).show();
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
        Button btnExe = findViewById(R.id.change_complete_button);
        btnExe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // ダイアログ表示
                mAlertDlg.show();

            }
        });




        //MainActivityから値を受け取る,初期値を設定
        final HashMap<String, String> status = (HashMap<String, String>) intent.getSerializableExtra("deliveryInfo");
        String name = status.get("name");
        slip_number = status.get("slipNumber");
        delivery_time = status.get("deliveryTime");
        String address = status.get("address");
        int unixtime = Integer.valueOf(status.get("unixTime"));
        date = new Date(unixtime * 1000L);
        int deliveryTime = Integer.valueOf(status.get("deliveryTime"));

        //ここでカレンダーの入力値を初期化している
        this.year = Integer.parseInt(sdfy.format(date));
        this.month = Integer.parseInt(sdfm.format(date));
        this.day = Integer.parseInt(sdfd.format(date));

        Calendar cal = Calendar.getInstance();
        cal.set(year,month,day);
        timeOfMillis = String.valueOf(cal.getTimeInMillis());



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



        //ここでプルダウンメニューの設定

        spinner = findViewById(R.id.spinner);


        // ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item,getResources().getStringArray(R.array.time_list));

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        // spinner に adapter をセット
        spinner.setAdapter(adapter);
        spinner.setSelection(deliveryTime);

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




        //ここで

        // idがdialogButtonのButtonを取得
        Button dialogButton =  findViewById(R.id.dialog_button1);

        // clickイベント追加
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            // クリックしたらダイアログを表示する処理
            public void onClick(View v) {



                // ダイアログクラスをインスタンス化
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                int time_y = Integer.parseInt(sdfy.format(date));
                int time_m = Integer.parseInt(sdfm.format(date));
                int time_d = Integer.parseInt(sdfd.format(date));

                Bundle args = new Bundle();
                args.putInt("time_y", time_y);
                args.putInt("time_m", time_m);
                args.putInt("time_d", time_d);
                datePickerFragment.setArguments(args);


                // 表示  getFragmentManager()は固定、sampleは識別タグ
                datePickerFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        //MainActivityから値を受け取る,初期値を設定
        //TODO: ここからマップけす


        Toolbar toolbar =  findViewById(R.id.time_change_toolbar); //R.id.toolbarは各自で設定したidを入れる
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
    // ダイアログで入力した値をtextViewに入れる - ダイアログから呼び出される
    public void setTextView(String value){
        TextView textView = findViewById(R.id.dialog_button1);
        textView.setText(value);
    }

    private void callTCAPI() {
        // ここでAPIを呼ぶ
        TimeChangeAPI api = new TimeChangeAPI();
        api.setReference(this);
        String body = "{\"slip_number\": " + slip_number +" ,\"delivery_time\": " + delivery_time +" ,\"time\": " + timeOfMillis + "}";
        System.out.println(timeOfMillis);
        System.out.println(delivery_time);
        System.out.println(slip_number);
        api.execute(url, body);

    }

    @Override
    public void callbackMethod(String json) {
        System.out.println("q");
    }

    @Override
    public void setDate(int y, int m, int d) {
        System.out.println("callback");
        System.out.println(y + " " + m + " " + d);
    }
}
