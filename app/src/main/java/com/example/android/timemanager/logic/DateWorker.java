package com.example.android.timemanager.logic;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public final class DateWorker {

    public static String getDateAsString(Calendar date){
        Locale myLocale = Locale.getDefault();
        SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("dd.MM.yyyy", myLocale);
        return simpleDateFormat.format(date.getTime());
    }

    public static String getTimeAsString(Calendar date){
        Locale myLocale = Locale.getDefault();
        SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("H:mm", myLocale);
        return simpleDateFormat.format(date.getTime());
    }
}