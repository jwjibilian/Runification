package com.example.jwjibilian.runificationandroid;

import android.content.Context;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.jwjibilian.runificationandroid.R;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.UUID;

public class TestHeartCommsActivity extends AppCompatActivity {

    private PebbleKit.PebbleDataReceiver mDataReciver;
    long heartRate = 0;


    private static final UUID APP_UUID = UUID.fromString("3ccbcc65-4bab-47af-ad7b-4a17126fcf1c");



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_heart_comms);

        PebbleKit.startAppOnPebble(getApplicationContext(), APP_UUID);
        // Register to get updates from Pebble
        if(mDataReciver == null) {
            mDataReciver= new PebbleKit.PebbleDataReceiver(APP_UUID) {


                public void receiveData(Context context, int transactionId, PebbleDictionary dict) {
                    // Always ACK
                    PebbleKit.sendAckToPebble(context, transactionId);

                    heartRate = dict.getInteger(0).intValue();

                    Log.d("FUCKING FUCK!", heartRate + "");




                }

            };
            PebbleKit.registerReceivedDataHandler(getApplicationContext(), mDataReciver);
        }

    }
    @Override
    protected void onResume(){
        super.onResume();


    }

}


