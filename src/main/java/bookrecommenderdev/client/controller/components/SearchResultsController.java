package bookrecommenderdev.client.controller.components;

import bookrecommenderdev.client.controller.errors.components.ErrorBannerController;
import bookrecommenderdev.model.exceptions.DataAccessException;
import bookrecommenderdev.model.data.PageFetcher;
import bookrecommenderdev.model.data.PageResult;
import bookrecommenderdev.client.routing.Router;
import bookrecommenderdev.client.routing.animation.TransitionAnimation;
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

/**
 * Controller JavaFX generico per la gestione e visualizzazione di risultati paginati.
 * <p>
 * Incapsula:
 * <ul>
 *   <li>caricamento dei dati tramite una sorgente {@link PageFetcher};</li>
 *   <li>rendering dei risultati tramite un renderer configurabile {@link Function};</li>
 *   <li>paginazione (prev/next) con calcolo degli indici e del totale;</li>
 *   <li>stati UI: placeholder iniziale, “nessun risultato”, errore con azione di retry.</li>
 * </ul>
 *
 * @param <T> tipo dell’elemento renderizzato
 */
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
  private boolean hasSearchedOnce = false;
  private List<T> lastResults;
  private Function<T, Parent> itemRenderer;

  /**
   * Imposta la sorgente dei dati paginati e la dimensione della pagina.
   * <p>
   * Reset dello stato e caricamento della prima pagina.
   *
   * @param pageFetcher sorgente paginata
   * @param pageSize    numero di elementi per pagina (deve essere &gt; 0)
   */
  public void setSource(PageFetcher<T> pageFetcher, int pageSize) {
    this.source = pageFetcher;
    if (pageSize <= 0) throw new IllegalArgumentException("pageSize must be > 0");
    this.pageSize = pageSize;
    lastResults = null;

    refreshFromStart();
  }

  /**
   * Imposta il renderer degli elementi.
   * <p>
   * Se sono presenti risultati già caricati, viene eseguito un re-render della lista corrente
   * senza effettuare una nuova richiesta alla sorgente.
   *
   * @param renderer funzione di rendering (elemento → nodo UI)
   */
  public void setItemRenderer(Function<T, Parent> renderer) {
    this.itemRenderer = renderer;

    if (lastResults != null && !lastResults.isEmpty() && totalResultCount > 0) {
      render(lastResults);
    }
  }

  /**
   * Verifica la presenza di un renderer e aggiorna lo stato della UI di conseguenza.
   *
   * @return {@code true} se il renderer è configurato, {@code false} altrimenti
   */
  private boolean verificaRenderer() {
    boolean isSet = itemRenderer != null;

    if (noRendererBox != null) {
      noRendererBox.setVisible(!isSet);
      noRendererBox.setManaged(!isSet);
    }

    if (!isSet) setResultsVisible(false);
    return isSet;
  }

  /**
   * Forza il re-render dei risultati correnti (se presenti) e aggiorna i controlli di paginazione.
   * <p>
   * Utile quando un’azione cambia solo l’aspetto degli item (es. testi/icone di selezione).
   */
  public void refreshView() {
    if (lastResults != null && totalResultCount > 0) {
      render(lastResults);
      setControls();
    }
  }

  /**
   * Ricarica la pagina corrente dalla sorgente.
   */
  public void refresh() {
    if (source == null) return;
    resolve(currentPageIndex);
  }

  /**
   * Reset dello stato e caricamento della prima pagina.
   */
  public void refreshFromStart() {
    if (source == null) return;
    currentPageIndex = 0;
    totalResultCount = 0;
    lastResults = null;
    hidePlaceholder();
    resolve(0);
  }

  /**
   * Nasconde il placeholder iniziale al primo utilizzo del componente.
   */
  public void hidePlaceholder() {
    if (hasSearchedOnce) return;
    hasSearchedOnce = true;

    if (initialPlaceholder != null) {
      initialPlaceholder.setVisible(false);
      initialPlaceholder.setManaged(false);
    }
  }

  /**
   * Imposta la visibilità dei separatori superiori/inferiori della sezione risultati.
   *
   * @param visible {@code true} per mostrarli, {@code false} per nasconderli
   */
  public void setSeparatorVisible(boolean visible) {
    topSeparator.setVisible(visible);
    topSeparator.setManaged(visible);
    bottomSeparator.setVisible(visible);
    bottomSeparator.setManaged(visible);
  }

  /**
   * Gestisce la richiesta al server utilizzando la chiave data {@code query}.
   * <p>
   * L'indice di pagina {@code pageIndex} viene utilizzato per avere un <i>offset</i> sui risultati,
   * <i>limitati</i> a una quantità fissa.
   *
   * <p>
   * In caso di assenza risultati mostra lo stato “nessun risultato”.
   * In caso di errore mostra un banner con azione di riprova.
   *
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
      showError("Errore durante la ricerca (database)", () -> resolve(pageIndex));

    } catch(RemoteException e) {
      setResultsVisible(false);
      showError("Server non raggiungibile.", () -> resolve(pageIndex));

    }
  }

  /**
   * Visualizza i risultati nel contenitore, separandoli con un {@link Separator}.
   *
   * @param results lista di elementi da renderizzare
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
   * Handler FXML di default: reindirizza alla pagina del libro indicato.
   * <p>
   * Usato solo se un renderer delega l’apertura a questo metodo.
   *
   * @param idLibro id del libro
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

  /**
   * Richiede una nuova pagina di risultati e disabilita temporaneamente i controlli.
   *
   * @param newIndex nuovo indice pagina (0-based)
   */
  private void goToPage(int newIndex) {

    setControlDisabled(previousPageButton, true);
    setControlDisabled(nextPageButton, true);

    currentPageIndex = newIndex;
    resolve(newIndex);
  }

  /**
   * Aggiorna visibilità e abilitazione dei pulsanti prev/next in base allo stato corrente.
   */
  private void setControls() {
    boolean canPrev = currentPageIndex > 0;
    boolean canNext = (currentPageIndex + 1) * pageSize < totalResultCount;

    setControlVisibility(previousPageButton, canPrev);
    setControlVisibility(nextPageButton, canNext);
    setControlDisabled(previousPageButton, !canPrev);
    setControlDisabled(nextPageButton, !canNext);
  }

  /** Disabilita/abilita un controllo dei risultati. */
  private void setControlDisabled(Button control, boolean disable) {
    control.setDisable(disable);
  }

  /** Imposta visibilità e managed per un controllo. */
  private void setControlVisibility(Button control, boolean visibility) {
    control.setVisible(visibility);
    control.setManaged(visibility);
  }

  /** Mostra/nasconde la sezione risultati (lista + paginazione). */
  private void setResultsVisible(boolean visible) {
    resultSection.setVisible(visible);
    resultSection.setManaged(visible);
  }

  /** Mostra lo stato “nessun risultato”. */
  private void showNoResultsText() {
    noResultsSection.setVisible(true);
    noResultsSection.setManaged(true);
  }

  /** Nasconde lo stato “nessun risultato”. */
  private void hideNoResultsText() {
    noResultsSection.setVisible(false);
    noResultsSection.setManaged(false);
  }

  /**
   * Aggiorna l’etichetta che mostra intervallo corrente e totale dei risultati.
   */
  private void setIndexCounter() {
    resultIndexCounter.setText(
        Math.min(pageSize*currentPageIndex+1, totalResultCount) +"-"+
        Math.min(pageSize*(1+currentPageIndex), totalResultCount) +" di "+
        totalResultCount + " risultati"
    );
  }

  /**
   * Mostra un banner d’errore con azioni opzionali di retry/back.
   *
   * @param message messaggio d’errore
   * @param retry   azione di riprova (può essere {@code null})
   */
  private void showError(String message, Runnable retry) {
    errorBannerController.show(message, retry, null);
  }
}
