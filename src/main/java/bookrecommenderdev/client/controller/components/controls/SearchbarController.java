package bookrecommenderdev.client.controller.components.controls;

import bookrecommenderdev.model.data.SearchRequest;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.Optional;
import java.util.function.Consumer;

import static bookrecommenderdev.Constants.MAX_SEARCH_LENGTH;
import static bookrecommenderdev.model.utils.InputVerifiers.*;

/**
 * Controller JavaFX della barra di ricerca.
 * <p>
 * Gestisce l’inserimento dei criteri di ricerca e consente di ciclare tra
 * le modalità disponibili (titolo, autore, autore + anno).
 * Alla conferma costruisce un {@link SearchRequest} e lo inoltra tramite callback.
 */
public class SearchbarController {

  /**
   * Tipologie di ricerca supportate dalla barra.
   */
  private enum SearchType {
    TITOLO,
    AUTORE,
    AUTORE_ANNO;

    /**
     * Restituisce il prossimo tipo di ricerca nel ciclo.
     *
     * @return tipo di ricerca successivo
     */
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
  @FXML private Label modeLabel;

  private SearchType tipo = SearchType.TITOLO;
  private Consumer<SearchRequest> onSearch = _ -> {};

  /**
   * Imposta la callback invocata quando viene eseguita una ricerca valida.
   *
   * @param onSearch consumer che riceve il {@link SearchRequest}
   */
  public void setOnSearch(Consumer<SearchRequest> onSearch) {
    this.onSearch = (onSearch == null) ? (_ -> {}) : onSearch;
  }

  /**
   * Handler FXML: tenta di costruire una richiesta di ricerca e,
   * se valida, la inoltra alla callback.
   */
  @FXML
  public void search() {
    buildRequest().ifPresent(onSearch);
  }

  /**
   * Costruisce il {@link SearchRequest} in base al tipo di ricerca corrente
   * e ai valori inseriti dall’utente.
   *
   * @return richiesta di ricerca opzionale (vuota se input non valido)
   */
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

  /**
   * Inizializza la barra di ricerca applicando le regole di validazione
   * sugli input testuali.
   */
  @FXML
  private void initialize() {
    preventMultipleSpacesAndLimit(searchInput, MAX_SEARCH_LENGTH);
    numericOnlyAndLimit(yearInput, 4);
  }

  /**
   * Handler FXML: cambia il criterio di ricerca ciclando
   * tra le modalità disponibili.
   */
  @FXML
  private void swapCriteria() {
    tipo = tipo.next();
    updateSearchBar();
  }

  /**
   * Aggiorna la UI della barra di ricerca in base
   * al tipo di ricerca selezionato.
   */
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
