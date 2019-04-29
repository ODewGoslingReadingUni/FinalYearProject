package com.company;

import java.time.LocalTime;

public class PersonData {
    private float hour;
    private String roomID;

    public PersonData(LocalTime time, String roomID){
        float hour = time.getHour() + ((float)time.getMinute() / 60);
        this.roomID = roomID;
    }

    public float getHour() {
        return hour;
    }

    public String getRoomType(){
        Room room = Controller.searchForRoom(roomID);
        if(room != null) return room.getType();
        else return "None";
    }

    public String getRoomName(){
        Room room = Controller.searchForRoom(roomID);
        if(room != null) return room.getName();
        else return "None";
    }
}
