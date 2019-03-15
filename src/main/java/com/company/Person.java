package com.company;

import javafx.scene.paint.Color;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;

public class Person extends AbstractObject{

    private Entrance entrance;
    private final int RADIUS = 8;
    private Color colour;
    private ArrayList<Activity> activities;
    private String name;
    private ArrayList<PersonData> data;

    //These variables are only used internally
    private int activityCount;
    private ArrayList<Coordinate> path;
    private float pathStage;
    private Activity currentActivity;
    private boolean dayFinished;

    private int toiletNeed;
    private final int toiletMax = 500;
    private int hunger;
    private final int hungerMax = 2000;

    //Constructors----------------------------------------------------------------------------

    public Person(){
        id = UUID.randomUUID().toString();

        //Initialise array
        this.activities = new ArrayList<>();
        name = "default name";

        //Setting internal variables to defaults
        hunger = 0;
        toiletNeed = 0;
        activityCount = 0;
        pathStage = 0;
        path = new ArrayList<>();
        data = new ArrayList<>();
        dayFinished = false;
    }

    public Person(float x, float y){
        this();

        this.x = x;
        this.y = y;
        colour = Color.GREEN;
    }

    public Person(String name, float x, float y, Color colour, ArrayList<Activity> schedule){
        this();

        this.x = x;
        this.y = y;
        this.colour = colour;
        this.activities = schedule;
        this.name = name;

        if(activities != null) {
            if(activities.size() > 0)currentActivity = activities.get(activityCount);
            else currentActivity = null;
        }
        else currentActivity = null;
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
        for(Activity a: activities){
            if(a.getLocation() == null){
                a.setLocation(Controller.searchForRoomByPoint(a.getX(), a.getY()));
            }
        }
        return activities;
    }

    public Activity getCurrentActivity(Building building){
        if(currentActivity == null) return null;
        if(dayFinished) return currentActivity;

        if(toiletNeed >= toiletMax){
            //If the person needs the toilet, go to toilet
            if(Controller.searchForRoomByPoint(getX(),getY()).getType().equals("Toilet")) {
                toiletNeed = 0;
            }

            if(!currentActivity.getLocation().getType().equals("Toilet")){
                pathStage = 0;
                Room nearestToilet = building.searchForNearestToilet(getX(), getY());
                currentActivity = new Activity(Controller.getHour(), nearestToilet);
                path = findPath(building,x,y,currentActivity.getX(), currentActivity.getY());
                return currentActivity;
            }
            return currentActivity;
        } else {
            //If they don't need the toilet, follow timetable
            if(activityCount + 1 >= activities.size()){
                //No more activities left- leave the building
                currentActivity = new Activity(Controller.getHour() + 1, entrance.getX(), entrance.getY());
                path = findPath(building,x,y,currentActivity.getX(), currentActivity.getY());
                pathStage = 0;
                dayFinished = true;
                return currentActivity;
            } else {
                if(Controller.getHour() < activities.get(activityCount + 1).getTime()){
                    //We are already on the correct activity
                    return currentActivity;
                } else {
                    //Get the next activity
                    activityCount++;
                    currentActivity = activities.get(activityCount);
                    path = findPath(building,x,y,currentActivity.getX(), currentActivity.getY());
                    pathStage = 0;
                    return currentActivity;
                }
            }
        }
    }

    public String getName(){
        return name;
    }

    public String getId(){
        return id;
    }

    public void addActivity(Activity activity){
        activities.add(activity);
    }

    private static Activity generateRandomActivity(){
        Random random = new Random();
        int timeNum = random.nextInt(8) + 9;
        int roomNum = random.nextInt(Controller.getRoomInfo().size() - 1);
        Activity activity = new Activity(timeNum, Controller.getRoomInfo().get(roomNum));
        return activity;
    }

    public static ArrayList<Activity> generateRandomSchedule(){
        Random rnd = new Random();
        int numberOfActivities = 2 + rnd.nextInt(6);
        ArrayList schedule = new ArrayList<Activity>();

        for(int i = 0; i < numberOfActivities; i++){
            schedule.add(generateRandomActivity());
        }

        return schedule;
    }

    public void setRandomSchedule(){
        activities.clear();
        activities.addAll(generateRandomSchedule());
    }

    public void setSchedule(ArrayList<Activity> schedule){
        activities = schedule;
        if(activities != null) {
            if(activities.size() > 0) currentActivity = activities.get(0);
        }
    }

    public void setName(String name){
        this.name = name;
    }

    public Element getXML(Document doc){
        Element personElement = doc.createElement("Person");

        Element nameElement = doc.createElement("name");
        nameElement.setTextContent(getName());
        personElement.appendChild(nameElement);

        Element x1Element = doc.createElement("x");
        x1Element.setTextContent("" + getX());
        personElement.appendChild(x1Element);

        Element y1Element = doc.createElement("y");
        y1Element.setTextContent("" + getY());
        personElement.appendChild(y1Element);

        Element colourElement = doc.createElement("Colour");
        colourElement.setTextContent(Helper.colorToRGBCode(getColour()));
        personElement.appendChild(colourElement);
        //System.out.println("" + Helper.colorToRGBCode(p.getColour()));

        Element scheduleElement = doc.createElement("Schedule");

        for(Activity a: getSchedule()){
            Element activityElement = doc.createElement("Activity");
            Element activityXElement = doc.createElement("ActivityX");
            Element activityYElement = doc.createElement("ActivityY");
            Element activityTimeElement = doc.createElement("ActivityTime");

            activityXElement.setTextContent("" + a.getX());
            activityYElement.setTextContent("" + a.getY());
            activityTimeElement.setTextContent("" + a.getTime());
            activityElement.appendChild(activityXElement);
            activityElement.appendChild(activityYElement);
            activityElement.appendChild(activityTimeElement);

            scheduleElement.appendChild(activityElement);
        }

        personElement.appendChild(scheduleElement);
        return personElement;
    }

    public void recordPersonData(PersonData personData){
        data.add(personData);
    }

    public ArrayList<PersonData> getPersonData() {
        return data;
    }

    public ArrayList<CategoricData> generateRoomTypeReport(){
        ArrayList<CategoricData> types = new ArrayList<>();
        boolean exists = false;

        for(PersonData pd: data){
            for(CategoricData cd: types){
                if(pd.getRoomType().equals(cd.category)){
                    cd.occurances++;
                    exists = true;
                    break;
                }
            }

            if(!exists){
                types.add(new CategoricData(pd.getRoomType()));
            }
            exists = false;
        }

        return types;
    }

    public ArrayList<CategoricData> generateRoomNameReport(){
        ArrayList<CategoricData> types = new ArrayList<>();
        boolean exists = false;

        for(PersonData pd: data){
            for(CategoricData cd: types){
                if(pd.getRoomName().equals(cd.category)){
                    cd.occurances++;
                    exists = true;
                    break;
                }
            }

            if(!exists){
                types.add(new CategoricData(pd.getRoomType()));
            }
            exists = false;
        }

        return types;
    }

    public void setEntrance(Entrance entrance) {
        this.entrance = entrance;
    }

    public void resetToStartOfDay(){
        dayFinished = false;

        //Reset activities
        pathStage = 0;
        activityCount = 0;
        if(activities != null) {
            if(activities.size() > 0)currentActivity = activities.get(activityCount);
            else currentActivity = null;
        }
        else currentActivity = null;
    }

    //Pathfinding and Movement------------------------------------------------------------------

    public void iterate(Building building, float timePeriod){
        move(building,timePeriod);
        toiletNeed++;
        hunger++;
    }

    public void move(Building building, float timePeriod){
        //Get target coordinates
        if(currentActivity == null) return;
        Activity a = getCurrentActivity(building);
        float targetX = a.getX();
        float targetY = a.getY();

        //Set up initial path only
        if(activities.size() > 0 && path.size() == 0) path = findPath(building,x,y,targetX, targetY);

        if(path.size() == 0) return;

        final float INCREMENT = 4;

        //Move if there's a path to follow and we are not at the target
        if(path.size() > 1 && pathStage < path.size() && !atTargetPosition(building)){
            Coordinate next = path.get((int)Math.floor(pathStage));
            if(next.x > x) x += INCREMENT / timePeriod;
            if(next.y > y) y += INCREMENT / timePeriod;
            if(next.x < x) x -= INCREMENT / timePeriod;
            if(next.y < y) y -= INCREMENT / timePeriod;
            pathStage += 1/timePeriod;
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

            counter ++;
            if(counter > 3000){
                System.out.println("Open List Size: " + openList.size());
                //If there is no possible path
                return null;
            }


            for(int i = 0; i < openList.size(); i++){
                if(openList.get(i).f < currentNode.f){
                    currentNode = openList.get(i);
                    currentIndex = i;
                }
            }

            System.out.println("current node x: " + currentNode.x + " y: " + currentNode.y + " f: " + currentNode.f);

            openList.remove(currentNode);
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

    private boolean atTargetPosition(Building building){
        Activity a = getCurrentActivity(building);
        float targetX = a.getX();
        float targetY = a.getY();

        if(x > targetX - 5 && x < targetX + 5 && y > targetY - 5 && y < targetY + 5){
            return true;
        }
        else return false;
    }


}
