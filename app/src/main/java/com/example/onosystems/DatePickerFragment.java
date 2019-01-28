package com.example.onosystems;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.telecom.Call;
import android.widget.DatePicker;

import java.io.Serializable;
import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    interface Callback {
        void setDate(int y, int m, int d);
    }
    private int y, m, d;
    private Callback callback;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        y = args.getInt("time_y");
        m = args.getInt("time_m");
        d = args.getInt("time_d");
        Context context = getActivity();
        if (!(context instanceof Callback)) {
            throw new ClassCastException("context が DatePickerFragment.Callbackを実装していません");
        }
        this.callback = (Callback) context;
        return new DatePickerDialog(getActivity(), this, y, m - 1, d);
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        callback.setDate(year, month, dayOfMonth);
        String dateStr = year + "年" + (month + 1) + "月" + dayOfMonth + "日";
        Activity activity = getActivity();
        if (activity instanceof CourierTimeChange) {
            ((CourierTimeChange)getActivity()).setTextView(dateStr);
        } else if(activity instanceof CustomerTimeChange) {
            ((CustomerTimeChange)getActivity()).setTextView(dateStr);

        }


    }
}
