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

    public ArrayList<Coordinate> generateNeighbours(){
        ArrayList<Coordinate> neighbours = new ArrayList<>();

        neighbours.add(new Coordinate(x+INCREMENT, y, this));
        neighbours.add(new Coordinate(x+INCREMENT, y+INCREMENT, this));
        neighbours.add(new Coordinate(x, y+INCREMENT, this));
        neighbours.add(new Coordinate(x-INCREMENT, y+INCREMENT, this));
        neighbours.add(new Coordinate(x-INCREMENT, y, this));
        neighbours.add(new Coordinate(x-INCREMENT, y-INCREMENT, this));
        neighbours.add(new Coordinate(x, y-INCREMENT, this));
        neighbours.add(new Coordinate(x+INCREMENT, y-INCREMENT, this));

        return neighbours;
    }
}
