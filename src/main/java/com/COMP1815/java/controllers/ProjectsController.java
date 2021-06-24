package com.COMP1815.java.controllers;

import com.COMP1815.java.Launcher;
import com.COMP1815.kotlin.model.Project;
import com.COMP1815.kotlin.model.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ProjectsController implements Initializable {
    private final ObservableList<String> projectsList = FXCollections.observableList(new ArrayList<>());
    private final Logger logger = Logger.getLogger("GLOBAL_LOGGER");

    @FXML
    private ListView<String> projects;

    @FXML
    void remove(ActionEvent event) {
        List<String> selected = projects.getSelectionModel().getSelectedItems();
        projectsList.removeAll(selected);
        event.consume();
    }

    @FXML
    void save(ActionEvent event) {
        if (projects.getSelectionModel().isEmpty())
            return;
        Project.Companion.getProjects()
                .removeIf(it -> !projectsList.contains(it.getName()));
        Task.Companion.getTasks() // Look for tasks
                .removeIf(it -> {
                    try {
                        return !projectsList.contains(Objects.requireNonNull(it.getProjectReference()).getName());
                    } catch (NullPointerException e) {
                        return false;
                    }
                });
        Launcher.opened = Project.Companion.getProjects() // Set the current project
                .stream()
                .filter(it -> it.getName().equals(projects.getSelectionModel().getSelectedItems().get(0)))
                .collect(Collectors.toList())
                .get(0);
        Launcher.loader.save(); // Save
        Launcher.view(); // Go to view pane
        event.consume();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        projectsList.addAll(Project.Companion.getProjects().stream().map(Project::getName).collect(Collectors.toSet()));
        projects.setItems(projectsList);
    }
}
