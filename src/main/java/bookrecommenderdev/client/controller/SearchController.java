package bookrecommenderdev.client.controller;

import bookrecommenderdev.client.controller.components.SearchResultsController;
import bookrecommenderdev.routing.AppContext;
import bookrecommenderdev.routing.route.Routable;
import javafx.fxml.FXML;
import javafx.scene.Parent;

import java.util.Map;

public class SearchController implements Routable {

  @FXML private Parent resultsSection;
  @FXML private SearchResultsController resultsSectionController;

  @Override
  public void onRoute(Map<String, String> params, AppContext context) {

    if (resultsSectionController == null) return;

    resultsSectionController.setContext(context, params.get("query"));
  }


}
