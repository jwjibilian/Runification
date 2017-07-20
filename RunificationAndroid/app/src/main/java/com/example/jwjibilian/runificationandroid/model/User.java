package com.example.jwjibilian.runificationandroid.model;

import java.io.Serializable;

/**
 * Created by jwjibilian on 7/20/17.
 */
public class User implements Serializable{


    public String getGender() {
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

    public void setGender(String gender) {
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

    private String name;
    private String gender;
    private int height;
    private int age;
    private float weight;
    private int level;
    private int restingHR;

    public User(String name, String gender, int height, int age, int level, int restingHR, float weight){
        this.name = name;
        this.gender = gender;
        this.height = height;
        this.age = age;
        this.level = level;
        this.restingHR = restingHR;
        this.weight = weight;
    }

    public void getName(){


    }
}
