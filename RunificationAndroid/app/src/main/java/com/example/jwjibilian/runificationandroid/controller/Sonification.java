package com.example.jwjibilian.runificationandroid.controller;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

import com.example.jwjibilian.runificationandroid.R;
import com.getpebble.android.kit.PebbleKit;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by nsebkhi3 on 7/20/2017.
 */

public class Sonification {
    private static final String TAG = "RUNIF";
    TrainingMode mode;

    private int highHr, lowHr, avgHr;
    double alpha = 0.5;
    MediaPlayer heartbeat;
    MediaPlayer intervalChange;
    MediaPlayer trainingComplete;

    private Handler hrSonifyTimer;
    private Runnable hrSonifyTh;

    private Handler heartbeatTimer;
    private Runnable hbThread;
    private Context context;

    public Sonification(Context context){
        this.context = context;
        mode = TrainingMode.WEIGHT_LOSS;    // Provide a default mode for compatibility

        // Init Pure Data
        try{initPD();}
        catch (IOException e){Log.i(TAG, e.getMessage());}

        // Heartbeat to indicate system is working
        heartbeat = MediaPlayer.create(context.getApplicationContext(), R.raw.heartbeat);
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

        // Set Media player
        intervalChange   = MediaPlayer.create(context.getApplicationContext(), R.raw.interval_change);
        trainingComplete = MediaPlayer.create(context.getApplicationContext(), R.raw.training_complete);
    }

    /**********************
     * Initialize Pure Data
     ***********************/
    private void initPD() throws IOException {
        // Init audio context
        int sampleRate = AudioParameters.suggestSampleRate();
        PdAudio.initAudio(sampleRate, 0, 2, 8, true);

        // Load PD patches
        File dir = context.getFilesDir();
        IoUtils.extractZipResource(context.getResources().openRawResource(R.raw.pd_sonification), dir, true);
        File overHrPd = new File(dir, "over_hr.pd");
        File underHrPd = new File(dir, "under_hr.pd");
        PdBase.openPatch(overHrPd);
        PdBase.openPatch(underHrPd);
    }

    private void processHr(){
        // initialize pd parameters
        float level = 0.0f;
        PdBase.sendFloat("overHr", 0.0f);
        PdBase.sendFloat("underHr", 0.0f);

        if (mode == TrainingMode.WEIGHT_LOSS){

            if (avgHr < lowHr){
                float diff = lowHr - avgHr;

                if      (diff < 10){level = 1.0f;}
                else if (diff < 20){level = 2.0f;}
                else if (diff < 30){level = 3.0f;}
                else if (diff < 40){level = 4.0f;}
                else               {level = 5.0f;}

                PdBase.sendFloat("underHr", 1.0f);
            }
            else if (avgHr > highHr){
                float diff = avgHr - highHr;

                if      (diff < 10){level = 1.0f;}
                else if (diff < 20){level = 2.0f;}
                else if (diff < 30){level = 3.0f;}
                else if (diff < 40){level = 4.0f;}
                else               {level = 5.0f;}

                PdBase.sendFloat("overHr", 1.0f);
            }
            else {}
        }
        else if (mode == TrainingMode.INTERVAL_RECOVERY){
            // Case = Higher than recovery
            if (avgHr > lowHr){
                float diff = avgHr - lowHr;

                if      (diff < 10){level = 1.0f;}
                else if (diff < 20){level = 2.0f;}
                else if (diff < 30){level = 3.0f;}
                else if (diff < 40){level = 4.0f;}
                else               {level = 5.0f;}

                PdBase.sendFloat("overHr", 1.0f);
            }
        }
        else if (mode == TrainingMode.INTERVAL_INTENSITY){
            // Case = Lower than intensity
            if (avgHr < highHr){
                float diff = highHr - avgHr;

                if      (diff < 10){level = 1.0f;}
                else if (diff < 20){level = 2.0f;}
                else if (diff < 30){level = 3.0f;}
                else if (diff < 40){level = 4.0f;}
                else               {level = 5.0f;}

                PdBase.sendFloat("underHr", 1.0f);
            }
        }

        PdBase.sendFloat("alertLevel", level);
    }

    public int updateHr(int newHr){
        // Update average HR
        avgHr = (int) ((alpha * newHr) + (1.0 - alpha) * avgHr);
        return avgHr;
    }

    public void setHrParams(int lowHr, int highHr, int restHr){
        this.lowHr = lowHr;
        this.highHr = highHr;
        this.avgHr = restHr;
    }

    public void setLowHr(int lowHr){
        this.lowHr = lowHr;
    }

    public void setHighHr(int highHr){
        this.highHr = highHr;
    }

    public void setMode(TrainingMode mode) {
        this.mode = mode;
    }

    public void playIntervalChange(){
        intervalChange.start();
    }

    public void playTrainingComplete(){
        trainingComplete.start();
    }

    public void start(){
        heartbeatTimer.postDelayed(hbThread, 10);     // Indicate if syst is working right away
        hrSonifyTimer.postDelayed(hrSonifyTh, 60000); // Give 1 minute for HR to stabilize
        PdAudio.startAudio(context);
    }

    public void stop(){
        heartbeatTimer.removeCallbacks(hbThread);
        hrSonifyTimer.removeCallbacks(hrSonifyTh);
        PdAudio.stopAudio();
    }
}
