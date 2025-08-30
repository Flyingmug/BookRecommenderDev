package com.example.bookrecommenderdev.server.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ServerBRController {
    @FXML
    private Label welcomeText;
    @FXML
    private Label statusText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to BR-Manager!");
    }

    @FXML
    public void serverStatusDisplay() {
        statusText.setText("Server running.");
    }
}