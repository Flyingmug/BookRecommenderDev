package bookrecommenderdev.client.controller.components.controls;

import bookrecommenderdev.routing.Router;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import static bookrecommenderdev.Constants.MAX_SEARCH_LENGTH;
import static bookrecommenderdev.utils.InputVerifiers.numericOnlyAndLimit;
import static bookrecommenderdev.utils.InputVerifiers.preventMultipleSpacesAndLimit;

public class SearchbarController {

  private enum SearchType {
    TITOLO,
    AUTORE,
    AUTORE_ANNO;

    SearchType next() {
      return switch (this) {
        case TITOLO -> AUTORE;
        case AUTORE -> AUTORE_ANNO;
        case AUTORE_ANNO -> TITOLO;
      };
    }
  }

  @FXML private TextField searchInput;
  @FXML private TextField yearInput;
  @FXML private Button swapButton;
  @FXML private Label modeLabel;

  private SearchType tipo = SearchType.TITOLO;

  @FXML
  public void search() {
    String input = searchInput.getText() == null ? "" : searchInput.getText().trim();
    if (input.isBlank()) return;

    switch (tipo) {
      case TITOLO -> Router.go("/search/title/" + input);
      case AUTORE -> Router.go("/search/author/" + input);
      case AUTORE_ANNO -> {
        String yearTxt = yearInput.getText() == null ? "" : yearInput.getText().trim();
        if (yearTxt.isBlank()) return;

        int year;
        try {
          year = Integer.parseInt(yearTxt);
        } catch (NumberFormatException e) {
          return;
        }

        Router.go("/search/author/" + input + "/year/" + year);
      }
    }
  }

  @FXML
  private void initialize() {
    preventMultipleSpacesAndLimit(searchInput, MAX_SEARCH_LENGTH);
    numericOnlyAndLimit(yearInput, 4);
  }

  @FXML void swapCriteria() {

    tipo = tipo.next();
    updateSearchBar();
  }

  private void updateSearchBar() {
    switch (tipo) {
      case TITOLO -> {
        modeLabel.setText("Titolo");
        searchInput.setPromptText("Cerca per titolo...");
        yearInput.setVisible(false);
        yearInput.setManaged(false);
      }
      case AUTORE -> {
        modeLabel.setText("Autore");
        searchInput.setPromptText("Cerca per autore...");
        yearInput.setVisible(false);
        yearInput.setManaged(false);
      }
      case AUTORE_ANNO -> {
        modeLabel.setText("Autore & Anno");
        searchInput.setPromptText("Cerca per autore...");
        yearInput.setVisible(true);
        yearInput.setManaged(true);
        yearInput.setPromptText("Anno");
      }
    }
  }
}
