package com.company;

import java.util.ArrayList;

public class Building {
    private ArrayList<Wall> walls;
    private ArrayList<Person> people;

    public Building(){
        walls = new ArrayList<Wall>();
        people = new ArrayList<Person>();
    }

    public ArrayList<Wall> getWalls(){
        return walls;
    }

    public ArrayList<Person> getPeople(){
        return people;
    }

    public void addWall(Wall wall){
        walls.add(wall);
    }

    public boolean checkForCollisionWithWall(float x, float y){
        for(Wall w: walls){
            if(w.testForCollision(x,y)) return true;
        }
        return false;
    }

    public void addPerson(Person person){
        people.add(person);
    }

    public void addPersonAt(float x, float y){
        people.add(new Person(x,y));
    }

    public void iterate(float timePeriod){
        for(Person p:people){
            p.move(this, timePeriod);
        }
    }

    public boolean isTraversable(float x, float y){
        return !checkForCollisionWithWall(x, y);
    }

    public static ArrayList<Wall> getMockWalls(){
        ArrayList<Wall> mock = new ArrayList<Wall>();
        mock.add(new Wall(20,150, 50, 50));
        mock.add(new Wall(20,20, 50, 200));
        mock.add(new Wall(150, 150,50,200));
        mock.add(new Wall(20,150,200,200));
        return mock;
    }

    public static ArrayList<Person> getMockPeople(){
        ArrayList<Person> people = new ArrayList<Person>();
        people.add(new Person(100,100));
        return people;
    }


}
