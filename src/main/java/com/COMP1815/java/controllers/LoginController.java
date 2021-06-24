package com.COMP1815.java.controllers;

import com.COMP1815.java.Launcher;
import com.COMP1815.kotlin.model.User;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import static com.COMP1815.java.Launcher.user;

public class LoginController implements Initializable {

    private final Logger logger = Logger.getLogger("GLOBAL_LOGGER");

    @FXML
    private TextField name;

    @FXML
    private PasswordField password;

    @FXML
    private Text error;

    private AnimationTimer animation;

    @FXML
    void login(ActionEvent event) {
        user = User.Companion.validateLogin(name.getText(), password.getText()); // set the global user
        if (user != null) { // If user found
            logger.info("Logged as: " + name.getText());
            clear();
            Launcher.menu();
        } else {
            logger.warning("Cannot perform login as: " + name.getText());
            error.setOpacity(1);
            animation.start(); // Animated error message start
        }
        event.consume();
    }

    @FXML
    void register(MouseEvent event) {
        User tempUser = User.Companion.findUserByEmail(name.getText());
        if (tempUser == null) { // User not found
            user = new User(name.getText(), password.getText(), false); // Create new user
            Launcher.loader.saveUsers(); // Save the new user
            logger.info("Registered and saved user: " + name.getText());
            clear(); // clear pane
            Launcher.menu(); // Show menu
        } else {
            logger.warning("Cannot register user: " + name.getText());
            error.setOpacity(1);
            animation.start(); // Animated error start
        }
        event.consume();
    }

    private void clear() {
        password.clear();
        name.clear();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Animation
        animation = new AnimationTimer() {
            @Override
            public void handle(long now) {
                error.setText("Wrong email or password detected");
                error.setOpacity(error.getOpacity() - 0.01);
                if (error.getOpacity() <= 0) {
                    stop();
                }
            }
        };
    }
}