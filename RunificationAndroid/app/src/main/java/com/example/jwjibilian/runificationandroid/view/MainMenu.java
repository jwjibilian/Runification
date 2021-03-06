package com.example.jwjibilian.runificationandroid.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.jwjibilian.runificationandroid.R;
import com.example.jwjibilian.runificationandroid.controller.PebbleConnectivity;
import com.example.jwjibilian.runificationandroid.model.User;


public class MainMenu extends AppCompatActivity {

    private PebbleConnectivity comms;

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        User user = User.getInstance(getApplicationContext());
        user.loadUser();

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
        trainingOptions.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), TrainingOptions.class);
                startActivity(myIntent);
            }
        });

        //Test Sonification Button
        Button sonifyTest = (Button) findViewById(R.id.sonifyTestBtn);
        sonifyTest.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), TestSonification.class);
                startActivity(myIntent);
            }
        });

        //this stuff is what I usded to get communications working with the watch
        comms = new PebbleConnectivity(getApplicationContext());
    }
}


