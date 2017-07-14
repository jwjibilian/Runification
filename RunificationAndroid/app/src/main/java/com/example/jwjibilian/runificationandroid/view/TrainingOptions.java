package com.example.jwjibilian.runificationandroid.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.jwjibilian.runificationandroid.R;

public class TrainingOptions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_options);


        Button WLossButton = (Button) findViewById(R.id.WLossButton);
        WLossButton.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), WeightLossTraining.class);
                startActivity(myIntent);
            }
        });




        Button ITrainingButton= (Button) findViewById(R.id.IntervalTrainingButton);
        ITrainingButton.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), IntervalTraining.class);
                startActivity(myIntent);
            }
        });


    }
}
