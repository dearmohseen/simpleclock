package com.mkhan.myapplication;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.InterstitialAd;

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

    public static final String START = "Start";
    public static final String PAUSE = "Pause";
    public static final String RESUME = "Resume";
    public static Ringtone ringtone;

    public static String getTodaysDate(String format) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df;
        if ("day".equalsIgnoreCase(format)) {
            df = new SimpleDateFormat(DAY_FORMAT, Locale.ENGLISH);
        } else {
            df = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        }

        String formattedDate = df.format(c.getTime());

        return formattedDate;
    }

    public static Intent openCalendarApp() {
        Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
        builder.appendPath("time");
        ContentUris.appendId(builder, new Date().getTime());
        Intent intent = new Intent(Intent.ACTION_VIEW)
                .setData(builder.build());
        return intent;
    }

    public static Button createBackToMainClockButton(int btnId, final Activity activityClass) {
        Button btnBackToMainClock = (Button) activityClass.findViewById(btnId);
        btnBackToMainClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(activityClass.getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                activityClass.startActivity(intent);
            }
        });

        return btnBackToMainClock;
    }

    public static void displayToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}

