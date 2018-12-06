package com.company;

public class Activity {
    private float x;
    private float y;;

    public Activity(float x, float y){
        this.x = x;
        this.y = y;
    }

    public Activity(){

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
