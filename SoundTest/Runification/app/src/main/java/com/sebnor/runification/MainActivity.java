package com.sebnor.runification;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

public class MainActivity extends AppCompatActivity {
    Random rand;
    private static final String TAG = "Sebnor";
    MediaPlayer mediaPlayer;

    private EditText hr;
    private EditText minHr;
    private EditText maxHr;

    private int prevHr;

    private Handler timer;
    private Runnable runnable;

    /**********************
     * Initialize Pure Data
     ***********************/
    private void initPD() throws IOException {
        int sampleRate = AudioParameters.suggestSampleRate();
        PdAudio.initAudio(sampleRate, 0, 2, 8, true);
    }

    private void loadPdPatch() throws IOException {
        File dir = getFilesDir();
        IoUtils.extractZipResource(getResources().openRawResource(R.raw.pd_sonification), dir, true);
        File overHrPd = new File(dir, "over_hr.pd");
        File underHrPd = new File(dir, "under_hr.pd");
//        PdBase.openPatch(pdPatch.getAbsolutePath());
        PdBase.openPatch(overHrPd);
        PdBase.openPatch(underHrPd);
    }

    private void processHr(int newHr){

        int minHrVal = Integer.parseInt(minHr.getText().toString());
        int maxHrVal = Integer.parseInt(maxHr.getText().toString());

        // Set pd parameters
        PdBase.sendFloat("overHr", 0.0f);
        PdBase.sendFloat("underHr", 0.0f);

        float level = 0.0f;

        // Check hr values
        if (newHr < minHrVal){
            hr.setTextColor(Color.BLUE);
            float diff = minHrVal - newHr;

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
        else if (newHr > maxHrVal){
            hr.setTextColor(Color.RED);

            float diff = newHr - maxHrVal;

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
            hr.setTextColor(Color.BLACK);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.heartbeat);

        // Set initial state of Heart rates values
        hr = (EditText)findViewById(R.id.hr);
        minHr = (EditText)findViewById(R.id.minEdit);
        maxHr = (EditText)findViewById(R.id.maxEdit);

        prevHr = 100;
        hr.setText(String.valueOf(prevHr));
        minHr.setText(String.valueOf(60));
        maxHr.setText(String.valueOf(150));

        // Set a text listener for heart rate values
        hr.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(Editable hr) {
                int newHr;

                try { newHr = Integer.parseInt(hr.toString());}
                catch (NumberFormatException e){ newHr = prevHr;}

                prevHr = newHr;
                processHr(newHr);
            }
        });


        // Init Pure Data
        try{
            initPD();
            loadPdPatch();
        } catch (IOException e){
            Log.i(TAG, e.getMessage());
        }

        // Init timer
//        timer = new Handler();

//        runnable = new Runnable() {
//            @Override
//            public void run() {
//
//                timer.postDelayed(this, 1000);
//            }
//        };
    }

    @Override
    public void onResume(){
        super.onResume();
//        PdAudio.startAudio(this);
    }

    @Override
    protected void onPause(){
        super.onPause();
        PdAudio.stopAudio();
    }

    public void onStartClick(View view){
//        timer.postDelayed(runnable, 1000);
        PdAudio.startAudio(this);
    }

    public void onStopClick(View view){
//        timer.removeCallbacks(runnable);
        PdAudio.stopAudio();
    }

}

