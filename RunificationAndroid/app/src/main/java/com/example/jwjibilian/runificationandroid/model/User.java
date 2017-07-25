package com.example.jwjibilian.runificationandroid.model;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by jwjibilian on 7/20/17.
 */

public class User {
    private static final User ourInstance = new User();
    private final String filename = "currentUser.txt";
    private static Context context;

    // General Info
    private String name;
    private int gender;
    private int age;
    private int level;

    // Hear Rate
    private int restingHR;
    private int lowHrWeightLoss;
    private int highHrWeightLoss;
    private int lowHrInterval;
    private int highHrInterval;

    // Pace
    private int racePace;
    private int lowPaceInterval;
    private int highPaceInterval;


    public static User getInstance(Context theContext) {
        context = theContext;
        return ourInstance;
    }

    private User() {
        this.name = "";
        this.gender = 0;
        this.age = 0;
        this.level = 0;
        this.restingHR = 70;
        this.lowHrWeightLoss = 0;
        this.highHrWeightLoss = 0;
        this.lowHrInterval = 0;
        this.highHrInterval = 0;
        this.lowPaceInterval = 0;
        this.highPaceInterval = 0;
        this.racePace = 10;
    }

    public void loadUser(){
        try {

            InputStream is = context.openFileInput(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            setName(reader.readLine());
            setGender(Integer.parseInt(reader.readLine()));
            setAge(Integer.parseInt(reader.readLine()));
            setLevel(Integer.parseInt(reader.readLine()));
            setRestingHR(Integer.parseInt(reader.readLine()));
            setLowHrWeightLoss(Integer.parseInt(reader.readLine()));
            setHighHrWeightLoss(Integer.parseInt(reader.readLine()));
            setLowHrInterval(Integer.parseInt(reader.readLine()));
            setHighHrInterval(Integer.parseInt(reader.readLine()));
            setLowPaceInterval(Integer.parseInt(reader.readLine()));
            setHighPaceInterval(Integer.parseInt(reader.readLine()));
            setRacePace(Integer.parseInt(reader.readLine()));
        }
        catch (FileNotFoundException e){
            Log.e("ULOADERR", "File Not Found, Creating a first user");
            saveUser();
        }
        catch (Exception e){
            Log.e("ULOADERR", e.getMessage());
            saveUser();
        }
    }

    public void saveUser(){
        File file = new File(context.getFilesDir(), filename);
        FileOutputStream outputStream;
        String string = name + "\n" + gender + "\n" + age + "\n" + level + "\n" + restingHR + "\n"
                + lowHrWeightLoss + "\n" + highHrWeightLoss + "\n"
                + lowHrInterval + "\n" + highHrInterval + "\n"
                + lowPaceInterval + "\n" + highPaceInterval+ "\n" + racePace + "\n";
        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.d("saveusererror", e.toString());
        }
    }

    public String getName() {
        return name;
    }

    public int getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }

    public int getLevel() {
        return level;
    }

    public int getRestingHR() {
        return restingHR;
    }

    public int getLowHrWeightLoss() {
        return lowHrWeightLoss;
    }

    public int getHighHrWeightLoss() {
        return highHrWeightLoss;
    }

    public int getLowHrInterval() {
        return lowHrInterval;
    }

    public int getHighHrInterval() {
        return highHrInterval;
    }

    public int getLowPaceInterval() {
        return lowPaceInterval;
    }

    public int getHighPaceInterval() {
        return highPaceInterval;
    }

    public int getRacePace() {
        return racePace;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setRestingHR(int restingHR) {
        this.restingHR = restingHR;
    }

    public void setLowHrWeightLoss(int lowHrWeightLoss) {
        this.lowHrWeightLoss = lowHrWeightLoss;
    }

    public void setHighHrWeightLoss(int highHrWeightLoss) {
        this.highHrWeightLoss = highHrWeightLoss;
    }

    public void setLowHrInterval(int lowHrInterval) {
        this.lowHrInterval = lowHrInterval;
    }

    public void setHighHrInterval(int highHrInterval) {
        this.highHrInterval = highHrInterval;
    }

    public void setLowPaceInterval(int lowPaceInterval) {
        this.lowPaceInterval = lowPaceInterval;
    }

    public void setHighPaceInterval(int highPaceInterval) {
        this.highPaceInterval = highPaceInterval;
    }

    public void setRacePace(int racePace) {
        this.racePace = racePace;
    }
}



