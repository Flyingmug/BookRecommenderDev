package com.example.bookrecommenderdev.server.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ServerBRController {
    @FXML
    private Label welcomeText;
    @FXML
    private Label welcomeText2;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}