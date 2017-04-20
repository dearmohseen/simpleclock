package com.mkhan.myapplication;

import android.content.res.Configuration;
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

                String size = (String)findViewById(R.id.topMostLayout).getTag();

                Toast toast = Toast.makeText(getBaseContext(),config.screenWidthDp + " : " +config.screenHeightDp + " : " + tag, Toast.LENGTH_SHORT);
                toast.show();

                //setTextSizes();
                //System.out.println("Mohseen On Click : Size " + config.screenWidthDp + " : " +config.screenHeightDp + " : " + tag);
                //openCalendarApp();
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

    }

    public void setTextSizes(){
        //System.out.println("Mohseen : setTextSizes " + width + " : " +height);

        //Horizontal = 1
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

        } else {
            /*if (width < 350) {
                textClock.setTextSize(90);
                textClockAM.setTextSize(18);
                textClockSeconds.setTextSize(18);
                textViewDay.setTextSize(24);
                textViewDate.setTextSize(18);
            } else if (width >= 350 && width < 450) {
                textClock.setTextSize(110);
                textClockAM.setTextSize(24);
                textClockSeconds.setTextSize(24);
                textViewDay.setTextSize(32);
                textViewDate.setTextSize(18);
            } else if (width >= 450 && width < 600) {
                textClock.setTextSize(120);
                textClockAM.setTextSize(28);
                textClockSeconds.setTextSize(28);
                textViewDay.setTextSize(32);
                textViewDate.setTextSize(20);
            } else if (width >= 600 && width < 700) {
                textClock.setTextSize(150);
                textClockAM.setTextSize(30);
                textClockSeconds.setTextSize(30);
                textViewDay.setTextSize(40);
                textViewDate.setTextSize(24);
            } else if (width >= 700 && width < 800 ) {
                textClockAM.setTextSize(40);
                textClockSeconds.setTextSize(40);
                textClock.setTextSize(200);
                textViewDay.setTextSize(52);
                textViewDate.setTextSize(28);
            } else if (width >= 800 ) {
                textClockAM.setTextSize(50);
                textClockSeconds.setTextSize(50);
                textClock.setTextSize(300);
                textViewDay.setTextSize(70);
                textViewDate.setTextSize(35);
            }*/
        }
    }

    public void openCalendarApp(){
        //Log.d("Testing",ACCESSIBILITY_SERVICE);
        //Toast.makeText(getBaseContext(),"Opening Calendar", Toast.LENGTH_SHORT).show();
        //startActivity(ClockUtility.openCalendarApp());
    }

}
