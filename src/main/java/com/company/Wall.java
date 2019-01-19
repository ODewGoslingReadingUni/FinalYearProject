package com.company;

import java.util.ArrayList;
import java.util.UUID;

public class Wall {
    private float x1;
    private float y1;

    private float x2;
    private float y2;

    private float width;
    private float height;

    private String id;


    public Wall(float x, float y, float width, float height){
        this.x1 = x;
        this.y1 = y;
        this.width = width;
        this.height = height;

        //Automatically set legacy variables for compatibility
        this.x2 = x1 + width;
        this.y2 = y1 + height;

        id = UUID.randomUUID().toString();
    }

    public float getX1(){
        return x1;
    }

    public float getX2(){
        return x2;
    }

    public float getY1(){
        return y1;
    }

    public float getY2(){
        return y2;
    }

    public void setPoint1(float x, float y){
        x1 = x;
        y1 = y;
        width = Math.abs(x1 - x2);
        height = Math.abs(y1 - y2);
    }

    public void setPoint2(float x, float y){
        x2 = x;
        y2 = y;
        width = Math.abs(x1 - x2);
        height = Math.abs(y1 - y2);
    }

    public ArrayList<Coordinate> getCollisionBox() {
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        coordinates.add(new Coordinate(x1, y1));
        coordinates.add(new Coordinate(x2, y2));
        return coordinates;
    }

    public boolean testForCollision(float x, float y){
        if(x > x1 && x < x2 && y > y1 && y < y2) return true;
        else return false;
    }

    public float getWidth(){
        return width;
    }

    public float getHeight(){
        return height;
    }

    public String getId(){
        return id;
    }

    public void setWidth(float width){
        this.width = width;
        x2 = x1 + width;
    }

    public void setHeight(float height){
        this.height = height;
        y2 = y1 + height;
    }

    public boolean isHorizontal(){
        if(width > height) return true;
        else return false;
    }
}