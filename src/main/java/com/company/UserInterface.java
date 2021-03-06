package com.company;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.stage.WindowEvent;

import java.io.File;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;

public class UserInterface extends Application {

    public static final int WIDTH = 1000;
    public static final int HEIGHT = 550;
    public static final float SPEED_INCREMENT = (float)10;

    public static Stage rootStage;
    private BorderPane root;
    private Canvas canvas;
    private Pane editPane;

    public static boolean playAnimation;
    public static BooleanProperty editMode;
    public static FloatProperty animationSpeed;
    public static StringProperty editButtonProperty;
    public static StringProperty playButtonProperty;
    public static StringProperty timeProperty;

    public static final float WALL_THICKNESS = 6;

    @Override
    public void start(Stage stage) {

        Controller.doInitialSetup(this);
        rootStage = stage;

        editMode = new SimpleBooleanProperty(false);
        animationSpeed = new SimpleFloatProperty((float)10);
        editButtonProperty = new SimpleStringProperty("Edit");
        playButtonProperty = new SimpleStringProperty("Pause");
        timeProperty = new SimpleStringProperty(Controller.getTime().getHour() + " : " + Controller.getTime().getMinute() + " : " + Controller.getTime().getSecond());

        //Set up the scene
        root = createUserInterface();
        Scene scene = new Scene(root);

        AnimationTimer timer = createAnimationTimer(canvas.getGraphicsContext2D());

        //Set up the stage and show it
        rootStage.setTitle("Building Model Version 1.00");
        rootStage.setScene(scene);
        rootStage.show();

        //Play animation
        timer.start();
        playAnimation = true;
        pauseAnimation();
    }

    public static void main(String[] args) {
        launch();
    }

    private BorderPane createUserInterface(){
        canvas = createCanvas();

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(canvas);
        borderPane.setTop(createMenuBar());
        borderPane.setBottom(createToolBar());
        return borderPane;
    }

    //Create UI components
    private Canvas createCanvas(){
        canvas = new Canvas(WIDTH,HEIGHT);

        ContextMenuEdit contextMenuEdit = new ContextMenuEdit(canvas);

        canvas.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent event) {
                contextMenuEdit.contextMenuRequest(event.getScreenX(), event.getScreenY(), event.getX(), event.getY());
            }
        });

        canvas.setOnMouseClicked(mouseEvent -> {
            contextMenuEdit.hideMenu();
        });

        canvas.setOnMousePressed(mouseEvent -> {
            if(mouseEvent.isPrimaryButtonDown()){
                Controller.openObjectByLocation((float)mouseEvent.getX(), (float) mouseEvent.getY());
            }
        });

        canvas.setCursor(Cursor.HAND);

        return canvas;
    }

    private Pane createEditPane(){
        Pane pane = new Pane();
        pane.setPrefWidth(canvas.getWidth());
        pane.setPrefHeight(canvas.getHeight());

        ContextMenuEdit contextMenuEdit = new ContextMenuEdit(pane);
        //ContextMenu contextMenu = contextMenuEdit.getContextMenu();

        pane.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent event) {
                contextMenuEdit.contextMenuRequest(event.getScreenX(), event.getScreenY(), event.getX(), event.getY());
            }
        });

        pane.setOnMouseClicked(mouseEvent -> {
            contextMenuEdit.hideMenu();
        });

        return pane;
    }

    private ToolBar createToolBar(){
        ToolBar toolBar = new ToolBar();

        Button slowDownButton = createSlowDownButton();
        Button pauseButton = createPauseButton();
        Button fastForwardButton = createFastForwardButton();

        toolBar.getItems().add(slowDownButton);
        toolBar.getItems().add(pauseButton);
        toolBar.getItems().add(fastForwardButton);

        toolBar.getItems().add(createEditButton());
        toolBar.getItems().add(createNewPersonButton());

        toolBar.getItems().add(createSpeedLabel());
        toolBar.getItems().add(createNewWallButton());
        toolBar.getItems().add(createTimeLabel());

        return toolBar;
    }

    private MenuBar createMenuBar(){
        MenuBar menuBar = new MenuBar();

        MenuItem menuItemNew = new MenuItem("New");
        menuItemNew.setOnAction(actionEvent -> {
            createNew();
        });

        MenuItem openMenuItem = new MenuItem("Open");
        openMenuItem.setOnAction( actionEvent -> {
            openFile();
        });

        MenuItem saveMenuItem = new MenuItem("Save");
        saveMenuItem.setOnAction( actionEvent -> {
            save();
        });

        MenuItem saveAsMenuItem = new MenuItem("Save As...");
        saveAsMenuItem.setOnAction(actionEvent -> {
            saveAs();
        });

        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.setOnAction(actionEvent -> {
            Platform.exit();
        });

        ArrayList<MenuItem> fileMenuItems = new ArrayList<>();
        fileMenuItems.add(menuItemNew);
        fileMenuItems.add(openMenuItem);
        fileMenuItems.add(saveMenuItem);
        fileMenuItems.add(saveAsMenuItem);
        fileMenuItems.add(exitMenuItem);

        Menu fileMenu = createMenu("File", fileMenuItems);
        menuBar.getMenus().add(fileMenu);

        MenuItem addWallItem = new MenuItem("Add new wall");
        addWallItem.setOnAction(actionEvent -> {
            createEditWallMenu(null);
        });

        MenuItem addPersonItem = new MenuItem("Add new person");
        addPersonItem.setOnAction(actionEvent -> {
            createEditPersonMenu(null);
        });

        MenuItem addRoomItem = new MenuItem("Add new room");
        addRoomItem.setOnAction(actionEvent -> {
            createEditRoomMenu(null);
        });

        ArrayList<MenuItem> editMenuItems = new ArrayList<>();
        editMenuItems.add(addWallItem);
        editMenuItems.add(addPersonItem);
        editMenuItems.add(addRoomItem);

        Menu editMenu = createMenu("Edit", editMenuItems);
        menuBar.getMenus().add(editMenu);

        MenuItem viewReportsItem = new MenuItem("Building Use Report");

        ArrayList<MenuItem> reportingMenuItems = new ArrayList<>();
        reportingMenuItems.add(viewReportsItem);

        Menu reportingMenu = createMenu("Reporting", reportingMenuItems);
        //menuBar.getMenus().add(reportingMenu);

        MenuItem resetItem = new MenuItem("Reset");
        resetItem.setOnAction(actionEvent -> {
            Controller.reset();
        });

        MenuItem simulateDayItem = new MenuItem("Simulate Full Day");
        simulateDayItem.setOnAction(actionEvent -> {
            Controller.runFullDay();
        });

        MenuItem fireAlarmItem = new MenuItem("Trigger Fire Alarm");
        fireAlarmItem.setOnAction(actionEvent -> {
            Controller.triggerFireAlarm();
        });

        ArrayList<MenuItem> simulationMenuItems = new ArrayList<>();
        simulationMenuItems.add(fireAlarmItem);
        simulationMenuItems.add(resetItem);
        simulationMenuItems.add(simulateDayItem);

        Menu simulationMenu = createMenu("Simulation", simulationMenuItems);
        menuBar.getMenus().add(simulationMenu);

        return menuBar;
    }

    private Button createPauseButton(){
        Button button = new Button("Pause");
        button.textProperty().bind(Bindings.createStringBinding(() -> playButtonProperty.get(), playButtonProperty));
        button.setOnAction( (event) ->{
            if(playAnimation) pauseAnimation();
            else resumeAnimation();
        });
        return button;
    }

    private Button createFastForwardButton(){
        Button button = new Button("Faster");
        button.setOnAction((event) ->{
            if(animationSpeed.get() < 30 * SPEED_INCREMENT) animationSpeed.set(animationSpeed.get() + SPEED_INCREMENT);
        });
        return button;
    }

    private Label createSpeedLabel(){
        Label label = new Label("Speed: " + animationSpeed.get());
        label.textProperty().bind(Bindings.createStringBinding(() -> "Speed: " + animationSpeed.get() / 10, animationSpeed));
        return label;
    }

    private Label createTimeLabel(){
        Label label = new Label("Time: " + animationSpeed.get());
        label.textProperty().bind(Bindings.createStringBinding(() -> "Time: " + timeProperty.get(), timeProperty));
        label.setAlignment(Pos.CENTER_RIGHT);
        return label;
    }

    private Button createSlowDownButton(){
        Button button = new Button("Slower");
        button.setOnAction((event) -> {
            if(animationSpeed.get() > 0) animationSpeed.set(animationSpeed.get() - SPEED_INCREMENT);
        });
        return button;
    }

    private Button createNewWallButton(){
        Button addWallButton = new Button("New Wall");
        addWallButton.visibleProperty().bind(Bindings.createBooleanBinding( () -> editMode.get(), editMode));
        addWallButton.managedProperty().bind(Bindings.createBooleanBinding( () -> editMode.get(), editMode));
        addWallButton.setOnAction(actionEvent -> {
            createEditWallMenu(null);
        });
        return addWallButton;
    }

    private Button createNewPersonButton(){
        Button button = new Button("New Person");
        button.visibleProperty().bind(Bindings.createBooleanBinding(() -> editMode.get(), editMode));
        button.managedProperty().bind(Bindings.createBooleanBinding(() -> editMode.get(), editMode));
        button.setOnAction( actionEvent -> {
            createEditPersonMenu(null);
        });
        return button;
    }

    public Stage createEditWallMenu(Wall wall){

        startEditing();

        EditWallMenu editWallMenu = new EditWallMenu(wall);
        Stage editStage = editWallMenu.getEditWallStage();

        return editStage;
    }

    public Stage createEditWallMenu(float x, float y){
        startEditing();

        EditWallMenu editWallMenu = new EditWallMenu(x,y);
        Stage editStage = editWallMenu.getEditWallStage();
        editStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                updateEditPane();
            }
        });

        return editStage;
    }

    public Stage createEditPersonMenu(Person person){

        startEditing();

        EditPersonMenu editPersonMenu = new EditPersonMenu(person);
        Stage editStage = editPersonMenu.getEditPersonStage();

        return editStage;
    }

    public Stage createEditRoomMenu(Room room){
        startEditing();

        EditRoomMenu editRoomMenu = new EditRoomMenu(room);
        Stage editStage = editRoomMenu.getEditRoomStage();

        return editStage;
    }

    public Stage createEditDoorMenu(Door door){
        startEditing();

        EditDoorMenu editDoorMenu = new EditDoorMenu(door);
        Stage editStage = editDoorMenu.getEditDoorStage();

        return editStage;
    }

    public Stage createEditEntranceMenu(Entrance entrance){
        EditEntranceMenu editEntranceMenu = new EditEntranceMenu(entrance);

        return editEntranceMenu.getEditEntranceStage();
    }

    private Menu createMenu(String name, ArrayList<MenuItem> menuItems){
        Menu menu = new Menu(name);
        menu.getItems().addAll(menuItems);
        return menu;
    }

    private Button createEditButton(){
        Button button = new Button("Edit");
        button.textProperty().bind(Bindings.createStringBinding( () -> editButtonProperty.get(), editButtonProperty));
        button.setOnAction( actionEvent -> {
            if(editMode.get()) finishEditing();
            else startEditing();
        });
        return button;
    }

    //Animation methods
    private AnimationTimer createAnimationTimer(GraphicsContext gc){
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if(playAnimation == true) {
                    Controller.doIteration(animationSpeed.get());
                    draw(gc);
                }
            }
        };
        return timer;
    }

    public void pauseAnimation(){
        playAnimation = false;
        playButtonProperty.setValue("Play");
    }

    private void resumeAnimation(){
        playAnimation = true;
        playButtonProperty.setValue("Pause");
        finishEditing();
    }

    private void draw(GraphicsContext gc){
        //Clear canvas
        gc.clearRect(0,0,WIDTH, HEIGHT);
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        //Draw building and people
        drawFloors(gc, Controller.getRoomInfo());
        drawWalls(gc, Controller.getWallLocations());
        drawDoors(gc, Controller.getDoorLocations());
        drawPeople(gc, Controller.getPeopleLocations());

        //Update the field that displays the time with the correct time
        timeProperty.setValue(Controller.getTime().getHour() + " : "
                + Controller.getTime().getMinute() + " : "
                + Controller.getTime().getSecond());
    }

    public void updateUI(){
        draw(canvas.getGraphicsContext2D());
    }

    private void drawPeople(GraphicsContext gc, ArrayList<Person> people){
        for(Person p: people){
            if(!p.getState().equals("outside")){
                gc.setFill(p.getColour());
                gc.fillOval(p.getX() - p.getRadius()/2, p.getY() - p.getRadius()/2, p.getRadius(), p.getRadius());
            }
        }
    }

    private void drawDoors(GraphicsContext gc, ArrayList<Door> doors){

        gc.setLineWidth(3);
        for(Door d: doors){
            if(d.getDoorType().equals("Entrance")) gc.setFill(Color.GRAY);
            else gc.setFill(Color.BROWN);
            gc.fillRect(d.getX(),d.getY(),d.getWidth(),d.getHeight());
        }
    }

    private void drawWalls(GraphicsContext gc, ArrayList<Wall> wallLocations){
        gc.setLineWidth(3);
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.BLACK);

        for(Wall w:wallLocations){
            float width = Math.abs(w.getX() - w.getX2());
            float height = Math.abs(w.getY() - w.getY2());
            gc.fillRect(w.getX(), w.getY(), width, height);
        }
    }

    private void drawFloors(GraphicsContext gc, ArrayList<Room> roomLocations){
        for(Room r: roomLocations){
            gc.setFill(r.getFloorColour());
            gc.setStroke(r.getFloorColour());
            gc.setLineWidth(1);
            gc.fillRect(r.getX()+0.5, r.getY()+0.5, r.getWidth(), r.getHeight());
        }
    }

    //Methods to change application state
    private void startEditing(){
        if(!editMode.get()){
            pauseAnimation();
            editButtonProperty.setValue("Stop Editing");
            editMode.set(true);

            //root.setCenter(editPane);

            //updateEditPane();
        }
    }

    private void finishEditing(){
        editButtonProperty.setValue("Edit");
        editMode.set(false);
        //root.setCenter(canvas);
        draw(canvas.getGraphicsContext2D());
    }

    //Static methods
    public static TextField addTextInputFieldToParent(Pane parent, String name){
        HBox hbox = new HBox();
        TextField tf = new TextField();

        hbox.getChildren().add(new Label(name));
        hbox.getChildren().add(tf);
        parent.getChildren().add(hbox);

        return tf;
    }

    public static ComboBox addComboBoxToParent(Pane parent, String name, ObservableList options){
        Label label = new Label(name);
        HBox hbox = new HBox();

        ComboBox comboBox = new ComboBox(options);
        comboBox.getSelectionModel().select(0);

        hbox.getChildren().add(label);
        hbox.getChildren().add(comboBox);

        parent.getChildren().add(hbox);

        return comboBox;
    }

    public static CheckBox addCheckBoxToParent(Pane parent, String name, boolean checked){
        HBox hbox = new HBox();

        CheckBox checkBox = new CheckBox(name);
        checkBox.setSelected(checked);

        hbox.getChildren().add(checkBox);

        parent.getChildren().add(hbox);

        return checkBox;
    }

    public static ColorPicker addColorPickerToParent(Pane parent, String name){
        HBox hbox = new HBox();

        ColorPicker cp = new ColorPicker();
        cp.setValue(Color.RED);
        Label label = new Label(name);

        hbox.getChildren().add(label);
        hbox.getChildren().add(cp);

        hbox.setMinHeight(32);

        parent.getChildren().add(hbox);
        return cp;
    }

    public void updateEditPane(){
        //editPane = createEditPane();
        editPane.getChildren().clear();

        ArrayList<Room> rooms = Controller.getRoomInfo();
        for(Room r:rooms){
            Rectangle rect = new Rectangle(r.getX()+0.5, r.getY()+0.5, r.getWidth(), r.getHeight());
            rect.setFill(r.getFloorColour());
            rect.setId(r.getId());
            rect.setCursor(Cursor.HAND);
            rect.setStroke(r.getFloorColour());
            rect.setOnMousePressed(mouseEvent -> {
                if(mouseEvent.isPrimaryButtonDown()){
                    Room roomToEdit = Controller.searchForRoom(rect.getId());
                    createEditRoomMenu(roomToEdit);
                }
            });
            editPane.getChildren().add(rect);
            //System.out.println("add rectangle");
        }

        ArrayList<Wall> walls = Controller.getWallLocations();
        for(Wall w: walls) {
            Rectangle rect = new Rectangle(w.getX(), w.getY(), w.getWidth(), w.getHeight());
            rect.setId(w.getId());
            rect.setOnMousePressed(mouseEvent ->{
                if(mouseEvent.isPrimaryButtonDown()){
                    //Look for the right wall
                    Wall wallToEdit  = Controller.searchForWall(rect.getId());

                    //Open menu to edit the wall
                    createEditWallMenu(wallToEdit);
                }
            });
            rect.setCursor(Cursor.HAND);
            editPane.getChildren().add(rect);
            //System.out.println("add rectangle");
        }

        ArrayList<Door> doors = Controller.getDoorLocations();
        for(Door d: doors){
            Rectangle rect = new Rectangle(d.getX(), d.getY(), d.getWidth(), d.getHeight());
            if(d.getDoorType().equals("Entrance")){
                rect.setFill(Color.GRAY);
            }
            else {
                rect.setFill(Color.BROWN);
            }
            rect.setStrokeWidth(0);
            rect.setId(d.getId());
            rect.setCursor(Cursor.HAND);
            rect.setOnMouseClicked(mouseEvent -> {
                Door doorToEdit = Controller.searchForDoor(rect.getId());
                if(doorToEdit.getDoorType().equals("Entrance")){
                    createEditEntranceMenu((Entrance)doorToEdit);
                }else{
                    createEditDoorMenu(doorToEdit);
                }
            });
            editPane.getChildren().add(rect);
            //System.out.println("add rectangle");
        }

        ArrayList<Person> people = Controller.getPeopleLocations();
        for(Person p: people){
            Circle circle = new Circle(p.getX(), p.getY(), p.getRadius()/2, p.getColour());
            circle.setId(p.getId());
            circle.setOnMouseClicked( mouseEvent -> {
                //Get the person record
                Person personToEdit  = Controller.searchForPerson(circle.getId());

                //Open the edit pane
                createEditPersonMenu(personToEdit);
            });
            circle.setCursor(Cursor.HAND);
            editPane.getChildren().add(circle);
            //System.out.println("add circle");
        }
    }

    //File stuff

    private void openFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        File file = fileChooser.showOpenDialog(rootStage);
        Controller.openFile(file);
        Controller.reset();
        finishEditing();
        draw(canvas.getGraphicsContext2D());
    }

    private void saveAs(){
        pauseAnimation();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        File file = fileChooser.showSaveDialog(rootStage);
        Controller.saveAs(file);
    }

    private void save(){
        if(Controller.currentFileExists()) Controller.save();
        else saveAs();
    }

    private void createNew(){
       boolean doOperation = showAlert();
       if(doOperation) {
           FileChooser fileChooser = new FileChooser();
           fileChooser.setTitle("Create New Building");
           File file = fileChooser.showSaveDialog(rootStage);
           Controller.createNewBuilding(file);
       }
    }

    //Showing alerts or dialogs

    private boolean showAlert(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Save current file before starting a new file?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.setTitle("Save current file?");
        Optional<ButtonType> result = alert.showAndWait();

        if(result.isPresent() && result.get() == ButtonType.YES){
            save();
            return true;
        }
        else if(result.isPresent() && result.get() == ButtonType.NO){
            return true;
        }
        else if(result.isPresent() && result.get() == ButtonType.CLOSE || result.get() == ButtonType.CANCEL) {
            return false;
        }
        else return false;
    }

    private ContextMenu createContextMenu(){
        ContextMenu contextMenu = new ContextMenu();

        MenuItem addWallItem = new MenuItem("Add New Wall");
        addWallItem.setOnAction(actionEvent -> {
            createEditWallMenu(null);
        });

        MenuItem addPersonItem = new MenuItem("Add New Person");

        contextMenu.getItems().add(addWallItem);
        contextMenu.getItems().add(addPersonItem);

        return contextMenu;
    }

    //Evacuation data

    public void showEvacuationData(LocalTime startTime, LocalTime endTime, ArrayList<NumericData> evacuationData){
        //Create charts of data to view

        XYChart evacuationChart = UIHelper.makeLineGraph(evacuationData, "Time (seconds)", "Number of people", "Evacuation");

        //Show start and end time
        Duration difference = Duration.between(startTime, endTime);
        long diffSeconds = difference.getSeconds();
        Font font = new Font(16);
        Label startTimeLabel = new Label("Start Time: " +  startTime.toString());
        startTimeLabel.setFont(font);
        Label endTimeLabel = new Label("End Time: " + endTime.toString());
        endTimeLabel.setFont(font);
        Label differenceLabel = new Label("Time Taken: " + diffSeconds + " seconds");
        differenceLabel.setFont(font);

        VBox vbox = new VBox();
        vbox.getChildren().add(startTimeLabel);
        vbox.getChildren().add(endTimeLabel);
        vbox.getChildren().add(differenceLabel);

        //Add charts to grid pane
        GridPane gridPane = new GridPane();
        gridPane.add(vbox, 0, 0);
        gridPane.add(evacuationChart, 1,0);


        //Create stage/scene
        Stage reportStage = new Stage();
        reportStage.setTitle("Evacuation Report");
        Scene reportScene = new Scene(gridPane);
        reportStage.setScene(reportScene);

        reportStage.show();
    }

}