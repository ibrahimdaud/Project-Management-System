package com.COMP1815.java.controllers;

import com.COMP1815.java.Launcher;
import com.COMP1815.kotlin.model.Team;
import com.COMP1815.kotlin.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TeamsWizardController implements Initializable {
    private final Logger logger = Logger.getLogger("GLOBAL_LOGGER");

    private final ObservableList<String> users = FXCollections.observableArrayList();
    private final ObservableList<String> team = FXCollections.observableArrayList();
    @FXML
    private ListView<String> viableUsers;

    @FXML
    private ListView<String> myTeam;

    @FXML
    private TextField teamName;

    @FXML
    void add(ActionEvent event) {
        List<String> selected = viableUsers.getSelectionModel().getSelectedItems();
        team.addAll(selected);
        users.removeAll(selected);
        selected.forEach(it -> logger.info("Added: " + it + " at " + (teamName.getText().isBlank() ? "unnamed team" : teamName.getText())));
        event.consume();
    }

    @FXML
    void cancel(ActionEvent event) {
        ((Stage) viableUsers.getScene().getWindow()).close(); // Closes the popup stage
        event.consume();
    }

    @FXML
    void rem(ActionEvent event) {
        List<String> selected = myTeam.getSelectionModel().getSelectedItems();
        team.removeAll(selected);
        viableUsers.getItems().addAll(myTeam.getSelectionModel().getSelectedItems());
        selected.forEach(it -> logger.info("Removed: " + it + " from "));
        event.consume();
    }

    @FXML
    void save(ActionEvent event) {
        if (teamName.getText().isBlank())
            return;
        Team t = Team.Companion.findTeamByLeader(Launcher.user.getEmail());
        if (t != null) {
            t.setName(teamName.getText());
        } else {
            new Team(teamName.getText(), Launcher.user, myTeam.getItems().stream().map(it -> User.Companion.findUserByEmail(it)).collect(Collectors.toList()));
        }

        Launcher.loader.saveTeams();
        ((Stage) viableUsers.getScene().getWindow()).close();
        event.consume();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        users.addAll(User.Companion.getUsers().stream().map(User::getEmail).collect(Collectors.toSet()));
        Team team = Team.Companion.findTeamByLeader(Launcher.user.getEmail());
        if (team != null) {
            Set<String> users = team.getUsers().stream().map(User::getEmail).collect(Collectors.toSet());
            this.team.addAll(users);
            this.users.removeAll(users);
        }

        viableUsers.setItems(this.users);
        myTeam.setItems(this.team);
    }
}
