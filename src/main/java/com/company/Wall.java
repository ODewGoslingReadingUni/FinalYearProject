package com.company;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.UUID;

public class Wall extends AbstractObject {
    private float x2;
    private float y2;

    public Wall(float x, float y, float width, float height){

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        //Automatically set legacy variables for compatibility
        this.x2 = this.x + width;
        this.y2 = this.y + height;

        id = UUID.randomUUID().toString();
    }

    public float getX2(){
        return x2;
    }

    public float getY2(){
        return y2;
    }

    public void setPoint1(float x, float y){
        this.x = x;
        this.y = y;
        x2 = this.x + width;
        y2 = this.y + height;
    }

    public void setPoint2(float x, float y){
        x2 = x;
        y2 = y;
        width = Math.abs(this.x - x2);
        height = Math.abs(this.y - y2);
    }

    public ArrayList<Coordinate> getCollisionBox() {
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        coordinates.add(new Coordinate(x, y));
        coordinates.add(new Coordinate(x2, y2));
        return coordinates;
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

    @Override
    public Element getXML(Document doc, String tagName){
        Element wallElement = doc.createElement(tagName);

        Element x1Element = doc.createElement("x");
        x1Element.setTextContent("" + x);
        wallElement.appendChild(x1Element);

        Element x2Element = doc.createElement("x2");
        x2Element.setTextContent("" + x2);
        wallElement.appendChild(x2Element);

        Element y1Element = doc.createElement("y");
        y1Element.setTextContent("" + y);
        wallElement.appendChild(y1Element);

        Element y2Element = doc.createElement("y2");
        y2Element.setTextContent("" + y2);
        wallElement.appendChild(y2Element);

        return wallElement;
    }
}