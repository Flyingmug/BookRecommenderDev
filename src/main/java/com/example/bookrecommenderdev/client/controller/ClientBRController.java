package com.example.bookrecommenderdev.client.controller;

import com.example.bookrecommenderdev.model.Libro;
import com.example.bookrecommenderdev.server.ServerInterface;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class ClientBRController {
    @FXML
    private Label welcomeText;
    @FXML
    private StackPane centerStackContainer;
    @FXML
    private ScrollPane resultPage;
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

    @FXML
    private Label resultTitle;
    @FXML
    private HBox resultTitleWrapper;
    @FXML
    private VBox booksResultDisplay;
    @FXML
    private VBox booksResultWrapper;

    @FXML
    private FontIcon noBooksIcon;

    private ServerInterface bookRecommender;


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

        // icone non visibili
        noBooksIcon = new FontIcon("mdi2b-book-alert");
        noBooksIcon.setIconSize(38);

        testBooksearchpage();
    }

    private void initRegistry() {
        try {
            Registry reg = LocateRegistry.getRegistry("localhost", 1099);
            bookRecommender = (ServerInterface) reg.lookup("serverBR");
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
        resultPage.setVisible(false);
        homePage.setVisible(true);
        resetSearchpage();
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
    private void resetSearchpage() {
        resultTitleWrapper.getChildren().remove(noBooksIcon);
    }


    @FXML
    protected void onSearchAction() {

        // get input
        String input = searchbar.getText();
        System.out.println("Searched: " + input);  // DEBUG

        if (input == null || input.isEmpty()) return;

        topSearchbar();
        homePage.setVisible(false);
        resultPage.setVisible(true);

        try {
            List<Libro> res = bookRecommender.searchTitolo(input);
            res.clear();

            // log di debug e set dei risultati
            if (!res.isEmpty()) {
                resultTitle.setText("Risultati");
                System.out.println("Numero di risultati: " + res.size());
                booksResultWrapper.setVisible(true);

                loadResults(res);
            } else {
                booksResultWrapper.setVisible(false);
                resultTitle.setText("Nessun risultato");
                resultTitleWrapper.getChildren().addLast(noBooksIcon);
                System.out.println("Empty result set.");
            }


        } catch(RemoteException e) {
            System.out.println("Error while fetching data");
            e.printStackTrace();
        }

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



    private void loadResults(List<Libro> results) {

        for (Libro l: results) {
            VBox row = new VBox(5); // spacing inside row
            Label titolo = new Label(l.getTitolo());
            Label autore = new Label(l.getAutori());
            Label anno = new Label(String.valueOf(l.getAnnoPubblicazione()));

            row.getChildren().addAll(titolo, autore, anno);
            row.setStyle("-fx-padding: 10; -fx-border-color: #99b1e9; -fx-border-width: 0 0 1 0;");

            booksResultDisplay.getChildren().add(row);
        }

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

    //
    //
    //
    //
    // test methods

    private void testBooksearchpage() {
        // simulate input insertion
        searchbar.setText("Heart");
        // simulate search icon click
        onSearchAction();
    }
}