package bookrecommenderdev.client.controller.components.controls;

import bookrecommenderdev.routing.Router;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class SearchbarController {

  @FXML private TextField searchInput;

  @FXML
  public void onSearchAction() {
    String input = searchInput.getText();
    Router.go("/search/" + input); // Parametro passato nel percorso
  }
  @FXML void testMethod() { System.out.println("TEST: Click detected"); }


}
