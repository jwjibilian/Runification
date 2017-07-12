package com.example.jwjibilian.runificationandroid;

import android.content.Context;
import android.os.Debug;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.jwjibilian.runificationandroid.R;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class TestHeartCommsActivity extends AppCompatActivity {

    private PebbleKit.PebbleDataReceiver mDataReciver;
    long heartRate = 0;
    Timer timer;
    TimerTask updateHR;

    private static final UUID APP_UUID = UUID.fromString("3ccbcc65-4bab-47af-ad7b-4a17126fcf1c");
    private String info = "TestComms";
    private TextView HR;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_heart_comms);
        Log.d(info, "MakingAThing" );
        try{
            HR = (TextView) findViewById(R.id.heartRateID);
        } catch ( Exception e) {
            Log.d("Err", e.getMessage());

        }




        PebbleKit.startAppOnPebble(getApplicationContext(), APP_UUID);



    }
    public void report(String s, String x){
        Log.i(s,x);

    }
    @Override
    protected void onResume(){
        super.onResume();

        Log.i(info, mDataReciver + "" );
        if(mDataReciver == null) {
            mDataReciver= new PebbleKit.PebbleDataReceiver(APP_UUID) {


                public void receiveData(Context context, int transactionId, PebbleDictionary dict) {
                    // Always ACK
                    PebbleKit.sendAckToPebble(context, transactionId);
                    Log.i("receiveData", "Got message from Pebble!");

                    heartRate = dict.getInteger(0).intValue();






                }

            };

            Log.i(info, mDataReciver + "HEART!" + heartRate );
            PebbleKit.registerReceivedDataHandler(getApplicationContext(), mDataReciver);
            timer = new Timer();
            updateHR = new MyTimerTask();
            timer.scheduleAtFixedRate(updateHR, 0,1000);


        }






    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {

            runOnUiThread(new Runnable(){

                @Override
                public void run() {
                    Log.i("Timer", "Trying to update HR" + HR + "   "+heartRate);
                    try {
                        HR.setText(heartRate + "");
                    } catch(Throwable e) {
                        Log.i("Error", e.getMessage());
                    }
                }});
        }

    }

}


