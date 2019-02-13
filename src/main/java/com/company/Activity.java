package com.company;

import java.util.UUID;

public class Activity {
    private float x;
    private float y;
    private String id;
    private Room room;
    private int time;

    public Activity(int time, Room location){
        this();

        this.time = time;
        this.room = location;

        Coordinate targetCoordinate = location.getRandomPointInRoom();
        x = targetCoordinate.x;
        y = targetCoordinate.y;
    }

    public Activity(int time, float x, float y){
        this();

        this.time = time;
        this.x = x;
        this.y = y;
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

    public Room getLocation(){
        return room;
    }

    public int getTime(){
        return time;
    }
}
