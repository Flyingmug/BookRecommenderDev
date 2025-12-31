package bookrecommenderdev.client.controller.components;

import bookrecommenderdev.client.controller.errors.components.ErrorBannerController;
import bookrecommenderdev.client.factory.BookResultItemFactory;
import bookrecommenderdev.client.factory.ConfirmActionDialogFactory;
import bookrecommenderdev.model.base.Libro;
import bookrecommenderdev.model.exceptions.DataAccessException;
import bookrecommenderdev.model.exceptions.NotFoundException;
import bookrecommenderdev.client.routing.AppContext;
import bookrecommenderdev.client.auth.AuthContext;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.rmi.RemoteException;
import java.util.List;

public class UserRecommendationsController {

  @FXML private VBox root;
  @FXML private VBox consigliContainer;
  @FXML private Button addButton;
  @FXML private StackPane emptyPlaceholder;
  @FXML private ErrorBannerController errorBannerController;


  private AppContext context;
  private int idLibroBase;
  private Integer idUtente; // null = non autenticato
  private List<Libro> selectedConsigli = List.of();


  public void setContext(AppContext context, int idLibroBase) {
    this.context = context;
    this.idLibroBase = idLibroBase;

    // default nascosto
    setVisible(false);

    if (!AuthContext.isAuthenticated()) return;
    idUtente = AuthContext.getUser().idUtente();

    try {
      boolean allowed = context.server().isLibroInLibrerieUtente(idUtente, idLibroBase);
      if (!allowed) return;

      setVisible(true);
      refresh();

    } catch (RemoteException e) {
      setVisible(true);
      showError("Server non raggiungibile.", () -> setContext(context, idLibroBase), null);

    } catch (DataAccessException e) {
      setVisible(true);
      showError("Errore di database.", () -> setContext(context, idLibroBase), null);

    }
  }

  private void refresh() {
    if (idUtente == null) return;

    try {
      selectedConsigli = context.server().getConsigliUtente(idUtente, idLibroBase);
      renderConsigli();

    } catch (RemoteException e) {
      showError("Server non raggiungibile.", this::refresh, null);
    } catch (DataAccessException e) {
      showError("Errore di database.", this::refresh, null);
    }
  }

  private void renderConsigli() {
    consigliContainer.getChildren().clear();

    for (Libro l : selectedConsigli) {
      consigliContainer.getChildren().add(buildRow(l));
    }

    // Mostra/Nascondi opzione di aggiunta
    boolean canAdd = selectedConsigli.size() < 3;
    addButton.setVisible(canAdd);
    addButton.setManaged(canAdd);
    if (selectedConsigli.isEmpty()) {
      showPlaceholder();
    } else {
      hidePlaceholder();
    }
  }

  private Parent buildRow(Libro l) {
    Parent item = BookResultItemFactory.create(
        l, id -> bookrecommenderdev.client.routing.Router.go("/book/" + id)
    );
    HBox.setHgrow(item, Priority.ALWAYS);

    Parent dialog = ConfirmActionDialogFactory.create(
        () -> confirmDelete(l.getIdLibro()), () -> {}
    );

    HBox row = new HBox(item, dialog);
    row.getStyleClass().add("user-recommended-row");
    row.setAlignment(Pos.CENTER);

    return row;
  }

  /**
   * todo doc nota: metodo usato.
   */
  @FXML
  private void onAdd() {
    if (context == null || idUtente == null) return;
    bookrecommenderdev.client.routing.Router.go(
        "/book/" + idLibroBase + "/recommendations/add",
        bookrecommenderdev.client.routing.animation.TransitionAnimation.LEFT_SLIDE
    );
  }

  private void confirmDelete(int idLibroCons) {
    try {
      context.server().deleteConsiglio(idUtente, idLibroBase, idLibroCons);
      refresh();
    } catch (NotFoundException e) {
      refresh();

    } catch (RemoteException e) {
      showError("Server non raggiungibile.", () -> confirmDelete(idLibroCons), null);

    } catch (DataAccessException e) {
      showError("Errore di database.", () -> confirmDelete(idLibroCons), null);
    }
  }

  private void setVisible(boolean visible) {
    root.setVisible(visible);
    root.setManaged(visible);
  }

  private void hidePlaceholder() {
    emptyPlaceholder.setVisible(false);
    emptyPlaceholder.setManaged(false);
  }

  private void showPlaceholder() {
    emptyPlaceholder.setVisible(true);
    emptyPlaceholder.setManaged(true);
  }

  private void showError(String message, Runnable retry, Runnable back) {
    errorBannerController.show(message, retry, back);
  }
}
