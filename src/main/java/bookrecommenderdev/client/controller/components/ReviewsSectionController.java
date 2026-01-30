package bookrecommenderdev.client.controller.components;

import bookrecommenderdev.client.controller.errors.components.ErrorBannerController;
import bookrecommenderdev.client.factory.ReviewItemFactory;
import bookrecommenderdev.model.exceptions.DataAccessException;
import bookrecommenderdev.model.base.Valutazione;
import bookrecommenderdev.client.routing.AppContext;
import bookrecommenderdev.model.dto.PaginaValutazioni;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.rmi.RemoteException;
import java.util.List;

import static bookrecommenderdev.Constants.REVIEWS_PAGE_SIZE;

/**
 * Controller JavaFX della sezione “recensioni” nella pagina di dettaglio di un libro.
 * <p>
 * Carica le valutazioni dal server in modo paginato, renderizza ogni recensione tramite
 * {@link ReviewItemFactory} e gestisce la navigazione tra pagine (prev/next) e gli stati
 * di errore/assenza risultati.
 */
public class ReviewsSectionController {

  @FXML private VBox reviewsSectionContainer;
  @FXML private Label resultReviewIndexCounter;
  @FXML private HBox reviewsTopSeparator;
  @FXML private HBox reviewsBottomSeparator;
  @FXML private Button nextReviewsButton;
  @FXML private Button previousReviewsButton;
  @FXML private VBox reviewsContainer;

  @FXML private ErrorBannerController errorBannerController;

  AppContext context;
  private int idLibro;
  int currentPageIndex;
  int totalResultCount;

  /**
   * Inizializza la sezione recensioni per un libro specifico.
   * <p>
   * Imposta contesto e id, resetta lo stato di paginazione e carica la prima pagina.
   *
   * @param idLibro  id del libro
   * @param context  contesto applicativo client
   */
  public void initializeForBook(int idLibro, AppContext context) {
    this.idLibro = idLibro;
    this.context = context;

    currentPageIndex = 0;
    totalResultCount = 0;

    resolveReviews(0);
  }

  /**
   * Richiede al server una pagina logica di recensioni e aggiorna la UI di conseguenza.
   * <p>
   * In assenza di risultati collassa la sezione; in caso di errore mostra un banner con retry.
   *
   * @param pageIndex indice della pagina da caricare (0-based)
   */
  private void resolveReviews(int pageIndex) {
    try {
      PaginaValutazioni data = context.server().cercaValutazioni(idLibro, pageIndex);

      List<Valutazione> reviews = data.results();
      int totalResults = data.totalCount();

      totalResultCount = totalResults;

      if (totalResults == 0 || reviews.isEmpty()) {
        showNoResults();
        setResultsControlsDisabled(false);
        return;
      }

      // Mostra sezione (utile in caso di retry)
      reviewsSectionContainer.setVisible(true);
      reviewsSectionContainer.setManaged(true);
      errorBannerController.hide();

      resultReviewIndexCounter.setText(formatIndexCounter());

      load(reviews);

    } catch (DataAccessException e) {
      showErrorState(
          "Errore database durante il caricamento delle recensioni.",
          () -> resolveReviews(pageIndex)
      );

    } catch (RemoteException e) {
      showErrorState(
          "Server non raggiungibile. Impossibile caricare le recensioni.",
          () -> resolveReviews(pageIndex)
      );
    }
  }

  /**
   * Renderizza la lista di recensioni nel contenitore, separando gli elementi con un {@link Separator}.
   * Al termine aggiorna lo stato dei controlli di paginazione.
   *
   * @param results lista di valutazioni da visualizzare
   */
  private void load(List<Valutazione> results) {

    // Rimuove eventuali elementi precedenti
    reviewsContainer.getChildren().clear();

    for (Valutazione v : results) {
      Parent row = ReviewItemFactory.create(v);
      reviewsContainer.getChildren().add(row);

      if (results.indexOf(v) < results.size() - 1) {
        Separator line = new Separator();
        reviewsContainer.getChildren().add(line);
        line.getStyleClass().add("review-separator");
      }
    }

    setControls();
  }

  /**
   * Aggiorna visibilità e abilitazione dei controlli di paginazione in base allo stato corrente.
   */
  private void setControls() {
    setPrevControlVisibility(currentPageIndex > 0);
    setNextControlVisibility((currentPageIndex + 1) * REVIEWS_PAGE_SIZE < totalResultCount);
    setResultsControlsDisabled(false);
  }

  /**
   * Handler UI: richiede la pagina precedente di recensioni.
   */
  @FXML
  private void onPrev() {
    if (totalResultCount <= 0 || currentPageIndex <= 0) return;
    goToPage(currentPageIndex - 1);
  }

  /**
   * Handler UI: richiede la pagina successiva di recensioni.
   */
  @FXML
  private void onNext() {
    if (totalResultCount <= 0 ||
        (currentPageIndex + 1) * REVIEWS_PAGE_SIZE > totalResultCount)
      return;

    goToPage(currentPageIndex + 1);
  }

  /**
   * Effettua una nuova richiesta alla pagina logica indicata, disabilitando temporaneamente i controlli.
   *
   * @param newIndex nuovo indice pagina (0-based)
   */
  private void goToPage(int newIndex) {
    setResultsControlsDisabled(true);

    currentPageIndex = newIndex;
    resolveReviews(newIndex);
  }

  /**
   * Disabilita/abilita i comandi di navigazione tra pagine.
   *
   * @param disable {@code true} per disabilitare i pulsanti, {@code false} per abilitarli
   */
  private void setResultsControlsDisabled(boolean disable) {
    previousReviewsButton.setDisable(disable);
    nextReviewsButton.setDisable(disable);
  }

  /**
   * Controlla la visibilità della sezione associata al controllo “pagina precedente”.
   */
  private void setPrevControlVisibility(boolean visibility) {
    reviewsTopSeparator.setVisible(visibility);
    reviewsTopSeparator.setManaged(visibility);
  }

  /**
   * Controlla la visibilità della sezione associata al controllo “pagina successiva”.
   */
  private void setNextControlVisibility(boolean visibility) {
    reviewsBottomSeparator.setVisible(visibility);
    reviewsBottomSeparator.setManaged(visibility);
  }

  /**
   * Genera il testo riepilogativo del numero di risultati.
   *
   * @return stringa del tipo “N risultati”
   */
  private String formatIndexCounter() {
    return totalResultCount + " risultati";
  }

  /**
   * Nasconde la sezione recensioni (nessun risultato disponibile).
   */
  private void showNoResults() {
    reviewsSectionContainer.setVisible(false);
    reviewsSectionContainer.setManaged(false);
  }

  /**
   * Imposta lo stato di errore della sezione mostrando un banner con azione di riprova.
   *
   * @param message messaggio di errore
   * @param retry   azione di riprova (può essere {@code null})
   */
  private void showErrorState(String message, Runnable retry) {
    // Mantiene la sezione visibile per evitare “salti” nella pagina
    reviewsSectionContainer.setVisible(true);
    reviewsSectionContainer.setManaged(true);

    // Riabilita i pulsanti per evitare che l’utente rimanga bloccato
    setResultsControlsDisabled(false);

    errorBannerController.show(message, retry, null);
  }
}