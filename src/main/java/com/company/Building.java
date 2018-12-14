package com.company;

import javafx.scene.paint.Color;

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

    public void updatePerson(String id, float x, float y, ArrayList<Activity> schedule, Color colour){
        Person personToUpdate = searchForPersonById(id);
        personToUpdate.setPosition(x,y);
        personToUpdate.setSchedule(schedule);
        personToUpdate.setColour(colour);
    }

    public void upsertPerson(Person person){
        if(searchForPersonById(person.getId()) != null){
            //Person already exists
            updatePerson(person.getId(), person.getX(), person.getY(), person.getSchedule(), person.getColour());
        }
        else{
            //Person does not already exist
            addPerson(person);
        }
    }

    public void iterate(float timePeriod){
        for(Person p:people){
            p.move(this, timePeriod);
        }
    }

    public boolean isTraversable(float x, float y){
        return !checkForCollisionWithWall(x, y);
    }

    public Person searchForPersonById(String id){
        for(Person p: people){
            if(p.getId().equals(id)){
                return p;
            }
        }
        return null;
    }

}
