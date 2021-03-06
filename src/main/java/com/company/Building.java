package com.company;

import javafx.scene.paint.Color;

import java.security.spec.ECField;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;

public class Building {
    private ArrayList<Wall> walls;
    private ArrayList<Person> people;
    private ArrayList<Room> rooms;
    private ArrayList<Door> doors;
    private ArrayList<Entrance> entrances;

    private boolean evacuation;
    private LocalTime evacuationStartTime;
    private LocalTime evacuationEndTime;
    private ArrayList<EvacuationData> evacuationDataList;

    private float dataTimer;
    private final float dataTimerMax = 60; //Every 1 minutes

    public Building(){
        walls = new ArrayList<Wall>();
        people = new ArrayList<Person>();
        rooms = new ArrayList<Room>();
        doors = new ArrayList<Door>();
        entrances = new ArrayList<Entrance>();

        evacuation = false;
        evacuationStartTime = null;
        evacuationEndTime = null;
        evacuationDataList = new ArrayList<>();
        dataTimer = 0;
    }


    //Getter methods
    public ArrayList<Wall> getWalls(){
        return walls;
    }

    public ArrayList<Wall> getAllWalls(){
        ArrayList<Wall> wallList = new ArrayList<>();
        wallList.addAll(walls);
        for(Room r: rooms){
            for(Wall w: r.getWalls()){
                wallList.add(w);
            }
        }
        return wallList;
    }

    public ArrayList<Person> getPeople(){
        return people;
    }

    public ArrayList<Room> getRooms(){
        return rooms;
    }

    public ArrayList<Door> getDoors(){
        ArrayList<Door> allDoors = new ArrayList<>(doors);
        allDoors.addAll(entrances);

        return allDoors;
    }

    public ArrayList<String> getEntranceNames(){
        ArrayList<String> names = new ArrayList<>();
        for(Entrance e: entrances){
            names.add(e.getName());
        }
        return names;
    }

    public Coordinate getDimensions(){
        float xMax = 0;
        float yMax = 0;

        for(Wall w: getAllWalls()){
            if(w.getX() + w.getWidth() > xMax) xMax = w.getX() + w.getWidth();
            if(w.getY() + w.getHeight() > yMax) yMax = w.getY() + w.getHeight();
        }

        return new Coordinate(xMax, yMax);
    }

    public Entrance findEntranceByName(String name){
        for(Entrance e: entrances){
            if(e.getName().equals(name)){
                return e;
            }
        }
        return null;
    }

    //Collision checking
    public String checkForCollisionWithWall(float x, float y){
        for(Wall w: walls){
            if(w.checkForCollision(x,y)) return w.getId();
        }
        for(Room r: rooms){
            for(Wall w: r.getWalls()){
                if(w.checkForCollision(x,y)) return w.getId();
            }
        }
        return null;
    }

    public boolean checkForCollisionWithDoor(float x, float y){
        for(Door d: doors){
            if(d.checkForCollision(x,y)) return true;
        }

        for(Entrance e: entrances){
            if(e.checkForCollision(x,y)) return true;
        }

        return false;
    }

    public boolean checkForCollisionWithPerson(float x, float y){
        for(Person p: people){
            if(p.checkForCollision(x,y)) return true;
        }

        return false;
    }

    public boolean checkForCollisionWithPerson(float x, float y, String id){
        for(Person p: people){
            if(p.checkForCollision(x,y) && !p.getId().equals(id) && !p.getState().equals("outside") && !p.getState().equals("inactive")) return true;
        }
        return false;
    }

    //Adding new objects and editing objects
    public void addWall(Wall wall){
        wall = alignWall(wall);
        walls.add(wall);
    }

    public void addPerson(Person person){
        people.add(person);
        person.setInitialActivity(this);
    }

    public void addRoom(Room room){
        room = alignRoom(room);

        //Add the room to the room list
        rooms.add(room);
    }

    public void addDoor(Door door){
        doors.add(door);
        System.out.println("Add door");
    }

    public void addEntrance(Entrance entrance){
        entrances.add(entrance);
        System.out.println("Add entrance");
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
        alignWall(wall);
    }

    public void updateRoom(String id, float x, float y, float width, float height, String type, Boolean hasWalls, String name){
        Room room = searchForRoomById(id);
        room.setAllAttributes(x,y,width,height,type, name);
        alignRoom(room);
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
            updateWall(wall.getId(), wall.getX(), wall.getY(), wall.getX2(), wall.getY2());
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

            updateRoom(room.getId(), room.getX(), room.getY(), room.getWidth(), room.getHeight(), room.getType(),hasWalls, room.getName());
        }
        else {
            addRoom(room);
        }
    }

    public boolean upsertDoor(String id, Door door){
        if(searchForDoorById(id) != null){
            //Edit existing door
            if(door.getDoorType().equals("Entrance")){
                Entrance entrance = (Entrance) door;
                System.out.println("Entrance name" + entrance.getName());
                return true;
            }
        } else {
            //Add a new door
            addDoor(door);
            return true;
        }
        return false;
    }

    public boolean updateEntrance(String id, String name, float x, float y){
        Door door = searchForDoorById(id);
        if(door.getDoorType().equals("Entrance")){
            Entrance entrance = (Entrance) door;
                entrance.setName(name);
                return true;
            }
        return false;
    }

    private Wall alignWall(Wall wall){
        //Align wall coordinates
        float x = Helper.alignObject(wall.getX(),6);
        float y = Helper.alignObject(wall.getY(),6);
        wall.setPoint1(x,y);

        return wall;
    }

    private Room alignRoom(Room room){
        //Align the room coordinates
        float x = Helper.alignObject(room.getX(),6);
        float y = Helper.alignObject(room.getY(),6);
        room.setXY(x,y);

        return room;
    }

    public String checkEntranceNameIsUnique(String entranceName, String entranceId){
        for(Entrance e: entrances){
            if(e.getName() != null){
                if(e.getName().equals(entranceName) && !e.getId().equals(entranceId)){
                    return "Entrance names must be unique- this name is already in use";
                }
            }
        }
        return null;
    }


    //Iteration and Pathfinding
    public void iterate(float timePeriod){
        for(Person p:people){
            p.iterate(this, timePeriod);
        }
        gatherData(timePeriod);
        if(isEmpty()) evacuationEndTime = Controller.getTime();
    }

    public boolean isTraversable(float x, float y, String id){
        if(id != null){
            //Do not check for collisions with people
            if(checkForCollisionWithDoor(x,y)){
                //If there is a door, it's traversable
                return true;
            } else if(checkForCollisionWithWall(x,y) == null && !checkForCollisionWithPerson(x,y,id)){
                //If there is no wall and no person it's traversable
                return true;
            }
            //If neither of the above conditions are true, it's not traversable
            return false;
        } else {
            //Check for collisions with people
            if(checkForCollisionWithDoor(x,y)){
                //If there is a door, it's traversable
                return true;
            } else if(checkForCollisionWithWall(x,y) == null){
                //If there is no wall it's traversable
                return true;
            }
            //If neither of the above conditions are true, it's not traversable
            return false;
        }
    }

    public void resetToStart(){
        evacuation = false;
        evacuationStartTime = null;
        evacuationEndTime = null;
        for(Person p: people){
            p.reset(this);
        }
    }

    public void triggerFireAlarm(){
        evacuation = true;
        evacuationStartTime = Controller.getTime();
        for(Person p: people){
            p.setState("alarm");
        }
    }


    //Searching Methods
    public Person searchForPersonById(String id){
        for(Person p: people){
            if(p.getId() == id){
                return p;
            }
        }
        return null;
    }

    public Room searchForRoomByName(String name){
        for(Room r: rooms){
            if(r.getName().equals(name)) return r;
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

    public Door searchForDoorById(String id){

        for(Door d: getDoors()){
            if(d.getId().equals(id)){
                return d;
            }
        }
        return null;
    }

    public boolean hasToilet(){
        for(Room r: rooms){
            if(r.getType().equals("Toilet")) return true;
        }
        return false;
    }

    public Room searchForNearestToilet(float x, float y){
        if(!hasToilet()) return null;

        float shortestDistance = 999999999;
        Room shortestRoom = null;

        for(Room r:rooms){
            if(r.getType().equals("Toilet")){
                float dist = Helper.distance(x,y,r.getX(), r.getY());
                if(dist < shortestDistance){
                    shortestDistance = dist;
                    shortestRoom = r;
                }
            }
        }
        return shortestRoom;
    }

    public Room searchForRoomByPoint(float x, float y){
        for(Room r: rooms){
            if(r.pointIsInRoom(x,y)) return r;
        }
        return null;
    }

    public Entrance searchForEntranceById(String id){
        System.out.println("entrances size: " + entrances.size());
        for(Entrance e: entrances){
            if(e.getId().equals(id)){
                return e;
            }
        }
        return null;
    }

    public Entrance searchForNearestExit(float x, float y){
        if(entrances.size() == 0) return null;

        float shortestDistance = 999999999;
        Entrance nearestEntrance = null;

        for(Entrance e: entrances){
            float dist = Helper.distance(x,y,e.getX(), e.getY());
            if(dist < shortestDistance){
                shortestDistance = dist;
                nearestEntrance = e;
            }
        }
        return nearestEntrance;
    }

    //Deleting methods
    public boolean deletePerson(String id){
        for(int i = 0; i < people.size(); i++){
            if(people.get(i).getId().equals(id)){
                people.remove(i);
                return true;
            }
        }
        return false;
    }

    public void deleteWall(String id){
        //Search the standard wall list
        System.out.println("delete wall");

        for(int i = 0; i < walls.size(); i++){
            if(walls.get(i).getId().equals(id)){
                walls.remove(walls.get(i));
                System.out.println("delete wall from walls");
            }
        }

        for(int i = 0; i < rooms.size(); i++){
            ArrayList<Wall> walls2 = rooms.get(i).getWalls();
            for(int j = 0; j < walls2.size(); j++){
                if(walls2.get(j).getId().equals(id)){
                    System.out.println("delete wall from rooms");
                    rooms.get(i).removeWall(walls2.get(j));
                }
            }
        }
    }

    public boolean deleteRoom(String id){
        for(int i = 0; i < rooms.size(); i++){
            if(rooms.get(i).getId().equals(id)){
                rooms.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean deleteDoor(String id){
        Door doorToDelete = searchForDoorById(id);
        if(doorToDelete != null) {
            if(doorToDelete.getDoorType().equals("Entrance")){
                entrances.remove(doorToDelete);
                return true;
            } else {
                doors.remove(doorToDelete);
                return true;
            }
        }
        else return false;
    }

    //Instrumentation Methods

    private void gatherData(float timePeriod){

        if(dataTimer > dataTimerMax){

            for(Room r: rooms){
                int count = countPeopleInRoom(r);
                r.recordRoomData(new RoomData(Controller.getTime(),count));
            }

            for(Person p: people){
                String roomID = getRoomFromPoint(p.getX(), p.getY());
                if(roomID != null) p.recordPersonData(new PersonData(Controller.getTime(), roomID));
            }
            dataTimer = 0;
        } else {
            dataTimer += (60/timePeriod)/10;
        }
        if(evacuation){
            recordEvacuationData();
        }
    }

    private int countPeopleInRoom(Room room){
        int count = 0;
        for(Person p: people){
            if(room.pointIsInRoom(p.getX(), p.getY())){
                count++;
            }
        }
        return count;
    }

    private String getRoomFromPoint(float x, float y){
        for(Room r: rooms){
            if(r.pointIsInRoom(x,y)){
                return r.getId();
            }
        }
        return "None";
    }

    public boolean isEmpty(){
        if(people.size() == 0) {
            System.out.println("no people");
            return true;
        }


        if(numPeopleInBuilding() == 0) {
            System.out.println("num people is 0");
            return true;
        }

        return false;
    }

    public int numPeopleInBuilding(){
        int count = 0;
        for(Person p: people){
            if(!p.getState().equals("outside")) count++;
        }
        return count;
    }

    private void recordEvacuationData(){
        long timeSinceEvacuation = Duration.between(evacuationStartTime, Controller.getTime()).getSeconds();
        evacuationDataList.add(new EvacuationData(numPeopleInBuilding(), timeSinceEvacuation));
        System.out.println("number of people in building: " + numPeopleInBuilding());
    }

    public ArrayList<NumericData> getEvacuationDataAsNumericData(){
        ArrayList<NumericData> numericData = new ArrayList<>();
        for(EvacuationData ed: evacuationDataList){
            numericData.add(new NumericData(ed.hour, ed.peopleInBuilding));
        }
        return numericData;
    }

    public boolean evacuationIsFinished(){
        if(evacuation && evacuationEndTime != null){
            return true;
        }
         else return false;
    }

    public LocalTime getEvacuationEndTime() {
        return evacuationEndTime;
    }

    public LocalTime getEvacuationStartTime() {
        return evacuationStartTime;
    }

    public void clearEvacuationData(){
        evacuationDataList = new ArrayList<EvacuationData>();
    }
}
