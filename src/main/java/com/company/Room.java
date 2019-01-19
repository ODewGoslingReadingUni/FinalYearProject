package com.company;

import javafx.scene.paint.Color;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.UUID;

public class Room {

    private ArrayList<Wall> walls;
    private float x;
    private float y;
    private float width;
    private float height;
    private Color floorColour;
    private String id;
    private String name;
    private String type;
    private final float WALL_THICKNESS = 4;

    public Room(float x, float y, float width, float height, String type, Boolean hasWalls){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;

        id = UUID.randomUUID().toString();

        //Add new walls
       if(hasWalls) walls = setUpWalls(x,y,width, height);
    }

    public ArrayList<Wall> getWalls(){
        return walls;
    }

    public boolean pointIsInRoom(float x, float y){

        boolean inXRange = false;
        boolean inYRange = false;

        if(x >= this.x && x <= this.x + width){
            inXRange = true;
        }

        if(y >= this.y && y <= this.y + height){
            inYRange = true;
        }

        if(inXRange && inYRange) return true;
        else return false;

    }

    public void setAllAttributes(float x, float y, float width, float height, String type, boolean hasWalls){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;

        //Remove old walls from the array
        walls.clear();

        //Add new walls
        if(hasWalls) walls = setUpWalls(x,y,width, height);
    }

    private ArrayList<Wall> setUpWalls(float x, float y, float width, float height){
        //Create walls for a room with these dimensions
        Wall rightWall = new Wall(x+width, y, WALL_THICKNESS,height);
        Wall leftWall = new Wall(x, y, WALL_THICKNESS, height);

        Wall topWall = new Wall(x, y, width, WALL_THICKNESS);
        Wall bottomWall = new Wall(x,y+height, width+WALL_THICKNESS, WALL_THICKNESS);

        ArrayList<Wall> wallList = new ArrayList<>();

        //Add walls to wall array
        wallList.add(rightWall);
        wallList.add(leftWall);
        wallList.add(topWall);
        wallList.add(bottomWall);

        return wallList;
    }

    //Getters and setters--------------------------------------------------------------------

    public String getId(){
        return id;
    }

    public float getX() {
        return x;
    }

    public float getY(){
        return y;
    }

    public float getWidth(){
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Color getFloorColour() {

        if(type.equals("Office")){
            return Color.web("#6699ff");
        } else if(type.equals("Corridor")){
            return Color.web("#d9d9d9");
        } else if(type.equals("Toilet")){
            return Color.web("#e6ffff");
        } else if(type.equals("Kitchen")){
            return Color.web("#f2f2f2");
        }

        return Color.web("#FFFFFF");
    }

    public String getName(){
        return name;
    }

    public String getType(){
        return type;
    }
}
