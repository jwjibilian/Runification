package com.example.jwjibilian.runificationandroid.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.jwjibilian.runificationandroid.R;
import com.example.jwjibilian.runificationandroid.controller.DataRecord;
import com.example.jwjibilian.runificationandroid.controller.Pace;
import com.example.jwjibilian.runificationandroid.controller.Sonification;
import com.example.jwjibilian.runificationandroid.controller.TrainingMode;
import com.example.jwjibilian.runificationandroid.model.User;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.UUID;

public class WeightLossTraining extends AppCompatActivity {
    private static final String TAG = "RUNIF";
    private User user = User.getInstance(this);

    private static final UUID APP_UUID = UUID.fromString("3ccbcc65-4bab-47af-ad7b-4a17126fcf1c");
    private static PebbleKit.PebbleDataReceiver pebbleDataReceiver;

    private TextView currHrTxt;
    private TextView avgHrTxt;
    private EditText lowHrTxt;
    private EditText highHrTxt;
    private  int avgHr;
    private DataRecord dr;

    Sonification sonify;

    private Handler recorder;
    private Runnable recorderTh;
    DecimalFormat formatter = new DecimalFormat("#0.00");

    private Handler pace;
    private Runnable paceTh;

    private Pace paceTr;
    private double paceVal = 0;

    /*****************************************
     * Load user parameters for this training
     *****************************************/
    private void loadHrParams(){
        // Get current user Heart Rate parameters
        int restHr = user.getRestingHR();
        int lowHr  = user.getLowHrWeightLoss();
        int highHr = user.getHighHrWeightLoss();

        // Update text fields of hr bounds
        lowHrTxt.setText(String.valueOf(lowHr));
        highHrTxt.setText(String.valueOf(highHr));
        currHrTxt.setText(String.valueOf(restHr));

        // Update Sonification Mgr
        sonify.setHrParams(lowHr, highHr, restHr);
        sonify.setMode(TrainingMode.WEIGHT_LOSS);
    }

    /*****************************************
     * Start/Stop buttons
     *****************************************/
    public void onStartClick(View view){
        // Update HR bounds
        
        sonify.setLowHr(Integer.parseInt(lowHrTxt.getText().toString()));
        sonify.setHighHr(Integer.parseInt(highHrTxt.getText().toString()));
        sonify.start();

        // Enable Stop and disable Start buttons
        Button startBtn = (Button)findViewById(R.id.startWeightLoss);
        Button stopBtn  = (Button)findViewById(R.id.stopWeightLoss);
        startBtn.setEnabled(false);
        stopBtn.setEnabled(true);
        pace.postDelayed(paceTh, 5000);
        recorder.postDelayed(recorderTh,4000);
    }

    public void onStopClick(View view){
        sonify.stop();

        // Disable Stop and Enable Start buttons
        Button startBtn = (Button)findViewById(R.id.startWeightLoss);
        Button stopBtn  = (Button)findViewById(R.id.stopWeightLoss);
        startBtn.setEnabled(true);
        stopBtn.setEnabled(false);
        pace.removeCallbacks(paceTh);
        recorder.removeCallbacks(recorderTh);
        dr.close();
    }

    /*****************************************
     * Activity Managers
     *****************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_loss_training);
        paceTr = new Pace(getApplicationContext(), (LocationManager) getSystemService(LOCATION_SERVICE));
        // Set handles to text fields
        lowHrTxt  = (EditText) findViewById(R.id.LowHRGoal);
        highHrTxt = (EditText) findViewById(R.id.HighHRGoal);
        currHrTxt = (TextView) findViewById(R.id.CurrentHR);
        avgHrTxt  = (TextView) findViewById(R.id.avgHR);

        dr = new DataRecord(getApplicationContext(), this, "Weight");

        sonify = new Sonification(this.getApplicationContext());
        loadHrParams();

        // Create Data receiver for Pebble
        pebbleDataReceiver= new PebbleKit.PebbleDataReceiver(APP_UUID) {
            public void receiveData(Context context, int transactionId, PebbleDictionary dict) {
                PebbleKit.sendAckToPebble(context, transactionId);

                // Get and Update HR value
                int newHr = dict.getInteger(0).intValue();
                updateHr(newHr);
            }
        };
        PebbleKit.registerReceivedDataHandler(getApplicationContext(), pebbleDataReceiver);

        recorder = new Handler();
        recorderTh = new Runnable() {
            @Override
            public void run() {
                dr.save(formatter.format(paceVal)+","+formatter.format(avgHr)+"\n");
                recorder.postDelayed(this, 4000);
            }
        };


        pace = new Handler();

        paceTh = new Runnable() {
            @Override
            public void run() {
                paceVal = paceTr.getPace();
                pace.postDelayed(paceTh, 5000);


            }
        };
    }

    private void updateHr(int newHr){
        currHrTxt.setText(String.valueOf(newHr));
        avgHr = sonify.updateHr(newHr);
        avgHrTxt.setText(String.valueOf(avgHr));
    }
}
