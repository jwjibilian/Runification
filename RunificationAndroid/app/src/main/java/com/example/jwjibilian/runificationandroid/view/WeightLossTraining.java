package com.example.jwjibilian.runificationandroid.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.jwjibilian.runificationandroid.R;
import com.example.jwjibilian.runificationandroid.controller.Sonification;
import com.example.jwjibilian.runificationandroid.model.User;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class WeightLossTraining extends AppCompatActivity {
    private static final String TAG = "RUNIF";
    private User user = User.getInstance(this);

    private static final UUID APP_UUID = UUID.fromString("3ccbcc65-4bab-47af-ad7b-4a17126fcf1c");
    private static PebbleKit.PebbleDataReceiver pebbleDataReceiver;

    private EditText currHrTxt;
    private EditText avgHrTxt;
    private EditText lowHrTxt;
    private EditText highHrTxt;

    Sonification sonify;

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
    }

    /*****************************************
     * Start/Stop buttons
     *****************************************/
    public void onStartClick(View view){
        sonify.start();

        // Enable Stop and disable Start buttons
        Button startBtn = (Button)findViewById(R.id.startWeightLoss);
        Button stopBtn  = (Button)findViewById(R.id.stopWeightLoss);
        startBtn.setEnabled(false);
        stopBtn.setEnabled(true);
    }

    public void onStopClick(View view){
        sonify.stop();

        // Disable Stop and Enable Start buttons
        Button startBtn = (Button)findViewById(R.id.startWeightLoss);
        Button stopBtn  = (Button)findViewById(R.id.stopWeightLoss);
        startBtn.setEnabled(true);
        stopBtn.setEnabled(false);
    }

    /*****************************************
     * Activity Managers
     *****************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_loss_training);

        // Set handles to text fields
        lowHrTxt  = (EditText)findViewById(R.id.LowHRGoal);
        highHrTxt = (EditText)findViewById(R.id.HighHRGoal);
        currHrTxt = (EditText)findViewById(R.id.CurrentHR);
        avgHrTxt  = (EditText)findViewById(R.id.avgHR);

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

        // Set Listeners for bounds text views
        lowHrTxt.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(Editable hr) {
                try {
                    int newLowHr = Integer.parseInt(hr.toString());
                    sonify.setLowHr(newLowHr);
                }
                catch (NumberFormatException e){ Log.e(TAG, e.getMessage());}
            }
        });

        highHrTxt.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(Editable hr) {
                try {
                    int newHighHr = Integer.parseInt(hr.toString());
                    sonify.setHighHr(newHighHr);
                }
                catch (NumberFormatException e){ Log.e(TAG, e.getMessage());}
            }
        });
    }

    private void updateHr(int newHr){
        currHrTxt.setText(String.valueOf(newHr));
        int avgHr = sonify.updateHr(newHr);
        avgHrTxt.setText(String.valueOf(avgHr));
    }
}
