package com.example.jwjibilian.runificationandroid.controller;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.lang.Runnable;


public class PebbleConnectivity {
    private static PebbleKit.PebbleDataReceiver mDataReciver;
    private static final UUID APP_UUID = UUID.fromString("3ccbcc65-4bab-47af-ad7b-4a17126fcf1c");
    private long heartRate = 0;
    private static Timer timer;
    private static TimerTask updateHR;
    private final String info = "PebbleConnectC";

    private final Handler handler = new Handler();


    /**
     *
     * This will create a connection bewtween the app and the watch.
     * Also sets what text views to be updated
     *
     * @param appContext Context that is open in the app
     */
    public PebbleConnectivity(Context appContext){
        PebbleKit.startAppOnPebble(appContext, APP_UUID);  // starts the app on the watch

//        Log.i(info, mDataReciver + "" );
//        if(mDataReciver == null) {
//            mDataReciver= new PebbleKit.PebbleDataReceiver(APP_UUID) {
//
//                /**
//                 * Creates the function to get data from the dictionary from pebble watch.
//                 * @param context The app context
//                 * @param transactionId Transaction id made by pebble app on phone
//                 * @param dict dict made by pebble app on phone
//                 */
//                public void receiveData(Context context, int transactionId, PebbleDictionary dict) {
//                    // Always ACK
//                    PebbleKit.sendAckToPebble(context, transactionId);
//                    Log.i("receiveData", "Got message from Pebble!");
//
//                    heartRate = dict.getInteger(0).intValue();
//                }
//
//            };

//            Log.i(info, mDataReciver + "HEART!" + heartRate );
//            PebbleKit.registerReceivedDataHandler(appContext, mDataReciver);
//            this.HR = HR;

//        }
    }

}
