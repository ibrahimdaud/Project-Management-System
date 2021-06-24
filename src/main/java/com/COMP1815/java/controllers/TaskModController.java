package com.COMP1815.java.controllers;

import com.COMP1815.java.Launcher;
import com.COMP1815.kotlin.model.Task;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TaskModController implements Initializable {

    @FXML
    private ListView<Task> tasksList;

    @FXML
    void back(ActionEvent event) {
        Launcher.menu();
        Launcher.loader.saveTasks(); // Save tasks
        event.consume();
    }

    @FXML
    void markCompleted(ActionEvent event) {
        final Task selectedItem = tasksList.getSelectionModel().getSelectedItem();
        solveDependencies(selectedItem); // Mark as done dependencies too
        event.consume();
    }

    private void solveDependencies(Task task) {
        if (task.getDependencies().size() != 0)
            task.getDependencies()
                    .stream()
                    .map(it -> Task.Companion.findTaskByUUID(it.toString()))
                    .filter(Objects::nonNull)
                    .forEach(this::solveDependencies); // Indirect recursive call
        task.setDone(true); // Set task as done
        tasksList.getItems().removeIf(it -> it.getId() == task.getId()); // Remove done list from tasks list
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final Stream<Task> taskStream = Launcher.opened.getTasks()
                .stream()
                .filter(it -> !it.getDone()); // Only tasks not already done
        final List<Task> tasks = Launcher.opened != null ? // Look for tasks
                taskStream.collect(Collectors.toList()) : new ArrayList<>();
        tasksList.setItems(FXCollections.observableArrayList(tasks));
        tasksList.setCellFactory(param -> new TaskCell.NormalTaskCell()); // Set Cell Factory
    }
}
