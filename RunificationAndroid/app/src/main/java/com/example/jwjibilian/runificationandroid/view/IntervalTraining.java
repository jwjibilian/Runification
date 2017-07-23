package com.example.jwjibilian.runificationandroid.view;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.jwjibilian.runificationandroid.R;
import com.example.jwjibilian.runificationandroid.controller.Sonification;
import com.example.jwjibilian.runificationandroid.model.User;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.UUID;

public class IntervalTraining extends AppCompatActivity {
    private static final String TAG = "RUNIF";
    private User user = User.getInstance(this);
    Sonification sonify;

    private static final UUID APP_UUID = UUID.fromString("3ccbcc65-4bab-47af-ad7b-4a17126fcf1c");
    private static PebbleKit.PebbleDataReceiver pebbleDataReceiver;

    private int totalDuration;
    private int intervalDuration;
    private int recoveryPace;
    private int recoveryHr;
    private int intensityPace;
    private int intensityHr;

    private EditText totalDurText;
    private EditText intervalDurText;
    private EditText recoveryPaceTxt;
    private EditText recoveryHrTxt;
    private EditText intensityPaceTxt;
    private EditText intensityHrTxt;
    private EditText currHrTxt;
    private EditText currPaceTxt;

    /*****************************************
     * Load user parameters for this training
     *****************************************/
    private void loadUserInfo(){

        // Get current user info
        recoveryPace    = user.getLowPaceInterval();
        recoveryHr      = user.getLowHrInterval();
        intensityPace   = user.getHighPaceInterval();
        intensityHr     = user.getHighHrInterval();

        // Set initial duration
        totalDuration = 30;
        intervalDuration = 1;

        // Update UI text fields
        totalDurText.setText(String.valueOf(totalDuration));
        intervalDurText.setText(String.valueOf(intervalDuration));
        recoveryPaceTxt.setText(String.valueOf(recoveryPace));
        recoveryHrTxt.setText(String.valueOf(recoveryHr));
        intensityPaceTxt.setText(String.valueOf(intensityPace));
        intensityHrTxt.setText(String.valueOf(intensityHr));
        currHrTxt.setText(String.valueOf(user.getRestingHR()));
        currPaceTxt.setText(String.valueOf(0));

        // Update Sonification Mgr
//        sonify.setHrParams(lowHr, highHr, restHr);
    }

    /*****************************************
     * Start/Stop buttons
     *****************************************/
    public void onStartClick(View view){
        sonify.start();

        // Enable Stop and disable Start buttons
        Button startBtn = (Button)findViewById(R.id.intervalStartBtn);
        Button stopBtn  = (Button)findViewById(R.id.intervalStopBtn);
        startBtn.setEnabled(false);
        stopBtn.setEnabled(true);
    }

    public void onStopClick(View view){
        sonify.stop();

        // Disable Stop and Enable Start buttons
        Button startBtn = (Button)findViewById(R.id.intervalStartBtn);
        Button stopBtn  = (Button)findViewById(R.id.intervalStopBtn);
        startBtn.setEnabled(true);
        stopBtn.setEnabled(false);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interval_training);

        // Set handles to text fields
        totalDurText  = (EditText)findViewById(R.id.totalDurTxt);
        intervalDurText  = (EditText)findViewById(R.id.intervalDurText);
        recoveryPaceTxt  = (EditText)findViewById(R.id.maxRecoveryPaceText);
        recoveryHrTxt  = (EditText)findViewById(R.id.maxRecoveryHrText);
        intensityPaceTxt  = (EditText)findViewById(R.id.minIntensityPaceText);
        intensityHrTxt  = (EditText)findViewById(R.id.minIntensityHrText);
        currHrTxt  = (EditText)findViewById(R.id.currHrText);
        currPaceTxt  = (EditText)findViewById(R.id.currPaceText);

        sonify = new Sonification(this.getApplicationContext());
        loadUserInfo();

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

    }

    private void updateHr(int newHr){
        currHrTxt.setText(String.valueOf(newHr));
        sonify.updateHr(newHr);
    }
}
