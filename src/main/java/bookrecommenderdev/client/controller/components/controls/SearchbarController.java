package bookrecommenderdev.client.controller.components.controls;

import bookrecommenderdev.routing.Router;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import static bookrecommenderdev.Constants.MAX_SEARCH_LENGTH;
import static bookrecommenderdev.utils.InputVerifiers.preventMultipleSpacesAndLimit;

public class SearchbarController {

  @FXML private TextField searchInput;

  @FXML
  public void onSearchAction() {
    String input = searchInput.getText();
    Router.go("/search/" + input); // Parametro passato nel percorso
  }

  @FXML
  private void initialize() {
    preventMultipleSpacesAndLimit(searchInput, MAX_SEARCH_LENGTH);
  }

  @FXML void testMethod() { System.out.println("TEST: Click detected"); }

}
