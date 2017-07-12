package com.sebnor.runification;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;
import java.io.IOException;
import java.util.Random;
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
//    MediaPlayer mediaPlayer;

    private EditText hr;
    private EditText minHr;
    private EditText maxHr;

    private Handler timer;
    private Runnable runnable;

    private void initPD() throws IOException {
        int sampleRate = AudioParameters.suggestSampleRate();
        PdAudio.initAudio(sampleRate, 0, 2, 8, true);
    }

    private void loadPdPatch() throws IOException {
        File dir = getFilesDir();
        IoUtils.extractZipResource(getResources().openRawResource(R.raw.test_1), dir, true);
        File pdPatch = new File(dir, "test_1.pd");
        PdBase.openPatch(pdPatch.getAbsolutePath());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rand = new Random();
//        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.heartbeat);
        hr = (EditText)findViewById(R.id.hr);
        minHr = (EditText)findViewById(R.id.minEdit);
        maxHr = (EditText)findViewById(R.id.maxEdit);

        minHr.setText(String.valueOf(60));
        maxHr.setText(String.valueOf(150));

        // Init Pure Data
        try{
            initPD();
            loadPdPatch();
        } catch (IOException e){
            Log.i(TAG, e.getMessage());
        }

        // Init timer
        timer = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                int newHR = rand.nextInt(180) + 20; // 20 - 200
                Log.i(TAG, "HR = " + newHR);
                hr.setText(String.valueOf(newHR));

                int minHrVal = Integer.parseInt(minHr.getText().toString());
                int maxHrVal = Integer.parseInt(maxHr.getText().toString());

                if (newHR < minHrVal){
                    hr.setTextColor(Color.BLUE);
                    PdBase.sendFloat("onOff", 1.0f);
                }
                else if (newHR > maxHrVal){
                    hr.setTextColor(Color.RED);
                    PdBase.sendFloat("onOff", 1.0f);
                }
                else {
                    hr.setTextColor(Color.BLACK);
                    PdBase.sendFloat("onOff", 0.0f);
                }

                timer.postDelayed(this, 1000);
            }
        };
    }

    @Override
    public void onResume(){
        super.onResume();
        PdAudio.startAudio(this);
    }

    @Override
    protected void onPause(){
        super.onPause();
        PdAudio.stopAudio();
    }

    public void onStartClick(View view){
        timer.postDelayed(runnable, 1000);
    }

    public void onStopClick(View view){
        timer.removeCallbacks(runnable);
    }

}

