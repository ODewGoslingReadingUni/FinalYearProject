package com.company;

import javafx.scene.paint.Color;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.nio.file.Path;
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

    private String state;
    private String pathType;
    private boolean dayFinished;
    private float targetX;
    private float targetY;

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

        state = "normal";
        pathType = state;
        targetX = 0;
        targetY = targetX;
    }

    public Person(float x, float y){
        this();

        this.x = x;
        this.y = y;
        colour = Color.GREEN;
    }

    public Person(String name, float x, float y, Color colour, ArrayList<Activity> schedule, Entrance entrance){
        this();

        this.x = x;
        this.y = y;
        this.colour = colour;
        this.activities = schedule;
        this.name = name;
        this.entrance = entrance;

        if(activities != null) {
            if(activities.size() > 0)currentActivity = activities.get(activityCount);
            else currentActivity = null;
        }
        else currentActivity = null;
    }

    //Getters and setters-----------------------------------------------------------------------

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

    public void setState(String state){
        this.state = state;
    }

    public ArrayList<Activity> getSchedule(){
        for(Activity a: activities){
            if(a.getLocation() == null){
                a.setLocation(Controller.searchForRoomByPoint(a.getX(), a.getY()));
            }
        }
        return activities;
    }

    public String getName(){
        return name;
    }

    public String getState(){
        return state;
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

        Element entranceElement = doc.createElement("entranceID");
        entranceElement.setTextContent("" + entrance.getId());
        personElement.appendChild(entranceElement);

        Element colourElement = doc.createElement("Colour");
        colourElement.setTextContent(Helper.colorToRGBCode(getColour()));
        personElement.appendChild(colourElement);

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

    public void setEntrance(Entrance entrance) {
        this.entrance = entrance;
    }

    //Data and reporting

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
                types.add(new CategoricData(pd.getRoomName()));
            }
            exists = false;
        }

        return types;
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
        toiletNeed++;
        hunger++;

        if(state.equals("outside")){
            System.out.println("outside state");
            return;
        }

        if(state.equals("alarm")){
            //Go to nearest exit
            System.out.println("Alarm state");
            if(pathType.equals("alarm")){
                move(timePeriod);
                System.out.println("moving");
                if(atTarget(x,y,targetX,targetY)){
                    state = "outside";
                }
            } else {
                Entrance nearestExit = building.searchForNearestExit(getX(),getY());
                targetX = nearestExit.x + nearestExit.width/2;
                targetY = nearestExit.y + nearestExit.height/2;
                path = Pathfinding.findPath(building,x,y,targetX,targetY);
                pathStage = 0;
                pathType = "alarm";
            }
        }
        else if(state.equals("toilet")){
            System.out.println("toilet state");
            //Person needs to visit toilet
            if(pathType.equals("toilet")){
                move(timePeriod);
                if(atTarget(x,y,targetX, targetY)){
                    toiletNeed = 0;
                    state = "normal";
                }
            } else {
                Room nearestToilet = building.searchForNearestToilet(getX(), getY());
                Coordinate target = nearestToilet.getRandomPointInRoom();
                targetX = target.x;
                targetY = target.y;
                path = Pathfinding.findPath(building, x,y,targetX, targetY);
                pathStage = 0;
                pathType = "toilet";
            }
        }
        else if(state.equals("normal")){
            //Person follows planned timetable

            System.out.println("normal state");

            if(toiletNeed > toiletMax) {
                state = "toilet";
                return;
            }

            if(pathType.equals("normal") && currentActivity.getTime() == Controller.getTime().getHour()){
                if(currentActivity != null){
                    if(!atTarget(x,y,targetX, targetY)){
                        move(timePeriod);
                    }
                }
            }
            else {
                //Set current activity to the correct one for the time
                setCurrentActivity(building);
            }
        }
        else if(state.equals("leaving")){
            if(atTarget(x,y,targetX, targetY)){
                state = "outside";
            }
            else{
                move(timePeriod);
            }
        }
    }

    public void move(float timePeriod){
        if(hasPath()){
            System.out.println("has path");
            final float INCREMENT = 4;

            //Move if there's a path to follow and we are not at the target
            if(path.size() > 1 && pathStage < path.size() && !atTarget(x,y,targetX, targetY)){
                Coordinate next = path.get((int)Math.floor(pathStage));
                if(next.x > x) x += INCREMENT / timePeriod;
                if(next.y > y) y += INCREMENT / timePeriod;
                if(next.x < x) x -= INCREMENT / timePeriod;
                if(next.y < y) y -= INCREMENT / timePeriod;
                pathStage += 1/timePeriod;
            }
        }

    }

    private boolean atTarget(float x, float y, float targetX, float targetY){

        if(x > targetX - 5 && x < targetX + 5 && y > targetY - 5 && y < targetY + 5){
            return true;
        }
        else return false;
    }

    private boolean hasPath(){
        if(path != null){
            if(path.size() > 0) return true;
            else return false;
        }
        else return false;
    }

    private Activity getActivityByTime(int hour){
        while(hour >= 9){
            for(Activity a: activities){
                if(a.getTime() == hour){
                    return a;
                }
            }
            hour --;
        }
        return null;
    }

    public void reset(Building building){
        //Return to start position
        x = entrance.getX();
        y = entrance.getY();

        //Reset timetable to first activity
        pathStage = 0;
        activityCount = 0;
        if(activities != null) {
            if(activities.size() > 0)currentActivity = activities.get(activityCount);
            else currentActivity = null;
        }
        else currentActivity = null;

        //Reset hunger, thirst, toilet need, etc.
        hunger = 0;
        toiletNeed = 0;
        path = new ArrayList<>();
        data = new ArrayList<>();
        dayFinished = false;

        state = "normal";
        pathType = state;
        targetX = 0;
        targetY = targetX;

        setCurrentActivity(building);
    }

    private void setCurrentActivity(Building building){
        //Set current activity to the correct one for the time
        currentActivity = getActivityByTime(Controller.getTime().getHour());

        if(currentActivity != null){
            if(isLastActivity(currentActivity) && Controller.getHour() > currentActivity.getTime()){
                Entrance nearestExit = building.searchForNearestExit(x,y);
                Coordinate target =  nearestExit.getCenter();
                targetX = target.x;
                targetY = target.y;
                path = Pathfinding.findPath(building,x,y,targetX,targetY);
                pathStage = 0;
                pathType = "leaving";
                state = "leaving";

            } else {
                targetX = currentActivity.getX();
                targetY = currentActivity.getY();
                path = Pathfinding.findPath(building, x,y,targetX, targetY);
                pathStage = 0;
                pathType = "normal";
            }
        }
    }

    private boolean isLastActivity(Activity activity){
        for(Activity a: activities){
            if(activity.getTime() < a.getTime()) return false;
        }
        return true;
    }

}