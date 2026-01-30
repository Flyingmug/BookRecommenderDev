package bookrecommenderdev.client.controller;

import bookrecommenderdev.client.controller.components.SearchResultsController;
import bookrecommenderdev.client.controller.components.controls.SearchbarController;
import bookrecommenderdev.client.controller.errors.components.ErrorBannerController;
import bookrecommenderdev.client.factory.BookResultItemFactory;
import bookrecommenderdev.client.factory.BookResultMinimalFactory;
import bookrecommenderdev.model.exceptions.AlreadyExistsException;
import bookrecommenderdev.model.exceptions.DataAccessException;
import bookrecommenderdev.model.base.Libro;
import bookrecommenderdev.model.data.PageFetcher;
import bookrecommenderdev.model.data.SearchRequest;
import bookrecommenderdev.client.routing.AppContext;
import bookrecommenderdev.client.routing.Router;
import bookrecommenderdev.client.auth.AuthContext;
import bookrecommenderdev.client.routing.route.Routable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static bookrecommenderdev.Constants.MAX_LIBRARY_NAME_LENGTH;
import static bookrecommenderdev.Constants.PAGE_SIZE;
import static bookrecommenderdev.model.utils.InputVerifiers.*;

/**
 * Controller JavaFX per la creazione di una nuova libreria utente.
 *
 * <p>Permette di:
 * <ul>
 *   <li>Inserire il nome della libreria (con validazioni di input);</li>
 *   <li>Cercare libri e selezionarli per includerli nella libreria;</li>
 *   <li>Inviare al server la richiesta di creazione con l’elenco degli ID selezionati.</li>
 * </ul>
 */
public class LibraryCreatorController implements Routable {

  @FXML private SearchbarController searchbarController;
  @FXML private SearchResultsController<Libro> resultsController;
  @FXML private TextField nameField;
  @FXML private VBox selectedContainer;
  @FXML private Button confirmButton;

  @FXML private ErrorBannerController errorBannerController;

  private AppContext context;
  private final Set<Integer> selectedIds = new HashSet<>();
  private final ObservableList<Libro> selectedBooks =  FXCollections.observableArrayList();

  /**
   * Metodo invocato dal sistema di routing quando la pagina viene raggiunta.
   *
   * @param params  parametri di percorso (non utilizzati)
   * @param context contesto applicativo client
   * @param state   stato di navigazione (non utilizzato)
   */
  @Override
  public void onRoute(Map<String, String> params, AppContext context, Object state) {
    this.context = context;
  }

  /**
   * Configura validazioni e binding UI, imposta l’azione di ricerca e prepara il rendering
   * selezionabile dei risultati.
   */
  @FXML
  private void initialize() {
    preventMultipleSpacesAndLimit(nameField, MAX_LIBRARY_NAME_LENGTH);
    setupGrowingField();

    confirmButton.disableProperty().bind(
        Bindings.createBooleanBinding(
            () -> isBlank(nameField.getText()) || selectedBooks.isEmpty(),
            nameField.textProperty(),
            selectedBooks
        )
    );

    searchbarController.setOnSearch(this::performSearch);

    resultsController.setItemRenderer(this::renderSelectableResultItem);
  }

  /**
   * Esegue una ricerca di libri usando la richiesta proveniente dalla searchbar.
   * <p>
   * La sorgente dati viene impostata sul {@link SearchResultsController} tramite un {@link PageFetcher}
   * che recupera la pagina richiesta dal server (RMI).
   *
   * @param req richiesta di ricerca (se non valida viene ignorata)
   */
  private void performSearch(SearchRequest req) {
    if (req == null || !req.isValid()) return;

    PageFetcher<Libro> source = indicePagina -> context.server().cercaLibro(req, indicePagina);
    resultsController.setSource(source, PAGE_SIZE);

    resultsController.refresh(); // loads first page or re-renders
  }

  /**
   * Visualizza un singolo risultato di ricerca come elemento selezionabile.
   * <p>
   * Il pulsante di azione cambia testo/icona in base allo stato di selezione del libro.
   *
   * @param libro libro da visualizzare
   * @return nodo UI contenente i dati
   */
  private Parent renderSelectableResultItem(Libro libro) {
    boolean selected = selectedIds.contains(libro.getIdLibro());

    String text = selected ? "Rimuovi" : "Aggiungi";
    String icon = selected ? "mdi2m-minus" : "mdi2p-plus";

    return BookResultItemFactory.create(
        libro,
        null,
        _ -> toggleSelection(libro),
        text,
        icon
    );
  }

  /**
   * Alterna lo stato di selezione di un libro.
   * <p>
   * Aggiorna la lista dei libri selezionati e forza
   * l’aggiornamento della vista risultati per riflettere testo/icona del pulsante.
   *
   * @param libro libro da aggiungere/rimuovere dalla selezione
   */
  private void toggleSelection(Libro libro) {
    int id = libro.getIdLibro();

    if (selectedIds.contains(id)) {
      selectedIds.remove(id);
      selectedBooks.removeIf(b -> b.getIdLibro() == id);
    } else {
      selectedIds.add(id);
      selectedBooks.add(libro);
    }

    refreshSelectedList();

    // update button text in current results page:
    resultsController.refreshView();
  }

  /**
   * Handler UI per confermare la creazione della libreria.
   * <p>
   * Valida l'input (nome non vuoto e almeno un libro), verifica il login,
   * poi invoca il server per registrare la nuova libreria.
   */
  @FXML
  private void onCreate() {
    String name = notNull(nameField.getText());
    if (name.isBlank() || selectedIds.isEmpty()) return;

    if (!AuthContext.isAuthenticated()) {
      Router.go("/login");
      return;
    }

    int userId = AuthContext.getUser().idUtente();

    try {
      context.server().registraLibreria(userId, name, new ArrayList<>(selectedIds));
      Router.go("/libraries");

    } catch (AlreadyExistsException e) {
      showError("Una libreria con lo stesso nome è presente.");

    } catch (DataAccessException e) {
      showError(e.getMessage());

    } catch (RemoteException e) {
      showError("Errore nella comunicazione con il server.");
    }
  }

  /**
   * Aggiorna la UI dei libri selezionati, mostrando una lista “minimale” (priva di dettagli) con azione di rimozione.
   */
  private void refreshSelectedList() {
    if (selectedContainer == null) return;

    selectedContainer.getChildren().clear();

    for (Libro l : selectedBooks) {
      Parent minimal = BookResultMinimalFactory.create(
          l,
          _ -> toggleSelection(l),
          "mdi2m-minus"
      );

      selectedContainer.getChildren().add(minimal);
    }
  }

  /**
   * Mostra un messaggio di errore tramite {@link ErrorBannerController}.
   *
   * @param message testo dell’errore
   */
  private void showError(String message) {
    errorBannerController.show(
        message, null, null
    );
  }

  /**
   * Configura il dimensionamento automatico per il contenitore grafico del nome della libreria,
   * adattando la larghezza in base al testo inserito (per motivi di stile).
   */
  private void setupGrowingField() {
    Text text = new Text();
    text.setManaged(false);
    text.setVisible(false);

    text.fontProperty().bind(nameField.fontProperty());
    text.textProperty().bind(nameField.textProperty());

    nameField.prefWidthProperty().bind(
        Bindings.createDoubleBinding(
            () -> Math.max(50, text.getLayoutBounds().getWidth() + 20),
            text.layoutBoundsProperty()
        )
    );
  }
}
