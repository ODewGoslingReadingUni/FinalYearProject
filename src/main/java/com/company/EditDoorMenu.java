package com.company;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EditDoorMenu {

    private Door door;

    public EditDoorMenu(Door door){
        this.door = door;
    }

    public Stage getEditDoorStage(){
        Stage stage = new Stage();
        stage.setTitle("Edit Door");
        stage.setWidth(300);
        stage.setHeight(200);

        HBox hbox = new HBox();
        hbox.setPadding(new Insets(10, 10, 10, 10));
        hbox.setSpacing(5);

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(actionEvent -> {
            Controller.deleteDoor(door.getId());
            Controller.doUIUpdate();
            stage.close();
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(actionEvent -> {
            stage.close();
        });

        hbox.getChildren().add(deleteButton);
        hbox.getChildren().add(cancelButton);

        Scene scene = new Scene(hbox);
        stage.setScene(scene);
        stage.show();
        return stage;
    }
}
