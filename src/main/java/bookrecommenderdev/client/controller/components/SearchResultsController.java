package bookrecommenderdev.client.controller.components;

import bookrecommenderdev.model.DataAccessException;
import bookrecommenderdev.model.data.PageFetcher;
import bookrecommenderdev.model.data.PageResult;
import bookrecommenderdev.model.data.SearchRequest;
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

  @FXML private VBox resultsNotFoundTitle;
  @FXML private Label resultsNotFoundQuery;
  @FXML private VBox booksResultSection;
  @FXML private VBox booksResultsContainer;
  @FXML private Label resultIndexCounter;
  @FXML private Button previousPageButton;
  @FXML private Button nextPageButton;

  @FXML private ErrorBannerController errorBannerController;


  int currentPageIndex;
  int totalResultCount;
  private PageFetcher<Libro> pageFetcher;
  private SearchRequest request;

  public void setSource(PageFetcher<Libro> pageFetcher, SearchRequest req) {
    request = req;

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
    currentPageIndex = 0;
    totalResultCount = 0;

    resolve(0);
  }

  /** <p>Gestisce la richiesta al server utilizzando la chiave data {@code query}.
   * <p>L'indice di pagina {@code pageIndex} viene utilizzato per avere un <i>offset</i> sui risultati,
   * <i>limitati</i> a una quantità fissa.
   * <p>todo completare una volte implementati i criteri di ricerca
   * <p>Verifica e riabilita l'uso dei pulsanti di controllo dei risultati.
   *
   * @param pageIndex Indice di offset.
   */
  private void resolve(int pageIndex) {
    try {

      PageResult<Libro> data = pageFetcher.fetch(pageIndex);

      List<Libro> books = data.results();
      totalResultCount = data.totalCount();

      if (books.isEmpty() || totalResultCount == 0) {
        setNoResultsTitle();
        setResultsVisible(false);
        return;
      }

      setNoResultsTitleVisible(false);
      setResultsVisible(true);
      load(books);
      setControls();

    } catch (DataAccessException e) {

      setResultsVisible(false);
      errorBannerController.show(
          "Errore durante la ricerca (database)",
          () -> resolve(pageIndex),
          null
      );

    } catch(RemoteException e) {

      setResultsVisible(false);
      errorBannerController.show(
          "Server non raggiungibile.",
          () -> resolve(pageIndex),
          null
      );
    }
  }

  /**
   * <p>Costruisce dinamicamente dei nodi per mostrare i dati di ciascun Libro.
   * <p>I nodi sono spaziati da nodi {@link Separator}.
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

  }


  /** <p>Richiede una nuova ricerca alla pagina logica precedente di risultati.
   * <p>Effettua un controllo della validità della chiave di ricerca e del nuovo indice. */
  @FXML
  private void onPrevious() {
    if (request == null) return;
    if (currentPageIndex <= 0) return;

    showDisabled(previousPageButton, true);

    goToPage(currentPageIndex - 1);
  }

  /** <p>Richiede una nuova ricerca alla pagina logica successiva di risultati.
   * <p>Effettua un controllo della validità della chiave di ricerca e del nuovo indice. */
  @FXML
  private void onNext() {
    if (request == null) return;
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

  private void setResultsVisible(boolean visible) {
    booksResultSection.setVisible(visible);
    booksResultSection.setManaged(visible);
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
  private void setNoResultsTitle() {
    setNoResultsTitleVisible(true);
    resultsNotFoundQuery.setText("Nessun risultato trovato per!");
  }
  private void setNoResultsTitleVisible(boolean b) {
    resultsNotFoundTitle.setVisible(b);
    resultsNotFoundTitle.setManaged(b);
  }
}
