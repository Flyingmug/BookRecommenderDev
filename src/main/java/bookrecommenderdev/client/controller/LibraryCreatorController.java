package bookrecommenderdev.client.controller;

import bookrecommenderdev.client.controller.components.SearchResultsController;
import bookrecommenderdev.client.controller.components.controls.SearchbarController;
import bookrecommenderdev.client.controller.errors.components.ErrorBannerController;
import bookrecommenderdev.client.factory.BookResultItemFactory;
import bookrecommenderdev.client.factory.BookResultMinimalFactory;
import bookrecommenderdev.model.exceptions.AlreadyExistsException;
import bookrecommenderdev.model.exceptions.DataAccessException;
import bookrecommenderdev.model.Libro;
import bookrecommenderdev.model.data.PageFetcher;
import bookrecommenderdev.model.data.SearchRequest;
import bookrecommenderdev.routing.AppContext;
import bookrecommenderdev.routing.Router;
import bookrecommenderdev.routing.auth.AuthContext;
import bookrecommenderdev.routing.route.Routable;
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
import static bookrecommenderdev.utils.InputVerifiers.*;

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

  @Override
  public void onRoute(Map<String, String> params, AppContext context, Object state) {
    this.context = context;
  }

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

  private void performSearch(SearchRequest req) {
    if (req == null || !req.isValid()) return;

    PageFetcher<Libro> source = indicePagina -> context.server().cercaLibro(req, indicePagina);
    resultsController.setSource(source, PAGE_SIZE);

    resultsController.refresh(); // loads first page or re-renders
  }

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

  @FXML
  private void onCreate() {
    String name = notNull(nameField.getText());
    if (name.isBlank() || selectedIds.isEmpty()) return;

    if (!AuthContext.isAuthenticated()) {
      Router.go("/login");
      return;
    }

    int userId = AuthContext.getUser().getId_utente();

    try {
      context.server().createLibreria(userId, name, new ArrayList<>(selectedIds));
      Router.go("/libraries");

    } catch (AlreadyExistsException e) {
      showError("Una libreria con lo stesso nome è presente.", null, null);

    } catch (DataAccessException e) {
      showError(e.getMessage(), null, null);

    } catch (RemoteException e) {
      showError("Errore nella comunicazione con il server.", null, null);
    }
  }

  private void refreshSelectedList() {
    if (selectedContainer == null) return;

    selectedContainer.getChildren().clear();

    for (Libro l : selectedBooks) {
      Parent minimal = BookResultMinimalFactory.create(
          l,
          "mdi2m-minus",
          _ -> toggleSelection(l)
      );

      selectedContainer.getChildren().add(minimal);
    }
  }

  private void showError(String message, Runnable retry, Runnable back) {
    errorBannerController.show(
        message, retry, back
    );
  }

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
