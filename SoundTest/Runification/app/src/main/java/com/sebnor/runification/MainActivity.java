package com.sebnor.runification;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.Random;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    Random rand;
    private static final String TAG = "Sebnor";
    MediaPlayer mediaPlayer;

    private EditText hr;
    private EditText minHr;
    private EditText maxHr;

    private Handler timer;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rand = new Random();
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.heartbeat);
        hr = (EditText)findViewById(R.id.hr);
        minHr = (EditText)findViewById(R.id.minEdit);
        maxHr = (EditText)findViewById(R.id.maxEdit);

        minHr.setText(String.valueOf(60));
        maxHr.setText(String.valueOf(150));

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
                    mediaPlayer.start();
                }
                else if (newHR > maxHrVal){
                    hr.setTextColor(Color.RED);
                    mediaPlayer.start();
                }
                else {
                    hr.setTextColor(Color.BLACK);
                }

                timer.postDelayed(this, 1000);
            }
        };

    }

    @Override
    public void onResume(){
        super.onResume();

    }

    public void onStartClick(View view){
        timer.postDelayed(runnable, 1000);
    }

    public void onStopClick(View view){
        timer.removeCallbacks(runnable);
    }

}

