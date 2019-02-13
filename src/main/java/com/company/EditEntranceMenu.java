package com.company;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class EditEntranceMenu {
    private Entrance entrance;

    public EditEntranceMenu(Entrance entrance){
        this.entrance = entrance;
    }

    public Stage getEditEntranceStage(){
        Stage stage = new Stage();
        stage.setTitle("Edit Entrance");
        stage.setWidth(300);
        stage.setHeight(200);

        VBox root = new VBox();
        root.setPadding(new Insets(10, 10, 10, 10));
        root.setSpacing(5);

        TextField nameField = UserInterface.addTextInputFieldToParent(root, "Name:");
        if(entrance.getName() != null){
            nameField.setText(entrance.getName());
        }

        Button saveButton = new Button("Save");
        saveButton.setOnAction(actionEvent -> {
            //Validate inputs
            String validationError = Controller.validateEntranceName(nameField.getText(), entrance.getId() );

            //Save the object
            if(validationError == null){
                entrance.setName(nameField.getText());
                stage.close();
            } else {
                //Display error message
                System.out.println(validationError);
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(actionEvent -> {
            stage.close();
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(actionEvent -> {
            Controller.deleteDoor(entrance.getId());
            Controller.doUIUpdate();
            stage.close();
        });

        root.getChildren().add(nameField);
        root.getChildren().add(saveButton);
        root.getChildren().add(cancelButton);
        root.getChildren().add(deleteButton);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        return stage;
    }
}
