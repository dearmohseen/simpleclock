package com.mkhan.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity {

    private TextClock textClock;
    private TextClock textClockAM;
    private TextClock textClockSeconds;
    private TextView  textViewDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textClock = (TextClock) findViewById(R.id.textClock);
        textClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(),"Opening Calendar", Toast.LENGTH_SHORT).show();
                openCalendarApp();
            }
        });

        textClockAM = (TextClock) findViewById(R.id.textClockAM);

        textClockSeconds = (TextClock) findViewById(R.id.textClockSeconds);

        //setPMTextSize();

        textViewDate = (TextView) findViewById(R.id.textViewDate);
        textViewDate.setText(ClockUtility.getTodaysDay());
        textViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(),"Opening Calendar", Toast.LENGTH_SHORT).show();
                openCalendarApp();
            }
        });

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-1322448895447717/9531904783");
        AdView mAdView = (AdView) findViewById(R.id.adView2);

        Bundle extras = new Bundle();
        extras.putBoolean("is_designed_for_families", true);

        AdRequest adRequest = new AdRequest.Builder().build();
        adRequest.isTestDevice(this);
        mAdView.loadAd(adRequest);

    }

    public void playSound(){
        Toast.makeText(getBaseContext(),"Clicked", Toast.LENGTH_SHORT).show();
    }

    public void setPMTextSize(){
        float size = Math.round((textClock.getTextSize()*1.1f)/3);
        textClockAM.setTextSize(TypedValue.COMPLEX_UNIT_PX,size );
        textClockSeconds.setTextSize(TypedValue.COMPLEX_UNIT_PX,size);

    }

    public void openCalendarApp(){
        //Log.d("Testing",ACCESSIBILITY_SERVICE);
        //startActivity(ClockUtility.openCalendarApp());
    }

}
