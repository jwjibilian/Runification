package com.example.jwjibilian.runificationandroid.view;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import org.xml.sax.HandlerBase;

import java.text.DecimalFormat;
import java.util.UUID;
import java.util.concurrent.RunnableFuture;

public class IntervalTraining extends AppCompatActivity {
    private static final String TAG = "RUNIF";
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    private User user = User.getInstance(this);
    Sonification sonify;

    private static final UUID APP_UUID = UUID.fromString("3ccbcc65-4bab-47af-ad7b-4a17126fcf1c");
    private static PebbleKit.PebbleDataReceiver pebbleDataReceiver;

    private int totalDuration;
    private int intervalDuration;
    private double recoveryPace;
    private int recoveryHr;
    private double intensityPace;
    private int intensityHr;
    LocationManager lm;

    private EditText totalDurText;
    private EditText intervalDurText;
    private EditText recoveryPaceTxt;
    private EditText recoveryHrTxt;
    private EditText intensityPaceTxt;
    private EditText intensityHrTxt;
    private EditText currHrTxt;
    private EditText currPaceTxt;
    private TextView intervalIdxTxt;
    private TextView intervalTotalTxt;

    private Handler intervalTimer;
    private Runnable intervalTh;
    private int numIntervals;
    private int intervalCnter;
    private TrainingMode currMode;

    private Pace paceReader;
    private Handler paceReadTimer;
    private Runnable paceReadTh;
    private DataRecord dr;
    private int newHr;

    private Handler recorder;
    private Runnable recorderTh;
    DecimalFormat formatter = new DecimalFormat("#0.00");

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
        sonify.setHrParams(recoveryHr, intensityHr, user.getRestingHR());
        sonify.setPaceParams(recoveryPace, intensityPace);
        sonify.setMode(TrainingMode.INTERVAL_RECOVERY);
    }

    /*****************************************
     * Start/Stop buttons
     *****************************************/
    public void onStartClick(View view){
        sonify.start();


        // Set durations
        totalDuration = Integer.parseInt(totalDurText.getText().toString());
        intervalDuration = Integer.parseInt(intervalDurText.getText().toString());
        numIntervals = totalDuration / intervalDuration;
        intervalIdxTxt.setText(String.valueOf(intervalCnter));
        intervalTotalTxt.setText(String.valueOf(numIntervals));

        // Set Pace/Hr values
        recoveryPace = Double.parseDouble(recoveryPaceTxt.getText().toString());
        recoveryHr   = Integer.parseInt(recoveryHrTxt.getText().toString());
        intensityPace = Double.parseDouble(intensityPaceTxt.getText().toString());
        intensityHr   = Integer.parseInt(intensityHrTxt.getText().toString());
        sonify.setPaceParams(recoveryPace, intensityPace);
        sonify.setHrParams(recoveryHr, intensityHr, user.getRestingHR());
        dr = new DataRecord(getApplicationContext(),this, "Int");
        // Start Interval creation
        intervalCnter = 1;
        intervalTh = new Runnable() {
            @Override
            public void run() {
                intervalCnter++;

                if (intervalCnter > numIntervals) {
                    sonify.playTrainingComplete();
                    stopSession();
                    return;
                }

                // Toggle training mode
                if (currMode == TrainingMode.INTERVAL_INTENSITY){
                    currMode = TrainingMode.INTERVAL_RECOVERY;
                }
                else {
                    currMode = TrainingMode.INTERVAL_INTENSITY;
                }
                sonify.setMode(currMode);
                sonify.playIntervalChange();
                intervalIdxTxt.setText(String.valueOf(intervalCnter));
                intervalTimer.postDelayed(this, 60000*intervalDuration);
            }
        };
        intervalTimer.postDelayed(intervalTh, 60000*intervalDuration);
        currMode = TrainingMode.INTERVAL_RECOVERY;
        intervalIdxTxt.setText(String.valueOf(intervalCnter));

        //start data recoder
        recorder.postDelayed(recorderTh,0);

        // Enable Stop and disable Start buttons
        Button startBtn = (Button)findViewById(R.id.intervalStartBtn);
        Button stopBtn  = (Button)findViewById(R.id.intervalStopBtn);
        startBtn.setEnabled(false);
        stopBtn.setEnabled(true);
    }

    public void onStopClick(View view){
        stopSession();
    }

    public void stopSession(){
        sonify.stop();
        intervalTimer.removeCallbacks(intervalTh);
        recorder.removeCallbacks(recorderTh);
        dr.close();
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
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        } else {
            //Permission is granted
        }

        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        // Set handles to text fields
        totalDurText        = (EditText)findViewById(R.id.totalDurTxt);
        intervalDurText     = (EditText)findViewById(R.id.intervalDurText);
        recoveryPaceTxt     = (EditText)findViewById(R.id.maxRecoveryPaceText);
        recoveryHrTxt       = (EditText)findViewById(R.id.maxRecoveryHrText);
        intensityPaceTxt    = (EditText)findViewById(R.id.minIntensityPaceText);
        intensityHrTxt      = (EditText)findViewById(R.id.minIntensityHrText);
        currHrTxt           = (EditText)findViewById(R.id.currHrText);
        currPaceTxt         = (EditText)findViewById(R.id.currPaceText);
        intervalIdxTxt      = (TextView)findViewById(R.id.currIntervalIdx);
        intervalTotalTxt    = (TextView)findViewById(R.id.totalNumIntervals);

        sonify = new Sonification(this.getApplicationContext());
        loadUserInfo();

        // Create Data receiver for Pebble
        pebbleDataReceiver= new PebbleKit.PebbleDataReceiver(APP_UUID) {
            public void receiveData(Context context, int transactionId, PebbleDictionary dict) {
                PebbleKit.sendAckToPebble(context, transactionId);

                // Get and Update HR value
                newHr = dict.getInteger(0).intValue();
                updateHr(newHr);
            }
        };
        PebbleKit.registerReceivedDataHandler(getApplicationContext(), pebbleDataReceiver);

        // Read Pace data
        paceReader = new Pace(getApplicationContext(),lm);
        paceReader.getPaceUpdateText(currPaceTxt);

        paceReadTimer = new Handler();
        paceReadTh = new Runnable() {
            @Override
            public void run() {
                updatePace(paceReader.getPace());
                paceReadTimer.postDelayed(this, 1000);  // Read pace every second
            }
        };
        paceReadTimer.postDelayed(paceReadTh, 0);   // start reading pace right away

        // Set timer
        intervalTimer = new Handler();

        recorder = new Handler();
        recorderTh = new Runnable() {
            @Override
            public void run() {
                dr.save(formatter.format(paceReader.getPace())+","+formatter.format(newHr)+"\n");
                recorder.postDelayed(this, 4000);
            }
        };


    }

    private void updateHr(int newHr){
        currHrTxt.setText(String.valueOf(newHr));
        sonify.updateHr(newHr);
    }

    private void updatePace(double newPace){
        sonify.updatePace(newPace);
    }
}
