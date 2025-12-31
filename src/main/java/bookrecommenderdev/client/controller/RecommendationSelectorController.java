package bookrecommenderdev.client.controller;

import bookrecommenderdev.client.controller.components.SearchResultsController;
import bookrecommenderdev.client.controller.components.controls.SearchbarController;
import bookrecommenderdev.client.controller.errors.components.ErrorBannerController;
import bookrecommenderdev.client.factory.BookResultItemFactory;
import bookrecommenderdev.model.Libro;
import bookrecommenderdev.model.data.PageFetcher;
import bookrecommenderdev.model.exceptions.AlreadyExistsException;
import bookrecommenderdev.model.exceptions.DataAccessException;
import bookrecommenderdev.model.exceptions.LimitExceededException;
import bookrecommenderdev.model.exceptions.NotFoundException;
import bookrecommenderdev.routing.AppContext;
import bookrecommenderdev.routing.Router;
import bookrecommenderdev.routing.animation.TransitionAnimation;
import bookrecommenderdev.routing.auth.AuthContext;
import bookrecommenderdev.routing.route.Routable;
import javafx.fxml.FXML;
import javafx.scene.Parent;

import java.rmi.RemoteException;
import java.util.Map;

import static bookrecommenderdev.Constants.PAGE_SIZE;
import static java.lang.Integer.parseInt;

public class RecommendationSelectorController implements Routable {

  @FXML private SearchbarController searchbarController;
  @FXML private SearchResultsController<Libro> resultsController;
  @FXML private ErrorBannerController errorBannerController;

  private AppContext context;
  private int idLibroBase;
  private int idUtente;
  private boolean setup = false;

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
    idUtente = AuthContext.getUser().getId_utente();

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

  @FXML
  private void initialize() {
    resultsController.setItemRenderer(this::renderSelectableBook);
  }

  private void setup() {
    if (setup) return;
    setup = true;

    searchbarController.setOnSearch(req -> {
      PageFetcher<Libro> source = indicePagina -> context.server().cercaLibro(req, indicePagina);

      resultsController.setSource(source, PAGE_SIZE);
      resultsController.refreshFromStart();
    });
  }

  private Parent renderSelectableBook(Libro l) {
    int selectedId = l.getIdLibro();

    boolean invalid = selectedId == idLibroBase;

    return BookResultItemFactory.create(
        l,
        null, // no apertura pagina libro
        invalid ? null : _ -> selectBook(selectedId),
        "Seleziona",
        "mdi2c-check"
//        invalid ? "Non puoi consigliare lo stesso libro": null
    );
  }

  private void selectBook(int idLibroCons) {
    if (idLibroCons == idLibroBase) return;

    try {
      context.server().inserisciConsiglio(idUtente, idLibroBase, idLibroCons);

      // success -> go back to book page
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

  private void showError(String message, Runnable retry, Runnable back) {
    errorBannerController.show(message, retry, back);
  }
}
