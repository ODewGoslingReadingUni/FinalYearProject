package com.company;

public class PersonData {
    private int tick;
    private String roomID;

    public PersonData(int tick, String roomID){
        this.tick = tick;
        this.roomID = roomID;
    }

    public int getTick() {
        return tick;
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
