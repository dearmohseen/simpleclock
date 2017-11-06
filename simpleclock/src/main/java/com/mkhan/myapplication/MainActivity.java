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
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
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

    private IntentFilter batteryFilter;
    private Intent batteryStatusIntent;

    private Button btnStopWatch;
    private Intent stopClockIntent;

    AdView mAdView1;
    private InterstitialAd mInterstitialAd;
    public SharedPreferences sharedPref;
    private ShareActionProvider mShareActionProvider;
    private CircleProgress circleProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);
        onNewIntent(getIntent());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        config = getResources().getConfiguration();
        width = config.screenWidthDp;
        height = config.screenHeightDp;

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

        initializeAdUnit();
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

        setTextSizes();

        prepareSharedPreference();
        updateBackgroundColor();
    }

    private void initializeAdUnit() {
        MobileAds.initialize(getApplicationContext(), getResources().getString(R.string.banner_ad_unit_id_1));
        mAdView1 = (AdView) findViewById(R.id.adView1);
        //mAdView1.setVisibility(View.GONE);

        AdRequest adRequest = new AdRequest.Builder().build();
        //adRequest.isTestDevice(this);
        mAdView1.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        //Test ad unit -- ca-app-pub-3940256099942544/1033173712
        //mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        mInterstitialAd.setAdUnitId(getString(R.string.interestial_ad_unit));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });

    }

    protected void onNewIntent(Intent intent) {
        String action = intent.getAction();
        String data = intent.getDataString();
        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            String path = data.substring(data.lastIndexOf("/") + 1);
            if("stopwatch".equalsIgnoreCase(path)){
                startActivity(stopClockIntent);
            }
        }
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item=menu.findItem(R.id.menu_item_share  );
        mShareActionProvider=(ShareActionProvider) MenuItemCompat.getActionProvider(item);
        //System.out.println(" onCreateOptionsMenu : mShareActionProvider " + mShareActionProvider);

        if(mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
        return true;
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, Utility.APP_STORE_URL + this.getPackageName());
        return shareIntent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Handle item selection
        switch (item.getItemId()) {
            case R.id.action_rate_app:
                //System.out.println("Mohseen : Rate App Clicked ");
                rateApp();
                return true;
            case R.id.menu_item_share:
                //System.out.println("Mohseen : Action setting Clicked ");
                this.startActivity(new Intent(this,SettingsActivity.class));
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

/*
       System.out.println("Mohseen Screen : setTextSizes " + width + " : " +height + " :: " + config.densityDpi + " :: "
                + getResources().getDisplayMetrics().density );
*/

       if ((config.screenLayout &Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE)
       {
           Log.d("Screen Size: ", "LARGE");
           if(config.orientation == 1) {
                if(width >= 480 && width <= 500) {
                       textClock.setTextSize(140);
                       textViewDay.setTextSize(38);
                       textClockAM.setTextSize(32);
                       /*textClockSeconds.setTextSize(28);
                       textViewDate.setTextSize(24);*/
                       btnStopWatch.setTextSize(28);
                       circleProgress.getLayoutParams().width = 110;
                    circleProgress.getLayoutParams().height = 110;
                   }
           }
       }
       else if ((getResources().getConfiguration().screenLayout &      Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
           int density = config.densityDpi;
           Log.d("Screen Size: ", "NORMAL : " + density);
           //DisplayMetrics.DENSITY_HIGH;
           if(config.orientation == 1) {

               if(width >= 360 && density > 320) {
                   Log.d("screen textClock: ", String.valueOf(textClock.getTextSize()));
                   textClock.setTextSize(TypedValue.COMPLEX_UNIT_SP,100);
                   textViewDay.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);
                   textClockAM.setTextSize(TypedValue.COMPLEX_UNIT_SP,28);
                   textClockSeconds.setTextSize(TypedValue.COMPLEX_UNIT_SP,28);
                   textViewDate.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                   btnStopWatch.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                   circleProgress.getLayoutParams().width = 200;
                   circleProgress.getLayoutParams().height = 200;
               }
           }
       }
       /*else if ((getResources().getConfiguration().screenLayout &      Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
           Log.d("Screen Size: ", "SMALL");
       }
       else if ((getResources().getConfiguration().screenLayout &      Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
           Log.d("Screen Size: ", "XLARGE");
       }
       else {
           Log.d("Screen Size: ","UNKNOWN_CATEGORY_SCREEN_SIZE");
       }*/

       //Determine density
       /*DisplayMetrics metrics = new DisplayMetrics();
       getWindowManager().getDefaultDisplay().getMetrics(metrics);
       int density = metrics.densityDpi;

       if (density==DisplayMetrics.DENSITY_HIGH) {
           Log.d("Screen Density: ","HIGH");
       }
       else if (density==DisplayMetrics.DENSITY_MEDIUM) {
           Log.d("Screen Density: ","MEDIUM");
       }
       else if (density==DisplayMetrics.DENSITY_LOW) {
           Log.d("Screen Density: ","LOW");
       }
       else if (density== DisplayMetrics.DENSITY_XHIGH) {
           Log.d("Screen Density: ","XHIGH");
       }
       else if (density==DisplayMetrics.DENSITY_XXHIGH) {
           Log.d("Screen Density: ","XXHIGH");
       }
       else {
           Log.d("Screen Density: ","UNKNOWN_CATEGORY");
       }*/
       //System.out.println("Mohseen : setTextSizes layoutSize " + layoutSize  );

    }

    public void openCalendarApp(){
        //Toast.makeText(getBaseContext(),"Opening Calendar", Toast.LENGTH_SHORT).show();
        ClockUtility.displayToast(getBaseContext(),"Opening Calendar");
        startActivity(ClockUtility.openCalendarApp());
    }

    private void initializeBattery(){
        batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatusIntent = getBaseContext().registerReceiver(mBatInfoReceiver, batteryFilter);
        circleProgress = (CircleProgress) findViewById(R.id.circleProgress);
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
        System.out.println("Mohseen : setTextSizes " + width + " : " +height + " :: " + config.densityDpi + " :: "
                + getResources().getDisplayMetrics().density + " Value : " + R.dimen.textClock_textSize );
        super.onResume();  // Always call the superclass method first
        mAdView1.resume();
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
        updateBackgroundColor();
        registerReceiver(mBatInfoReceiver, batteryFilter);
    }

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int  level= intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
            int  plugged= intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,0);

            circleProgress.setProgress(level);
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


