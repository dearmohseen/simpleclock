package com.mkhan.myapplication;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
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

    private ProgressBar mProgress;
    private Chronometer chronometer;

    public static boolean isTimerOn = false;
    long mLastStopTime;

    Button btnBackToMainClock , btnTimerPlay , btnTimerReset;

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
        mProgress = (ProgressBar) findViewById(R.id.progressBarTimer);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
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

    private void togglePlayButtontext(Button btn){
        if(btn.getText().equals(ClockUtility.START)){
            if ( mLastStopTime == 0 )
                chronometer.setBase( SystemClock.elapsedRealtime() );
            else {
                long intervalOnPause = (SystemClock.elapsedRealtime() - mLastStopTime);
                chronometer.setBase( chronometer.getBase() + intervalOnPause );
            }
            chronometer.start();
            btnTimerPlay.setText(ClockUtility.PAUSE);
        } else if(btn.getText().equals(ClockUtility.PAUSE)){
            chronometer.stop();
            mLastStopTime = SystemClock.elapsedRealtime();
            btnTimerPlay.setText(ClockUtility.START);
        } else{
            resetTimer();
        }
    }

    private void resetTimer(){
        mLastStopTime = 0;
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());
        btnTimerPlay.setText(ClockUtility.START);
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
