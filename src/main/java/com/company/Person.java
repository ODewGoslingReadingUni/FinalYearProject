package com.company;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class Person {
    private float x;
    private float y;
    private final int RADIUS = 8;
    private Color colour;
    private ArrayList<Activity> activities;
    private String id;
    private String name;

    //These variables are only used internally
    private int activityCount;
    private ArrayList<Coordinate> path;
    private float pathStage;

    //Constructors----------------------------------------------------------------------------

    public Person(){
        id = UUID.randomUUID().toString();

        //Initialise array
        this.activities = new ArrayList<>();
        name = "default name";

        //Setting internal variables to defaults
        activityCount = 0;
        path = new ArrayList<>();
        pathStage = 0;
    }

    public Person(float x, float y){
        id = UUID.randomUUID().toString();

        this.x = x;
        this.y = y;
        colour = Color.GREEN;
        activities = new ArrayList<Activity>();
        name = "default name";

        //Setting internal variables to defaults
        activityCount = 0;
        path = new ArrayList<>();
        pathStage = 0;
    }

    public Person(String name, float x, float y, Color colour, ArrayList<Activity> schedule){
        id = UUID.randomUUID().toString();

        this.x = x;
        this.y = y;
        this.colour = colour;
        this.activities = schedule;
        this.name = name;

        //Setting internal variables to defaults
        activityCount = 0;
        path = new ArrayList<>();
        pathStage = 0;
    }

    //Getters and setters-----------------------------------------------------------------------

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public void setPosition(float x, float y){
        this.x = x;
        this.y = y;
    }

    public int getRadius(){
        return RADIUS;
    }

    public Color getColour(){
        return colour;
    }

    public void setColour(Color colour){
        this.colour = colour;
    }

    public ArrayList<Activity> getSchedule(){
        return activities;
    }

    public Activity getCurrentActivity(){
        if(activities.size() > 0) return activities.get(activityCount);
        else return new Activity(x,y);
    }

    public String getName(){
        return name;
    }

    public String getId(){
        return id;
    }

    public void addActivity(int x, int y){
        activities.add(new Activity(x,y));
    }

    public void addActivity(Activity activity){
        activities.add(activity);
    }

    public void setSchedule(ArrayList<Activity> schedule){
        activities = schedule;
    }

    public void setName(String name){
        this.name = name;
    }

    //Pathfinding and Movement------------------------------------------------------------------

    public void move(Building building, float timePeriod){
        //Set up initial path only
        if(activities.size() > 0 && path.size() == 0) path = findPath(building,x,y,getCurrentActivity().getX(), getCurrentActivity().getY());

        final float INCREMENT = 4;

        //Move if there's a path to follow
        if(path.size() > 1 && pathStage < path.size()){
            Coordinate next = path.get((int)Math.floor(pathStage));
            if(next.x > x) x += INCREMENT / timePeriod;
            if(next.y > y) y += INCREMENT / timePeriod;
            if(next.x < x) x -= INCREMENT / timePeriod;
            if(next.y < y) y -= INCREMENT / timePeriod;
            pathStage += 1/timePeriod;
        } else if(pathStage >= path.size()){
            goToNextActivity(building);
        }

        //Get target coordinates
        Activity a = getCurrentActivity();
        float targetX = a.getX();
        float targetY = a.getY();

        if(x > targetX - 5 && x < targetX + 5 && y > targetY - 5 && y < targetY + 5){
            goToNextActivity(building);
        }
    }

    private ArrayList<Coordinate> findPath(Building building, float startX, float startY, float targetX, float targetY){
        //Create lists to store data
        ArrayList<Coordinate> openList = new ArrayList<>();
        ArrayList<Coordinate> closedList = new ArrayList<>();

        //Add starting node
        openList.add(new Coordinate(startX, startY, 0, Helper.distance(startX,startY,targetX, targetY)));

        int counter = 0;

        //Loop until we find the target node
        while(openList.size() > 0){

            //Find the lowest f value in the list
            int currentIndex = 0;
            Coordinate currentNode = openList.get(currentIndex);

            //System.out.println("current node x: " + currentNode.x + " y: " + currentNode.y + " f: " + currentNode.f);

            for(Coordinate c: openList){
                //System.out.println("open list " + counter +" x: " + c.x + " y: " + c.y + " f: " + c.f);
            }
            counter ++;
            if(counter > 1000){
                System.out.println("Open List Size: " + openList.size());
            }

            for(int i = 0; i < openList.size(); i++){
                if(openList.get(i).f < currentNode.f){
                    currentNode = openList.get(i);
                    currentIndex = i;
                }
            }

            System.out.println("current node x: " + currentNode.x + " y: " + currentNode.y + " f: " + currentNode.f);

            openList.remove(currentIndex);
            closedList.add(currentNode);

            //Checking if we have reached the target
            if(Helper.approximatelyEqual(currentNode.x, targetX, 4) && Helper.approximatelyEqual(currentNode.y, targetY, 4)){
                System.out.println("Target Reached");

                 ArrayList<Coordinate> pathFromEnd = new ArrayList<>();
                 pathFromEnd.add(currentNode);
                 while(currentNode.parent != null){
                     currentNode = currentNode.parent;
                     pathFromEnd.add(currentNode);
                 }

                 Collections.reverse(pathFromEnd);
                 return pathFromEnd;
            }

            ArrayList<Coordinate> neighbours = currentNode.generateNeighbours();

            for(Coordinate c: neighbours){

                //If neighbouring node is already in the closed list
                if(coordinateInList(closedList, c.x,c.y)){
                    continue;
                }

                //If node is not traversable, add to closed list
                if(!building.isTraversable(c.x, c.y)){
                    closedList.add(c);
                    continue;
                }

                //Calculate values
                c.g = currentNode.g + 4;
                c.h = Helper.distance(c.x, c.y, targetX, targetY);
                c.f = c.h + c.g;

                useLowestGValue(openList, c);
            }
        }
        return path;
    }

    private boolean coordinateInList(ArrayList<Coordinate> list, float x, float y){
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).x == x && list.get(i).y == y){
                return true;
            }
        }
        return false;
    }

    private void useLowestGValue(ArrayList<Coordinate> list, Coordinate c){
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).x == c.x && list.get(i).y == c.y){
                if(list.get(i).g > c.g){
                    list.get(i).g = c.g;
                    return;
                }
                else{
                    return;
                }
            }
        }
        //If coordinate is not already in the list
        list.add(c);
    }

    private void goToNextActivity(Building building){
        if(activityCount < activities.size() - 1) activityCount++;
        else activityCount = 0;

        path = findPath(building,x,y,getCurrentActivity().getX(), getCurrentActivity().getY());
        pathStage = 0;
    }
}
