package com.example.android.timemanager.logic;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.example.android.timemanager.AddItemActivity;

import java.util.Calendar;

/**
 * Created by Вита
 * Диалог для выбора даты
 */

public class DatePickerFragment extends DialogFragment {

    DatePickerDialog.OnDateSetListener activity;

    public DatePickerFragment(DatePickerDialog.OnDateSetListener activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = (Calendar) getArguments().getSerializable("datePicker");
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), activity, year, month, day);
        //dialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
        return dialog;
    }

    public static DatePickerFragment newInstance(Calendar calendar, DatePickerDialog.OnDateSetListener activity) {
        Bundle args = new Bundle();
        args.putSerializable("datePicker", calendar);
        DatePickerFragment fragment = new DatePickerFragment(activity);
        fragment.setArguments(args);
        return fragment;
    }
}
