package com.example.jwjibilian.runificationandroid.view;

import android.content.Context;
import android.os.Debug;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.jwjibilian.runificationandroid.R;
import com.example.jwjibilian.runificationandroid.controller.PebbleConnectivity;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class TestHeartCommsActivity extends AppCompatActivity {





    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    private String info = "TestComms";
    private static TextView HR;
    private PebbleConnectivity comms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_heart_comms);
        Log.d(info, "MakingAThing" );
        try{
            HR = (TextView) findViewById(R.id.heartRateID);
        } catch ( Exception e) {
            Log.d("TextViewHR", e.getMessage());

        }
    comms = new PebbleConnectivity(getApplicationContext(), HR);
    comms.startApp(getApplicationContext());




    }
    public void report(String s, String x){
        Log.i(s,x);

    }
    @Override
    protected void onResume(){
        super.onResume();
        comms.startListener();







    }



}


