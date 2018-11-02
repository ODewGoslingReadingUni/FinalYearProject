package com.company;

import javafx.scene.paint.Color;

import java.awt.*;
import java.util.ArrayList;

public class Person {
    private Point position;
    private final int RADIUS = 10;
    private Color colour;

    private ArrayList<PersonTask> tasks;

    public Person(int x, int y){
        position = new Point(x,y);
    }

    public Point getPosition(){
        return position;
    }

    public int getXPosition(){
        return position.x;
    }

    public int getYPosition(){
        return position.y;
    }

    public int getRadius(){
        return RADIUS;
    }

}
