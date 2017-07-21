package com.example.jwjibilian.runificationandroid.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    EditText feet;
    EditText inches;
    EditText age;
    EditText weight;
    Spinner level;
    EditText restingHR;
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

        uName = (EditText) findViewById(R.id.UserName);
        feet = (EditText) findViewById(R.id.FeetText);
        inches = (EditText) findViewById(R.id.InchText);
        age = (EditText) findViewById(R.id.AgeText);
        weight = (EditText) findViewById(R.id.WeightText);
        restingHR = (EditText) findViewById(R.id.RestingHRText);

        u = User.getInstance(getApplicationContext());
        u.loadUser();
        uName.setText(u.getName());

        level.setSelection(u.getLevel());
        int height = u.getHeight();
        feet.setText((height/12) + "");
        inches.setText((height%12) + "");
        weight.setText(u.getWeight() + "");
        restingHR.setText(u.getRestingHR() + "");
        gender.setSelection(u.getGender());
        age.setText(u.getAge() +"");

        Button save = (Button) findViewById(R.id.SaveButton);
        save.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                u.setAge(Integer.parseInt(age.getText() + ""));
                u.setRestingHR(Integer.parseInt(restingHR.getText() + ""));
                u.setAge(Integer.parseInt(age.getText() + ""));
                u.setWeight(Float.parseFloat(weight.getText()+""));
                u.setLevel(level.getSelectedItemPosition());
                u.setName(uName.getText()+"");
                int height = (Integer.parseInt(feet.getText() + "") * 12) + Integer.parseInt(inches.getText()+"");
                u.setHeight(height);
                u.setGender(Integer.parseInt(gender.getSelectedItemPosition()+""));
                u.saveUser();
            }
        });
    }

}
