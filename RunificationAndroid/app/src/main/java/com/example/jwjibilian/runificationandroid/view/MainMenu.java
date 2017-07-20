package com.example.jwjibilian.runificationandroid.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.jwjibilian.runificationandroid.R;
import com.example.jwjibilian.runificationandroid.controller.PebbleConnectivity;
import com.example.jwjibilian.runificationandroid.model.User;

public class MainMenu extends AppCompatActivity {


    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    private String info = "TestComms";
    private static TextView HR;
    private PebbleConnectivity comms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        //comms = new PebbleConnectivity(getApplicationContext(), HR);
        //comms.startApp(getApplicationContext());
        //comms.startListener();

        User user = User.getInstance(getApplicationContext());

        //User Info Button
        Button userInfo = (Button) findViewById(R.id.UserInfo);
        userInfo.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), UserInfo.class);
                startActivity(myIntent);
            }
        });


        //Training Button
        Button trainingOptions = (Button) findViewById(R.id.SetTrainingButton);
        trainingOptions.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), TrainingOptions.class);
                startActivity(myIntent);
            }
        });


        //this stuff is what I usded to get communications working with the watch
/*        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        Log.d(info, "MakingAThing" );
        try{
            HR = (TextView) findViewById(R.id.heartRateID);
        } catch ( Exception e) {
            Log.d("TextViewHR", e.getMessage());

        }
    comms = new PebbleConnectivity(getApplicationContext(), HR);
    comms.startApp(getApplicationContext());*/


    }

    @Override
    protected void onResume() {
        super.onResume();
        //Starts the communications with the watch
        //comms.startListener();


    }


}


