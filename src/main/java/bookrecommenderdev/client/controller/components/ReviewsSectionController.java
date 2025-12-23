package bookrecommenderdev.client.controller.components;

import bookrecommenderdev.client.factory.ReviewItemFactory;
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

  @FXML VBox reviewsSectionContainer;
  @FXML public Label resultReviewIndexCounter;
  @FXML public HBox reviewsTopSeparator;
  @FXML public HBox reviewsBottomSeparator;
  @FXML public Button nextReviewsButton;
  @FXML public Button previousReviewsButton;
  @FXML public VBox reviewsContainer;

  AppContext context;
  private long idLibro;
  int currentResultPageIndex;
  int totalResultCount;

  public void initializeForBook(long idLibro, AppContext context) {
    this.idLibro = idLibro;
    this.context = context;

    currentResultPageIndex = 0;
    totalResultCount = 0;

    resolveReviews(0);
  }


  /** <p>Gestisce una richiesta a una pagina logica di recensioni per un libro.
   * La richiesta viene fatta utilizzando il campo assegnato nel metodo {@link #initializeForBook(long, AppContext)}.
   * */
  private void resolveReviews(int pageIndex) {
    try {
      PaginaValutazioni data = context.server().getValutazioni(idLibro, pageIndex);
      if (data == null) throw new RemoteException();  // temp fixme

      List<Valutazione> reviews = data.results();
      int totalResults = data.totalCount();

      if (reviews.isEmpty() || totalResults == 0) {
        System.out.println("Empty result set."); // DEBUG
        showNoResults();
        return;
      }

      totalResultCount = totalResults;

      System.out.println("Numero di risultati: " + totalResults); // DEBUG

      loadResults(reviews);

    } catch(RemoteException e) {
      System.out.println("SEARCHERR Error while fetching data");
      e.printStackTrace();
    }

  }

  /**
   * <p>Costruisce dinamicamente dei nodi per mostrare i dati di ciascuna Valutazione.
   * <p>Ciascun nodo è separato da un {@link Separator}.
   * <p>Riabilita l'uso dei pulsanti di controllo dei risultati.
   * @param results lista di dati risultanti
   */
  private void loadResults(List<Valutazione> results) {

    // Rimozione di eventuali elementi precedenti
    reviewsContainer.getChildren().clear();

    resultReviewIndexCounter.setText(formatIndexCounter());

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
    setPrevControlVisibility(currentResultPageIndex > 0);
    setNextControlVisibility((currentResultPageIndex + 1) * REVIEWS_PAGE_SIZE < totalResultCount);
    setResultsControlsDisabled(false);
    showDisabled(previousReviewsButton, false);
    showDisabled(nextReviewsButton, false);
  }


  /** <p>Richiede una nuova ricerca alla pagina logica precedente di risultati.
   * <p>Effettua un controllo della validità della chiave di ricerca e del nuovo indice. */
  @FXML
  private void onPrevReviews() {
    if (totalResultCount <= 0 || currentResultPageIndex <= 0) return;

    showDisabled(previousReviewsButton, true);

    goToPage(currentResultPageIndex - 1);
  }

  /** <p>Richiede una nuova ricerca alla pagina logica successiva di risultati.
   * <p>Effettua un controllo della validità della chiave di ricerca e del nuovo indice. */
  @FXML
  private void onNextReviews() {
    if (totalResultCount <= 0 ||
        (currentResultPageIndex + 1) * REVIEWS_PAGE_SIZE > totalResultCount)
      return;

    showDisabled(nextReviewsButton, true);

    goToPage(currentResultPageIndex + 1);

  }

  /** Effettua una nuova richiesta per i risultati alla pagina logica di indice {@code newIndex}. */
  private void goToPage(int newIndex) {
    setResultsControlsDisabled(true);

    currentResultPageIndex = newIndex;
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

}
