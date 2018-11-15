package com.company;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import java.util.ArrayList;

public class UserInterface extends Application {
    public static final int WIDTH = 640;
    public static final int HEIGHT = 480;
    public static final float SPEED_INCREMENT = (float)5;

    public static boolean playAnimation;
    public static FloatProperty animationSpeed;

    private Canvas canvas;

    @Override
    public void start(Stage stage) {

        Controller.doInitialSetup();

        animationSpeed = new SimpleFloatProperty((float)15);

        //Set up the scene
        BorderPane root = createUserInterface();
        Scene scene = new Scene(root);

        AnimationTimer timer = createAnimationTimer(canvas.getGraphicsContext2D());

        //Set up the stage and show it
        stage.setTitle("Building Model v0");
        stage.setScene(scene);
        stage.show();

        //Play animation
        timer.start();
        playAnimation = true;
    }

    public static void main(String[] args) {
        launch();
    }

    private BorderPane createUserInterface(){
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(createCanvas());
        borderPane.setTop(createToolBar());
        return borderPane;
    }

    private Canvas createCanvas(){
        canvas = new Canvas(WIDTH,HEIGHT);
        return canvas;
    }

    private ToolBar createToolBar(){
        ToolBar toolBar = new ToolBar();

        Button slowDownButton = createSlowDownButton();
        Button pauseButton = createPauseButton();
        Button fastForwardButton = createFastForwardButton();

        toolBar.getItems().add(slowDownButton);
        toolBar.getItems().add(pauseButton);
        toolBar.getItems().add(fastForwardButton);

        toolBar.getItems().add(createSpeedLabel());

        return toolBar;
    }

    private Button createPauseButton(){
        Button button = new Button("Pause");
        button.setOnAction( (event) ->{
            playAnimation = !playAnimation;
        });
        return button;
    }

    private Button createFastForwardButton(){
        Button button = new Button("Faster");
        button.setOnAction((event) ->{
            animationSpeed.set(animationSpeed.get() + SPEED_INCREMENT);
            System.out.println(animationSpeed.get());
            playAnimation = true;
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
            System.out.println(animationSpeed.get());
            playAnimation = true;
        });
        return button;
    }

    private void drawWalls(GraphicsContext gc, ArrayList<Wall> wallLocations){
        gc.setLineWidth(3);
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.BLACK);

        for(Wall w:wallLocations){
            float width = Math.abs(w.getX1() - w.getX2());
            float height = Math.abs(w.getY1() - w.getY2());
            gc.fillRect(w.getX1(), w.getY1(), width, height);

            /*gc.beginPath();
            gc.moveTo(w.getX1() + 0.5, w.getY1() + 0.5);
            gc.lineTo(w.getX2() + 0.5, w.getY2() + 0.5);
            gc.stroke();*/
        }
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
}