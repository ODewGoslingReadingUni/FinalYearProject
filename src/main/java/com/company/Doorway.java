package com.company;

import java.util.UUID;

public class Doorway {
    private float x;
    private float y;
    private float width;
    private float height;
    private String id;

    public Doorway(float x, float y, boolean horizontal){
        this.x = x;
        this.y = y;
        id = UUID.randomUUID().toString();

        if(horizontal){
            width = 12;
            height = 5;
        } else{
            width = 5;
            height = 12;
        }
    }

    public boolean checkCollision(float x, float y){
        if(x > this.x && x < this.x + width && y > this.y && y < this.y + height){
            return true;
        }
        else return false;
    }

    //Getter methods
    public String getId(){
        return id;
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public float getWidth(){
        return width;
    }

    public float getHeight(){
        return height;
    }

}
