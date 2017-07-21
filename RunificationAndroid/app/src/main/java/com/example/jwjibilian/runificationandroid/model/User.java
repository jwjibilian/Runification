package com.example.jwjibilian.runificationandroid.model;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
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
    private String name;
    private int gender;
    private int height;
    private int age;
    private float weight;
    private int level;
    private int restingHR;
    private static Context context;

    public static User getInstance(Context theContext) {

        context = theContext;
        return ourInstance;
    }

    private User() {
        this.name = "";
        this.gender = 0;
        this.height = 0;
        this.age = 0;
        this.level = 0;
        this.restingHR = 70;
        this.weight = 0;
    }


    public String getName() {
        return name;
    }

    public int getGender() {
        return gender;
    }

    public int getHeight() {
        return height;
    }

    public int getAge() {
        return age;
    }

    public float getWeight() {
        return weight;
    }

    public int getLevel() {
        return level;
    }

    public int getRestingHR() {
        return restingHR;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setRestingHR(int restingHR) {
        this.restingHR = restingHR;
    }
    public void loadUser(){
        try {
            String str = "";
            StringBuffer buf = new StringBuffer();
            InputStream is = context.openFileInput(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
/*            while ((str = reader.readLine()) != null) {
                buf.append(str + "\n" );
            }*/
            setName(reader.readLine());
            setGender(Integer.parseInt(reader.readLine()));
            setHeight(Integer.parseInt(reader.readLine()));
            setAge(Integer.parseInt(reader.readLine()));
            setWeight(Float.parseFloat(reader.readLine()));
            setLevel(Integer.parseInt(reader.readLine()));
            setRestingHR(Integer.parseInt(reader.readLine()));
        } catch (Exception e){
            Log.e("ULOADERR", e.getMessage());

        }

        
    }

    public void saveUser(){
        FileOutputStream outputStream;
        String string = name + "\n" + gender + "\n" + height + "\n" + age + "\n" + weight + "\n"
                + level + "\n" + restingHR + "\n";
        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }




}


