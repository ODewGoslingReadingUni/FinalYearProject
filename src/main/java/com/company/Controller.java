package com.company;

import java.util.ArrayList;

public class Controller {

    private static Building building;
    private static float timer;

    public static void doInitialSetup(){
        building = new Building();
        timer = 0;

        //Add walls
        building.addWall(new Wall(20,150, 47, 53));
        building.addWall(new Wall(17,23, 50, 200));
        building.addWall(new Wall(147, 153,50,200));
        building.addWall(new Wall(20,150,197,203));
        building.addWall(new Wall(67,73, 50,130));

        //Add people
        Person p = new Person(30,60);
        p.addActivity(100,100);
        p.addActivity(30,60);
        building.addPerson(p);
    }

    public static ArrayList<Wall> getWallLocations(){
        return building.getWalls();
    }

    public static ArrayList<Person> getPeopleLocations(){
        return building.getPeople();
    }

    public static void doIteration(float speed){
        float timePeriod = 60/speed;
        //timer++;
        //if(timer > timePeriod) {
            building.iterate(timePeriod);
            timer = 0;
       // }
    }
}
