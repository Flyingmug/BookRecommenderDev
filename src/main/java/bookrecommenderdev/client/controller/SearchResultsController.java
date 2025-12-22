package bookrecommenderdev.client.controller;

import bookrecommenderdev.routing.AppContext;
import bookrecommenderdev.routing.route.Routable;
import bookrecommenderdev.client.factory.BookResultItemFactory;
import bookrecommenderdev.model.Libro;
import bookrecommenderdev.routing.Router;
import bookrecommenderdev.routing.animation.TransitionAnimation;
import bookrecommenderdev.server.dto.PaginaLibriRisultati;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import static bookrecommenderdev.Constants.PAGE_SIZE;


public class SearchResultsController implements Routable {

  // searchPage
  @FXML private Label resultTitle;
  @FXML private HBox resultNotFoundTitle;
  @FXML private VBox booksResultSection;
  @FXML private VBox booksResultsContainer;
  @FXML private Label resultIndexCounter;
  @FXML private Button previousPageButton;
  @FXML private Button nextPageButton;
  @FXML private ScrollPane booksResultsPage;

  private AppContext context;
  int currentResultPageIndex;
  int totalResultCount;
  String currentSearch;


  @Override
  public void onRoute(Map<String, String> params, AppContext context) {
    this.context = context;
    currentSearch = params.get("query"); // set the current search query
    search(currentSearch);
  }

  @FXML
  private void onPublicBookPage(Integer idLibro) {
    Router.go("/book/" + idLibro, TransitionAnimation.LEFT_SLIDE);
  }

  /**
   * Effettua una richiesta di ricerca tramite la chiave fornita.
   * @param query Chiave di ricerca.
   */
  private void search(String query) {
    System.out.println("SEARCH Searched: " + query);  // DEBUG
    if (query == null || query.isEmpty()) return;

    // reset
    currentResultPageIndex = 0;
    totalResultCount = 0;
    currentSearch = query;

    resolveSearch(query, currentResultPageIndex);
  }

  /** <p>Gestisce la richiesta al server utilizzando la chiave data {@code query}.
   * <p>L'indice di pagina {@code pageIndex} viene utilizzato per avere un <i>offset</i> sui risultati,
   * questi <i>limitati</i> a una quantità fissa.
   * <p>todo completare una volte implementati i criteri di ricerca
   * @param query Chiave di ricerca.
   * @param pageIndex Indice di offset.
   */
  private void resolveSearch(String query, int pageIndex) {

    try {
      PaginaLibriRisultati data = context.server().searchTitolo(query, pageIndex);
      if (data == null) throw new RemoteException();  // temp fixme

      List<Libro> books = data.results();
      int totalResults = data.totalCount();

      if (books.isEmpty() || totalResults == 0) {
        System.out.println("Empty result set."); // DEBUG
        booksResultSection.setVisible(false);
        showNoResults();
        return;
      }

      totalResultCount = totalResults;

      System.out.println("Numero di risultati: " + totalResults); // DEBUG


      loadResults(books);

    } catch(RemoteException e) {
      System.out.println("SEARCHERR Error while fetching data");
      e.printStackTrace();
    }

  }

  /**
   * <p>Costruisce dinamicamente dei nodi per mostrare i dati di ciascun Libro.
   * <p>Ciascun nodo è separato da un {@link Separator}.
   * <p>Riabilita l'uso dei pulsanti di controllo dei risultati.
   * @param results lista di dati risultanti
   */
  private void loadResults(List<Libro> results) {

    // Rimozione di eventuali elementi precedenti
    booksResultsContainer.getChildren().clear();

    resultIndexCounter.setText(formatIndexCounter());

    for (Libro l: results) {
      VBox row = BookResultItemFactory.createBookResultItem(l, this::onPublicBookPage);
      booksResultsContainer.getChildren().add(row);

      if (results.indexOf(l) < results.size() - 1) {
        Separator line = new Separator();
        line.setStyle("-fx-border-colo: #99b1e9");
        booksResultsContainer.getChildren().add(line);
      }
    }

    setControls();
  }

  /** Imposta l'utilizzo dei pulsanti di controllo logicamente rispetto ai valori dei risultati di ricerca. */
  private void setControls() {
    setPrevControlVisibility(currentResultPageIndex > 0);
    setNextControlVisibility((currentResultPageIndex + 1) * PAGE_SIZE < totalResultCount);
    setResultsControlsDisabled(false);
    showDisabled(previousPageButton, false);
    showDisabled(nextPageButton, false);
  }


  /** <p>Richiede una nuova ricerca alla pagina logica precedente di risultati.
   * <p>Effettua un controllo della validità della chiave di ricerca e del nuovo indice. */
  @FXML
  private void onPreviousResults() {
    if (currentSearch == null || currentSearch.isEmpty()) return;
    if (currentResultPageIndex <= 0) return;

    showDisabled(previousPageButton, true);

    goToPage(currentResultPageIndex - 1);
    booksResultsPage.setVvalue(1);  // vai a fondo pagina
  }

  /** <p>Richiede una nuova ricerca alla pagina logica successiva di risultati.
   * <p>Effettua un controllo della validità della chiave di ricerca e del nuovo indice. */
  @FXML
  private void onNextResults() {
    if (currentSearch == null || currentSearch.isEmpty()) return;
    if ((currentResultPageIndex + 1) * PAGE_SIZE > totalResultCount) return;

    showDisabled(nextPageButton, true);

    goToPage(currentResultPageIndex + 1);
    booksResultsPage.setVvalue(0);  // vai a inizio pagina
  }

  /** Effettua una nuova richiesta per i risultati alla pagina logica di indice {@code newIndex}. */
  private void goToPage(int newIndex) {
    // if (newIndex < 0 || newIndex * PAGE_SIZE >= totalResultCount) return;
    // fixme verification divided in two separate methods, but shouldn't be any problem

    setResultsControlsDisabled(true);

    currentResultPageIndex = newIndex;
    resolveSearch(currentSearch, newIndex);
  }



  /** Disabilita i comandi di controlli dei risultati. */
  private void setResultsControlsDisabled(boolean disable) {
    previousPageButton.setDisable(disable);
    nextPageButton.setDisable(disable);
  }

  /** Controlla la visibilità del pulsante di pagina precedente. */
  private void setPrevControlVisibility(boolean visibility) {
    previousPageButton.setVisible(visibility);
  }

  /** Controlla la visibilità del pulsante di pagina successiva. */
  private void setNextControlVisibility(boolean visibility) {
    nextPageButton.setVisible(visibility);
  }

  /** Mostra la selezione del pulsante sulla grafica, aggiungendovi la classe rispettiva. */
  private void showDisabled(Button controlButton, boolean b) {
    if (b) {
      controlButton.getStyleClass().add("control-button-customdisabled");
    } else {
      controlButton.getStyleClass().remove("control-button-customdisabled");
    }
  }

  /** Genera una stringa di testo per mostrare il numero di risultati visualizzati contro il totale. */
  private String formatIndexCounter() {
    return Math.min(
        PAGE_SIZE*currentResultPageIndex+1, totalResultCount) +"-"+
        Math.min(PAGE_SIZE*(1+currentResultPageIndex), totalResultCount) +" di "+
        totalResultCount + " risultati";
  }

  /** Cambia la visibilità del titolo di pagina e del messaggio di "no risultati". */
  private void showNoResults() {
    resultTitle.setVisible(false);
    resultTitle.setManaged(false);
    resultNotFoundTitle.setVisible(true);
    resultNotFoundTitle.setManaged(true);
  }

}
