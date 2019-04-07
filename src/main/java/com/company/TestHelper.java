package com.company;

import javafx.scene.paint.Color;

import java.util.ArrayList;

public class TestHelper {

    public static Building setupBuilding(){
        Building building = new Building();

        building.addRoom(new Room(0,0,60,60,"Office", true, "office1"));
        building.addRoom(new Room(60,0,60,60,"Corridor", true, "corridor1"));
        building.addRoom(new Room(0,60,60,60,"Office", true, "office2"));

        building.addDoor(new Door(60,30,false));
        Entrance entry = new Entrance(120,30,false);
        entry.setName("TestEntrance");
        building.addEntrance(entry);

        return building;
    }
}
