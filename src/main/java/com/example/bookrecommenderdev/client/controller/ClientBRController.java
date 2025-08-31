package com.example.bookrecommenderdev.client.controller;

import com.example.bookrecommenderdev.server.ServerImplementation;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.image.Image;

import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientBRController {
    @FXML
    private Label welcomeText;
    @FXML
    private StackPane centerStackContainer;
    @FXML
    private VBox resultDisplayVBox;
    @FXML
    private VBox homePage;
    @FXML
    private TextField searchbar;
    @FXML
    private HBox navbar;
    @FXML
    private StackPane searchbarWrapper;
    @FXML
    private Button searchButton;
    @FXML
    private Region mainPageSpacer;

    private ServerImplementation bookRecommender;

    @FXML
    public void initialize() {
        initRegistry();

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

            centerStackContainer.setBackground(new Background(backgroundImage));
        }
    }

    private void initRegistry() {
        try {
            Registry reg = LocateRegistry.getRegistry("localhost", 1099);
            bookRecommender = (ServerImplementation) reg.lookup("serverBR");
        } catch(RemoteException e) {
            e.printStackTrace();
            // error display on main page
        } catch(NotBoundException e) {
            e.printStackTrace();
            // error display on main page
        }
    }

    @FXML
    protected void onHomepage() {
        resultDisplayVBox.setVisible(false);
        homePage.setVisible(true);
        resetHomepage();
    }

    private void resetHomepage() {

        if (!homePage.getChildren().contains(searchbarWrapper)) {
            searchbarWrapper.getStyleClass().remove("searchbar-navbar");
            searchbarWrapper.getStyleClass().add("searchbar-center");
            searchbar.setText("");
            homePage.getChildren().addAll(welcomeText, searchbarWrapper, mainPageSpacer);
        }
    }



    @FXML
    protected void onSearchAction() {

        // get input
        String input = searchbar.getText();
        System.out.println(input);  // DEBUG

        if (input == null || input.isEmpty()) return;

        topSearchbar();
        homePage.setVisible(false);

        openResultsPage();

    }
    private void topSearchbar() {
        homePage.getChildren().clear();
        searchbarWrapper.getStyleClass().remove("searchbar-center");
        searchbarWrapper.getStyleClass().add("searchbar-navbar");
        navbar.getChildren().addFirst(searchbarWrapper);
    }
    private void openResultsPage() {

    }


    @FXML
    protected void onLibraryList() {
        try {
            bookRecommender.getListLibrerie(1);

        } catch (RemoteException e) {

        }
    }

    @FXML
    protected void onRegister() {

    }

    @FXML
    protected void onLogin() {

    }

    @FXML
    protected void onLibraryOpen() {

    }

    @FXML
    protected void onLogout() {

    }

    @FXML
    protected void onLibraryDelete() {

    }

    @FXML
    protected void onLibraryCreate() {

    }

    @FXML
    protected void onLibraryInsert() {

    }

    @FXML
    protected void onSearchCriteriaDisplay() {

    }

    @FXML
    protected void onCriteriaSelection() {

    }

    @FXML
    protected void onNextResults() {

    }

    @FXML
    protected void onPreviousResults() {

    }

    @FXML
    protected void onCreateUser() {

    }

    @FXML
    protected void onDeleteUser() {

    }

    @FXML
    protected void onProfileSettings() {

    }

    @FXML
    protected void onAction() {

    }
}