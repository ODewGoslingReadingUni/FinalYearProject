package com.company;

import org.junit.Test;
import org.w3c.dom.Document;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class ControllerTest {

    @Test
    public void getWallLocations() {
        Controller.doInitialSetup(null);

    }

    @Test
    public void getPeopleLocations() {
    }

    @Test
    public void testCreateFile(){
        //Setup
        Controller.doInitialSetup(null);
        Controller.addWallAtLocation(30,30,100,"Horizontal");
        Controller.addWallAtLocation(50,50,75,"Vertical");
        Controller.addDoorAtLocation(35,35,"door");
        Controller.upsertRoom(new Room(100,100,50,50,"Office", true, "Test Room"));

        //Create the document
        Document doc = Controller.createXMLDoc();

        //Count how many walls and doors are added
        int numWalls = doc.getElementsByTagName("Wall").getLength();
        int numDoors = doc.getElementsByTagName("Door").getLength();

        //Assert that there are the expected number of walls and doors
        assertEquals(2, numWalls);
        assertEquals(1, numDoors);
    }

}