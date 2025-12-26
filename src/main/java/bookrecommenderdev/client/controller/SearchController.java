package bookrecommenderdev.client.controller;

import bookrecommenderdev.client.controller.components.SearchResultsController;
import bookrecommenderdev.model.Libro;
import bookrecommenderdev.model.data.PageFetcher;
import bookrecommenderdev.model.data.SearchRequest;
import bookrecommenderdev.routing.AppContext;
import bookrecommenderdev.routing.Router;
import bookrecommenderdev.routing.route.Routable;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;

import java.util.Map;

public class SearchController implements Routable {

  @FXML private Label searchedTitle;
  @FXML private Parent resultsSection;
  @FXML private SearchResultsController resultsSectionController;


  @Override
  public void onRoute(Map<String, String> params, AppContext context) {

    SearchRequest request;

    if (params.containsKey("q")) {
      request = SearchRequest.perTitolo(params.get("q"));
      searchedTitle.setText("Titolo: " + params.get("q"));

    } else if (params.containsKey("a") && params.containsKey("y")) {
      request = SearchRequest.perAutoreAnno(
          params.get("a"),
          Integer.parseInt(params.get("y"))
      );
      searchedTitle.setText("Autore: " + params.get("a") + " (" + params.get("y") + ")");

    } else if (params.containsKey("a")) {
      request = SearchRequest.perAutore(params.get("a"));
      searchedTitle.setText("Autore: " + params.get("a"));

    } else {
      // Defensive fallback — invalid route
      Router.go("/");
      return;
    }

    PageFetcher<Libro> source = page ->
        context.server().cercaLibro(request, page);

    resultsSectionController.setSource(source, request);
  }

}
