package com.example.jwjibilian.runificationandroid.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.jwjibilian.runificationandroid.R;
import com.example.jwjibilian.runificationandroid.model.User;

public class UserInfo extends AppCompatActivity {
    EditText uName;
    Spinner gender;
    EditText age;
    Spinner level;
    EditText restingHR;
    EditText racePace;
    User u;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        gender = (Spinner) findViewById(R.id.GenderSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.genders_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapter);

        level = (Spinner) findViewById(R.id.LevelSlider);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.level_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        level.setAdapter(adapter2);

        uName       = (EditText) findViewById(R.id.UserName);
        age         = (EditText) findViewById(R.id.AgeText);
        restingHR   = (EditText) findViewById(R.id.RestingHRText);
        racePace    = (EditText) findViewById(R.id.racePaceText);

        u = User.getInstance(getApplicationContext());

        // Set all fields from saved user info
        u.loadUser();
//        Log.i("USER_INFO","Name: " + u.getName());
//        Log.i("USER_INFO","Gender: " + String.valueOf(u.getGender()));
//        Log.i("USER_INFO","Age: " + String.valueOf(u.getAge()));
//        Log.i("USER_INFO","Level: " + String.valueOf(u.getLevel()));
//        Log.i("USER_INFO","Rest HR: " + String.valueOf(u.getRestingHR()));
//        Log.i("USER_INFO","Race PAce: " + String.valueOf(u.getRacePace()));
        uName.setText(u.getName());
        gender.setSelection(u.getGender());
        age.setText(String.valueOf(u.getAge()));
        level.setSelection(u.getLevel());
        restingHR.setText(String.valueOf(u.getRestingHR()));
        racePace.setText(String.valueOf(u.getRacePace()));

        Button save = (Button) findViewById(R.id.SaveButton);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                u.setAge(Integer.parseInt(age.getText().toString()));
                u.setRestingHR(Integer.parseInt(restingHR.getText().toString()));
                u.setAge(Integer.parseInt(age.getText().toString()));
                u.setLevel(level.getSelectedItemPosition());
                u.setName(uName.getText()+"");
                u.setGender(gender.getSelectedItemPosition());

                // Compute HR params
                int maxHr = 211 - (int)(0.64 * u.getAge());
                int reserveHr = maxHr - u.getRestingHR();

                // Compute Weight Loss HR
                int lowHrWeightLoss  = (int)(0.5 * reserveHr) + u.getRestingHR();
                int highHrWeightLoss = (int)(0.6 * reserveHr) + u.getRestingHR();
                u.setLowHrWeightLoss(lowHrWeightLoss);
                u.setHighHrWeightLoss(highHrWeightLoss);

                // Compute Interval HR
                int lowHrInterval  = (int)(0.65 * reserveHr) + u.getRestingHR();
                int highHrInterval = (int)(0.75 * reserveHr) + u.getRestingHR();
                u.setLowHrInterval(lowHrInterval);
                u.setHighHrInterval(highHrInterval);

                // Compute Interval paces
                int racePaceVal = Integer.parseInt(racePace.getText().toString());
                u.setRacePace(racePaceVal);
                u.setHighPaceInterval((int)(racePaceVal / 1.03));
                u.setLowPaceInterval(10);

                // Save user info into internal storage (file)
                u.saveUser();
            }
        });
    }

}
