package com.company;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class NewWallMenuFX {

    private Stage parentStage;
    private VBox root;

    public NewWallMenuFX(Stage parentStage){
        this.parentStage = parentStage;
    }

    public Stage createEditMenu(){
        Stage editStage = new Stage();
        editStage.initOwner(parentStage);
        editStage.setWidth(512);
        editStage.setHeight(128);
        editStage.setTitle("Edit Building Toolbar");

        ToolBar tb = new ToolBar();
        tb.getItems().add(createAddWallButton());

        root = new VBox();
        root.getChildren().add(tb);

        Scene scene = new Scene(root);
        editStage.setScene(scene);
        editStage.show();

        return editStage;
    }

    private Button createAddWallButton(){
        Button addWallButton = new Button("Add Wall");
        addWallButton.setOnAction( actionEvent -> {
            root.getChildren().add(createAddWallHBox());
        });
        return addWallButton;
    }

    private HBox createAddWallHBox(){
        HBox layout = new HBox();

        Label createNewWall = new Label("Add new wall: ");
        TextField xField = new TextField();
        xField.setPromptText("x");
        TextField yField = new TextField();
        yField.setPromptText("y");
        TextField lengthField = new TextField();
        lengthField.setPromptText("length");
        Button addWall = new Button("Add");
        addWall.setOnAction( actionEvent -> {
            Controller.addWallAtLocation(Float.parseFloat(xField.getText()), Float.parseFloat(yField.getText()),5,  Float.parseFloat(lengthField.getText()));
        });

        layout.getChildren().add(createNewWall);
        layout.getChildren().add(xField);
        layout.getChildren().add(yField);
        layout.getChildren().add(lengthField);
        layout.getChildren().add(addWall);

        return layout;
    }
}
