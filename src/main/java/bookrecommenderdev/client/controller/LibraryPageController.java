package bookrecommenderdev.client.controller;

import bookrecommenderdev.client.controller.components.SearchResultsController;
import bookrecommenderdev.client.controller.components.controls.ConfirmActionDialogController;
import bookrecommenderdev.client.controller.errors.components.ErrorBannerController;
import bookrecommenderdev.client.factory.BookResultItemFactory;
import bookrecommenderdev.model.base.Libreria;
import bookrecommenderdev.model.base.Libro;
import bookrecommenderdev.model.exceptions.DataAccessException;
import bookrecommenderdev.model.exceptions.NotFoundException;
import bookrecommenderdev.client.routing.Router;
import bookrecommenderdev.client.routing.animation.TransitionAnimation;
import bookrecommenderdev.client.auth.AuthContext;
import bookrecommenderdev.model.data.PageFetcher;
import bookrecommenderdev.client.routing.AppContext;
import bookrecommenderdev.client.routing.route.Routable;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;

import java.rmi.RemoteException;
import java.util.Map;

import static bookrecommenderdev.Constants.PAGE_SIZE;
import static bookrecommenderdev.model.utils.InputVerifiers.safeParseInt;


/**
 * Controller della pagina dettaglio di una libreria.
 * <p>
 * Visualizza il nome della libreria, i libri contenuti (tramite {@link SearchResultsController})
 * e permette l’eliminazione della libreria tramite una finestra di conferma.
 */
public class LibraryPageController implements Routable {

  @FXML private ConfirmActionDialogController deleteControlController;

  @FXML private Label libraryTitle;
  @FXML private SearchResultsController<Libro> resultsController;

  @FXML private ErrorBannerController errorBannerController;

  private AppContext context;
  private int idLibreria;

  /**
   * Metodo invocato dal sistema di routing quando la pagina viene raggiunta.
   *
   * <p>Valida l’ID libreria, verifica l’autenticazione dell’utente ed effettua la richiesta di ricerca del contenuto.
   *
   * @param params  parametri di percorso (atteso: {@code "id"})
   * @param context contesto applicativo client
   * @param state   stato di navigazione (non utilizzato)
   */
  @Override
  public void onRoute(Map<String, String> params, AppContext context, Object state) {
    this.context = context;

    Integer parsed = safeParseInt(params.get("id"));
    if (parsed == null || parsed <= 0) {
      Router.go("/not-found", TransitionAnimation.LEFT_SLIDE);
      return;
    }
    idLibreria = parsed;

    if (!AuthContext.isAuthenticated()) {
      // Controllo di sicurezza secondario
      Router.go("/login", TransitionAnimation.LEFT_SLIDE);
      return;
    }

    int idUtente = AuthContext.getUser().idUtente();

    resolve(idUtente);
  }

  /**
   * Collega le azioni del dialogo di conferma all’operazione di eliminazione della libreria.
   */
  @FXML
  private void initialize() {
    if (deleteControlController != null) {
      deleteControlController.setOnConfirm(this::deleteLibraryConfirmed);
      deleteControlController.setOnCancel(() -> {});
    }
  }

  /**
   * Esegue l’eliminazione della libreria dopo conferma dell’utente.
   * <p>
   * Disabilita temporaneamente il controllo di eliminazione e, in caso di successo,
   * reindirizza alla pagina di visualizzazione delle librerie.
   */
  private void deleteLibraryConfirmed() {
    if (context == null) return;

    if (!AuthContext.isAuthenticated()) {
      // Controllo di sicurezza secondario
      Router.go("/login", TransitionAnimation.LEFT_SLIDE);
      return;
    }

    int idUtente = AuthContext.getUser().idUtente();

//    if (deleteButton != null) deleteButton.setDisable(true);
    if (deleteControlController != null) deleteControlController.setDisabled(true);

    try {
      context.server().deleteLibreria(idUtente, idLibreria);
      Router.go("/libraries", TransitionAnimation.RIGHT_SLIDE);

    } catch (NotFoundException e) {
      Router.go("/not-found", TransitionAnimation.LEFT_SLIDE);

    } catch (DataAccessException e) {
      if (deleteControlController != null) deleteControlController.setDisabled(false);
      showError(e.getMessage());

    } catch (RemoteException e) {
      if (deleteControlController != null) deleteControlController.setDisabled(false);
      showError("Errore di comunicazione con il server.");

    }
  }

  /**
   * Ottiene e carica i dati della libreria.
   * L'ID della libreria è ottenuto dal metodo {@code onRoute}.
   *
   * @param idUtente id dell’utente proprietario (usato per autorizzazione e query lato server)
   */
  private void resolve(int idUtente) {
    try {
      Libreria lib = context.server().getLibreriaById(idUtente, idLibreria);
      libraryTitle.setText(lib.getNome());

      PageFetcher<Libro> source = page ->
          context.server().searchInLibreria(idUtente, idLibreria, page);

      resultsController.setItemRenderer(this::renderBookItem);
      resultsController.setSource(source, PAGE_SIZE);

    } catch (NotFoundException e) {
      Platform.runLater(() -> Router.go("/not-found", TransitionAnimation.LEFT_SLIDE));

    } catch (DataAccessException e) {
      showError("Errore nel reperimento della libreria.");

    } catch (RemoteException e) {
      showError("Errore nella comunicazione con il server.");

    }
  }

  /**
   * Visualizza un libro della libreria come elemento cliccabile, con navigazione alla pagina del libro.
   *
   * @param l libro da visualizzare
   * @return nodo UI contenente i dati
   */
  private Parent renderBookItem(Libro l) {
    return BookResultItemFactory.create(
        l, id -> Router.go("/book/" + id, TransitionAnimation.LEFT_SLIDE)
    );
  }

  /**
   * Mostra un errore tramite {@link ErrorBannerController}.
   *
   * @param message testo dell’errore
   */
  private void showError(String message) {
    errorBannerController.show(message, null, null);
  }
}