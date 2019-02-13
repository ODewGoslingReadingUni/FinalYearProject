package com.company;

public class RoomData {
    private int tick;
    private int numberOfPeople;

    public RoomData(int tick, int numberOfPeople){
        this.tick = tick;
        this.numberOfPeople = numberOfPeople;
    }

    public int getTick() {
        return tick;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }
}
