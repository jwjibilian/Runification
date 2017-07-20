package com.example.jwjibilian.runificationandroid.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.jwjibilian.runificationandroid.R;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;

public class WeightLossTraining extends AppCompatActivity {
    private static final String TAG = "RUNIF";
    public static final String USER_PREF = "User";
    private int highHr, lowHr, avgHr;
    double alpha = 0.7;
    MediaPlayer heartbeat;

    private EditText currHrTxt;
    private EditText lowHrTxt;
    private EditText highHrTxt;

    private Handler heartbeatTimer;
    private Runnable hbThread;

    private Handler hrSonifyTimer;
    private Runnable hrSonifyTh;

    /**********************
     * Initialize Pure Data
     ***********************/
    private void initPD() throws IOException {
        // Init audio context
        int sampleRate = AudioParameters.suggestSampleRate();
        PdAudio.initAudio(sampleRate, 0, 2, 8, true);

        // Load PD patches
        File dir = getFilesDir();
        IoUtils.extractZipResource(getResources().openRawResource(R.raw.pd_sonification), dir, true);
        File overHrPd = new File(dir, "over_hr.pd");
        File underHrPd = new File(dir, "under_hr.pd");
        PdBase.openPatch(overHrPd);
        PdBase.openPatch(underHrPd);
    }

    /*****************************************
     * Load user parameters for this training
     *****************************************/
    private void loadHrParams(){
        // Get current username
        SharedPreferences currentUser = getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        String username = currentUser.getString("username", null);

        // Get HR bounds for this user
        SharedPreferences userInfo = getSharedPreferences(username, Context.MODE_PRIVATE);
        avgHr = userInfo.getInt("rest_hr", 0);
        lowHr  = userInfo.getInt("low_hr_beginner", 0);
        highHr = userInfo.getInt("high_hr_beginner", 0);

        // Update text fields of hr bounds
        lowHrTxt.setText(String.valueOf(lowHr));
        highHrTxt.setText(String.valueOf(highHr));
        currHrTxt.setText(String.valueOf(avgHr));
    }

    /*****************************************
     * Start/Stop buttons
     *****************************************/
    public void onStartClick(View view){
        heartbeatTimer.postDelayed(hbThread, 10);     // Indicate if syst is working right away
        hrSonifyTimer.postDelayed(hrSonifyTh, 60000); // Give 1 minute for HR to stabilize
        PdAudio.startAudio(this);

        // Enable Stop and disable Start buttons
        Button startBtn = (Button)findViewById(R.id.startWeightLoss);
        Button stopBtn  = (Button)findViewById(R.id.stopWeightLoss);
        startBtn.setEnabled(false);
        stopBtn.setEnabled(true);
    }

    public void onStopClick(View view){
        heartbeatTimer.removeCallbacks(hbThread);
        hrSonifyTimer.removeCallbacks(hrSonifyTh);
        PdAudio.stopAudio();

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

        loadHrParams();

        // Init Pure Data
        try{initPD();}
        catch (IOException e){Log.i(TAG, e.getMessage());}

        // Heartbeat to indicate system is working
        heartbeat = MediaPlayer.create(getApplicationContext(), R.raw.heartbeat);
        heartbeatTimer = new Handler();
        hbThread = new Runnable() {
            @Override
            public void run() {
                long hbDelay = 150 * 1000; // Delay btw heartbeats in ms
                heartbeat.start();
                heartbeatTimer.postDelayed(this, hbDelay);
            }
        };

        // Sonify HR
        hrSonifyTimer = new Handler();
        hrSonifyTh = new Runnable() {
            @Override
            public void run() {
                processHr();
                long hrSonifyDelay = 30 * 1000; // Delay btw sonification in ms
                hrSonifyTimer.postDelayed(this, hrSonifyDelay);
            }
        };
    }

    private void processHr(){
        // initialize pd parameters
        float level = 0.0f;
        PdBase.sendFloat("overHr", 0.0f);
        PdBase.sendFloat("underHr", 0.0f);

        // Check hr values
        if (avgHr < lowHr){
            currHrTxt.setTextColor(Color.BLUE);
            float diff = lowHr - avgHr;

            if (diff < 10){
                level = 1.0f;
            }
            else if (diff < 20){
                level = 2.0f;
            }
            else if (diff < 30){
                level = 3.0f;
            }
            else if (diff < 40){
                level = 4.0f;
            }
            else {
                level = 5.0f;
            }

            PdBase.sendFloat("alertLevel", level);
            PdBase.sendFloat("underHr", 1.0f);
        }
        else if (avgHr > highHr){
            currHrTxt.setTextColor(Color.RED);

            float diff = avgHr - highHr;

            if (diff < 10){
                level = 1.0f;
            }
            else if (diff < 20){
                level = 2.0f;
            }
            else if (diff < 30){
                level = 3.0f;
            }
            else if (diff < 40){
                level = 4.0f;
            }
            else {
                level = 5.0f;
            }

            PdBase.sendFloat("alertLevel", level);
            PdBase.sendFloat("overHr", 1.0f);
        }
        else {
            currHrTxt.setTextColor(Color.BLACK);
        }
    }

    private void updateHr(int newHr){
        // Update current hr display
        currHrTxt.setText(String.valueOf(newHr));

        // Update average HR
        avgHr = (int) ((alpha * newHr) + (1.0 - alpha) * avgHr);
    }
}
