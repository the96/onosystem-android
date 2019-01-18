package com.example.onosystems;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

//import java.sql.Time;

public class DialogFlagment extends DialogFragment {

    // ダイアログが生成された時に呼ばれるメソッド ※必須
    public Dialog onCreateDialog(Bundle savedInstanceState){
        Bundle args = getArguments();
        int time_y = args.getInt("time_y");
        int time_m = args.getInt("time_m");
        int time_d = args.getInt("time_d");


        // 今日の日付のカレンダーインスタンスを取得
        final Calendar calendar = Calendar.getInstance();

        // ダイアログ生成  DatePickerDialogのBuilderクラスを指定してインスタンス化します
        DatePickerDialog dateBuilder = new DatePickerDialog(
                getActivity(),
                new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // 選択された年・月・日を整形 ※月は0-11なので+1している
                        String dateStr = year + "年" + (month + 1) + "月" + dayOfMonth + "日";

                        // MainActivityのインスタンスを取得
                        CourierTimeChange courierTimeChange = (CourierTimeChange) getActivity();
                        courierTimeChange.setTextView(dateStr);
                    }
                },
                //ここで初期値を入れる
                time_y,time_m-1,time_d

        );

        // dateBuilderを返す

        return dateBuilder;
    }
}
