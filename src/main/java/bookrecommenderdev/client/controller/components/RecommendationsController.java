package bookrecommenderdev.client.controller.components;

import bookrecommenderdev.client.factory.RecommendedItemFactory;
import bookrecommenderdev.model.data.PageFetcher;
import bookrecommenderdev.client.routing.AppContext;
import bookrecommenderdev.client.routing.Router;
import bookrecommenderdev.client.routing.animation.TransitionAnimation;
import bookrecommenderdev.model.dto.LibroConsigliato;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

import static bookrecommenderdev.Constants.RECOMMENDATIONS_PAGE_SIZE;

/**
 * Controller JavaFX per la visualizzazione dei libri consigliati.
 * <p>
 * Mostra una lista paginata di suggerimenti relativi a un libro di riferimento
 * e consente la navigazione alla pagina di dettaglio dei libri consigliati.
 */
public class RecommendationsController {

  @FXML private VBox root;
  @FXML private SearchResultsController<LibroConsigliato> resultsController;
  private AppContext context;
  private int idLibroBase;

  /**
   * Inizializza il componente impostando il contesto applicativo
   * e il libro di riferimento.
   * <p>
   * Configura la sorgente dei dati, il renderer degli elementi
   * e avvia il caricamento della prima pagina di risultati.
   *
   * @param context contesto applicativo client
   * @param idLibro id del libro per cui mostrare i suggerimenti
   */
  public void setContext(AppContext context, int idLibro) {
    this.context = context;
    this.idLibroBase = idLibro;

    setVisible(true);

    PageFetcher<LibroConsigliato> source =
        page -> context.server().cercaSuggerimentiLibro(idLibroBase, page);

    resultsController.hidePlaceholder();
    resultsController.setSeparatorVisible(false);

    resultsController.setItemRenderer(this::renderSuggestionItem);
    resultsController.setSource(source, RECOMMENDATIONS_PAGE_SIZE);

    resultsController.refreshFromStart();
  }

  /**
   * Crea il nodo grafico per un singolo suggerimento di libro.
   *
   * @param cons libro consigliato
   * @return nodo JavaFX rappresentante il suggerimento
   */
  private Parent renderSuggestionItem(LibroConsigliato cons) {
    return RecommendedItemFactory.create(
        cons,
        id -> Router.go("/book/" + id, TransitionAnimation.LEFT_SLIDE)
    );
  }

  /**
   * Imposta la visibilità del componente.
   *
   * @param visible {@code true} per mostrare il componente, {@code false} per nasconderlo
   */
  private void setVisible(boolean visible) {
    if (root == null) return;
    root.setVisible(visible);
    root.setManaged(visible);
  }
}