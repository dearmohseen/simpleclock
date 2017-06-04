package com.mkhan.myapplication;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.SignInButton;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by khanm on 4/21/2017.
 */

public class TimerActivity extends AppCompatActivity implements View.OnClickListener {

    private Timer myTimer;
    AdView mAdView;
    private Configuration config;
    private int width , height;
    public SharedPreferences sharedPref;

    private int hours, minutes, seconds;
    NumberPicker numberPickerHour , numberPickerMinute , numberPickerSecond;
    private ProgressBar mProgress;
    private int mProgressStatus = 0;

   // private Chronometer chronometer;
    private TextView txtTimerValue;

    public static boolean isTimerOn = false;
    //long mLastStopTime;

    Button btnBackToMainClock , btnTimerPlay , btnTimerReset;
    CountDownTimer timer;
    long milliLeft,timerHour, timerMinute , timerSec;
    StringBuilder sbTextValue = new StringBuilder();

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_timer);
        initializeAdUnit();
        initializeComponents();
        prepareSharedPreference();
        updateBackgroundColor();
        setTextSizes();
    }

    private void initializeComponents() {
        config = getResources().getConfiguration();
        width = config.screenWidthDp;
        height = config.screenHeightDp;

        numberPickerHour = (NumberPicker) findViewById(R.id.numberPickerHour);
        numberPickerHour.setMinValue(0);
        numberPickerHour.setMaxValue(10);
        numberPickerHour.setWrapSelectorWheel(true);
        numberPickerHour.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                hours = newVal;
            }
        });

        numberPickerMinute = (NumberPicker) findViewById(R.id.numberPickerMinutes);
        numberPickerMinute.setMinValue(0);
        numberPickerMinute.setMaxValue(59);
        numberPickerMinute.setWrapSelectorWheel(true);
        numberPickerMinute.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                minutes = newVal;
            }
        });

        numberPickerSecond = (NumberPicker) findViewById(R.id.numberPickerSeconds);
        numberPickerSecond.setMinValue(0);
        numberPickerSecond.setMaxValue(59);
        numberPickerSecond.setWrapSelectorWheel(true);
        numberPickerSecond.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                seconds = newVal;
            }
        });

        mProgress = (ProgressBar) findViewById(R.id.progressBarTimer);
        //chronometer = (Chronometer) findViewById(R.id.chronometer);
        txtTimerValue = (TextView) findViewById(R.id.txtTimerValue);

        btnBackToMainClock = ClockUtility.createBackToMainClockButton(R.id.btnBackToMainClock,this);
        btnTimerPlay = (Button) findViewById(R.id.btnTimerPlay) ;
        btnTimerPlay.setOnClickListener(this);
        btnTimerReset = (Button) findViewById(R.id.btnTimerReset) ;
        btnTimerReset.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnTimerPlay:
                System.out.println("Mohseen " + SystemClock.elapsedRealtime());
                isTimerOn = true;
                togglePlayButtontext((Button) v);
                break;
            case R.id.btnTimerReset:
                isTimerOn = false;
                togglePlayButtontext((Button) v);
                break;
        }
    }

    private long calculateInputTime(){
        Log.d(this.getLocalClassName(),"calculateInputTime " + hours + ":" + minutes + ":" + seconds);
        long inputTime = 0;
        if(hours > 0){
            inputTime = hours * 3600;
        }
        if(minutes > 0){
            if(inputTime > 0){
                inputTime = inputTime +  minutes * 60;
            } else {
                inputTime = minutes * 60 ;
            }
        }
        if(seconds > 0){
            if(inputTime > 0){
                inputTime = inputTime + seconds;
            } else {
                inputTime = seconds;
            }

        }
        Log.d(this.getLocalClassName()," Input Time " + inputTime * 1000);
        return inputTime * 1000;
    }

    private void togglePlayButtontext(Button btn){
        if(btn.getText().equals(ClockUtility.START)){
           long inputTime = calculateInputTime();
           if(inputTime > 0 ) {
               timerStart(inputTime);
               btnTimerPlay.setText(ClockUtility.PAUSE);
           } else {
               ClockUtility.displayToast(getApplicationContext(),"Please Select Timer Value");
           }
        } else if(btn.getText().equals(ClockUtility.PAUSE)){
            //chronometer.stop();
            timerPause();
            btnTimerPlay.setText(ClockUtility.RESUME);
        } else if(btn.getText().equals(ClockUtility.RESUME)){
            timerResume();
            btnTimerPlay.setText(ClockUtility.PAUSE);
        }
        else{
            resetTimer();
        }
    }

    private void resetTimer(){
        if(timer != null){
            timer.cancel();
        }

        txtTimerValue.setText("00:00:00");

        btnTimerPlay.setText(ClockUtility.START);
    }

    public void updateTimerValueToUI(){
        sbTextValue.delete(0,sbTextValue.length());
        //Log.d(this.getLocalClassName() + " Mohseen ",timerHour + ":" + timerMinute + ":" + timerSec );
        if(timerHour > 0){
            sbTextValue.append(String.format("%02d:%02d:%02d",timerHour,timerMinute,timerSec));

        } else if(timerMinute > 0){
            sbTextValue.append(String.format("%02d:%02d",timerMinute,timerSec));
        } else {
            sbTextValue.append(String.format("%02d",timerSec));
        }
        Log.d(this.getLocalClassName() + " Mohseen ", sbTextValue.toString() );

        txtTimerValue.setText(sbTextValue.toString());
        Log.d(this.getLocalClassName() + " Mohseen 1", txtTimerValue.getText().toString() );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        System.out.println("onCreateOptionsMenu : " + actionBar );

        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(false);      // Disable the button
            actionBar.setDisplayHomeAsUpEnabled(false); // Remove the left caret
            actionBar.setDisplayShowHomeEnabled(false); // Remove the icon
        }
        return true;
    }

    public void timerStart(long timeLengthMilli) {

        timer = new CountDownTimer(timeLengthMilli, 1000) {

            @Override
            public void onTick(long milliTillFinish) {

               Log.d("Mohseen",Long.toString(milliTillFinish/1000));
                milliLeft = milliTillFinish;
                timerHour = TimeUnit.MILLISECONDS.toHours(milliTillFinish);
                timerMinute = TimeUnit.MILLISECONDS.toMinutes(milliLeft) - TimeUnit.HOURS.toMinutes(timerHour);
                timerSec = TimeUnit.MILLISECONDS.toSeconds(milliLeft) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliLeft));
        /*        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(milliLeft),
                        TimeUnit.MILLISECONDS.toMinutes(milliLeft) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliLeft)),
                        TimeUnit.MILLISECONDS.toSeconds(milliLeft) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliLeft)));
                Log.d("TimerActivity hms ",hms);
        */
                updateTimerValueToUI();

            }

            public void onFinish() {
                txtTimerValue.setText("Times Up!");
                btnTimerPlay.setText(ClockUtility.START);
            }
        }.start();

    }

    public void timerPause() {
        timer.cancel();
    }

    private void timerResume() {
        System.out.println("Mohseen : timerResume - milliLeft : " + milliLeft );
        //if(timer == null) {
            timerStart(milliLeft);
        //}
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        System.out.println("Mohseen : onSaveInstanceState - StartTime : " + txtTimerValue.getText() );
        outState.putString("timerValue",txtTimerValue.getText().toString());
        outState.putInt("inputHour",hours);
        outState.putInt("inputMinutes",minutes);
        outState.putInt("inputSeconds",seconds);
        outState.putString("btnTimerPlay.text",btnTimerPlay.getText().toString());
        outState.putLong("timerTimeLeft",milliLeft);
    }

    //This method gets called when orientation changes
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);

        txtTimerValue.setText(savedInstanceState.getString("timerValue"));
        hours = savedInstanceState.getInt("inputHour");
        numberPickerHour.setValue(hours);
        minutes = savedInstanceState.getInt("inputMinutes");
        numberPickerMinute.setValue(minutes);
        seconds = savedInstanceState.getInt("inputSeconds");
        numberPickerSecond.setValue(seconds);
      //  System.out.println("Mohseen : onRestoreInstanceState  - HR " + hours + " Min " + minutes + " Sec " + seconds);
        milliLeft = savedInstanceState.getLong("timerTimeLeft");
        String btnTimerPlayText = savedInstanceState.getString("btnTimerPlay.text");
        btnTimerPlay.setText(btnTimerPlayText);

        //System.out.println("Mohseen : onRestoreInstanceState btnStopWatchPlay - " +  btnTimerPlayText);
        if(ClockUtility.PAUSE.equalsIgnoreCase(btnTimerPlayText)){
            timerResume();
        }
    }


    @Override
    public void onPause() {
        mAdView.pause();
        super.onPause();
        System.out.println("Mohseen TimerActivity : onPause ");
    }

    @Override
    public void onDestroy() {
        mAdView.destroy();
        timer.cancel();
        super.onDestroy();
        System.out.println("Mohseen TimerActivity : onDestroy ");
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("Mohseen TimerActivity : onResume ");
        mAdView.resume();
        //timerResume();
        //updateBackgroundColor();
    }


    private void setTextSizes(){
        System.out.println("Mohseen : setTextSizes " + width + " : " +height);

        if(config.orientation == 1) {

            //mProgress.getLayoutParams().width = width + 100;
                /*if(width > 375) {
                    int size = 200;
                    textClock.setTextSize(130);
                    textViewDay.setTextSize(44);
                    textClockAM.setTextSize(28);
                    textClockSeconds.setTextSize(28);
                    textViewDate.setTextSize(24);
                }*/

        }
    }

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

    private void initializeAdUnit(){
        MobileAds.initialize(getApplicationContext(), getResources().getString(R.string.banner_ad_unit_id_1));
        mAdView = (AdView) findViewById(R.id.adView);
        //mAdView1.setVisibility(View.INVISIBLE);

        AdRequest adRequest = new AdRequest.Builder().build();
        adRequest.isTestDevice(this);
        if(mAdView != null) {
            mAdView.loadAd(adRequest);
        }
    }

    public void prepareSharedPreference(){
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

}
