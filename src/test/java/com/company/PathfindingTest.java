package com.company;

import javafx.scene.paint.Color;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class PathfindingTest {

    @Test
    public void findPath() {
        //Setup
        Building building = TestHelper.setupBuilding();

        Person testPerson = new Person(120,30);

        //Find a path from the entrance to one of the rooms in the building
        ArrayList<Coordinate> path = Pathfinding.findPath(building,120,30,30,10, testPerson.getId());

        //The path should be found, and hence the pathfinding result should be not null
        assertNotEquals(null, path);
    }

    @Test
    public void findNullPath(){
        Building building = TestHelper.setupBuilding();
        Person testPerson = new Person(120,30);

        //Attempt to reach an unreachable coordinate
        ArrayList<Coordinate> path = Pathfinding.findPath(building,120,30, 30,90, testPerson.getId());

        //The path should be null as this coordinate should not be reachable
        assertEquals(null, path);
    }
}