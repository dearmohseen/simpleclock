package com.mkhan.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity {

    private TextClock textClock;
    private TextClock textClockAM;
    private TextClock textClockSeconds;
    private TextView  textViewDate;
    private TextView  textViewDay;
    private ToggleButton toggleButtonSecond;
    private Configuration config;
    private int width , height;
    private String tag;
    IntentFilter batteryFilter;
    Intent batteryStatus;
    private TextView  batteryText;

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            batteryText.setText("Battery : " + String.valueOf(level) + " %");
        }
    }; 



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        config = getResources().getConfiguration();
        width = config.screenWidthDp;
        height = config.screenHeightDp;
        tag = (String)findViewById(R.id.topMostLayout).getTag();

        textClock = (TextClock) findViewById(R.id.textClock);

        //textClock.setTextSize();
        textClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*String size = (String)findViewById(R.id.topMostLayout).getTag();
                String message = config.screenWidthDp + " : " +config.screenHeightDp + " : " + tag;
                Toast toast = Toast.makeText(getBaseContext(),message, Toast.LENGTH_SHORT);
                toast.show();*/

                //setTextSizes();
             //   System.out.println("Mohseen On Click : Size " + config.screenWidthDp + " : " +config.screenHeightDp + " : " + tag);
                openCalendarApp();
            }
        });

        textClockAM = (TextClock) findViewById(R.id.textClockAM);

        textClockSeconds = (TextClock) findViewById(R.id.textClockSeconds);

        textViewDay = (TextView) findViewById(R.id.textViewDay);
        textViewDay.setText(ClockUtility.getTodaysDate("day"));

        textViewDate = (TextView) findViewById(R.id.textViewDate);
        textViewDate.setText(ClockUtility.getTodaysDate("date"));
        textViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalendarApp();
            }
        });

        MobileAds.initialize(getApplicationContext(), getResources().getString(R.string.banner_ad_unit_id_1));
        AdView mAdView1 = (AdView) findViewById(R.id.adView1);
        //mAdView1.setVisibility(View.INVISIBLE);

        Bundle extras = new Bundle();
        extras.putBoolean("is_designed_for_families", true);

        AdRequest adRequest = new AdRequest.Builder().build();
        adRequest.isTestDevice(this);
        mAdView1.loadAd(adRequest);

        setTextSizes();

        initializeBattery();
    }

    public void setTextSizes(){
        //System.out.println("Mohseen : setTextSizes " + width + " : " +height);

        if(config.orientation == 1) {

            if(tag.equalsIgnoreCase("normal")){
                if(width > 375) {
                    textClock.setTextSize(130);
                    textViewDay.setTextSize(44);
                    textClockAM.setTextSize(28);
                    textClockSeconds.setTextSize(28);
                    textViewDate.setTextSize(24);
                }
            }

        }
    }

    public void openCalendarApp(){
        //Log.d("Testing",ACCESSIBILITY_SERVICE);
        Toast.makeText(getBaseContext(),"Opening Calendar", Toast.LENGTH_SHORT).show();
        startActivity(ClockUtility.openCalendarApp());
    }

    private void initializeBattery(){
        batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatus = getBaseContext().registerReceiver(this.mBatInfoReceiver, batteryFilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        /*int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level / (float)scale;*/


        batteryText = (TextView) findViewById(R.id.batteryText);
        batteryText.setText("Battery : " + String.valueOf(level) + " %");
        //this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }


}


