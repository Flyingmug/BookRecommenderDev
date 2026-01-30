package bookrecommenderdev.client.controller;

import bookrecommenderdev.client.controller.components.SearchResultsController;
import bookrecommenderdev.client.controller.components.controls.SearchbarController;
import bookrecommenderdev.client.controller.errors.components.ErrorBannerController;
import bookrecommenderdev.client.factory.BookResultItemFactory;
import bookrecommenderdev.model.base.Libro;
import bookrecommenderdev.model.data.PageFetcher;
import bookrecommenderdev.model.exceptions.AlreadyExistsException;
import bookrecommenderdev.model.exceptions.DataAccessException;
import bookrecommenderdev.model.exceptions.LimitExceededException;
import bookrecommenderdev.model.exceptions.NotFoundException;
import bookrecommenderdev.client.routing.AppContext;
import bookrecommenderdev.client.routing.Router;
import bookrecommenderdev.client.routing.animation.TransitionAnimation;
import bookrecommenderdev.client.auth.AuthContext;
import bookrecommenderdev.client.routing.route.Routable;
import javafx.fxml.FXML;
import javafx.scene.Parent;

import java.rmi.RemoteException;
import java.util.Map;

import static bookrecommenderdev.Constants.PAGE_SIZE;
import static java.lang.Integer.parseInt;

/**
 * Controller JavaFX per selezionare un libro da consigliare a partire da un "libro base".
 * <p>
 * Accesso consentito solo a utenti autenticati e solo se il libro base è presente
 * nelle librerie dell’utente.
 */
public class RecommendationSelectorController implements Routable {

  @FXML private SearchbarController searchbarController;
  @FXML private SearchResultsController<Libro> resultsController;
  @FXML private ErrorBannerController errorBannerController;

  private AppContext context;
  private int idLibroBase;
  private int idUtente;
  private boolean setup = false;

  /**
   * Valida l’ID libro, verifica autenticazione e controlla il requisito:
   * <p><i>il libro base deve appartenere alle librerie dell’utente</i>.
   */
  @Override
  public void onRoute(Map<String, String> params, AppContext context, Object state) {
    this.context = context;

    Integer parsed;
    try {
      parsed = parseInt(params.get("id"));
    }  catch (NumberFormatException e) {
      parsed = null;
    }

    if (parsed == null || parsed <= 0) {
      Router.go("/not-found", TransitionAnimation.LEFT_SLIDE);
      return;
    }
    idLibroBase = parsed;

    if (!AuthContext.isAuthenticated()) {
      Router.go("/login", TransitionAnimation.LEFT_SLIDE);
      return;
    }
    idUtente = AuthContext.getUser().idUtente();

    // Gate: base book must be in user's libraries
    try {
      boolean allowed = context.server().isLibroInLibrerieUtente(idUtente, idLibroBase);
      if (!allowed) {
        Router.go("/not-found", TransitionAnimation.LEFT_SLIDE); // or a dedicated "not allowed" page
        return;
      }
    } catch (RemoteException e) {
      showError("Server non raggiungibile.", () -> onRoute(params, context, state), () -> Router.go("/book/" + idLibroBase));
      return;
    } catch (DataAccessException e) {
      showError(e.getMessage(), () -> onRoute(params, context, state), () -> Router.go("/book/" + idLibroBase));
      return;
    }

    setup();
  }

  /**
   * Imposta il renderer dei risultati con pulsante di selezione.
   */
  @FXML
  private void initialize() {
    resultsController.setItemRenderer(this::renderSelectableBook);
  }

  /**
   * Registra l’handler della ricerca (una sola volta).
   * Ogni ricerca imposta una sorgente paginata e ricarica dalla prima pagina.
   */
  private void setup() {
    if (setup) return;
    setup = true;

    searchbarController.setOnSearch(req -> {
      PageFetcher<Libro> source = indicePagina -> context.server().cercaLibro(req, indicePagina);

      resultsController.setSource(source, PAGE_SIZE);
      resultsController.refreshFromStart();
    });
  }

  /**
   * Visualizza un risultato come elemento selezionabile.
   * Il libro base non è selezionabile (non si può consigliare se stesso).
   */
  private Parent renderSelectableBook(Libro l) {
    int selectedId = l.getIdLibro();

    boolean invalid = selectedId == idLibroBase;

    return BookResultItemFactory.create(
        l,
        null, // no apertura pagina libro
        invalid ? null : _ -> selectBook(selectedId),
        "Seleziona",
        "mdi2c-check"
    );
  }

  /**
   * Invia al server la selezione del libro consigliato.
   * In caso di successo ritorna alla pagina del libro base.
   */
  private void selectBook(int idLibroCons) {
    if (idLibroCons == idLibroBase) return;

    try {
      context.server().inserisciSuggerimentoLibro(idUtente, idLibroBase, idLibroCons);

      // success -> torna alla home
      Router.go("/book/" + idLibroBase, TransitionAnimation.RIGHT_SLIDE);

    } catch (AlreadyExistsException e) {
      showError("Hai già consigliato questo libro.", null, null);

    } catch (LimitExceededException e) {
      showError("Hai già raggiunto il limite di 3 consigli.", () -> Router.go("/book/" + idLibroBase), null);

    } catch (NotFoundException e) {
      Router.go("/not-found", TransitionAnimation.LEFT_SLIDE);

    } catch (DataAccessException e) {
      showError(e.getMessage(), () -> selectBook(idLibroCons), null);

    } catch (RemoteException e) {
      showError("Errore di comunicazione con il server.", () -> selectBook(idLibroCons), null);
    }
  }

  /**
   * Mostra un errore tramite {@link ErrorBannerController}.
   *
   * @param message testo dell’errore
   * @param retry   azione opzionale di riprova
   * @param back    azione opzionale di ritorno
   */
  private void showError(String message, Runnable retry, Runnable back) {
    errorBannerController.show(message, retry, back);
  }
}
