package bookrecommenderdev.client.controller.components;

import bookrecommenderdev.client.controller.errors.components.ErrorBannerController;
import bookrecommenderdev.model.exceptions.DataAccessException;
import bookrecommenderdev.model.data.PageFetcher;
import bookrecommenderdev.model.data.PageResult;
import bookrecommenderdev.model.data.SearchRequest;
import bookrecommenderdev.routing.Router;
import bookrecommenderdev.routing.animation.TransitionAnimation;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.rmi.RemoteException;
import java.util.List;
import java.util.function.Function;

import static bookrecommenderdev.Constants.PAGE_SIZE;


public class SearchResultsController<T> {

  @FXML private StackPane initialPlaceholder;
  @FXML private StackPane noResultsSection;
  @FXML private Label noResultsText;
  @FXML private VBox resultSection;
  @FXML private VBox resultsContainer;
  @FXML private Label resultIndexCounter;
  @FXML private Button previousPageButton;
  @FXML private Button nextPageButton;
  @FXML private VBox noRendererBox;
  @FXML private Separator topSeparator;
  @FXML private Separator bottomSeparator;

  @FXML private ErrorBannerController errorBannerController;

  int currentPageIndex;
  int totalResultCount;
  private int pageSize = PAGE_SIZE; // default
  private PageFetcher<T> source;
  private SearchRequest request;
  private boolean hasSearchedOnce = false;
  private List<T> lastResults;
  private Function<T, Parent> itemRenderer;

  public void setSource(PageFetcher<T> pageFetcher, int pageSize) {
    this.source = pageFetcher;
    if (pageSize <= 0) throw new IllegalArgumentException("pageSize must be > 0");
    this.pageSize = pageSize;
    lastResults = null;

    refreshFromStart();
  }

  public void setItemRenderer(Function<T, Parent> renderer) {
    this.itemRenderer = renderer;

    // Ricarica gli elementi senza eseguire un refresh dell'intera pagina todo add this note to doc
    if (lastResults != null && !lastResults.isEmpty() && totalResultCount > 0) {
      render(lastResults);
    }
  }

  private boolean verificaRenderer() {
    boolean isSet = itemRenderer != null;

    if (noRendererBox != null) {
      noRendererBox.setVisible(!isSet);
      noRendererBox.setManaged(!isSet);
    }

    if (!isSet) setResultsVisible(false);
    return isSet;
  }

  public void refreshView() {
    if (lastResults != null && totalResultCount > 0) {
      render(lastResults);
      setControls();
    }
  }

  public void refresh() {
    if (source == null) return;
    resolve(currentPageIndex);
  }

  public void refreshFromStart() {
    if (source == null) return;
    currentPageIndex = 0;
    totalResultCount = 0;
    lastResults = null;
    hidePlaceholder();
    resolve(0);
  }

  public void hidePlaceholder() {
    if (hasSearchedOnce) return;
    hasSearchedOnce = true;

    if (initialPlaceholder != null) {
      initialPlaceholder.setVisible(false);
      initialPlaceholder.setManaged(false);
    }
  }

  public void setSeparatorVisible(boolean visible) {
    topSeparator.setVisible(visible);
    topSeparator.setManaged(visible);
    bottomSeparator.setVisible(visible);
    bottomSeparator.setManaged(visible);
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
      PageResult<T> data = source.fetch(pageIndex);

      List<T> items = data.results();
      totalResultCount = data.totalCount();

      lastResults = items;

      if (items.isEmpty() || totalResultCount == 0) {
        showNoResultsText();
        setResultsVisible(false);
        return;
      }

      hideNoResultsText();
      setResultsVisible(true);
      render(items);
      setControls();

    } catch (DataAccessException e) {
      setResultsVisible(false);
      showError("Errore durante la ricerca (database)", () -> resolve(pageIndex), null);

    } catch(RemoteException e) {
      setResultsVisible(false);
      showError("Server non raggiungibile.", () -> resolve(pageIndex), null);

    }
  }

  /**
   * <p>Costruisce dinamicamente dei nodi per mostrare i dati di ciascun elemento.
   * <p>I nodi sono spaziati da nodi {@link Separator}.
   * @param results lista di dati risultanti
   */
  private void render(List<T> results) {
    // Controllo della presenza di un elemento di render
    if (!verificaRenderer()) return;

    // Rimozione di eventuali elementi precedenti
    resultsContainer.getChildren().clear();
    setIndexCounter();

    for (int i = 0; i < results.size(); i++) {
      Parent row = itemRenderer.apply(results.get(i));
      resultsContainer.getChildren().add(row);

      if (i < results.size() - 1) {
        resultsContainer.getChildren().add(new Separator());
      }
    }
  }

  /**
   * Link di default: reindirizza alla pagina del libro indicato.
   * @param idLibro Id libro
   */
  @FXML
  private void onBookPage(Integer idLibro) {
    Router.go("/book/" + idLibro, TransitionAnimation.LEFT_SLIDE);
  }

  /** <p>Richiede una nuova ricerca alla pagina logica precedente di risultati.
   * <p>Effettua un controllo della validità della chiave di ricerca e del nuovo indice. */
  @FXML
  private void onPrevious() {
    if (source == null) return;
    if (currentPageIndex <= 0) return;

    goToPage(currentPageIndex - 1);
  }

  /** <p>Richiede una nuova ricerca alla pagina logica successiva di risultati.
   * <p>Effettua un controllo della validità della chiave di ricerca e del nuovo indice. */
  @FXML
  private void onNext() {
    if (source == null) return;
    if ((currentPageIndex + 1) * pageSize > totalResultCount) return;

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
    boolean canNext = (currentPageIndex + 1) * pageSize < totalResultCount;

    setControlVisibility(previousPageButton, canPrev);
    setControlVisibility(nextPageButton, canNext);
    setControlDisabled(previousPageButton, !canPrev);
    setControlDisabled(nextPageButton, !canNext);
  }

  /** Disabilita i comandi di controlli dei risultati. */
  private void setControlDisabled(Button control, boolean disable) {
    control.setDisable(disable);
  }

  /** Controlla la visibilità del pulsante. */
  private void setControlVisibility(Button control, boolean visibility) {
    control.setVisible(visibility);
    control.setManaged(visibility);
  }

  private void setResultsVisible(boolean visible) {
    resultSection.setVisible(visible);
    resultSection.setManaged(visible);
  }

  /** Cambia la visibilità del titolo di pagina e del messaggio di "no risultati". */
  private void showNoResultsText() {
    noResultsSection.setVisible(true);
    noResultsSection.setManaged(true);
  }

  private void hideNoResultsText() {
    noResultsSection.setVisible(false);
    noResultsSection.setManaged(false);
  }

  /** Mostra il numero di risultati visualizzati contro il totale. */
  private void setIndexCounter() {
    resultIndexCounter.setText(
        Math.min(pageSize*currentPageIndex+1, totalResultCount) +"-"+
        Math.min(pageSize*(1+currentPageIndex), totalResultCount) +" di "+
        totalResultCount + " risultati"
    );
  }

  private void showError(String message, Runnable retry, Runnable back) {
    errorBannerController.show(message, retry, back);
  }
}
