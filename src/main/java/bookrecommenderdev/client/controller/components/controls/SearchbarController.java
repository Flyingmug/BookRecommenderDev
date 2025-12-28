package bookrecommenderdev.client.controller.components.controls;

import bookrecommenderdev.model.data.SearchRequest;
import bookrecommenderdev.routing.Router;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.Optional;
import java.util.function.Consumer;

import static bookrecommenderdev.Constants.MAX_SEARCH_LENGTH;
import static bookrecommenderdev.utils.InputVerifiers.*;

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
  private Consumer<SearchRequest> onSearch;

  public void setOnSearch(java.util.function.Consumer<SearchRequest> onSearch) {
    this.onSearch = (onSearch == null) ? (req -> {}) : onSearch;
  }

  @FXML
  public void search() {
    buildRequest().ifPresent(onSearch);
  }

  private Optional<SearchRequest> buildRequest() {
    String input = notNull(searchInput.getText());
    if (input.isBlank()) return Optional.empty();

    return switch (tipo) {
      case TITOLO -> Optional.of(SearchRequest.perTitolo(input));
      case AUTORE -> Optional.of(SearchRequest.perAutore(input));
      case AUTORE_ANNO -> {
        String yearTxt = notNull(yearInput.getText());
        if (yearTxt.isBlank()) yield Optional.empty();

        try {
          int year = Integer.parseInt(yearTxt);
          yield Optional.of(SearchRequest.perAutoreAnno(input, year));
        } catch (NumberFormatException e) {
          yield Optional.empty();
        }
      }
    };
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
        modeLabel.setStyle("-fx-background-color: #daf5fe;");
        searchInput.setPromptText("Cerca per titolo...");
        yearInput.setVisible(false);
        yearInput.setManaged(false);
      }

      case AUTORE -> {
        modeLabel.setText("Autore");
        modeLabel.setStyle("-fx-background-color: #dae1fe;");

        searchInput.setPromptText("Cerca per autore...");
        yearInput.setVisible(false);
        yearInput.setManaged(false);
      }

      case AUTORE_ANNO -> {
        modeLabel.setText("Autore & Anno");
        modeLabel.setStyle("-fx-background-color: #e9dafe;");

        searchInput.setPromptText("Cerca per autore...");
        yearInput.setVisible(true);
        yearInput.setManaged(true);
        yearInput.setPromptText("Anno");
      }

    }
  }
}
