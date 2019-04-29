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
        name = "Entrance";
        id = UUID.randomUUID().toString();

        if(horizontal){
            width = 16;
            height = UserInterface.WALL_THICKNESS;
        } else{
            width = UserInterface.WALL_THICKNESS;
            height = 16;
        }
    }

    public Entrance(float x, float y, boolean horizontal, String id){
        this.x = x;
        this.y = y;
        name = "Entrance";

        if(horizontal){
            width = 16;
            height = UserInterface.WALL_THICKNESS;
        } else{
            width = UserInterface.WALL_THICKNESS;
            height = 16;
        }

        this.id = id;
    }

    @Override
    public Element getXML(Document doc) {
        Element doorElement = doc.createElement("Door");

        doorElement = addXML(doc, doorElement);

        Element nameElement = doc.createElement("name");
        nameElement.setTextContent(name);
        doorElement.appendChild(nameElement);

        Element idElement = doc.createElement("id");
        idElement.setTextContent(id);
        doorElement.appendChild(idElement);

        return doorElement;
    }

    @Override
    public String getDoorType(){
        return "Entrance";
    }
}
