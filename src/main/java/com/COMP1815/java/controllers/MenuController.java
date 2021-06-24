package com.COMP1815.java.controllers;

import com.COMP1815.java.Launcher;
import com.COMP1815.java.models.MenuModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class MenuController {
    private final MenuModel model;

    public MenuController() {
        model = MenuModel.getInstance();
        model.setController(this);
    }

    @FXML
    void close(MouseEvent event) {
        Launcher.stage.close();
        event.consume();
    }

    @FXML
    void createProject(ActionEvent event) {
        model.createProject();
        event.consume();
    }

    @FXML
    void loadProject(ActionEvent event) {
        model.loadProject();
        event.consume();
    }

    @FXML
    void alterProject(ActionEvent event) {
        model.alterProject();
        event.consume();
    }

}
