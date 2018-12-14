package com.company;

import java.util.UUID;

public class Activity {
    private float x;
    private float y;
    private String id;

    public Activity(float x, float y){
        this.x = x;
        this.y = y;

        id = UUID.randomUUID().toString();
    }

    public Activity(){
        id = UUID.randomUUID().toString();
    }

    public void setPoint(float x, float y){
        this.x = x;
        this.y = y;
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }
}
