package com.example.jwjibilian.runificationandroid.view;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.jwjibilian.runificationandroid.R;
import com.example.jwjibilian.runificationandroid.controller.Sonification;
import com.example.jwjibilian.runificationandroid.model.User;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;

public class TestSonification extends AppCompatActivity {

    private User user = User.getInstance(this);

    private double intensityPace;
    private double recoveryPace;
    private int lowHr;
    private int highHr;

    private EditText lowHrText, highHrText, currHrText;
    private EditText recoveryPaceText, intensityPaceText, currPaceText;

    private MediaPlayer intervalChange;
    private MediaPlayer trainingComplete;
    private MediaPlayer heartbeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_sonification);

        // Set Media players
        heartbeat = MediaPlayer.create(getApplicationContext(), R.raw.heartbeat);
        intervalChange   = MediaPlayer.create(getApplicationContext(), R.raw.interval_change);
        trainingComplete = MediaPlayer.create(getApplicationContext(), R.raw.training_complete);

        // Set handles to text fields
        lowHrText        = (EditText)findViewById(R.id.LowHRGoal);
        highHrText        = (EditText)findViewById(R.id.HighHRGoal);
        currHrText        = (EditText)findViewById(R.id.currHR);
        recoveryPaceText        = (EditText)findViewById(R.id.LowPaceGoal);
        intensityPaceText        = (EditText)findViewById(R.id.HighPaceGoal);
        currPaceText        = (EditText)findViewById(R.id.currPace);

        // Get current user info
        recoveryPace    = user.getLowPaceInterval();
        intensityPace   = user.getHighPaceInterval();
        lowHr      = user.getLowHrInterval();
        highHr     = user.getHighHrInterval();

        // Update UI text fields
        lowHrText.setText(String.valueOf(lowHr));
        highHrText.setText(String.valueOf(highHr));
        currHrText.setText(String.valueOf(user.getRestingHR()));

        recoveryPaceText.setText(String.valueOf(recoveryPace));
        intensityPaceText.setText(String.valueOf(intensityPace));
        currPaceText.setText("10.0");

        // Init Pure Data
        try{initPD();}
        catch (IOException e){
            Log.i("TEST_RUN", e.getMessage());}
        PdAudio.startAudio(getApplicationContext());
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        PdAudio.stopAudio();
    }

    /***********************
     * Button clicked
     **********************/
    public void onTestHrClick(View view){
        float level = 0.0f;
        PdBase.sendFloat("underHr", 0.0f);
        PdBase.sendFloat("overHr", 0.0f);
        PdBase.sendFloat("alertLevel", 0.0f);
        int currHr = Integer.parseInt(currHrText.getText().toString());
        lowHr = Integer.parseInt(lowHrText.getText().toString());
        highHr = Integer.parseInt(highHrText.getText().toString());

        if (currHr < lowHr){
            float diff = lowHr - currHr;

            if      (diff < 10){level = 1.0f;}
            else if (diff < 20){level = 2.0f;}
            else if (diff < 30){level = 3.0f;}
            else if (diff < 40){level = 4.0f;}
            else               {level = 5.0f;}

            PdBase.sendFloat("underHr", 1.0f);
        }
        else if (currHr > highHr){
            float diff = currHr - highHr;

            if      (diff < 10){level = 1.0f;}
            else if (diff < 20){level = 2.0f;}
            else if (diff < 30){level = 3.0f;}
            else if (diff < 40){level = 4.0f;}
            else               {level = 5.0f;}

            PdBase.sendFloat("overHr", 1.0f);
        }

        PdBase.sendFloat("alertLevel", level);
    }

    public void onTestPaceClick(View view){
        float level = 0.0f;
        PdBase.sendFloat("underPace", 0.0f);
        PdBase.sendFloat("overPace", 0.0f);
        PdBase.sendFloat("alertPaceLevel", 0.0f);
        double currPace = Double.parseDouble(currPaceText.getText().toString());
        recoveryPace = Double.parseDouble(recoveryPaceText.getText().toString());
        intensityPace = Double.parseDouble(intensityPaceText.getText().toString());

        if (currPace < recoveryPace){
            double diff = recoveryPace - currPace;

            if      (diff < 0.5){level = 1.0f;}
            else if (diff < 1.0){level = 2.0f;}
            else if (diff < 1.5){level = 3.0f;}
            else if (diff < 2.0){level = 4.0f;}
            else               {level = 5.0f;}

            PdBase.sendFloat("overPace", 1.0f);
        }
        else if (currPace > intensityPace){
            double diff = currPace - intensityPace;

            if      (diff < 0.5){level = 1.0f;}
            else if (diff < 1.0){level = 2.0f;}
            else if (diff < 1.5){level = 3.0f;}
            else if (diff < 2.0){level = 4.0f;}
            else               {level = 5.0f;}

            PdBase.sendFloat("underPace", 1.0f);
        }

        PdBase.sendFloat("alertPaceLevel", level);
    }

    public void onHeartbeatClick(View view){
        heartbeat.start();
    }

    public void onIntervalChangeClick(View view){
        intervalChange.start();
    }

    public void onTrainingCompleteClick(View view){
        trainingComplete.start();
    }

    public void onStopClick(View view){
        PdBase.sendFloat("overHr", 0.0f);
        PdBase.sendFloat("underHr", 0.0f);
        PdBase.sendFloat("alertLevel", 0.0f);

        PdBase.sendFloat("overPace", 0.0f);
        PdBase.sendFloat("underPace", 0.0f);
        PdBase.sendFloat("alertPaceLevel", 0.0f);
    }

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
        File overRecoveryPacePd = new File(dir, "over_pace.pd");
        File underIntensityPacePd = new File(dir, "under_pace.pd");
        PdBase.openPatch(overHrPd);
        PdBase.openPatch(underHrPd);
        PdBase.openPatch(overRecoveryPacePd);
        PdBase.openPatch(underIntensityPacePd);
    }

}
