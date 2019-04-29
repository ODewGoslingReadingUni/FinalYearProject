package com.company;

import javafx.scene.paint.Color;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class Room extends AbstractObject{

    private ArrayList<Wall> walls;
    private String name;
    private String type;
    private final float WALL_THICKNESS = UserInterface.WALL_THICKNESS;
    private ArrayList<RoomData> data;

    public Room(float x, float y, float width, float height, String type, Boolean hasWalls, String name){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;
        this.name = name;

        id = UUID.randomUUID().toString();
        data = new ArrayList<>();

        //Add new walls
        walls = setUpWalls(x,y,width, height);
        //walls = new ArrayList<Wall>();
    }

    public Room(float x, float y, float width, float height, String type, String name, ArrayList<Wall> walls){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;
        this.name = name;

        id = UUID.randomUUID().toString();
        data = new ArrayList<>();
        this.walls = new ArrayList<>();
        this.walls = walls;
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

    private boolean checkForCollisionWithRoomWalls(float x, float y){
        for(Wall w: walls){
            if(w.checkForCollision(x,y)){
                return true;
            }
        }
        return false;
    }

    public void setAllAttributes(float x, float y, float width, float height, String type, String name){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;
        this.name = name;

        //Remove old walls from the array
        //walls.clear();

        //Add new walls
        //if(hasWalls)
        if(walls.size() == 4) walls = setUpWalls(x,y,width, height);
    }

    public void setXY(float x, float y){
        this.x = x;
        this.y = y;
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

    public void removeWall(Wall wall){
        walls.remove(wall);
    }

    public void recordRoomData(RoomData roomData){
        data.add(roomData);
    }

    public ArrayList<CategoricData> generateRoomUsageReport(){
        ArrayList<CategoricData> usage = new ArrayList<>();

        float timeUsed = 0;
        float timeEmpty = 0;

        for(RoomData rd: data) {
            if (rd.getNumberOfPeople() > 0) timeUsed++;
            else timeEmpty++;
        }

        float total = timeEmpty + timeUsed;
        float percentUsed, percentEmpty;
        if(total > 0){
            System.out.println("more than 0");
            percentEmpty = (timeEmpty / total) * 100;
            percentUsed = (timeUsed / total) * 100;
        } else {
            System.out.println("more than 0");
            percentEmpty = 0;
            percentUsed = 0;
        }

        CategoricData usedData = new CategoricData("In Use");
        CategoricData emptyData = new CategoricData("Empty");
        usedData.occurances = (int)percentUsed;
        emptyData.occurances = (int) percentEmpty;

        usage.add(usedData);
        usage.add(emptyData);

        return usage;
    }

    //Getters and setters--------------------------------------------------------------------

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

    public Element getXML(Document doc){
        Element roomElement = doc.createElement("Room");

        Element xElement = doc.createElement("x");
        xElement.setTextContent("" + getX());
        roomElement.appendChild(xElement);

        Element yElement = doc.createElement("y");
        yElement.setTextContent("" + getY());
        roomElement.appendChild(yElement);

        Element widthElement = doc.createElement("width");
        widthElement.setTextContent("" + getWidth());
        roomElement.appendChild(widthElement);

        Element heightElement = doc.createElement("height");
        heightElement.setTextContent("" + getHeight());
        roomElement.appendChild(heightElement);

        Element nameElement = doc.createElement("name");
        nameElement.setTextContent(getName());
        roomElement.appendChild(nameElement);

        Element typeElement = doc.createElement("type");
        typeElement.setTextContent(type);
        roomElement.appendChild(typeElement);

        for(Wall w: walls){
            roomElement.appendChild(w.getXML(doc, "RoomWall"));
        }

        return roomElement;
    }

    public ArrayList<RoomData> getRoomData(){
        return data;
    }

    public ArrayList<NumericData> getRoomDataAsNumericData(){
        ArrayList<NumericData> numericData = new ArrayList<>();
        for(RoomData rd: data){
            numericData.add(new NumericData(rd.getHour(), rd.getNumberOfPeople()));
        }
        return numericData;
    }

    public Coordinate getRandomPointInRoom(){
        Random r = new Random();
        int x, y;

        do{
            x = r.nextInt((int)width  - 30) + (int)this.x + 30;
            y = r.nextInt((int)height - 30) + (int)this.y + 30;
        } while (checkForCollisionWithRoomWalls(x,y) || Controller.isTraversible(x,y,""));

        return new Coordinate(x,y);
    }

}
