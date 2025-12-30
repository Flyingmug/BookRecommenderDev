package bookrecommenderdev.client.controller.components;

import bookrecommenderdev.client.factory.RecommendedItemFactory;
import bookrecommenderdev.model.data.PageFetcher;
import bookrecommenderdev.routing.AppContext;
import bookrecommenderdev.routing.Router;
import bookrecommenderdev.routing.animation.TransitionAnimation;
import bookrecommenderdev.server.dto.LibroConsigliato;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

import static bookrecommenderdev.Constants.RECOMMENDATIONS_PAGE_SIZE;

public class RecommendationsController {

  @FXML private VBox root;
  @FXML private SearchResultsController<LibroConsigliato> resultsController;

  private AppContext context;
  private int idLibroBase;

  public void setContext(AppContext context, int idLibro) {
    this.context = context;
    this.idLibroBase = idLibro;

    setVisible(true);

    PageFetcher<LibroConsigliato> source =
        page -> context.server().cercaConsigli(idLibroBase, page);

    resultsController.hidePlaceholder();

    resultsController.setItemRenderer(this::renderSuggestionItem);

    resultsController.setSource(source, RECOMMENDATIONS_PAGE_SIZE);

    resultsController.refreshFromStart();
  }


  private Parent renderSuggestionItem(LibroConsigliato cons) {
    return RecommendedItemFactory.create(
        cons, id -> Router.go("/book/" + id, TransitionAnimation.LEFT_SLIDE)
    );
  }

  private void setVisible(boolean visible) {
    if (root == null) return;
    root.setVisible(visible);
    root.setManaged(visible);
  }
}
