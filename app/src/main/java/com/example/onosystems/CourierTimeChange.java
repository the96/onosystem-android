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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class CourierTimeChange extends AppCompatActivity implements TimeChangeAPI.Callback, DatePickerFragment.Callback {
    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日"); //日付フォーマット
    public SimpleDateFormat sdfy = new SimpleDateFormat("yyyy"); //日付フォーマット
    public SimpleDateFormat sdfm = new SimpleDateFormat("MM"); //日付フォーマット
    public SimpleDateFormat sdfd = new SimpleDateFormat("dd"); //日付フォーマット
    public ArrayList<HashMap<String, String>> status;
    HashMap<String, String> item;
    public int hourOfDay = 0;
    Calendar cal = Calendar.getInstance();
    int index;
    AlertDialog mAlertDlg;
    Date date;
    int year, month, day, count;
    Long timeOfMillis;
    String slip_number;
    String delivery_time;
    public int deliveryTime;
    //private int index = 0;//0:時間指定なし、1:9-12、2:12-15、3:15-18、4:18-21
    private Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_time_change);

        Intent intent = getIntent();
        //MainActivityから値を受け取る,初期値を設定
        item = (HashMap<String, String>) intent.getSerializableExtra("item");
        String name = item.get("name");
        slip_number = item.get("slipNumber");
        delivery_time = item.get("deliveryTime");
        String address = item.get("address");
        int unixtime = Integer.valueOf(item.get("unixTime"));
        date = new Date(unixtime * 1000L);
        deliveryTime = Integer.valueOf(item.get("deliveryTime"));


        //ここでカレンダーの入力値を初期化している
        this.year = Integer.parseInt(sdfy.format(date));
        this.month = Integer.parseInt(sdfm.format(date));
        this.day = Integer.parseInt(sdfd.format(date));
        System.out.println(year + ","+month+","+day);



     //ここで変更決定時のダイアログを作成

        // 1. AlertDialog.Builder クラスのインスタンスを生成
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. ダイアログタイトル、表示メッセージ、ボタンを設定
        builder.setTitle(R.string.dlg_title);
        builder.setMessage(R.string.dlg_msg2);
        builder.setPositiveButton("変更", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // OK ボタンクリック処理
                if(deliveryTime == 1){
                    hourOfDay = 9;

                }else if(deliveryTime == 2){
                    hourOfDay = 12;

                }else if(deliveryTime == 3){
                    hourOfDay = 15;

                }else if(deliveryTime == 4){
                    hourOfDay = 18;

                }else{
                    hourOfDay = 0;
                }


                cal.set(year,month,day,hourOfDay,0);
                System.out.println(cal.getTime());
                timeOfMillis = ((cal.getTimeInMillis()) / 1000L);
                System.out.println(timeOfMillis);

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

        intent.putExtra("deliveryInfo",name);



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
                //スピナーのIDを更新
                deliveryTime = spinner.getSelectedItemPosition();
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
                args.putInt("time_year", year);
                args.putInt("time_month", month);
                args.putInt("time_dayOfMonth", day);
                args.putInt("time_count", count);

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
                    intent.putExtra("deliveryInfo", status);
                    intent.putExtra("itemNumber", index);
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
        String body = "{\"slip_number\": " + slip_number +" ,\"delivery_time\": " + deliveryTime +" ,\"time\": " + timeOfMillis + "}";
        System.out.println(timeOfMillis);
        System.out.println(deliveryTime);
        System.out.println(slip_number);
        api.execute(PostURL.getChangeTimeCourierURL(), body);
    }

    @Override
    public void callbackMethod(String json) {
        System.out.println("q");
    }

    @Override
    public void setDate(int y, int m, int d, int cnt) {
        year = y;
        month = m;
        day = d;
        count = cnt;

        System.out.println("callback");
        System.out.println(y + " " + m + " " + d);
    }
}
