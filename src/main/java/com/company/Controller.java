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
import java.util.ArrayList;

public class Controller {

    private static Building building;
    private static float timer;
    private static final float WALL_WIDTH = 5;
    private static File currentFile;
    public static Person tempPerson;

    public static void doInitialSetup(){
        building = new Building();
        timer = 0;
        tempPerson = new Person(0,0);
    }

    public static ArrayList<Wall> getWallLocations(){
        return building.getWalls();
    }

    public static ArrayList<Person> getPeopleLocations(){
        return building.getPeople();
    }

    public static void doIteration(float speed){
        float timePeriod = 60/speed;
        building.iterate(timePeriod);
    }

    public static void saveAs(File file){

        try{
            //Create document builder
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            //Add root element
            Element rootElement = doc.createElement("RootElement");
            doc.appendChild(rootElement);

            //Store information about building layout
            Element buildingElement = doc.createElement("Building");
            rootElement.appendChild(buildingElement);

            for(Wall w: getWallLocations()){
                Element wallElement = doc.createElement("Wall");

                Element x1Element = doc.createElement("x1");
                x1Element.setTextContent("" + w.getX1());
                wallElement.appendChild(x1Element);

                Element x2Element = doc.createElement("x2");
                x2Element.setTextContent("" + w.getX2());
                wallElement.appendChild(x2Element);

                Element y1Element = doc.createElement("y1");
                y1Element.setTextContent("" + w.getY1());
                wallElement.appendChild(y1Element);

                Element y2Element = doc.createElement("y2");
                y2Element.setTextContent("" + w.getY2());
                wallElement.appendChild(y2Element);

                buildingElement.appendChild(wallElement);
            }

            for (Person p: getPeopleLocations()){
                Element personElement = doc.createElement("Person");

                Element x1Element = doc.createElement("x");
                x1Element.setTextContent("" + p.getX());
                personElement.appendChild(x1Element);

                Element y1Element = doc.createElement("y");
                y1Element.setTextContent("" + p.getY());
                personElement.appendChild(y1Element);

                Element colourElement = doc.createElement("Colour");
                colourElement.setTextContent(Helper.colorToRGBCode(p.getColour()));
                System.out.println("" + Helper.colorToRGBCode(p.getColour()));

                Element scheduleElement = doc.createElement("Schedule");

                for(Activity a: p.getSchedule()){
                    Element activityElement = doc.createElement("Activity");
                    Element activityXElement = doc.createElement("ActivityX");
                    Element activityYElement = doc.createElement("ActivityY");

                    activityXElement.setTextContent("" + a.getX());
                    activityYElement.setTextContent("" + a.getY());
                    activityElement.appendChild(activityXElement);
                    activityElement.appendChild(activityYElement);

                    scheduleElement.appendChild(activityElement);
                }

                personElement.appendChild(scheduleElement);
            }

            //Rename file
            File file2 = new File(file.getAbsolutePath() + ".xml");

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file2);

            currentFile = file;
            transformer.transform(source, result);

        } catch(Exception e){
            System.out.println("Error occured while using saveAs");
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

            NodeList nodeList = doc.getElementsByTagName("Wall");
            ArrayList<Wall> wallList = new ArrayList<>();

            for(int i = 0; i < nodeList.getLength(); i++) {
                if(nodeList.item(i).getNodeType() == Node.ELEMENT_NODE){
                    Element current = (Element)nodeList.item(i);
                    float x1 = Float.parseFloat(current.getElementsByTagName("x1").item(0).getTextContent());
                    float x2 = Float.parseFloat(current.getElementsByTagName("x2").item(0).getTextContent());
                    float y1 = Float.parseFloat(current.getElementsByTagName("y1").item(0).getTextContent());
                    float y2 = Float.parseFloat(current.getElementsByTagName("y2").item(0).getTextContent());
                    buildingNew.addWall(new Wall(x1,x2,y1,y2));
                }
                else{
                    System.out.println("Could not read file successfully");
                    return buildingNew;
                }
            }

            NodeList personNodeList = doc.getElementsByTagName("Person");
            ArrayList<Person> personList = new ArrayList<>();

            for(int i = 0; i < personNodeList.getLength(); i++){
                if(nodeList.item(i).getNodeType() == Node.ELEMENT_NODE){
                    Element current = (Element)nodeList.item(i);
                    float x = Float.parseFloat(current.getElementsByTagName("x").item(0).getTextContent());
                    float y = Float.parseFloat(current.getElementsByTagName("y").item(0).getTextContent());
                    Color color = Color.web(current.getElementsByTagName("Colour").item(0).getTextContent());

                    ArrayList<Activity> schedule = new ArrayList<>();
                    Element scheduleElement = (Element) current.getElementsByTagName("Schedule").item(0);
                    NodeList activityNodes = scheduleElement.getElementsByTagName("Activity");
                    for(int j = 0; j < activityNodes.getLength(); j++){
                        Element activityElement = (Element)activityNodes.item(j);
                        float activityX = Float.parseFloat(activityElement.getElementsByTagName("ActivityX").item(0).getTextContent());
                        float activityY = Float.parseFloat(activityElement.getElementsByTagName("ActivityY").item(0).getTextContent());
                        schedule.add(new Activity(activityX, activityY));
                    }
                    buildingNew.addPerson(new Person(x,y,color,schedule));
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

    public static void addWallAtLocation(float x, float y, float width, float height){
        Wall wall = new Wall(x,x+width, y, y+height);
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

    public Person addPersonAt(float x, float y){
        Person person = new Person(x,y);
        building.addPerson(person);
        return person;
    }

    public static Person addPersonWithSchedule(float x, float y, Color colour, ArrayList<Activity> schedule){
        Person person = new Person(x,y,colour, schedule);
        building.addPerson(person);
        return person;
    }

    public static void createNewBuilding(File file){
        if(file != null) {
            building = new Building();
            currentFile = file;
            save();
        } else{
            System.out.println("Cannot create new building file");
        }
    }

    public static boolean currentFileExists(){
        if(currentFile != null) return true;
        else return false;
    }

}
