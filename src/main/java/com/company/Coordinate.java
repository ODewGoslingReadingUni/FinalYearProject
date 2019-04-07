package com.company;

import java.util.ArrayList;

public class Coordinate {
    public float x;
    public float y;
    public float f;
    public float g;
    public float h;
    public Coordinate parent;

    private final float INCREMENT = 4;

    public Coordinate(float x, float y){
        this.x = x;
        this.y = y;
    }

    public Coordinate(float x, float y, Coordinate parent){
        this.x = x;
        this.y = y;
        this.parent = parent;
        g = parent.g + 4;
    }

    public Coordinate(float x, float y, float g, float h){
        this.x = x;
        this.y = y;
        this.g = g;
        this.h = h;
        f = g + h;
    }

    public ArrayList<Coordinate> generateNeighbours(float width, float height){
        ArrayList<Coordinate> neighbours = new ArrayList<>();
        float minX = 0;
        float minY = 0;
        float maxX = width;
        float maxY = height;

        if(x+INCREMENT < maxX) neighbours.add(new Coordinate(x+INCREMENT, y, this));
        if(x+INCREMENT < maxX && y+INCREMENT < maxY) neighbours.add(new Coordinate(x+INCREMENT, y+INCREMENT, this));
        if(y+INCREMENT < maxY) neighbours.add(new Coordinate(x, y+INCREMENT, this));
        if(x-INCREMENT > minX && y+INCREMENT < maxY) neighbours.add(new Coordinate(x-INCREMENT, y+INCREMENT, this));
        if(x-INCREMENT > minX) neighbours.add(new Coordinate(x-INCREMENT, y, this));
        if(x-INCREMENT > minX && y-INCREMENT > minY) neighbours.add(new Coordinate(x-INCREMENT, y-INCREMENT, this));
        if(y-INCREMENT > minY) neighbours.add(new Coordinate(x, y-INCREMENT, this));
        if(x+INCREMENT < maxX && y-INCREMENT > minY) neighbours.add(new Coordinate(x+INCREMENT, y-INCREMENT, this));

        return neighbours;
    }


}
