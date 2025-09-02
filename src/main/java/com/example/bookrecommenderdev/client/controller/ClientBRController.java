package com.example.bookrecommenderdev.client.controller;

import com.example.bookrecommenderdev.model.Libro;
import com.example.bookrecommenderdev.server.ServerInterface;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.util.Pair;
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
    private HBox noResultsTitleWrapper;
    @FXML
    private Label resultIndexCounter;

    private ServerInterface bookRecommender;

    int i = 0;  // DEBUG


    @FXML
    public void initialize() {
        initRegistry();
        initSetupLayout();

        //
        //
        // TEST
        testBooksearchpage();
    }

    /**
     * Inizializza gli elementi grafici privati visualizzabili solo dopo certe azioni.
     */
    private void initSetupLayout() {

        // background
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

        // icona di ricerca
        FontIcon noBooksIcon = new FontIcon("mdi2b-book-alert-outline");
        noBooksIcon.setIconSize(38);
        // titolo ricerca fallita
        Label noResultsTitle = new Label("Nessun risultato trovato");
        noResultsTitle.setFont(new Font("Arial", 30));
        noResultsTitle.setPadding(new Insets(5, 10, 5, 10));
        noResultsTitleWrapper = new HBox(noResultsTitle, noBooksIcon);
        noResultsTitleWrapper.setAlignment(Pos.CENTER);

        HBox.setMargin(noResultsTitleWrapper, new Insets(100, 0, 0, 0));

    }
    /**
     * Inizializza l'oggetto remoto del server dal repository.
     */
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

    /**
     * Caricamento pagina iniziale
     */
    @FXML
    protected void onHomepage() {
        resultPage.setVisible(false);
        resetHomepage();
        homePage.setVisible(true);
    }
    /**
     * Ripristina gli elementi della pagina iniziale nelle loro posizioni originali
     */
    private void resetHomepage() {

        if (!homePage.getChildren().contains(searchbarWrapper)) {
            System.out.println("searchbar missing -> moving it");
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
        System.out.println("Searched: " + input);  // DEBUG

        // if input is empty do nothing
        if (input == null || input.isEmpty()) return;

        topSearchbar();
        homePage.setVisible(false);
        resultPage.setVisible(true);

        try {
            Pair<List<Libro>, Integer> data = bookRecommender.searchTitolo(input);
            List<Libro> res = data.getKey();    // testing purposes
            if ((i%2)==0)   // testing purposes
                res.clear();
            i++;    // testing purposes


            if (!data.getKey().isEmpty() && data.getValue() > 0) {
                // Data present
                System.out.println("Numero di risultati: " + data.getValue());   // DEBUG

                setResultsFoundTitle(true);
                booksResultWrapper.setVisible(true);

                loadResults(data); // crea gli oggetti per rappresentare i dati
            } else {
                // No results
                System.out.println("Empty result set.");   // DEBUG

                setResultsFoundTitle(false);
                booksResultWrapper.setVisible(false);
            }


        } catch(RemoteException e) {
            System.out.println("Error while fetching data");
            e.printStackTrace();
        }

    }
    private void topSearchbar() {
        if (!navbar.getChildren().contains(searchbarWrapper)) {
            homePage.getChildren().clear();
            searchbarWrapper.getStyleClass().remove("searchbar-center");
            searchbarWrapper.getStyleClass().add("searchbar-navbar");
            navbar.getChildren().addFirst(searchbarWrapper);
        }
    }

    /**
     * Rimpiazza i children del contenitore del titolo a seconda del risultato della ricerca
     * @param success risultati trovati o meno
     */
    private void setResultsFoundTitle(boolean success) {

        resultTitleWrapper.getChildren().clear();

        if (success) {
            resultTitleWrapper.getChildren().add(resultTitle);
        } else {
            resultTitleWrapper.getChildren().addAll(noResultsTitleWrapper);
        }
    }

    private void loadResults(Pair<List<Libro>, Integer> data) {

        // elimina eventuali elementi precedenti
        if(!booksResultDisplay.getChildren().isEmpty())
            booksResultDisplay.getChildren().clear();

        List<Libro> results = data.getKey();



        for (Libro l: results) {
            VBox row = new VBox(5); // spacing inside row
            System.out.println(l.getAutori());
            Label titolo = new Label(l.getTitolo());
            Label autore = new Label(l.getAutori());
            Label anno = new Label(l.getAnnoPubblicazione() > 0 ? Integer.toString(l.getAnnoPubblicazione()) : "");

//            private String formatAnno(int anno) {
//                return anno > 0 ? Integer.toString(anno) : "";
//            }
//            Label anno = new Label(formatAnno(l.getAnnoPubblicazione()));

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