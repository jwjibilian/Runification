package com.example.jwjibilian.runificationandroid.controller;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.example.jwjibilian.runificationandroid.view.TestHeartCommsActivity;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.lang.Runnable;

/**
 * Created by jwjibilian on 7/14/17.
 */

public class PebbleConnectivity {
    private static PebbleKit.PebbleDataReceiver mDataReciver;
    private static final UUID APP_UUID = UUID.fromString("3ccbcc65-4bab-47af-ad7b-4a17126fcf1c");
    private long heartRate = 0;
    private static Timer timer;
    private static TimerTask updateHR;
    private final String info = "PebbleConnectC";

    private final Handler handler = new Handler();

    private TextView HR;

    /**
     *
     * This will create a connection bewtween the app and the watch.
     * Also sets what text views to be updated
     *
     * @param appContext Context that is open in the app
     * @param HR Text view for the Heart rate
     */
    public PebbleConnectivity(Context appContext, TextView HR){


        Log.i(info, mDataReciver + "" );
        if(mDataReciver == null) {
            mDataReciver= new PebbleKit.PebbleDataReceiver(APP_UUID) {

                /**
                 * Creates the function to get data from the dictionary from pebble watch.
                 * @param context The app context
                 * @param transactionId Transaction id made by pebble app on phone
                 * @param dict dict made by pebble app on phone
                 */
                public void receiveData(Context context, int transactionId, PebbleDictionary dict) {
                    // Always ACK
                    PebbleKit.sendAckToPebble(context, transactionId);
                    Log.i("receiveData", "Got message from Pebble!");

                    heartRate = dict.getInteger(0).intValue();






                }

            };

            Log.i(info, mDataReciver + "HEART!" + heartRate );
            PebbleKit.registerReceivedDataHandler(appContext, mDataReciver);
            this.HR = HR;
/*
            timer = new Timer();
            updateHR = new TestHeartCommsActivity.MyTimerTask();
            timer.scheduleAtFixedRate(updateHR, 0,1000);

*/

        }



    }

    /**
     * starts the app on the phone
     * @param appContext
     */
    public void startApp(Context appContext){
        PebbleKit.startAppOnPebble(appContext, APP_UUID);


    }

    /**
     * This starts
     */
    public void startListener() {

        Runnable runnable = new Runnable() {


            @Override
            public void run() {
                try {


                    Log.i("Timer", "Trying to update HR" + HR + "   " + heartRate);
                    HR.setText(heartRate + "");


                } catch (Exception e) {
                    Log.i("Error", e.getMessage());
                } finally {
                    //also call the same runnable to call it at regular interval
                    handler.postDelayed(this, 1000);
                }
            }



//        class MyTimerTask extends TimerTask {
//
//            @Override
//            public void run() {
//
//                runOnUiThread(new Runnable(){
//
//                    @Override
//                    public void run() {
//                        Log.i("Timer", "Trying to update HR" + HR + "   "+heartRate);
//                        try {
//                            HR.setText(heartRate + "");
//                        } catch(Throwable e) {
//                            Log.i("Error", e.getMessage());
//                        }
//                    }});
//            }
//
//        }

        };

        new Thread(runnable).start();
    }



}
