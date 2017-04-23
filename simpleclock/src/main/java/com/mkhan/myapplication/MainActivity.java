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
import android.widget.ImageView;
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
    Intent batteryStatusIntent;
    private TextView  batteryText;
    boolean chargingStatus;
    ImageView batteryImage;


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
        mAdView1.setVisibility(View.INVISIBLE);

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

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context ctxt, Intent intent) {

            BatteryManager bm = (BatteryManager)getSystemService(BATTERY_SERVICE);

            int  health= intent.getIntExtra(BatteryManager.EXTRA_HEALTH,0);

            int  level= intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
            int  plugged= intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,0);
            boolean  present= intent.getExtras().getBoolean(BatteryManager.EXTRA_PRESENT);
            int  scale= intent.getIntExtra(BatteryManager.EXTRA_SCALE,0);
            int  status= intent.getIntExtra(BatteryManager.EXTRA_STATUS,0);
            String  technology= intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);
            int  temperature= intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0);
            int  voltage= intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE,0);
            //plugged = 0;
            level = 100;
            if(plugged > 0 ){//battery is charging
                System.out.println("Battery is Charging : "  +  plugged);
                batteryImage.setImageResource(R.drawable.ic_battery_charging);
            } else {
                if(level == 100){
                    batteryImage.setImageResource(R.drawable.ic_battery_full);
                } else if(level >= 75 && level < 100){
                    batteryImage.setImageResource(R.drawable.ic_battery_80);
                } else if(level >= 45 && level < 80){
                    batteryImage.setImageResource(R.drawable.ic_battery_60);
                } else if(level >= 25 && level < 45){
                    batteryImage.setImageResource(R.drawable.ic_battery_40);
                } else if(level < 25){
                    batteryImage.setImageResource(R.drawable.ic_battery_20);
                }
            }

            batteryText.setText(level + " %");
            /*batteryText.setText(
                    "Health: "+health+"\n"+
                            "Level: "+level+"\n"+
                            "Plugged: "+plugged+"\n"+
                            "Present: "+present+"\n"+
                            "Scale: "+scale+"\n"+
                            "Status: "+status+"\n"+
                            "Technology: "+technology+"\n"+
                            "Temperature: "+temperature+"\n"+
                            "Voltage: "+voltage+"\n");*/
            //batteryText.setTextSize(18);
        }
    };

}


