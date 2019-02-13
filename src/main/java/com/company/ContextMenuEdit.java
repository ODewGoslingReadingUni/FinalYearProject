package com.company;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;

public class ContextMenuEdit {

    private ContextMenu contextMenu;
    private Pane parent;
    private double x;
    private double y;

    public ContextMenuEdit(Pane parent){
        contextMenu = new ContextMenu();
        this.parent = parent;
        x = 0;
        y = 0;

        MenuItem addWallItem = new MenuItem("Add New Wall");
        addWallItem.setOnAction(actionEvent -> {
            EditWallMenu editWallMenu = new EditWallMenu((float)x,(float)y);
            editWallMenu.getEditWallStage().show();
        });

        MenuItem addPersonItem = new MenuItem("Add New Person");
        addPersonItem.setOnAction(actionEvent -> {
            EditPersonMenu editPersonMenu = new EditPersonMenu(null);
            editPersonMenu.getEditPersonStage().show();
        });

        MenuItem addRoomItem = new MenuItem("Add New Room");
        addRoomItem.setOnAction(actionEvent -> {
            EditRoomMenu editRoomMenu = new EditRoomMenu(Helper.alignObject((float)x, 6),Helper.alignObject((float)y, 6));
            editRoomMenu.getEditRoomStage().show();
        });

        MenuItem addDoorItem = new MenuItem("Add Door");
        addDoorItem.setOnAction(actionEvent -> {
            Controller.addDoorAtLocation((float)x,(float)y, "door");
        });

        MenuItem addEntranceItem = new MenuItem("Add Entrance");
        addEntranceItem.setOnAction(actionEvent -> {
            Controller.addDoorAtLocation((float)x, (float)y, "Entrance");
        });

        contextMenu.getItems().add(addDoorItem);
        contextMenu.getItems().add(addWallItem);
        contextMenu.getItems().add(addPersonItem);
        contextMenu.getItems().add(addRoomItem);
        contextMenu.getItems().add(addEntranceItem);
    }

    public void contextMenuRequest(double x, double y, double wallX, double wallY){
        contextMenu.hide();
        contextMenu.show(parent, x, y);
        this.x = wallX;
        this.y = wallY;
    }

    public void hideMenu(){
        contextMenu.hide();
    }
}
