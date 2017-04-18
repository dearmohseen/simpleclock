package com.mkhan.myapplication;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by mkhan on 3/31/2017.
 */


 class ClockUtility {

    private static String DATE_FORMAT = "MMM d, yyyy";
    private static String DAY_FORMAT = "EEEE";

    public static String getTodaysDate(String format){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df;
        if("day".equalsIgnoreCase(format)){
            df = new SimpleDateFormat(DAY_FORMAT,Locale.ENGLISH);
        } else {
            df = new SimpleDateFormat(DATE_FORMAT,Locale.ENGLISH);
        }

        String formattedDate = df.format(c.getTime());

        return formattedDate;
    }

    public static Intent openCalendarApp(){
        Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
        builder.appendPath("time");
        ContentUris.appendId(builder, new Date().getTime());
        Intent intent = new Intent(Intent.ACTION_VIEW)
                .setData(builder.build());
        return  intent;
    }


}