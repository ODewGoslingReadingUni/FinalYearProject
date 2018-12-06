package com.company;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

public class UserInterface extends Application {

    public static final int WIDTH = 640;
    public static final int HEIGHT = 480;
    public static final float SPEED_INCREMENT = (float)5;

    public static Stage rootStage;
    private BorderPane root;
    private Canvas canvas;
    private Pane editPane;

    public static boolean playAnimation;
    public static BooleanProperty editMode;
    public static FloatProperty animationSpeed;
    public static StringProperty editButtonProperty;
    public static StringProperty playButtonProperty;

    @Override
    public void start(Stage stage) {

        Controller.doInitialSetup();
        rootStage = stage;

        editMode = new SimpleBooleanProperty(false);
        animationSpeed = new SimpleFloatProperty((float)15);
        editButtonProperty = new SimpleStringProperty("Edit");
        playButtonProperty = new SimpleStringProperty("Pause");

        //Set up the scene
        root = createUserInterface();
        Scene scene = new Scene(root);

        AnimationTimer timer = createAnimationTimer(canvas.getGraphicsContext2D());

        //Set up the stage and show it
        rootStage.setTitle("Building Model v0");
        rootStage.setScene(scene);
        rootStage.show();

        //Play animation
        timer.start();
        playAnimation = true;
    }

    public static void main(String[] args) {
        launch();
    }

    private BorderPane createUserInterface(){
        canvas = createCanvas();
        editPane = createEditPane();

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(canvas);
        borderPane.setTop(createMenuBar());
        borderPane.setBottom(createToolBar());
        return borderPane;
    }

    private Canvas createCanvas(){
        canvas = new Canvas(WIDTH,HEIGHT);
        return canvas;
    }

    private Pane createEditPane(){
        Pane pane = new Pane();
        pane.setPrefWidth(canvas.getWidth());
        pane.setPrefHeight(canvas.getHeight());
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
            if(animationSpeed.get() < 10 * SPEED_INCREMENT) animationSpeed.set(animationSpeed.get() + SPEED_INCREMENT);
        });
        return button;
    }

    private Label createSpeedLabel(){
        Label label = new Label("Speed: " + animationSpeed.get());
        label.textProperty().bind(Bindings.createStringBinding(() -> "Speed: " + animationSpeed.get(), animationSpeed));
        return label;
    }

    private Button createSlowDownButton(){
        Button button = new Button("Slower");
        button.setOnAction((event) -> {
            if(animationSpeed.get() > SPEED_INCREMENT) animationSpeed.set(animationSpeed.get() - SPEED_INCREMENT);
        });
        return button;
    }

    //Animation timer that allows drawing of graphics
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

    private void pauseAnimation(){
        playAnimation = false;
        playButtonProperty.setValue("Play");
    }

    private void resumeAnimation(){
        playAnimation = true;
        playButtonProperty.setValue("Pause");
        finishEditing();
    }

    //Drawing the graphics to the graphics context.
    private void draw(GraphicsContext gc){
        //Clear canvas
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        //Draw building and people
        drawPeople(gc, Controller.getPeopleLocations());
        drawWalls(gc, Controller.getWallLocations());
    }

    private void drawPeople(GraphicsContext gc, ArrayList<Person> people){
        gc.setFill(Color.FORESTGREEN);
        for(Person p: people){
            gc.fillOval(p.getX(), p.getY(), p.getRadius(), p.getRadius());
        }
    }

    private void drawWalls(GraphicsContext gc, ArrayList<Wall> wallLocations){
        gc.setLineWidth(3);
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.BLACK);

        for(Wall w:wallLocations){
            float width = Math.abs(w.getX1() - w.getX2());
            float height = Math.abs(w.getY1() - w.getY2());
            gc.fillRect(w.getX1(), w.getY1(), width, height);
        }
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

    private void startEditing(){
        pauseAnimation();
        editButtonProperty.setValue("Stop Editing");
        editMode.set(true);

        root.setCenter(editPane);

        updateEditPane();
    }

    private void finishEditing(){
        editButtonProperty.setValue("Edit");
        editMode.set(false);
        root.setCenter(canvas);
    }

    private Button createNewWallButton(){
        Button addWallButton = new Button("New Wall");
        addWallButton.visibleProperty().bind(Bindings.createBooleanBinding( () -> editMode.get(), editMode));
        addWallButton.managedProperty().bind(Bindings.createBooleanBinding( () -> editMode.get(), editMode));
        addWallButton.setOnAction(actionEvent -> {
            createAddWallMenu();
        });
        return addWallButton;
    }

    private Button createNewPersonButton(){
        Button button = new Button("New Person");
        button.visibleProperty().bind(Bindings.createBooleanBinding(() -> editMode.get(), editMode));
        button.managedProperty().bind(Bindings.createBooleanBinding(() -> editMode.get(), editMode));
        button.setOnAction( actionEvent -> {
            createAddPersonMenu();
        });
        return button;
    }

    private Stage createAddWallMenu(){

        Stage editStage = new Stage();
        editStage.initOwner(rootStage);
        editStage.setWidth(256);
        editStage.setHeight(256);
        editStage.setTitle("New Wall");

        VBox mainVbox = new VBox();
        TextField xPositionField = addTextInputFieldToParent(mainVbox, "x:");
        TextField yPositionField = addTextInputFieldToParent(mainVbox, "y:");
        TextField lengthField = addTextInputFieldToParent(mainVbox, "Length:");

        ObservableList<String> options = FXCollections.observableArrayList("Horizontal", "Vertical");
        ComboBox comboBox = new ComboBox(options);
        mainVbox.getChildren().add(comboBox);
        comboBox.getSelectionModel().select(0);

        Button addButton = new Button("Add");
        addButton.setOnAction(actionEvent -> {
            Controller.addWallAtLocation(
                    Float.parseFloat(xPositionField.getText()),
                    Float.parseFloat(yPositionField.getText()),
                    Float.parseFloat(lengthField.getText()),
                    comboBox.getValue().toString());

            updateEditPane();
        });

        mainVbox.getChildren().add(addButton);

        Scene scene = new Scene(mainVbox);
        editStage.setScene(scene);
        editStage.show();

        return editStage;

    }

    private Stage createAddPersonMenu(){
        Controller.tempPerson = new Person(0,0);

        Stage editStage = new Stage();
        editStage.initOwner(rootStage);
        editStage.setWidth(256);
        editStage.setHeight(256);
        editStage.setTitle("New Person");

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10, 10, 10, 10));
        vbox.setSpacing(5);

        TextField textFieldX = addTextInputFieldToParent(vbox, "x: ");
        TextField textFieldY = addTextInputFieldToParent(vbox, "y: ");
        ColorPicker cp = addColorPickerToParent(vbox, "Dot Colour: ");

        TableView<Activity> tableView = new TableView<Activity>();

        TableColumn xColumn = new TableColumn<Activity, String>("X Coordinate");
        TableColumn yColumn = new TableColumn<Activity, String>("Y Coordinate");
        TableColumn orderColumn = new TableColumn<Activity, String>("Order");
        TableColumn nameColumn = new TableColumn<Activity, String>("Activity Name");

        tableView.getColumns().addAll(orderColumn, xColumn, yColumn);
        tableView.editableProperty().set(true);
        tableView.setEditable(true);

        vbox.getChildren().add(tableView);

        ArrayList<Activity> schedule = new ArrayList<>();

        Button addActivityButton = new Button("Add Activity");
        addActivityButton.setOnAction(actionEvent -> {
            Controller.tempPerson.addActivity(new Activity());
            tableView.setItems(FXCollections.observableList(Controller.tempPerson.getSchedule()));
        });

        Button addPersonButton = new Button("Add Person");
        addPersonButton.setOnAction(actionEvent -> {
            Controller.addPersonWithSchedule(
                    Helper.getFloatFromTextField(textFieldX),
                    Helper.getFloatFromTextField(textFieldY),
                    cp.getValue(),
                    new ArrayList<Activity>());
            updateEditPane();
        });

        vbox.getChildren().add(addPersonButton);

        Scene scene = new Scene(vbox);
        editStage.setScene(scene);
        editStage.show();

        return editStage;
    }

    private TextField addTextInputFieldToParent(Pane parent, String name){
        HBox hbox = new HBox();
        TextField tf = new TextField();

        hbox.getChildren().add(new Label(name));
        hbox.getChildren().add(tf);
        parent.getChildren().add(hbox);

        return tf;
    }

    private ComboBox addComboBoxToParent(Pane parent, String name, ObservableList options){
        Label label = new Label(name);
        HBox hbox = new HBox();

        ComboBox comboBox = new ComboBox(options);
        comboBox.getSelectionModel().select(0);

        hbox.getChildren().add(label);
        hbox.getChildren().add(comboBox);

        parent.getChildren().add(hbox);

        return comboBox;
    }

    private CheckBox addCheckBoxToParent(Pane parent, String name, boolean checked){
        HBox hbox = new HBox();

        CheckBox checkBox = new CheckBox(name);
        checkBox.setSelected(checked);

        hbox.getChildren().add(checkBox);

        parent.getChildren().add(hbox);

        return checkBox;
    }

    private ColorPicker addColorPickerToParent(Pane parent, String name){
        HBox hbox = new HBox();

        ColorPicker cp = new ColorPicker();
        Label label = new Label(name);

        hbox.getChildren().add(label);
        hbox.getChildren().add(cp);

        hbox.setMinHeight(32);

        parent.getChildren().add(hbox);
        return cp;
    }

    private void updateEditPane(){
        editPane.getChildren().clear();

        ArrayList<Wall> walls = Controller.getWallLocations();
        for(Wall w: walls) {
            Rectangle rect = new Rectangle(w.getX1(), w.getY1(), w.getWidth(), w.getHeight());
            rect.setCursor(Cursor.HAND);
            editPane.getChildren().add(rect);
        }

        ArrayList<Person> people = Controller.getPeopleLocations();
        for(Person p: people){
            Circle circle = new Circle(p.getX(), p.getY(), p.getRadius(), p.getColour());
            editPane.getChildren().add(circle);
        }
    }

    private void openFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        File file = fileChooser.showOpenDialog(rootStage);
        Controller.openFile(file);
        finishEditing();
        draw(canvas.getGraphicsContext2D());
    }

    private void saveAs(){
        pauseAnimation();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        //fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("XML Files (*.xml)", "*.xml"));
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

}