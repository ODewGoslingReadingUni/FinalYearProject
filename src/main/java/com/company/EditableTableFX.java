package com.company;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.ArrayList;

public class EditableTableFX {

    private ArrayList<ActivityFX> table;
    private VBox root;
    private Pane parent;

    public EditableTableFX(Pane parent, String title){
        table = new ArrayList<ActivityFX>();

        root = new VBox();
        root.setSpacing(5);
        Label titleLabel = new Label(title);
        titleLabel.setUnderline(true);
        titleLabel.setFont(new Font(14));
        root.getChildren().add(titleLabel);

        parent.getChildren().add(root);
        this.parent = parent;
    }

    public void addNewRow(){
        table.add(new ActivityFX(root));
    }

    public void deleteRow(int index){
        root.getChildren().remove(index);
    }

    public ArrayList<ActivityFX> getTable(){
        return table;
    }

    public ArrayList<Activity> getData(){
        ArrayList<Activity> activities = new ArrayList<>();
        for(ActivityFX a: table){
            activities.add(a.getActivity());
        }
        return activities;
    }

    //Inner class
    private class ActivityFX {

        private HBox root;
        private TextField xTextField;
        private TextField yTextField;
        private int index;

        private ActivityFX(Pane parent){
            root = new HBox();
            root.setSpacing(5);

            xTextField = UserInterface.addTextInputFieldToParent(root, "x:");
            yTextField = UserInterface.addTextInputFieldToParent(root, "y:");

            if(table.size() > 0) index = table.size() - 1;

            Button btn = new Button("Delete activity");
            btn.setOnAction(actionEvent -> {
                deleteRow(index);
            });

            root.getChildren().add(btn);

            parent.getChildren().add(root);
        }

        private Activity getActivity(){
            Activity activity = new Activity(Float.parseFloat(xTextField.getText()), Float.parseFloat(yTextField.getText()));
            return activity;
        }

        private HBox getRoot(){
            return root;
        }
    }

}
