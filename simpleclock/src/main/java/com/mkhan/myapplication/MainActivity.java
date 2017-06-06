package com.mkhan.myapplication;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
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

    AdView mAdView1;
    public SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
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
        mAdView1 = (AdView) findViewById(R.id.adView1);
        //mAdView1.setVisibility(View.GONE);

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
                //System.out.println("Mohseen btnStopWatch.onClick ");
                startActivity(stopClockIntent);
            }
        });

        prepareSharedPreference();
        updateBackgroundColor();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Handle item selection
        switch (item.getItemId()) {
            case R.id.action_rate_app:
                //System.out.println("Mohseen : Rate App Clicked ");
                rateApp();
                return true;
            case R.id.action_settings:
                //System.out.println("Mohseen : Action setting Clicked ");
                this.startActivity(new Intent(this,SettingsActivity.class));
                return true;
            case R.id.action_timer:
                Intent intent = new Intent(this,TimerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                this.startActivity(intent);
                return true;
            default:
                this.startActivity(new Intent(this,SettingsActivity.class));
                return true;
        }
    }

    private void rateApp(){
        Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        if (Build.VERSION.SDK_INT >= 21) {
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        } else {
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        }

        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
        }
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
        //Toast.makeText(getBaseContext(),"Opening Calendar", Toast.LENGTH_SHORT).show();
        ClockUtility.displayToast(getBaseContext(),"Opening Calendar");
        startActivity(ClockUtility.openCalendarApp());
    }

    private void initializeBattery(){
        batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatusIntent = getBaseContext().registerReceiver(mBatInfoReceiver, batteryFilter);
        batteryText = (TextView) findViewById(R.id.batteryText);

        batteryImage = (ImageView) findViewById(R.id.batteryImage);
        batteryImage.setMaxWidth(batteryText.getWidth());
        batteryImage.setMaxHeight(batteryText.getHeight());
    }

    @Override
    public void onPause() {
        unregisterBatteryReceiver();
        mAdView1.pause();
        super.onPause();  // Always call the superclass method first
        //System.out.println("Mohseen On Pause ");
    }

    private void unregisterBatteryReceiver(){
        //System.out.println("Mohseen unregisterBatteryReceiver " + mBatInfoReceiver);
        if(mBatInfoReceiver != null) {
            try {
                unregisterReceiver(mBatInfoReceiver);
            } catch(Exception e){
               // Log.d(this.getLocalClassName(),"mBatInfoReceiver already unregistered " + e.getMessage());
            }

        }
    }

    @Override
    public void onDestroy() {
        //System.out.println("Mohseen On onDestroy ");
        super.onDestroy();
        unregisterBatteryReceiver();
        mAdView1.destroy();
          // Always call the superclass method first

    }

    @Override
    public void onResume() {
        //System.out.println("Mohseen onResume ");
        super.onResume();  // Always call the superclass method first
        mAdView1.resume();
        updateBackgroundColor();
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
                //System.out.println("Battery is Charging : "  +  plugged);
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

    private void updateBackgroundColor(){
        String color = sharedPref.getString(getString(R.string.pref_background_color),"#000000");
        ConstraintLayout mainConstraintLayout = (ConstraintLayout) findViewById(R.id.mainConstraintLayout);
        GradientDrawable gd = (GradientDrawable) mainConstraintLayout.getBackground();
        gd.setColor(Color.parseColor(color));
        int width_px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        if("#000000".equalsIgnoreCase(color)){
            gd.setStroke(width_px, Color.WHITE);
        } else {
            gd.setStroke(width_px, Color.BLACK);
        }
    }


    public void prepareSharedPreference(){
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }
}


