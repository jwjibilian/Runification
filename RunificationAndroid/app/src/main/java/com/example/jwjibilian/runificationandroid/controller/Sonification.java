package com.example.jwjibilian.runificationandroid.controller;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

import com.example.jwjibilian.runificationandroid.R;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by nsebkhi3 on 7/20/2017.
 */

public class Sonification {
    private static final String TAG = "RUNIF";
    private int highHr, lowHr, avgHr;
    double alpha = 0.7;
    MediaPlayer heartbeat;

    private Handler hrSonifyTimer;
    private Runnable hrSonifyTh;

    private Handler heartbeatTimer;
    private Runnable hbThread;
    private Context context;

    public Sonification(Context context){
        this.context = context;

        // Init Pure Data
        try{initPD();}
        catch (IOException e){Log.i(TAG, e.getMessage());}

        // Heartbeat to indicate system is working
        heartbeat = MediaPlayer.create(context, R.raw.heartbeat);
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

        // Check hr values
        if (avgHr < lowHr){
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
        }
    }

    public void updateHr(int newHr){
        // Update average HR
        avgHr = (int) ((alpha * newHr) + (1.0 - alpha) * avgHr);
    }

    public void setHrParams(int lowHr, int highHr, int restHr){
        this.lowHr = lowHr;
        this.highHr = highHr;
        this.avgHr = restHr;
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
