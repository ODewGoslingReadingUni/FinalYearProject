package com.company;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.UUID;

public abstract class AbstractObject {
    protected float x;
    protected float y;
    protected float width;
    protected float height;
    protected String id;
    protected String parentId;

    //Constructor methods

    public AbstractObject(){
        id = UUID.randomUUID().toString();
    }

    //Getter methods

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public String getId() {
        return id;
    }

    public boolean isHorizontal(){
        if(width > height) return true;
        else return false;
    }

    public Coordinate getCenter(){
        float xLocation = x + width / 2;
        float yLocation = y + height / 2;
        Coordinate location = new Coordinate(xLocation, yLocation);
        return location;
    }

    //Setter methods

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height){
        this.height = height;
    }

    //Collision Checking
    public boolean checkForCollision(float x, float y){
        if(x > this.x && x < this.x + width && y > this.y && y < this.y + height){
            return true;
        }
        else return false;
    }

    //Methods for saving as XML

    public Element getXML(Document doc, String tagName){
        Element element = doc.createElement(tagName);
        element = addStandardXML(doc, element);
        return element;
    }

    protected Element addStandardXML(Document doc, Element element){
        Element x1Element = doc.createElement("x");
        x1Element.setTextContent("" + x);
        element.appendChild(x1Element);

        Element y1Element = doc.createElement("y");
        y1Element.setTextContent("" + y);
        element.appendChild(y1Element);

        Element widthElement = doc.createElement("width");
        widthElement.setTextContent("" + width);
        element.appendChild(widthElement);

        Element heightElement = doc.createElement("height");
        heightElement.setTextContent("" + height);
        element.appendChild(heightElement);

        return element;
    }
}
