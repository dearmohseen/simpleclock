package com.mkhan.myapplication;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class StopClockActivity extends AppCompatActivity {

    TextView txtStopWatch ;
    Button btnStopWatchPlay;
    Button btnLap;
    Button btnStopWatchClock;
    Button btnStopWatchReset;

    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;
    String milliSecond;

    Handler handler;

    int Seconds, Minutes, MilliSeconds ;
    ListView listView ;

    ArrayList<String> listElementsArrayList ;

    ArrayAdapter<String> adapter ;
    private Configuration config;
    private int width , height;
    private final String START = "Start";
    private final String PAUSE = "Pause";
    private final String RESUME = "Resume";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_clock);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

//        System.out.println("Mohseen : stopwatch " + savedInstanceState);

        int display_mode = getResources().getConfiguration().orientation;


        //if(savedInstanceState == null){
        config = getResources().getConfiguration();
        width = config.screenWidthDp;
        height = config.screenHeightDp;

        txtStopWatch = (TextView) findViewById(R.id.txtStopWatch);
        handler = new Handler();

        createPlay();
        createLapList();
        createLapButton();
        createMainClockButon();
        createReset();

        //setTextSizes();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //System.out.println("Mohseen : onSaveInstanceState - StartTime : " + StartTime );
        outState.putString("clockValue",txtStopWatch.getText().toString());
        outState.putLong("StartTime",StartTime);
        outState.putLong("TimeBuff",TimeBuff);
        outState.putStringArrayList("listElementsArrayList", listElementsArrayList);
        outState.putString("btnStopWatchPlay.text",btnStopWatchPlay.getText().toString());
        outState.putBoolean("btnLap.enable",btnLap.isEnabled());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        txtStopWatch.setText(savedInstanceState.getString("clockValue"));
        StartTime = savedInstanceState.getLong("StartTime");
        TimeBuff = savedInstanceState.getLong("TimeBuff");
        setupList(savedInstanceState.getStringArrayList("listElementsArrayList"));
       String btnPlayText = savedInstanceState.getString("btnStopWatchPlay.text");
        btnStopWatchPlay.setText(btnPlayText);
        btnLap.setEnabled(savedInstanceState.getBoolean("btnLap.enable"));

       //System.out.println("Mohseen : onRestoreInstanceState btnStopWatchPlay - " +  btnPlayText);

        if(PAUSE.equalsIgnoreCase(btnPlayText)){
            handler.postDelayed(runnable, 0);
        }
    }

    private void setupList(ArrayList<String> list){
        listElementsArrayList = list;
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listElementsArrayList
        ){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =super.getView(position, convertView, parent);
                TextView textView=(TextView) view.findViewById(android.R.id.text1);
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,25);
                textView.setGravity(Gravity.CENTER);
                return view;
            }
        };
        listView.setAdapter(adapter);
    }

    public void createLapList(){
        listView = (ListView)findViewById(R.id.listStopwatch);
        setupList(new ArrayList<String>());
    }

    public void createMainClockButon(){
        btnStopWatchClock = (Button) findViewById(R.id.btnStopWatchClock);
        btnStopWatchClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                //finish();
            }
        });
    }

    public void createReset(){
        btnStopWatchReset = (Button) findViewById(R.id.btnStopWatchReset);
        btnStopWatchReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MillisecondTime = 0L ;
                StartTime = 0L ;
                TimeBuff = 0L ;
                UpdateTime = 0L ;
                Seconds = 0 ;
                Minutes = 0 ;
                MilliSeconds = 0 ;

                listElementsArrayList.clear();
                adapter.clear();
                adapter.notifyDataSetChanged();
                handler.removeCallbacks(runnable);
                txtStopWatch.setText("00:00:00");
                btnStopWatchPlay.setText(START);
                btnLap.setEnabled(false);

            }
        });
    }


    public void createLapButton(){
        btnLap = (Button) findViewById(R.id.btnLap);
        btnLap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*System.out.println("Mohseen Lap : " + txtStopWatch.getText().toString()
                        + " List Size : " + listElementsArrayList.size());*/
                //listElementsArrayList.add(listElementsArrayList.size()+1 + ".  " +txtStopWatch.getText().toString());
                adapter.add(adapter.getCount()+1 + ".  " +txtStopWatch.getText().toString());
                adapter.notifyDataSetChanged();
                listView.smoothScrollToPosition(adapter.getCount());
            }
        });
        btnLap.setEnabled(false);
    }

    public void createPlay(){

        btnStopWatchPlay = (Button) findViewById(R.id.btnStopWatchPlay);
        btnStopWatchPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playBtnClick(btnStopWatchPlay.getText().toString());
            }
        });
    }

    private void playBtnClick(String btnPlayText){
        //System.out.println("Mohseen Play Click : " + btnPlayText + " - StartTime - " + StartTime + " TimeBuff - " + TimeBuff);

        if(!"pause".equalsIgnoreCase(btnPlayText)){
            StartTime = SystemClock.uptimeMillis();
        }
        //System.out.println("Mohseen : StartTime - " + StartTime + " TimeBuff - " + TimeBuff);

        handler.postDelayed(runnable, 0);
        toggleStartPauseText(btnPlayText);

    }

    public void toggleStartPauseText(String btnPlayText){
       // System.out.println("Mohseen : toggleStartPauseText - " + btnPlayText);

        if(btnPlayText.equals(START)){
            btnStopWatchPlay.setText(PAUSE);
            btnLap.setEnabled(true);
        } else if(btnPlayText.equals(PAUSE)){
            btnStopWatchPlay.setText(RESUME);
            TimeBuff += MillisecondTime;
            btnLap.setEnabled(false);
            handler.removeCallbacks(runnable);
        }  else{
            btnLap.setEnabled(true);
            btnStopWatchPlay.setText(PAUSE);
        }

    }

    public Runnable runnable = new Runnable() {

        public void run() {

            MillisecondTime = SystemClock.uptimeMillis() - StartTime;
            UpdateTime = TimeBuff + MillisecondTime;
            Seconds = (int) (UpdateTime / 1000);
            Minutes = Seconds / 60;
            Seconds = Seconds % 60;
            MilliSeconds = (int) (UpdateTime % 1000);
            milliSecond = String.valueOf(MilliSeconds);

            //System.out.println("Mohseen : Milliseconds " + milliSecond);
            if(milliSecond.length()>2){
                txtStopWatch.setText(String.format("%02d", Minutes)  + ":"
                        + String.format("%02d", Seconds) + ":"
                        + milliSecond.substring(0,2) );
            }
            else if(milliSecond.length()==1){

                txtStopWatch.setText(String.format("%02d", Minutes)  + ":"
                        + String.format("%02d", Seconds) + ":0"
                        + milliSecond);
            } else {
                txtStopWatch.setText(String.format("%02d", Minutes)  + ":"
                        + String.format("%02d", Seconds) + ":"
                        + milliSecond);
            }

            handler.postDelayed(this, 0);
        }

    };

    /*public void setTextSizes(){
        System.out.println("Mohseen : setTextSizes "+ config.orientation + " : " + width + " : " +height);

        if(config.orientation == 1) {

            if(width <= 350) {
                final int size = 14;
                btnStopWatchClock.setTextSize(size);
                btnStopWatchPlay.setTextSize(size);
                btnLap.setTextSize(size);
                btnStopWatchReset.setTextSize(size);
            } else if(width > 350) {
                final int size = 24;
                    btnStopWatchClock.setTextSize(size);
                    btnStopWatchPlay.setTextSize(size);
                    btnLap.setTextSize(size);
                    btnStopWatchReset.setTextSize(size);
                }
        }
    }*/
}
