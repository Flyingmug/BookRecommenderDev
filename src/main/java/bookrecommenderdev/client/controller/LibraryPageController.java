package bookrecommenderdev.client.controller;

import bookrecommenderdev.client.controller.components.SearchResultsController;
import bookrecommenderdev.client.controller.components.controls.ConfirmActionDialogController;
import bookrecommenderdev.client.controller.errors.components.ErrorBannerController;
import bookrecommenderdev.client.factory.BookResultItemFactory;
import bookrecommenderdev.model.*;
import bookrecommenderdev.model.exceptions.DataAccessException;
import bookrecommenderdev.model.exceptions.NotFoundException;
import bookrecommenderdev.routing.Router;
import bookrecommenderdev.routing.animation.TransitionAnimation;
import bookrecommenderdev.routing.auth.AuthContext;
import bookrecommenderdev.model.data.PageFetcher;
import bookrecommenderdev.routing.AppContext;
import bookrecommenderdev.routing.route.Routable;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;

import java.rmi.RemoteException;
import java.util.Map;

import static bookrecommenderdev.Constants.PAGE_SIZE;

public class LibraryPageController implements Routable {

  @FXML private ConfirmActionDialogController deleteControlController;

  @FXML private Label libraryTitle;
  @FXML private Parent resultsSection;
  @FXML private SearchResultsController<Libro> resultsController;

  @FXML private ErrorBannerController errorBannerController;

  private AppContext context;
  private int idLibreria;

  /**
   * todo doc
   */
  @Override
  public void onRoute(Map<String, String> params, AppContext context, Object state) {
    this.context = context;

    Integer parsed = parseInt(params.get("id"));
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

    int idUtente = AuthContext.getUser().getId_utente();

    resolve(idUtente);
  }

  /**
   * todo doc
   */
  @FXML
  private void initialize() {
    if (deleteControlController != null) {
      deleteControlController.setOnConfirm(this::deleteLibraryConfirmed);
      deleteControlController.setOnCancel(() -> {});
    }
  }

  /**
   * todo doc
   */
  private void deleteLibraryConfirmed() {
    if (context == null) return;

    if (!AuthContext.isAuthenticated()) {
      // Controllo di sicurezza secondario
      Router.go("/login", TransitionAnimation.LEFT_SLIDE);
      return;
    }

    int idUtente = AuthContext.getUser().getId_utente();

//    if (deleteButton != null) deleteButton.setDisable(true);
    if (deleteControlController != null) deleteControlController.setDisabled(true);

    try {
      context.server().deleteLibreria(idUtente, idLibreria);
      Router.go("/libraries", TransitionAnimation.RIGHT_SLIDE);

    } catch (NotFoundException e) {
      Router.go("/not-found", TransitionAnimation.LEFT_SLIDE);

    } catch (DataAccessException e) {
      if (deleteControlController != null) deleteControlController.setDisabled(false);
      showError(e.getMessage(), null, null);

    } catch (RemoteException e) {
      if (deleteControlController != null) deleteControlController.setDisabled(false);
      showError("Errore di comunicazione con il server.", null, null);

    }
  }

  /**
   * todo doc
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
      showError("Errore nel reperimento della libreria.", null, null);

    } catch (RemoteException e) {
      showError("Errore nella comunicazione con il server.", null, null);

    }
  }

  private Parent renderBookItem(Libro l) {
    return BookResultItemFactory.create(
        l, id -> Router.go("/book/" + id, TransitionAnimation.LEFT_SLIDE)
    );
  }


  /**
   * todo doc
   */
  private static Integer parseInt(String s) {
    if (s == null) return null;
    try { return Integer.parseInt(s); }
    catch (NumberFormatException e) { return null; }
  }

  /**
   * todo doc
   */
  private void showError(String message, Runnable retry, Runnable back) {
    errorBannerController.show(message, retry, back);
  }
}