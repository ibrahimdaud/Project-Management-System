package com.COMP1815.java.controllers;

import com.COMP1815.java.Launcher;
import com.COMP1815.kotlin.model.Project;
import com.COMP1815.kotlin.model.Task;
import com.COMP1815.kotlin.model.Team;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class CreationWizardController implements Initializable {

    @FXML
    private AnchorPane startPane;

    @FXML
    private Button back;

    @FXML
    private Button next;

    @FXML
    private TextField projectName;

    @FXML
    private TextField startTime;

    @FXML
    private AnchorPane tasksView;

    @FXML
    private ListView<String> teams;

    @FXML
    private ListView<String> teams1;

    @FXML
    private Button assign;

    @FXML
    private Button unsign;

    @FXML
    private AnchorPane teamsView;

    @FXML
    private TextField taskName;

    @FXML
    private TextArea taskDescription;

    @FXML
    private ListView<String> tasks;

    @FXML
    private TextField taskDuration;

    @FXML
    private ListView<String> dependencies;

    @FXML
    void addAsDependency(ActionEvent event) {
        final ObservableList<String> selectedItems = FXCollections.observableList(tasks.getSelectionModel()
                .getSelectedItems()
                .filtered(it -> !dependencies.getItems().contains(it))
                .stream()
                .map(it -> it.substring(0, it.indexOf(" [")))
                .collect(Collectors.toList()));
        dependencies.getItems().addAll(selectedItems);
        event.consume();
    }

    private final List<Task> addedTasks = new ArrayList<>();

    @FXML
    void addTask(ActionEvent event) {
        if (!taskName.getText().isBlank() && !tasksList.contains(taskName.getText()))
            if (!taskDuration.getText().isBlank()) {
                Task t = new Task( // creates a new Task based on fields
                        taskName.getText(),
                        taskDescription.getText(),
                        Integer.parseInt(taskDuration.getText()),
                        Objects.requireNonNull(Team.Companion.findTeamByLeader(Launcher.user.getEmail())),
                        null
                );
                if (dependencies.getItems().size() > 0) {
                    final List<UUID> collect = addedTasks.stream()
                            .filter(it -> dependencies.getItems().contains(it.getName()))
                            .map(Task::getId)
                            .collect(Collectors.toList());

                    t.getDependencies() // Adds dependencies to that class
                            .addAll(collect);
                }
                addedTasks.add(t);
                tasksList.clear();
                addedTasks.forEach(task -> {
                    String deps = task.getDependencies().stream()
                            .map(it -> Objects.requireNonNull(Task.Companion.findTaskByUUID(it.toString())).getName())
                            .collect(Collectors.joining(" & ", "[ ", " ]"));
                    tasksList.add(task.getName().concat(" ").concat(deps));
                });
            }

        clearTask(); // Clear tasks pane

        event.consume();
    }

    void clearTask() {
        taskName.clear();
        taskDuration.clear();
        taskDescription.clear();
        dependencies.getItems().clear();
    }

    @FXML
    void removeDependency(ActionEvent event) {
        dependencies.getItems().removeAll(dependencies.getSelectionModel().getSelectedItems());
        event.consume();
    }

    @FXML
    void removeTask(MouseEvent event) {
        tasks.getItems().removeAll(tasks.getSelectionModel().getSelectedItems());
        event.consume();
    }


    private final List<Parent> p = new ArrayList<>();
    private int index = 0;

    @FXML
    void back(ActionEvent event) {
        load(-1); // Perform back operation
        event.consume();
    }

    @FXML
    void cancel(ActionEvent event) {
        Launcher.menu();
        clear();
        event.consume();
    }

    Date finalStartDate;
    @FXML
    void next(ActionEvent event) {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        switch (index) {
            case 0: { // Check validity for pane 1
                if (projectName.getText().isBlank())
                    return;
                try {
                    if (startTime.getText().isBlank())
                        return;
                    else {
                        finalStartDate = format.parse(startTime.getText());
                    }
                } catch (Exception e) {
                    return;
                }
                if (Project.Companion.getProjects().stream().anyMatch(it -> it.getName().equals(projectName.getText())))
                    return;
                break;
            }
            case 1: { // Check validity for pane 2
                if (teams1.getItems().isEmpty())
                    return;
                break;
            }
            case 2: { // Check validity for pane 3
                if (!tasks.getItems().isEmpty()) {
                    Launcher.opened = new Project(
                            projectName.getText(),
                            finalStartDate,
                            addedTasks,
                            Launcher.user,
                            teams1.getItems().stream().map(Team.Companion::findTeamByName).collect(Collectors.toList())
                    );
                    addedTasks.forEach(it -> it.setProjectReference(Launcher.opened));
                    Launcher.loader.saveTasks();
                    Launcher.loader.saveProjects();
                    Launcher.view();
                    event.consume();
                    return;
                }
                break;
            }
            default:
                break;
        }
        load(1); // Perform the next pane visualization
        event.consume();
    }

    @FXML
    void newTeamWizard(MouseEvent event) {
        Launcher.newTeamWizard();
        prop.addAll(
                Team.Companion.getTeams()
                        .stream()
                        .filter(it -> it.getLeader().getEmail().equals(Launcher.user.getEmail()))
                        .map(Team::getName)
                        .collect(Collectors.toSet())
        );
        prop.removeAll(prop1);
        event.consume();
    }

    private void load(int cursor) {
        if (cursor == -1 && index != 0) { // Check for bound
            index--;
            p.get(index).toFront();
        } else if (cursor == 1 && index != p.size() - 1) { // Check for bounds
            index++;
            p.get(index).toFront();
        } else if (index == 0) { // If try to go back when pane = 0 go to menu
            Launcher.menu();
        } else if (index == p.size()) {
            if (!tasksList.isEmpty())
                Launcher.view(); // If reached the end of wizard show the view GUI
        }
    }

    private final ObservableList<String> prop = FXCollections.observableArrayList();
    private final ObservableList<String> prop1 = FXCollections.observableArrayList();
    private final ObservableList<String> tasksList = FXCollections.observableArrayList();
    private final ObservableList<String> dependenciesList = FXCollections.observableArrayList();
//    private Task currentTask = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        back.setDisable(false);
        next.setDisable(false);

        // Show only teams that have the current user as admin
        prop.addAll(Team.Companion.getTeams().stream().filter(it -> it.getLeader().getEmail().equals(Launcher.user.getEmail())).map(Team::getName).collect(Collectors.toSet()));

        assign.setOnAction(it -> { // Assign button action (Teams)
            final ObservableList<String> selectedItems = teams.getSelectionModel().getSelectedItems();
            teams1.getItems().addAll(selectedItems);
            teams.getItems().removeAll(selectedItems);
            it.consume();
        });
        unsign.setOnAction(it -> { // Remove button action (Teams)
            final ObservableList<String> selectedItems = teams1.getSelectionModel().getSelectedItems();
            teams.getItems().addAll(selectedItems);
            teams1.getItems().removeAll(selectedItems);
            it.consume();
        });

        taskDuration.textProperty().addListener((bo, o, n) -> { // Allows only digits inside the textDuration field
            if (n.chars().anyMatch(it -> !Character.isDigit(it))) {
                taskDuration.setText(o);
            }
        });

        teams.setItems(prop);
        teams1.setItems(prop1);

        tasks.setItems(tasksList);
        dependencies.setItems(dependenciesList);

        p.add(0, startPane);
        p.add(1, tasksView);
        p.add(2, teamsView);

        p.get(0).toFront();
    }

    private void clear() {
        projectName.clear();
        startTime.clear();
    }
}

