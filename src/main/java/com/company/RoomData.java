package com.company;

import java.time.LocalTime;

public class RoomData {
    private int tick;
    private float hour;
    private float numberOfPeople;

    public RoomData(LocalTime time, int numberOfPeople){
        hour = time.getHour() + ((float)time.getMinute() / 60);
        this.numberOfPeople = numberOfPeople;
    }

    public int getTick() {
        return tick;
    }

    public float getHour(){
        return hour;
    }

    public float getNumberOfPeople() {
        return numberOfPeople;
    }
}
