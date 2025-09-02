package com.example.bookrecommenderdev.client.controller;

import com.example.bookrecommenderdev.model.Libro;
import com.example.bookrecommenderdev.server.ServerInterface;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.util.Pair;
import org.kordamp.ikonli.javafx.FontIcon;

import java.awt.*;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Optional;

public class ClientBRController {
  final static int PAGE_SIZE = 50;

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
  @FXML
  private Button previousPageButton;
  @FXML
  private Button nextPageButton;


  private ServerInterface bookRecommender;
  int currentResultPageIndex;
  int bookResultCount;
  String currentSearchInput;

  @FXML
  public void initialize() {
    initRegistry();
    initSetupLayout();

    currentResultPageIndex = 0;
    bookResultCount = 0;
    currentSearchInput = "";
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
    currentResultPageIndex=0;
    currentSearchInput="";
    homePage.setVisible(true);
  }
  /**
   * Ripristina gli elementi della pagina iniziale nelle loro posizioni originali.
   */
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
    String newInput = searchbar.getText();
    System.out.println("Searched: " + newInput);  // DEBUG
    if (newInput == null || newInput.isEmpty()) return;

    topSearchbar();
    homePage.setVisible(false);
    resultPage.setVisible(true);

    // if there is a new input, set it as the current search value
    boolean newSearch = !newInput.equals(currentSearchInput);
    if (newSearch) {
      setPrevControlVisibility(false);
      currentResultPageIndex = 0;
      currentSearchInput = newInput;
    }

    try {
      Pair<List<Libro>, Integer> data = bookRecommender.searchTitolo(newInput, currentResultPageIndex);
      List<Libro> books = data.getKey();
      int totalResults = data.getValue();

      if (books.isEmpty() || totalResults == 0) {
        System.out.println("Empty result set."); // DEBUG
        showNoResults();
        return;
      }

      System.out.println("Numero di risultati: " + totalResults); // DEBUG

      if (newSearch) setNextControlVisibility(totalResults > PAGE_SIZE);

      setResultsFoundTitle(true);
      booksResultWrapper.setVisible(true);
      loadResults(data);

    } catch(RemoteException e) {
      System.out.println("Error while fetching data");
      e.printStackTrace();
    }

  }
  private void showNoResults() {
    setResultsFoundTitle(false);
    booksResultWrapper.setVisible(false);
  }
  private void topSearchbar() {
    if (!navbar.getChildren().contains(searchbarWrapper)) {
      homePage.getChildren().clear();
      searchbarWrapper.getStyleClass().remove("searchbar-center");
      searchbarWrapper.getStyleClass().add("searchbar-navbar");
      navbar.getChildren().addFirst(searchbarWrapper);
    }
  }
  private void setPrevControlVisibility(boolean visibility) {
    previousPageButton.setVisible(visibility);
  }
  private void setNextControlVisibility(boolean visibility) {
    nextPageButton.setVisible(visibility);
  }

  /**
   * Rimpiazza i children del contenitore del titolo a seconda del risultato della ricerca.
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

  /**
   * Attraverso data costruisce degli oggetti di tipo VBox per mostrare i dati di ciascun Libro
   * @param data
   */
  private void loadResults(Pair<List<Libro>, Integer> data) {

    // elimina eventuali elementi precedenti
    if(!booksResultDisplay.getChildren().isEmpty())
      booksResultDisplay.getChildren().clear();

    List<Libro> results = data.getKey();

    bookResultCount = data.getValue();
    resultIndexCounter.setText(formatIndexCounter());

    for (Libro l: results) {
      VBox row = new VBox(5);
      row.setStyle("-fx-padding: 10; -fx-border-color: #99b1e9; -fx-border-width: 0 0 1 0;");

      Label titolo = new Label(l.getTitolo());
      titolo.setMaxWidth(750);
      titolo.setEllipsisString("...");
      titolo.setFont(new Font("Arial", 14));
      titolo.setTextFill(Paint.valueOf("#1e81c5"));
      titolo.getStyleClass().add("result-book");
      Button titoloButton = new Button();
      titoloButton.setGraphic(titolo);
      titoloButton.getStyleClass().add("result-book-button");
      titoloButton.setOnAction(e -> onPublicBookPage(l.getIdLibro()));


      Label autori = new Label(l.getAutori());
      autori.setMaxWidth(750);
      autori.setEllipsisString("...");

      autori.setFont(new Font("Arial", 12));

      Label anno = new Label(l.getAnnoPubblicazione() > 0 ? Integer.toString(l.getAnnoPubblicazione()) : "");
      anno.setFont(new Font("Arial", 12));

      row.getChildren().addAll(titoloButton, autori, anno);
      booksResultDisplay.getChildren().add(row);
    }

  }
  private String formatIndexCounter() {
    return Math.min(PAGE_SIZE*currentResultPageIndex+1, bookResultCount) +
        "-" + Math.min(PAGE_SIZE*(1+currentResultPageIndex), bookResultCount) +
        " di " + bookResultCount + " risultati";
  }

  @FXML
  protected void onNextResults() {
    goToPage(currentResultPageIndex + 1);
    resultPage.setVvalue(0);
  }
  @FXML
  protected void onPreviousResults() {
    goToPage(currentResultPageIndex - 1);
    resultPage.setVvalue(1);
  }
  private void goToPage(int newIndex) {
    if (newIndex < 0 || newIndex * PAGE_SIZE >= bookResultCount) return;

    currentResultPageIndex = newIndex;
    onSearchAction();

    setPrevControlVisibility(currentResultPageIndex > 0);
    setNextControlVisibility((currentResultPageIndex + 1) * PAGE_SIZE < bookResultCount);
  }

  @FXML
  protected void onLibraryList() {
    try {
      bookRecommender.getListLibrerie(1);

    } catch (RemoteException e) {

    }
  }

  @FXML
  protected void onPublicBookPage(int id_libro) {
    System.out.println("id: " + id_libro);
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