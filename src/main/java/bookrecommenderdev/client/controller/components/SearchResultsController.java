package bookrecommenderdev.client.controller.components;

import bookrecommenderdev.routing.AppContext;
import bookrecommenderdev.client.factory.BookResultItemFactory;
import bookrecommenderdev.model.Libro;
import bookrecommenderdev.routing.Router;
import bookrecommenderdev.routing.animation.TransitionAnimation;
import bookrecommenderdev.server.dto.PaginaLibriRisultati;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;

import java.rmi.RemoteException;
import java.util.List;

import static bookrecommenderdev.Constants.PAGE_SIZE;


public class SearchResultsController {

  // searchPage
  @FXML private Label resultTitle;
  @FXML private VBox resultsNotFoundTitle;
  @FXML private Label resultsNotFoundQuery;
  @FXML private VBox booksResultSection;
  @FXML private VBox booksResultsContainer;
  @FXML private Label resultIndexCounter;
  @FXML private Button previousPageButton;
  @FXML private Button nextPageButton;

  private AppContext context;
  int currentResultPageIndex;
  int totalResultCount;
  String currentSearch;


  public void setContext(AppContext context, String query) {
    this.context = context;
    this.currentSearch = query;
    search(query);
  }


  @FXML
  private void onBookPage(Integer idLibro) {
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

    resolve(query, currentResultPageIndex);
  }

  /** <p>Gestisce la richiesta al server utilizzando la chiave data {@code query}.
   * <p>L'indice di pagina {@code pageIndex} viene utilizzato per avere un <i>offset</i> sui risultati,
   * questi <i>limitati</i> a una quantità fissa.
   * <p>todo completare una volte implementati i criteri di ricerca
   * @param query Chiave di ricerca.
   * @param pageIndex Indice di offset.
   */
  private void resolve(String query, int pageIndex) {

    try {
      PaginaLibriRisultati data = context.server().searchTitolo(query, pageIndex);
      if (data == null) throw new RemoteException();  // temp fixme

      List<Libro> books = data.results();
      int totalResults = data.totalCount();

      if (books.isEmpty() || totalResults == 0) {
        System.out.println("Empty result set."); // DEBUG
        booksResultSection.setVisible(false);
        showNoResults(query);
        return;
      }

      totalResultCount = totalResults;

      System.out.println("Numero di risultati: " + totalResults); // DEBUG

      load(books);

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
  private void load(List<Libro> results) {

    // Rimozione di eventuali elementi precedenti
    booksResultsContainer.getChildren().clear();

    resultIndexCounter.setText(formatIndexCounter());

    for (Libro l: results) {
      Parent row = BookResultItemFactory.createBookResultItem(l, this::onBookPage);
      booksResultsContainer.getChildren().add(row);

      if (results.indexOf(l) < results.size() - 1) {
        Separator line = new Separator();
        booksResultsContainer.getChildren().add(line);
      }
    }

    setControls();
  }


  /** <p>Richiede una nuova ricerca alla pagina logica precedente di risultati.
   * <p>Effettua un controllo della validità della chiave di ricerca e del nuovo indice. */
  @FXML
  private void onPrevious() {
    if (currentSearch == null || currentSearch.isEmpty()) return;
    if (currentResultPageIndex <= 0) return;

    showDisabled(previousPageButton, true);

    goToPage(currentResultPageIndex - 1);
  }

  /** <p>Richiede una nuova ricerca alla pagina logica successiva di risultati.
   * <p>Effettua un controllo della validità della chiave di ricerca e del nuovo indice. */
  @FXML
  private void onNext() {
    if (currentSearch == null || currentSearch.isEmpty()) return;
    if ((currentResultPageIndex + 1) * PAGE_SIZE > totalResultCount) return;

    showDisabled(nextPageButton, true);

    goToPage(currentResultPageIndex + 1);
  }

  /** Effettua una nuova richiesta per i risultati alla pagina logica di indice {@code newIndex}. */
  private void goToPage(int newIndex) {

    setResultsControlsDisabled(true);

    currentResultPageIndex = newIndex;
    resolve(currentSearch, newIndex);
  }


  /** Imposta l'utilizzo dei pulsanti di controllo logicamente rispetto ai valori dei risultati di ricerca. */
  private void setControls() {
    setPrevControlVisibility(currentResultPageIndex > 0);
    setNextControlVisibility((currentResultPageIndex + 1) * PAGE_SIZE < totalResultCount);
    setResultsControlsDisabled(false);
    showDisabled(previousPageButton, false);
    showDisabled(nextPageButton, false);
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
  private void showNoResults(String query) {
    resultTitle.setVisible(false);
    resultTitle.setManaged(false);
    resultsNotFoundTitle.setVisible(true);
    resultsNotFoundTitle.setManaged(true);
    resultsNotFoundQuery.setText("Nessun risultato trovato per: " + query);
  }

}
