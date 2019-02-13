package com.company;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.UUID;

public class Entrance extends Door{

    private String name;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public Entrance(float x, float y, boolean horizontal){
        this.x = x;
        this.y = y;
        id = UUID.randomUUID().toString();

        if(horizontal){
            width = 12;
            height = UserInterface.WALL_THICKNESS;
        } else{
            width = UserInterface.WALL_THICKNESS;
            height = 12;
        }
    }

    @Override
    public Element getXML(Document doc) {
        Element doorElement = doc.createElement("Door");

        doorElement = addXML(doc, doorElement);

        Element nameElement = doc.createElement("name");
        nameElement.setTextContent(name);
        doorElement.appendChild(nameElement);

        return doorElement;
    }

    @Override
    public String getDoorType(){
        return "Entrance";
    }
}
