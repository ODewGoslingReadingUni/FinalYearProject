package com.company;

import javafx.scene.paint.Color;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.time.LocalTime;
import java.util.ArrayList;

public class Controller {

    private static Building building;
    private static final float WALL_WIDTH = UserInterface.WALL_THICKNESS;
    private static File currentFile;
    private static UserInterface activeUI;
    public static Person tempPerson;
    private static int tick;
    private static LocalTime time;

    //Setup methods

    public static void doInitialSetup(UserInterface ui){
        building = new Building();
        tempPerson = new Person(0,0);
        activeUI = ui;
        tick = 0;
        time = LocalTime.of(9,0,0);
    }

    public static void reset(){
        tick = 0;
        time = LocalTime.of(9,0,0);
        building.resetToStart();
    }

    //Getter methods

    public static ArrayList<Wall> getWallLocations(){
        ArrayList<Wall> walls = new ArrayList<>();
        walls.addAll(building.getWalls());

        for(Room r: building.getRooms()){
            walls.addAll(r.getWalls());
        }
        return walls;
    }

    public static ArrayList<Wall> getWallList(){
        ArrayList<Wall> walls = new ArrayList<>();
        walls.addAll(building.getWalls());
        return walls;
    }

    public static ArrayList<Person> getPeopleLocations(){
        return building.getPeople();
    }

    public static ArrayList<Room> getRoomInfo(){
        return building.getRooms();
    }

    public static ArrayList<Door> getDoorLocations(){
        return building.getDoors();
    }

    public static int getHour(){
        return time.getHour();
    }

    public static LocalTime getTime(){
        return time;
    }

    //Iteration and calculation methods
    public static void doIteration(float speed){
        float timePeriod = 60/speed;
        building.iterate(timePeriod);
        if(building.evacuationIsFinished()){
            System.out.println("show evacuation data");
            activeUI.showEvacuationData(building.getEvacuationStartTime(), building.getEvacuationEndTime() ,building.getEvacuationDataAsNumericData());
            reset();
            activeUI.pauseAnimation();
        }
        time = time.plusSeconds((long)speed/10);
        tick++;
    }

    public static void runFullDay(){
        time = LocalTime.of(9,0,0);
        while(time.getHour() < 18){
            doIteration(10);
        }
        reset();
    }

    //File stuff

    public static void saveAs(File file){

        try{
            //Create document builder
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            //Add root element
            Element rootElement = doc.createElement("RootElement");
            doc.appendChild(rootElement);

            //Information about state of simulation
            Element simulationElement = doc.createElement("simulation");

            Element hoursElement = doc.createElement("hours");
            hoursElement.setTextContent(""+ getHour());
            simulationElement.appendChild(hoursElement);

            Element minutesElement = doc.createElement("minutes");
            minutesElement.setTextContent("" + time.getMinute());
            simulationElement.appendChild(minutesElement);

            rootElement.appendChild(simulationElement);

            //Store information about building layout
            Element buildingElement = doc.createElement("Building");
            rootElement.appendChild(buildingElement);

            for(Wall w: getWallList()){
                buildingElement.appendChild(w.getXML(doc, "Wall"));
            }

            for (Person p: getPeopleLocations()){
                buildingElement.appendChild(p.getXML(doc));
            }

            for(Room r: getRoomInfo()){
                buildingElement.appendChild(r.getXML(doc));
            }

            for(Door d: getDoorLocations()){
                buildingElement.appendChild(d.getXML(doc));
            }

            //Rename file
            File file2;
            System.out.println(Helper.getFileExtension(file));

            if(Helper.getFileExtension(file).equals("xml")){
                file2 = new File(file.getAbsolutePath());
            } else{
                file2 = new File(file.getAbsolutePath() + ".xml");
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file2);

            currentFile = file;
            transformer.transform(source, result);

        } catch(Exception e){
            System.out.println("Did not save file.");
        }
    }

    public static void save(){
        if(currentFile != null) saveAs(currentFile);
    }

    public static void openFile(File file){
        building = openXMLFile(file);
    }

    public static Building openXMLFile(File file){
        Building buildingNew = new Building();

        try {
            File xmlFile = file;

            if(!Helper.getFileExtension(file).equals("xml")){
                System.out.println("File not xml format. Could not open file");
                return buildingNew;
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            //Was recommended to do this
            doc.getDocumentElement().normalize();

            //Get information about simulation state
            int hours, minutes;
            if(doc.getElementsByTagName("simulation").getLength() > 0){
                Element simulationElem = (Element)doc.getElementsByTagName("simulation").item(0);
                hours = Integer.parseInt(simulationElem.getElementsByTagName("hours").item(0).getTextContent());
                minutes = Integer.parseInt(simulationElem.getElementsByTagName("minutes").item(0).getTextContent());
            }
            else{
                hours = 9;
                minutes = 0;
            }


            time = LocalTime.of(hours, minutes);

            //Get information about walls
            NodeList wallNodeList = doc.getElementsByTagName("Wall");

            for(int i = 0; i < wallNodeList.getLength(); i++) {
                if(wallNodeList.item(i).getNodeType() == Node.ELEMENT_NODE){
                    Element current = (Element)wallNodeList.item(i);
                    buildingNew.addWall(processWallElement(current));
                }
                else{
                    System.out.println("Could not read file successfully");
                    return buildingNew;
                }
            }

            NodeList roomNodeList = doc.getElementsByTagName("Room");

            System.out.println("Parsing rooms");

            for(int i = 0; i < roomNodeList.getLength(); i++){
                System.out.println("Rooms found");
                if(roomNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element current = (Element) roomNodeList.item(i);
                    String name = current.getElementsByTagName("name").item(0).getTextContent();
                    float x = Float.parseFloat(current.getElementsByTagName("x").item(0).getTextContent());
                    float y = Float.parseFloat(current.getElementsByTagName("y").item(0).getTextContent());
                    float width = Float.parseFloat(current.getElementsByTagName("width").item(0).getTextContent());
                    float height = Float.parseFloat(current.getElementsByTagName("height").item(0).getTextContent());
                    String type = current.getElementsByTagName("type").item(0).getTextContent();

                    NodeList wallNodes = current.getElementsByTagName("RoomWall");
                    System.out.println("Number of walls found:" + wallNodes.getLength());
                    ArrayList<Wall> walls = new ArrayList<>();
                    if (wallNodes.getLength() > 0) {
                        for (int j = 0; j < wallNodes.getLength(); j++) {
                            Element wallElem = (Element) wallNodes.item(j);
                            walls.add(processWallElement(wallElem));
                            System.out.println("Wall Array Size:" + walls.size());
                        }
                        buildingNew.addRoom(new Room(x,y,width, height,type,name, walls));
                    }
                    else{
                        buildingNew.addRoom(new Room(x,y,width,height,type,false,name));
                    }
                }
            }

            NodeList doorNodeList = doc.getElementsByTagName("Door");

            System.out.println("Parsing doors");

            for(int i = 0; i < doorNodeList.getLength(); i++){
                System.out.println("Doors found");
                if(doorNodeList.item(i).getNodeType() == Node.ELEMENT_NODE){
                    Element current = (Element) doorNodeList.item(i);
                    float x = Float.parseFloat(current.getElementsByTagName("x").item(0).getTextContent());
                    float y = Float.parseFloat(current.getElementsByTagName("y").item(0).getTextContent());
                    float width = Float.parseFloat(current.getElementsByTagName("width").item(0).getTextContent());
                    float height = Float.parseFloat(current.getElementsByTagName("height").item(0).getTextContent());
                    String type = current.getElementsByTagName("doorType").item(0).getTextContent();

                    if(type.equals("Entrance")){
                        String name = current.getElementsByTagName("name").item(0).getTextContent();
                        Entrance entranceToAdd;
                        String entranceID = current.getElementsByTagName("id").item(0).getTextContent();
                        if(width > height) entranceToAdd  = new Entrance(x,y,true, entranceID);
                        else entranceToAdd = new Entrance(x,y,false, entranceID);
                        entranceToAdd.setName(name);
                        buildingNew.addEntrance(entranceToAdd);
                    } else {
                        if(width > height) buildingNew.addDoor(new Door(x,y,true));
                        else buildingNew.addDoor(new Door(x,y,false));
                    }

                }
            }

            NodeList personNodeList = doc.getElementsByTagName("Person");

            for(int i = 0; i < personNodeList.getLength(); i++){
                if(personNodeList.item(i).getNodeType() == Node.ELEMENT_NODE){
                    Element current = (Element)personNodeList.item(i);
                    String name = current.getElementsByTagName("name").item(0).getTextContent();
                    float x = Float.parseFloat(current.getElementsByTagName("x").item(0).getTextContent());
                    float y = Float.parseFloat(current.getElementsByTagName("y").item(0).getTextContent());
                    Color color = Color.web(current.getElementsByTagName("Colour").item(0).getTextContent());
                    String entranceID = current.getElementsByTagName("entranceID").item(0).getTextContent();
                    ArrayList<Activity> schedule = new ArrayList<>();
                    Element scheduleElement = (Element) current.getElementsByTagName("Schedule").item(0);
                    NodeList activityNodes = scheduleElement.getElementsByTagName("Activity");
                    for(int j = 0; j < activityNodes.getLength(); j++){
                        Element activityElement = (Element)activityNodes.item(j);
                        float activityX = Float.parseFloat(activityElement.getElementsByTagName("ActivityX").item(0).getTextContent());
                        float activityY = Float.parseFloat(activityElement.getElementsByTagName("ActivityY").item(0).getTextContent());
                        int activityTime = Integer.parseInt(activityElement.getElementsByTagName("ActivityTime").item(0).getTextContent());
                        schedule.add(new Activity(activityTime,activityX, activityY));
                    }
                    Entrance personEntrance = buildingNew.searchForEntranceById(entranceID);
                    System.out.println("entrance: " + personEntrance);
                    buildingNew.addPerson(new Person(name, x,y,color,schedule, personEntrance));
                }
            }

            //File successfully opened
            currentFile = file;
            return buildingNew;

        }catch (Exception e){
            System.out.println("Failed to open file");
            return buildingNew;
        }

    }

    public static boolean currentFileExists(){
        if(currentFile != null) return true;
        else return false;
    }

    //Adding and editing methods

    public static void createNewBuilding(File file){
        if(file != null) {
            building = new Building();
            currentFile = file;
            save();
        } else{
            System.out.println("Cannot create new building file");
        }
    }

    public static void addWallAtLocation(float x, float y, float width, float height){
        Wall wall = new Wall(x, y, width, height);
        building.addWall(wall);
    }

    public static void addWallAtLocation(float x, float y, float length, String direction){
        if(direction.equals("Horizontal")){
            addWallAtLocation(x,y,length, WALL_WIDTH);
        }
        else{
            addWallAtLocation(x,y,WALL_WIDTH, length);
        }
    }

    public static void addDoorAtLocation(float x, float y, String type){
        String wallId = building.checkForCollisionWithWall(x,y);

        if(wallId != null){
            Wall w = building.searchForWallById(wallId);
            boolean isHorizontal = w.isHorizontal();
            if(isHorizontal){
                y = w.getY();
            } else {
                x = w.getX();
            }

            if(type.equals("Entrance")){
                Entrance entrance = new Entrance(x,y,isHorizontal);
                System.out.println("DOOR TYPE entrance");
                building.addEntrance(entrance);
            }
            else{
                Door door = new Door(x,y,isHorizontal);
                System.out.println("DOOR TYPE normal");
                building.addDoor(door);
            }

        } else{
            System.out.println("Cannot add door, point is not on a wall");
        }
        activeUI.updateEditPane();
    }

    public static void upsertPerson(Person person){
        building.upsertPerson(person);
    }

    public static void editWall(Wall wall){
        building.upsertWall(wall);
    }

    public static void upsertRoom(Room room){
        building.upsertRoom(room);
    }

    public static void upsertDoor(String id, Door door){
        building.upsertDoor(id, door);
    }

    //Search methods

    public static Person searchForPerson(String id){
        return building.searchForPersonById(id);
    }

    public static ArrayList<PersonData> getPersonData(String id){
        Person person = searchForPerson(id);
        return person.getPersonData();
    }

    public static Wall searchForWall(String id){
        return building.searchForWallById(id);
    }

    public static Room searchForRoom(String id){
        return building.searchForRoomById(id);
    }

    public static Room searchForRoomByName(String name){
        return building.searchForRoomByName(name);
    }

    public static Door searchForDoor(String id){
        return building.searchForDoorById(id);
    }

    public static Entrance searchForEntrance(String id){
        return building.searchForEntranceById(id);
    }

    public static Room searchForRoomByPoint(float x, float y){
        return building.searchForRoomByPoint(x,y);
    }

    //Delete methods

    public static void deletePerson(String id){
        building.deletePerson(id);
    }

    public static void deleteWall(String id){
        building.deleteWall(id);
        doUIUpdate();
    }

    public static void deleteRoom(String id){
        building.deleteRoom(id);
        doUIUpdate();
    }

    public static void deleteDoor(String id){
        building.deleteDoor(id);
    }

    //Miscellaneous methods

    public static void doUIUpdate(){
        activeUI.updateEditPane();
    }

    public static void triggerFireAlarm(){
        building.triggerFireAlarm();

    }

    private static Wall processWallElement(Element element){

        float x1 = Float.parseFloat(element.getElementsByTagName("x").item(0).getTextContent());
        float x2 = Float.parseFloat(element.getElementsByTagName("x2").item(0).getTextContent());
        float y1 = Float.parseFloat(element.getElementsByTagName("y").item(0).getTextContent());
        float y2 = Float.parseFloat(element.getElementsByTagName("y2").item(0).getTextContent());

        float width = Math.abs(x1 - x2);
        float height = Math.abs(y1 - y2);

        return new Wall(x1,y1,width,height);
    }

    public static int getTick(){
        return tick;
    }

    public static ArrayList<String> getEntranceNames(){
        return building.getEntranceNames();
    }

    public static Entrance findEntranceByName(String name){
        return building.findEntranceByName(name);
    }

    //Validation

    public static String validateEntranceName(String entranceName, String entranceId){
        String validationError = building.checkEntranceNameIsUnique(entranceName, entranceId);
        return validationError;
    }
}
