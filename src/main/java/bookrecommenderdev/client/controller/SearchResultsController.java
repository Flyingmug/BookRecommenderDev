package bookrecommenderdev.client.controller;

import bookrecommenderdev.routing.AppContext;
import bookrecommenderdev.routing.Routable;
import bookrecommenderdev.client.factory.BookDisplayFactory;
import bookrecommenderdev.model.Libro;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Pair;
import org.kordamp.ikonli.javafx.FontIcon;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;


public class SearchResultsController implements Routable {
  final static int PAGE_SIZE = 50;
  static BookDisplayFactory bookDisplayCreator = new BookDisplayFactory();

  // searchPage
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
  @FXML
  private ScrollPane booksResultsPage;

  private AppContext context;
  int currentResultPageIndex;
  int bookResultCount;
  String currentSearchInput;


  @Override
  public void onRoute(Map<String, String> params, AppContext context) {
    this.context = context;
  }

  @FXML
  public void initialize() {

    currentResultPageIndex = 0;
    bookResultCount = 0;
    currentSearchInput = "";

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

  @FXML
  protected void onSearchAction() {

    // get input
//    String newInput = searchbar.getText();
    String newInput = "";
    System.out.println("Searched: " + newInput);  // DEBUG
    if (newInput == null || newInput.isEmpty()) return;

//    topSearchbar();
    booksResultsPage.setVisible(true);

    // if there is a new input, set it as the current search value
    boolean newSearch = !newInput.equals(currentSearchInput);
    if (newSearch) {
      setPrevControlVisibility(false);
      currentResultPageIndex = 0;
      currentSearchInput = newInput;
    }

    try {
      Pair<List<Libro>, Integer> data = context.server().searchTitolo(newInput, currentResultPageIndex);
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
   * @param data dati ricevuti
   */
  private void loadResults(Pair<List<Libro>, Integer> data) {

    // elimina eventuali elementi precedenti
    booksResultDisplay.getChildren().clear();

    List<Libro> results = data.getKey();

    bookResultCount = data.getValue();
    resultIndexCounter.setText(formatIndexCounter());

    for (Libro l: results) {
      VBox row = bookDisplayCreator.createVBox(l, this::onPublicBookPage);
      booksResultDisplay.getChildren().add(row);
    }

  }

  private void onPublicBookPage(Integer integer) {

  }

  private String formatIndexCounter() {
    return Math.min(PAGE_SIZE*currentResultPageIndex+1, bookResultCount) +
        "-" + Math.min(PAGE_SIZE*(1+currentResultPageIndex), bookResultCount) +
        " di " + bookResultCount + " risultati";
  }




  @FXML
  protected void onNextResults() {
    goToPage(currentResultPageIndex + 1);
    booksResultsPage.setVvalue(0);
  }
  @FXML
  protected void onPreviousResults() {
    goToPage(currentResultPageIndex - 1);
    booksResultsPage.setVvalue(1);
  }
  private void goToPage(int newIndex) {
    if (newIndex < 0 || newIndex * PAGE_SIZE >= bookResultCount) return;

    currentResultPageIndex = newIndex;
    onSearchAction();

    setPrevControlVisibility(currentResultPageIndex > 0);
    setNextControlVisibility((currentResultPageIndex + 1) * PAGE_SIZE < bookResultCount);
  }



  //
  //
  //
  //
  // test methods

//  private void testBooksearchpage() {
//    // simulate input insertion
//    searchbar.setText("Goat Brothers");
//    // simulate search icon click
//    onSearchAction();
//  }
}
