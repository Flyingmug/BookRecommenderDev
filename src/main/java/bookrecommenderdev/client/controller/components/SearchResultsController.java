package bookrecommenderdev.client.controller.components;

import bookrecommenderdev.model.data.PageFetcher;
import bookrecommenderdev.model.data.PageResult;
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

  int currentPageIndex;
  int totalResultCount;
  String currentSearch;
  private PageFetcher<Libro> pageFetcher;


  public void setSource(PageFetcher<Libro> pageFetcher, String query) {
    this.currentSearch = query;

    this.pageFetcher = pageFetcher;

    search();
  }


  @FXML
  private void onBookPage(Integer idLibro) {
    Router.go("/book/" + idLibro, TransitionAnimation.LEFT_SLIDE);
  }

  /**
   * Effettua una richiesta di ricerca tramite la chiave fornita.
   */
  private void search() {
    System.out.println("SEARCH Searched: " + currentSearch);  // DEBUG
    // if (query == null || query.isEmpty()) return;  // fixme remove?
    if (currentSearch == null || currentSearch.isEmpty()) return;

    // reset
    currentPageIndex = 0;
    totalResultCount = 0;

    resolve(0);
  }

  /** <p>Gestisce la richiesta al server utilizzando la chiave data {@code query}.
   * <p>L'indice di pagina {@code pageIndex} viene utilizzato per avere un <i>offset</i> sui risultati,
   * <i>limitati</i> a una quantità fissa.
   * <p>todo completare una volte implementati i criteri di ricerca
   * @param pageIndex Indice di offset.
   */
  private void resolve(int pageIndex) {

    try {

      PageResult<Libro> data = pageFetcher.fetch(pageIndex);
      if (data == null) throw new RemoteException();

      List<Libro> books = data.results();
      totalResultCount = data.totalCount();

      if (books.isEmpty() || totalResultCount == 0) {
        booksResultSection.setVisible(false);
        showNoResults(currentSearch);
        return;
      }

      load(books);

    } catch(RemoteException e) {
      System.out.println("SEARCHERR Error while fetching data");
      e.printStackTrace();
    }

  }

  /**
   * <p>Costruisce dinamicamente dei nodi per mostrare i dati di ciascun Libro.
   * <p>I nodi sono spaziati da nodi {@link Separator}.
   * <p>Verifica e riabilita l'uso dei pulsanti di controllo dei risultati.
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
    if (currentPageIndex <= 0) return;

    showDisabled(previousPageButton, true);

    goToPage(currentPageIndex - 1);
  }

  /** <p>Richiede una nuova ricerca alla pagina logica successiva di risultati.
   * <p>Effettua un controllo della validità della chiave di ricerca e del nuovo indice. */
  @FXML
  private void onNext() {
    if (currentSearch == null || currentSearch.isEmpty()) return;
    if ((currentPageIndex + 1) * PAGE_SIZE > totalResultCount) return;

    showDisabled(nextPageButton, true);

    goToPage(currentPageIndex + 1);
  }

  /** Effettua una nuova richiesta per i risultati alla pagina logica di indice {@code newIndex}. */
  private void goToPage(int newIndex) {

    setControlDisabled(previousPageButton, true);
    setControlDisabled(nextPageButton, true);

    currentPageIndex = newIndex;
    resolve(newIndex);
  }


  /** Imposta l'utilizzo dei pulsanti di controllo logicamente rispetto ai valori dei risultati di ricerca. */
  private void setControls() {
    boolean canPrev = currentPageIndex > 0;
    boolean canNext = (currentPageIndex + 1) * PAGE_SIZE < totalResultCount;

    setPrevControlVisibility(canPrev);
    setNextControlVisibility(canNext);
    setControlDisabled(previousPageButton, !canPrev);
    setControlDisabled(nextPageButton, !canNext);
    showDisabled(previousPageButton, !canPrev);
    showDisabled(nextPageButton, !canNext);
  }
  /** Disabilita i comandi di controlli dei risultati. */
  private void setControlDisabled(Button control, boolean disable) {
    control.setDisable(disable);
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
  private void showDisabled(Button control, boolean b) {
    if (b) {
      control.getStyleClass().add("control-button-customdisabled");
    } else {
      control.getStyleClass().remove("control-button-customdisabled");
    }
  }

  /** Genera una stringa di testo per mostrare il numero di risultati visualizzati contro il totale. */
  private String formatIndexCounter() {
    return Math.min(
        PAGE_SIZE*currentPageIndex+1, totalResultCount) +"-"+
        Math.min(PAGE_SIZE*(1+currentPageIndex), totalResultCount) +" di "+
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
