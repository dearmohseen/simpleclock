package com.mkhan.myapplication;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by khanm on 4/21/2017.
 */

public class TimerActivity extends Activity implements View.OnClickListener {

    private Timer myTimer;
    AdView mAdView;
    private Configuration config;
    private int width , height;

    private int hours, minutes, seconds;
    MyNumberPicker numberPickerHour , numberPickerMinute , numberPickerSecond;
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
        setTextSizes();
    }

    private void initializeComponents() {
        config = getResources().getConfiguration();
        width = config.screenWidthDp;
        height = config.screenHeightDp;

        numberPickerHour = (MyNumberPicker) findViewById(R.id.numberPickerHour);
        numberPickerHour.setMinValue(0);
        numberPickerHour.setMaxValue(10);
        numberPickerHour.setWrapSelectorWheel(true);
        numberPickerHour.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                hours = newVal;
            }
        });

        numberPickerMinute = (MyNumberPicker) findViewById(R.id.numberPickerMinutes);
        numberPickerMinute.setMinValue(0);
        numberPickerMinute.setMaxValue(59);
        numberPickerMinute.setWrapSelectorWheel(true);
        numberPickerMinute.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                minutes = newVal;
            }
        });

        numberPickerSecond = (MyNumberPicker) findViewById(R.id.numberPickerSeconds);
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
        long inputTime = 0;
        if(hours > 0){
            inputTime = hours * 3600;
        }
        if(minutes > 0){
            if(inputTime > 0){
                inputTime = inputTime * minutes * 60;
            } else {
                inputTime = minutes * 60;
            }
        }
        if(seconds > 0){
            if(inputTime > 0){
                inputTime = inputTime * seconds;
            } else {
                inputTime = seconds;
            }

        }

        return inputTime * 1000;
    }

    private void togglePlayButtontext(Button btn){
        if(btn.getText().equals(ClockUtility.START)){
           long inputTime = calculateInputTime();
           if(inputTime > 0 ) {
               timerStart(inputTime + 1000);
            /*if ( mLastStopTime == 0 ) {
                //chronometer.setBase( SystemClock.elapsedRealtime() + 10);
                timerStart(300000);
            }
            else {
                long intervalOnPause = (SystemClock.elapsedRealtime() - mLastStopTime);
                timerResume();
            }*/
               btnTimerPlay.setText(ClockUtility.PAUSE);
           } else {
               ClockUtility.displayToast(getApplicationContext(),"Please Select Timer Value");
           }
        } else if(btn.getText().equals(ClockUtility.PAUSE)){
            //chronometer.stop();
            timerPause();
            //mLastStopTime = SystemClock.elapsedRealtime();
            btnTimerPlay.setText(ClockUtility.RESUME);
        } else if(btn.getText().equals(ClockUtility.RESUME)){
            timerResume();
            //mLastStopTime = SystemClock.elapsedRealtime();
            btnTimerPlay.setText(ClockUtility.PAUSE);
        }
        else{
            resetTimer();
        }
    }

    private void resetTimer(){
        //mLastStopTime = 0;
        //chronometer.stop();
        //chronometer.setBase(SystemClock.elapsedRealtime());
        if(timer != null){
            timer.cancel();
        }

        txtTimerValue.setText("00:00:00");
        btnTimerPlay.setText(ClockUtility.START);
    }

    public void updateTimerValueToUI(){
        sbTextValue.delete(0,sbTextValue.length());
        Log.d(this.getLocalClassName(),timerHour + ":" + timerMinute + ":" + timerSec );
        if(timerHour > 0){
            sbTextValue.append(Long.toString(timerHour) + ":");
        }
        if(timerMinute > 0 || timerHour > 0){
            sbTextValue.append(Long.toString(timerMinute)+ ":");
        }
        //if(timerSec > 0){
            sbTextValue.append(Long.toString(timerSec));
        //}
        txtTimerValue.setText(sbTextValue.toString());
    }

    public void timerStart(long timeLengthMilli) {

        milliLeft = timeLengthMilli;
        timerMinute = (timeLengthMilli / (1000 * 60));
        timerSec = ((timeLengthMilli / 1000) - timerMinute * 60);
        updateTimerValueToUI();

        timer = new CountDownTimer(timeLengthMilli, 1000) {

            @Override
            public void onTick(long milliTillFinish) {

                Log.d("Mohseen",Long.toString(milliTillFinish/1000));
                milliLeft = milliTillFinish;
                timerMinute = (milliTillFinish / (1000 * 60));
                timerSec = ((milliTillFinish / 1000) - timerMinute * 60);
                //txtTimerValue.setText(Long.toString(timerMinute) + ":" + Long.toString(timerSec));
                updateTimerValueToUI();
            }

            public void onFinish() {
                txtTimerValue.setText("Times Up!");
                btnTimerPlay.setText(ClockUtility.START);
            }
        }.start();
        //timer.start();
    }

    public void timerPause() {
        timer.cancel();
    }

    private void timerResume() {
        timerStart(milliLeft);
    }

    @Override
    public void onPause() {
        mAdView.pause();
        super.onPause();
        //System.out.println("Mohseen On Pause ");
    }

    @Override
    public void onDestroy() {
        mAdView.destroy();
        super.onDestroy();
        //System.out.println("Mohseen On onDestroy ");
    }

    @Override
    public void onResume() {
        super.onResume();
        //System.out.println("Mohseen onResume ");
        mAdView.resume();
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

}
