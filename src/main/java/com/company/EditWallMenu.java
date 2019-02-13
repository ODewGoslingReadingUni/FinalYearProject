package com.company;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EditWallMenu {

    private Wall wall;

    public EditWallMenu(float x, float y){
        Wall w = new Wall(x,y,0,0);
        this.wall = w;
    }

    public EditWallMenu(Wall wall){
        this.wall = wall;
    }

    public Stage getEditWallStage(){
        Stage editStage = new Stage();
        editStage.setWidth(256);
        editStage.setHeight(256);

        VBox mainVbox = new VBox();
        mainVbox.setSpacing(5);
        TextField xPositionField = UserInterface.addTextInputFieldToParent(mainVbox, "x:");
        TextField yPositionField = UserInterface.addTextInputFieldToParent(mainVbox, "y:");
        TextField lengthField = UserInterface.addTextInputFieldToParent(mainVbox, "Length:");

        ObservableList<String> options = FXCollections.observableArrayList("Horizontal", "Vertical");
        ComboBox comboBox = new ComboBox(options);
        mainVbox.getChildren().add(comboBox);

        //Fill fields for an existing record
        if(wall != null){
            xPositionField.setText(""+ wall.getX());
            yPositionField.setText("" + wall.getY());

            float width = wall.getWidth();
            float height = wall.getHeight();
            if(width > height) lengthField.setText("" + width);
            else lengthField.setText("" + height);

            if(width > height) comboBox.getSelectionModel().select(0);
            else comboBox.getSelectionModel().select(1);

            editStage.setTitle("Edit Wall");
        }
        else{
            editStage.setTitle("New Wall");
            comboBox.getSelectionModel().select(0);
        }

        Button addButton = new Button("Save");
        addButton.setOnAction(actionEvent -> {
            if(wall == null) {
                Controller.addWallAtLocation(
                        Float.parseFloat(xPositionField.getText()),
                        Float.parseFloat(yPositionField.getText()),
                        Float.parseFloat(lengthField.getText()),
                        comboBox.getValue().toString());
                Controller.doUIUpdate();
                editStage.close();
            } else {
                wall.setPoint1(Helper.getFloatFromTextField(xPositionField), Helper.getFloatFromTextField(yPositionField));
                if(comboBox.getValue().toString().equals("Horizontal")) {
                    wall.setWidth(Helper.getFloatFromTextField(lengthField));
                    wall.setHeight(UserInterface.WALL_THICKNESS);
                }else{
                    wall.setHeight(Helper.getFloatFromTextField(lengthField));
                    wall.setWidth(UserInterface.WALL_THICKNESS);
                }
                Controller.editWall(wall);
            }
            Controller.doUIUpdate();
            editStage.close();
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(actionEvent -> {
            if(wall.getId() != null){
                Controller.deleteWall(wall.getId());
                editStage.close();
            }
            else {
                editStage.close();
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(actionEvent -> {
            editStage.close();
        });

        HBox formattingBox = new HBox();
        formattingBox.setSpacing(10);
        formattingBox.getChildren().add(addButton);
        formattingBox.getChildren().add(cancelButton);
        if(wall != null) formattingBox.getChildren().add(deleteButton);

        mainVbox.getChildren().add(formattingBox);

        Scene scene = new Scene(mainVbox);
        editStage.setScene(scene);
        editStage.show();

        return editStage;
    }
}
