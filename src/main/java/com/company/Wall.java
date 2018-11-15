package com.company;


import java.util.ArrayList;

public class Wall {
    private float x1;
    private float y1;

    private float x2;
    private float y2;

    public Wall(float x1, float x2, float y1, float y2){
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
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

    public void setPoint1(int x, int y){
        x1 = x;
        y1 = y;
    }

    public void setPoint2(int x, int y){
        x2 = x;
        y2 = y;
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
}