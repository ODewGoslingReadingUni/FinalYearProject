package com.company;

import javafx.scene.paint.Color;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Building {
    private ArrayList<Wall> walls;
    private ArrayList<Person> people;
    private ArrayList<Room> rooms;
    private ArrayList<Doorway> doors;

    public Building(){
        walls = new ArrayList<Wall>();
        people = new ArrayList<Person>();
        rooms = new ArrayList<Room>();
        doors = new ArrayList<Doorway>();
    }

    public ArrayList<Wall> getWalls(){
        return walls;
    }

    public ArrayList<Person> getPeople(){
        return people;
    }

    public ArrayList<Room> getRooms(){
        return rooms;
    }

    public ArrayList<Doorway> getDoors(){
        return doors;
    }

    public void addWall(Wall wall){
        walls.add(wall);
    }

    public String checkForCollisionWithWall(float x, float y){
        for(Wall w: walls){
            if(w.testForCollision(x,y)) return w.getId();
        }
        for(Room r: rooms){
            for(Wall w: walls){
                if(w.testForCollision(x,y)) return w.getId();
            }
        }
        return null;
    }

    public boolean checkForCollisionWithDoor(float x, float y){
        for(Doorway d: doors){
            if(d.checkCollision(x,y)) return true;
        }
        return false;
    }

    public void addPerson(Person person){
        people.add(person);
    }

    public void addPersonAt(float x, float y){
        people.add(new Person(x,y));
    }

    public void addRoom(Room room){
        //Add the room to the room list
        rooms.add(room);

        //Add the walls this room has to the wall list
        /*for(Wall w: room.getWalls()){
            addWall(w);
        }*/
    }

    public void addDoor(Doorway door){
        doors.add(door);
    }

    public void updatePerson(String id, float x, float y, ArrayList<Activity> schedule, Color colour){
        Person personToUpdate = searchForPersonById(id);
        personToUpdate.setPosition(x,y);
        personToUpdate.setSchedule(schedule);
        personToUpdate.setColour(colour);
    }

    public void updateWall(String id, float x1, float y1, float x2, float y2){
        Wall wall = searchForWallById(id);
        wall.setPoint1(x1, y1);
        wall.setPoint2(x2, y2);
    }

    public void updateRoom(String id, float x, float y, float width, float height, String type, Boolean hasWalls){
        Room room = searchForRoomById(id);
        room.setAllAttributes(x,y,width,height,type,hasWalls);
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

    public void upsertWall(Wall wall){
        if(searchForWallById(wall.getId()) != null){
            updateWall(wall.getId(), wall.getX1(), wall.getY1(), wall.getX2(), wall.getY2());
        }
        else{
            addWall(wall);
        }
    }

    public void upsertRoom(Room room){
        if(searchForRoomById(room.getId()) != null){
            //Room already exists- do update
            boolean hasWalls = true;
            if(room.getWalls().size() == 0) hasWalls = false;

            updateRoom(room.getId(), room.getX(), room.getY(), room.getWidth(), room.getHeight(), room.getType(),hasWalls);
        }
        else {
            addRoom(room);
        }
    }

    public void iterate(float timePeriod){
        for(Person p:people){
            p.move(this, timePeriod);
        }
    }

    public boolean isTraversable(float x, float y){
        if(checkForCollisionWithDoor(x,y)){
            //If there is a door, it's traversable
            return true;
        } else if(checkForCollisionWithWall(x,y) == null){
            //If there is no wall, it's traversable
            return true;
        }
        //If neither of the above conditions are true, it's not traversable.
        return false;
    }

    public Person searchForPersonById(String id){
        for(Person p: people){
            if(p.getId() == id){
                return p;
            }
        }
        return null;
    }

    public Person searchForPersonByXY(float x, float y){
        for(Person p: people) {
                if(p.getX() == x && p.getY() == y) {
                    return p;
                }
            }
        return null;
    }

    public Wall searchForWallByXY(float x, float y){
        for(Wall w: walls) {
            if(w.getX1() == x && w.getY1() == y){
                return w;
            }
        }

        for(Room r: rooms){
            for(Wall w: r.getWalls()){
                if(w.getX1() == x && w.getY1() == y){
                    return w;
                }
            }
        }
        return null;
    }

    public Wall searchForWallById(String id){
        for(Wall w: walls) {
            if(w.getId().equals(id)){
                return w;
            }
        }

        for(Room r: rooms) {
            for (Wall w : r.getWalls()) {
                if (w.getId().equals(id)) {
                    return w;
                }
            }
        }
        return null;
    }

    public Room searchForRoomById(String id){
        for(Room r: rooms){
            if(r.getId().equals(id)){
                return r;
            }
        }
        return null;
    }

    public boolean deletePerson(String id){
        for(int i = 0; i < people.size(); i++){
            if(people.get(i).getId().equals(id)){
                people.remove(i);
                return true;
            }
        }
        return false;
    }

}
