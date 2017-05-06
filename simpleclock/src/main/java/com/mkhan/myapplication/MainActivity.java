package com.mkhan.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
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
    private IntentFilter batteryFilter;
    private Intent batteryStatusIntent;
    private TextView  batteryText;
    boolean chargingStatus;
    private ImageView batteryImage;
    private Button btnStopWatch;
    private Intent stopClockIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        config = getResources().getConfiguration();
        width = config.screenWidthDp;
        height = config.screenHeightDp;
        tag = (String)findViewById(R.id.topMostLayout).getTag();
//        System.out.println("Mohseen : tag : " + findViewById(R.id.topMostLayout).getTag());

        textClock = (TextClock) findViewById(R.id.textClock);

        textClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        mAdView1.setVisibility(View.INVISIBLE);

        AdRequest adRequest = new AdRequest.Builder().build();
        adRequest.isTestDevice(this);
        mAdView1.loadAd(adRequest);

        setTextSizes();

        initializeBattery();

        stopClockIntent = new Intent(getApplicationContext(), StopClockActivity.class);
        stopClockIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        btnStopWatch = (Button) findViewById(R.id.btnStopWatch);
        btnStopWatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(stopClockIntent);
            }
        });
    }

    private void setTextSizes(){
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
        Toast.makeText(getBaseContext(),"Opening Calendar", Toast.LENGTH_SHORT).show();
        startActivity(ClockUtility.openCalendarApp());
    }

    private void initializeBattery(){
        batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatusIntent = getBaseContext().registerReceiver(this.mBatInfoReceiver, batteryFilter);
        batteryText = (TextView) findViewById(R.id.batteryText);

        batteryImage = (ImageView) findViewById(R.id.batteryImage);
        batteryImage.setMaxWidth(batteryText.getWidth());
        batteryImage.setMaxHeight(batteryText.getHeight());
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        unregisterReceiver(this.mBatInfoReceiver);
        //System.out.println("Mohseen On Pause ");
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        //System.out.println("Mohseen onResume ");
        registerReceiver(mBatInfoReceiver, batteryFilter);
    }

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context ctxt, Intent intent) {

            int  level= intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
            int  plugged= intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,0);
            //plugged = 0;
            //level = 100;
            if(plugged > 0 ){//battery is charging
                System.out.println("Battery is Charging : "  +  plugged);
                batteryImage.setImageResource(R.drawable.ic_battery_charging);
            } else {
                if(level > 90 ){
                    batteryImage.setImageResource(R.drawable.ic_battery_full);
                } else if(level >= 70 && level <= 90){
                    batteryImage.setImageResource(R.drawable.ic_battery_80);
                } else if(level >= 50 && level < 70){
                    batteryImage.setImageResource(R.drawable.ic_battery_60);
                } else if(level >= 30 && level < 50){
                    batteryImage.setImageResource(R.drawable.ic_battery_40);
                } else if(level < 30){
                    batteryImage.setImageResource(R.drawable.ic_battery_20);
                }
            }

            batteryText.setText(level + " %");

        }
    };

}


