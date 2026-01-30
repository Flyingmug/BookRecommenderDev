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

/**
 * Controller JavaFX della sezione “consigli dell’utente” relativa a un libro.
 * <p>
 * Mostra l’elenco dei suggerimenti che l’utente autenticato ha inserito per il libro corrente,
 * consente l’aggiunta (fino a un limite) e la rimozione dei suggerimenti già selezionati.
 * La sezione viene nascosta se l’utente non è autenticato o non è autorizzato.
 */
public class UserRecommendationsController {

  @FXML private VBox root;

  @FXML private VBox consigliContainer;

  @FXML private Button addButton;

  @FXML private StackPane emptyPlaceholder;

  @FXML private ErrorBannerController errorBannerController;

  private AppContext context;

  private int idLibroBase;

  private Integer idUtente;

  private List<Libro> selectedConsigli = List.of();

  /**
   * Inizializza la sezione con le informazioni di un libro dato e un contesto applicativo.
   * <p>
   * La sezione viene mostrata solo se l’utente è autenticato e il libro base
   * appartiene alle librerie dell’utente (gate di autorizzazione).
   *
   * @param context    contesto applicativo client
   * @param idLibroBase id del libro per cui gestire i suggerimenti dell’utente
   */
  public void setContext(AppContext context, int idLibroBase) {
    this.context = context;
    this.idLibroBase = idLibroBase;

    // Default nascosto: la sezione appare solo se autenticato e autorizzato
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
      showError("Server non raggiungibile.", () -> setContext(context, idLibroBase));

    } catch (DataAccessException e) {
      setVisible(true);
      showError("Errore di database.", () -> setContext(context, idLibroBase));
    }
  }

  /**
   * Ricarica dal server i suggerimenti dell’utente e aggiorna la UI.
   */
  private void refresh() {
    if (idUtente == null) return;

    try {
      selectedConsigli = context.server().getSuggerimentiUtente(idUtente, idLibroBase);
      renderConsigli();

    } catch (RemoteException e) {
      showError("Server non raggiungibile.", this::refresh);

    } catch (DataAccessException e) {
      showError("Errore di database.", this::refresh);
    }
  }

  /**
   * Visualizza la lista dei suggerimenti dell’utente nel contenitore.
   * <p>
   * Gestisce anche la visibilità del pulsante di aggiunta e del placeholder “vuoto”.
   */
  private void renderConsigli() {
    consigliContainer.getChildren().clear();

    for (Libro l : selectedConsigli) {
      consigliContainer.getChildren().add(buildRow(l));
    }

    // Mostra/nasconde opzione di aggiunta (limite: 3)
    boolean canAdd = selectedConsigli.size() < 3;
    addButton.setVisible(canAdd);
    addButton.setManaged(canAdd);

    if (selectedConsigli.isEmpty()) {
      showPlaceholder();
    } else {
      hidePlaceholder();
    }
  }

  /**
   * Costruisce la riga UI per un singolo suggerimento: item libro + dialog di conferma rimozione.
   *
   * @param l libro suggerito dall’utente
   * @return nodo riga completo
   */
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
   * Handler FXML: naviga alla pagina di selezione del libro da consigliare.
   * <p>
   * La rotta è costruita in base al libro base corrente.
   */
  @FXML
  private void onAdd() {
    if (context == null || idUtente == null) return;

    bookrecommenderdev.client.routing.Router.go(
        "/book/" + idLibroBase + "/recommendations/add",
        bookrecommenderdev.client.routing.animation.TransitionAnimation.LEFT_SLIDE
    );
  }

  /**
   * Conferma e invia al server la rimozione di un suggerimento.
   * In caso di successo (o di suggerimento già assente) ricarica lo stato.
   *
   * @param idLibroCons id del libro consigliato da rimuovere
   */
  private void confirmDelete(int idLibroCons) {
    try {
      context.server().deleteSuggerimentoLibro(idUtente, idLibroBase, idLibroCons);
      refresh();

    } catch (NotFoundException e) {
      // Stato locale non allineato: ricarica comunque
      refresh();

    } catch (RemoteException e) {
      showError("Server non raggiungibile.", () -> confirmDelete(idLibroCons));

    } catch (DataAccessException e) {
      showError("Errore di database.", () -> confirmDelete(idLibroCons));
    }
  }

  /**
   * Imposta la visibilità dell’intera sezione.
   *
   * @param visible {@code true} per mostrare la sezione, {@code false} per nasconderla
   */
  private void setVisible(boolean visible) {
    root.setVisible(visible);
    root.setManaged(visible);
  }

  /** Nasconde il placeholder di lista vuota. */
  private void hidePlaceholder() {
    emptyPlaceholder.setVisible(false);
    emptyPlaceholder.setManaged(false);
  }

  /** Mostra il placeholder di lista vuota. */
  private void showPlaceholder() {
    emptyPlaceholder.setVisible(true);
    emptyPlaceholder.setManaged(true);
  }

  /**
   * Mostra un errore tramite {@link ErrorBannerController} con azione di riprova.
   *
   * @param message messaggio di errore
   * @param retry   azione di riprova (può essere {@code null})
   */
  private void showError(String message, Runnable retry) {
    errorBannerController.show(message, retry, null);
  }
}