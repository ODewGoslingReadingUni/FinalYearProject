package com.company;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.UUID;

public class Door extends AbstractObject {

    //Constructor Methods

    protected Door(){
        this.x = 0;
        this.y = 0;
        id = UUID.randomUUID().toString();
    }

    public Door(float x, float y, boolean horizontal){
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

    //Getter methods

    public String getDoorType(){
        return "Door";
    }

    //Saving methods

    public Element getXML(Document doc){
        Element doorElement = doc.createElement("Door");

        doorElement = addXML(doc, doorElement);

        return doorElement;
    }

    protected Element addXML(Document doc, Element doorElement){
        Element xElement = doc.createElement("x");
        xElement.setTextContent("" + x);
        doorElement.appendChild(xElement);

        Element y1Element = doc.createElement("y");
        y1Element.setTextContent("" + y);
        doorElement.appendChild(y1Element);

        Element widthElement = doc.createElement("width");
        widthElement.setTextContent("" + width);
        doorElement.appendChild(widthElement);

        Element heightElement = doc.createElement("height");
        heightElement.setTextContent("" + height);
        doorElement.appendChild(heightElement);

        Element doorTypeElement = doc.createElement("doorType");
        doorTypeElement.setTextContent(getDoorType());
        doorElement.appendChild(doorTypeElement);

        return doorElement;
    }

}
