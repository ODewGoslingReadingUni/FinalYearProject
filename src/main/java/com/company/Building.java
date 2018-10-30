package com.company;

import java.util.ArrayList;

public class Building {
    private ArrayList<Wall> walls;

    public ArrayList<Wall> getWalls(){
        return walls;
    }

    public void addWall(Wall wall){
        walls.add(wall);
    }

    public static ArrayList<Wall> getMockWalls(){
        ArrayList<Wall> mock = new ArrayList<Wall>();
        mock.add(new Wall(20,150, 50, 50));
        mock.add(new Wall(20,20, 50, 200));
        mock.add(new Wall(150, 150,50,200));
        mock.add(new Wall(20,150,200,200));
        return mock;
    }
}
