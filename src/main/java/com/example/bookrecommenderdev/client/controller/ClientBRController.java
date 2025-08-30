package com.example.bookrecommenderdev.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.image.Image;

import java.net.URL;

public class ClientBRController {
    @FXML
    private Label welcomeText;
    @FXML
    private StackPane mainSectionStackPane;
    @FXML
    private VBox resultDisplayVBox;

    @FXML
    public void initialize() {
        URL imageUrl = getClass().getResource("/com/example/bookrecommenderdev/assets/library-background2.jpg");
        if (imageUrl != null) {
            Image image = new Image(imageUrl.toExternalForm());

            BackgroundImage backgroundImage = new BackgroundImage(
                image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, false, true)
            );

            mainSectionStackPane.setBackground(new Background(backgroundImage));
        }
    }

    @FXML
    protected void onSearchAction() {

        //


        // cambiamenti UI
        welcomeText.setVisible(false);
        resultDisplayVBox.setVisible(true);
    }

    @FXML
    protected void onLibraryListAction() {

    }

    @FXML
    protected void onRegisterAction() {

    }

    @FXML
    protected void onLoginAction() {

    }

    @FXML
    protected void onLibraryOpenAction() {

    }

    @FXML
    protected void onLogoutAction() {

    }

    @FXML
    protected void onLibraryDeleteAction() {

    }

    @FXML
    protected void onLibraryCreateAction() {

    }

    @FXML
    protected void onLibraryInsertAction() {

    }

    @FXML
    protected void onSearchCriteriaDisplayAction() {

    }

    @FXML
    protected void onCriteriaSelectionAction() {

    }

    @FXML
    protected void onNextResultsAction() {

    }

    @FXML
    protected void onPreviousResultsAction() {

    }

    @FXML
    protected void onCreateUserAction() {

    }

    @FXML
    protected void onDeleteUserAction() {

    }

    @FXML
    protected void onProfileSettingsAction() {

    }

    @FXML
    protected void onAction() {

    }
}