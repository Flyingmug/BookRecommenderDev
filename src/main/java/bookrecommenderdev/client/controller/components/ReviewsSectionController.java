package bookrecommenderdev.client.controller.components;

import bookrecommenderdev.client.controller.errors.components.ErrorBannerController;
import bookrecommenderdev.client.factory.ReviewItemFactory;
import bookrecommenderdev.model.exceptions.DataAccessException;
import bookrecommenderdev.model.Valutazione;
import bookrecommenderdev.routing.AppContext;
import bookrecommenderdev.server.dto.PaginaValutazioni;
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


  public void initializeForBook(int idLibro, AppContext context) {
    this.idLibro = idLibro;
    this.context = context;

    currentPageIndex = 0;
    totalResultCount = 0;

    resolveReviews(0);
  }


  /** <p>Gestisce una richiesta a una pagina logica di recensioni per un libro.
   * La richiesta viene fatta utilizzando il campo assegnato nel metodo {@link #initializeForBook(int, AppContext)}.
   * */
  private void resolveReviews(int pageIndex) {
    try {
      PaginaValutazioni data = context.server().cercaValutazioni(idLibro, pageIndex); // never null now

      List<Valutazione> reviews = data.results();
      int totalResults = data.totalCount();

      totalResultCount = totalResults;

      if (totalResults == 0 || reviews.isEmpty()) {
        showNoResults();
        // riabilitazione comandi
        setResultsControlsDisabled(false);
        showDisabled(previousReviewsButton, false);
        showDisabled(nextReviewsButton, false);
        return;
      }

      // mostra sezione (serve in caso di "riprova")
      reviewsSectionContainer.setVisible(true);
      reviewsSectionContainer.setManaged(true);
      errorBannerController.hide();

      resultReviewIndexCounter.setText(formatIndexCounter());

      load(reviews);

    } catch (DataAccessException e) {

      showErrorState("Errore database durante il caricamento delle recensioni.", () -> resolveReviews(pageIndex));
    } catch (RemoteException e) {

      showErrorState("Server non raggiungibile. Impossibile caricare le recensioni.", () -> resolveReviews(pageIndex));
    }
  }

  /**
   * <p>Costruisce dinamicamente dei nodi per mostrare i dati di ciascuna Valutazione.
   * <p>Ciascun nodo è separato da un {@link Separator}.
   * <p>Riabilita l'uso dei pulsanti di controllo dei risultati.
   * @param results lista di dati risultanti
   */
  private void load(List<Valutazione> results) {

    // Rimozione di eventuali elementi precedenti
    reviewsContainer.getChildren().clear();

    for (Valutazione v: results) {
      Parent row = ReviewItemFactory.createReviewNode(v);
      reviewsContainer.getChildren().add(row);

      if (results.indexOf(v) < results.size() - 1) {
        Separator line = new Separator();
        reviewsContainer.getChildren().add(line);
        line.getStyleClass().add("review-separator");
      }
    }

    setControls();
  }

  /** Imposta l'utilizzo dei pulsanti di controllo logicamente rispetto ai valori dei risultati di ricerca. */
  private void setControls() {
    setPrevControlVisibility(currentPageIndex > 0);
    setNextControlVisibility((currentPageIndex + 1) * REVIEWS_PAGE_SIZE < totalResultCount);
    setResultsControlsDisabled(false);
    showDisabled(previousReviewsButton, false);
    showDisabled(nextReviewsButton, false);
  }


  /** <p>Richiede una nuova ricerca alla pagina logica precedente di risultati.
   * <p>Effettua un controllo della validità della chiave di ricerca e del nuovo indice. */
  @FXML
  private void onPrev() {
    if (totalResultCount <= 0 || currentPageIndex <= 0) return;

    showDisabled(previousReviewsButton, true);

    goToPage(currentPageIndex - 1);
  }

  /** <p>Richiede una nuova ricerca alla pagina logica successiva di risultati.
   * <p>Effettua un controllo della validità della chiave di ricerca e del nuovo indice. */
  @FXML
  private void onNext() {
    if (totalResultCount <= 0 ||
        (currentPageIndex + 1) * REVIEWS_PAGE_SIZE > totalResultCount)
      return;

    showDisabled(nextReviewsButton, true);

    goToPage(currentPageIndex + 1);

  }

  /** Effettua una nuova richiesta per i risultati alla pagina logica di indice {@code newIndex}. */
  private void goToPage(int newIndex) {
    setResultsControlsDisabled(true);

    currentPageIndex = newIndex;
    resolveReviews(newIndex);
  }



  /** Disabilita i comandi di controlli dei risultati. */
  private void setResultsControlsDisabled(boolean disable) {
    previousReviewsButton.setDisable(disable);
    nextReviewsButton.setDisable(disable);
  }
  /** Controlla la visibilità del pulsante di pagina precedente. */
  private void setPrevControlVisibility(boolean visibility) {
    reviewsTopSeparator.setVisible(visibility);
    reviewsTopSeparator.setManaged(visibility);
  }

  /** Controlla la visibilità del pulsante di pagina successiva. */
  private void setNextControlVisibility(boolean visibility) {
    reviewsBottomSeparator.setVisible(visibility);
    reviewsBottomSeparator.setManaged(visibility);
  }

  /** Genera una stringa di testo per mostrare il numero totale di risultati. */
  private String formatIndexCounter() {
    return totalResultCount + " risultati";
  }

  /** Collassa la pagina e imposta la visibilità a {@code false}. */
  private void showNoResults() {
    reviewsSectionContainer.setVisible(false);
    reviewsSectionContainer.setManaged(false);
  }

  /** Mostra la selezione del pulsante sulla grafica, aggiungendovi la classe rispettiva. */
  private void showDisabled(Button controlButton, boolean b) {
    if (b) {
      controlButton.getStyleClass().add("control-button-customdisabled");
    } else {
      controlButton.getStyleClass().remove("control-button-customdisabled");
    }
  }

  private void showErrorState(String message, Runnable retry) {
    // Keep section visible OR hide it — your choice.
    // I recommend keeping it visible so the page doesn't "collapse".
    reviewsSectionContainer.setVisible(true);
    reviewsSectionContainer.setManaged(true);

    // Re-enable buttons so user isn't stuck
    setResultsControlsDisabled(false);
    showDisabled(previousReviewsButton, false);
    showDisabled(nextReviewsButton, false);

    // If you have an ErrorBanner in this section:
    errorBannerController.show(message, retry, null);
  }
}
