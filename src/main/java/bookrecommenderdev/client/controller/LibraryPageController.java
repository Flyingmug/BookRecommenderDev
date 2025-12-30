package bookrecommenderdev.client.controller;

import bookrecommenderdev.client.controller.components.SearchResultsController;
import bookrecommenderdev.client.controller.components.controls.ConfirmDialogController;
import bookrecommenderdev.client.controller.errors.components.ErrorBannerController;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.rmi.RemoteException;
import java.util.Map;

public class LibraryPageController implements Routable {

  @FXML private Button deleteButton;
  @FXML private Parent deleteConfirm;
  @FXML private ConfirmDialogController deleteConfirmController;
  @FXML private Label libraryTitle;
  @FXML private Parent resultsSection;
  @FXML private SearchResultsController resultsSectionController;

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

    fetchTitle(idUtente);
  }

  /**
   * todo doc
   */
  @FXML
  private void onDeleteLibrary() {
    showDeleteConfirm();
  }

  /**
   * todo doc
   */
  @FXML
  private void initialize() {
    if (deleteConfirmController != null) {
      deleteConfirmController.setOnConfirm(this::deleteLibraryConfirmed);
      deleteConfirmController.setOnCancel(this::hideDeleteConfirm);
    }
  }

  /**
   * todo doc
   */
  private void showDeleteConfirm() {
    deleteButton.setVisible(false);
    deleteButton.setManaged(false);

    deleteConfirm.setVisible(true);
    deleteConfirm.setManaged(true);

    deleteConfirmController.requestInitialFocus();
  }

  /**
   * todo doc
   */
  private void hideDeleteConfirm() {
    deleteButton.setVisible(true);
    deleteButton.setManaged(true);

    deleteConfirm.setVisible(false);
    deleteConfirm.setManaged(false);
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

    if (deleteButton != null) deleteButton.setDisable(true);

    try {
      context.server().deleteLibreria(idUtente, idLibreria);
      Router.go("/libraries", TransitionAnimation.RIGHT_SLIDE);

    } catch (NotFoundException e) {
      Router.go("/not-found", TransitionAnimation.LEFT_SLIDE);

    } catch (DataAccessException e) {
      if (deleteButton != null) deleteButton.setDisable(false);
      showError(e.getMessage(), null, null);

    } catch (RemoteException e) {
      if (deleteButton != null) deleteButton.setDisable(false);
      showError("Errore di comunicazione con il server.", null, null);

    }
  }

  /**
   * todo doc
   */
  private void fetchTitle(int idUtente) {
    try {
      Libreria lib = context.server().getLibreriaById(idUtente, idLibreria);
      libraryTitle.setText(lib.getNome());

      PageFetcher<Libro> source = page ->
          context.server().searchInLibreria(idUtente, idLibreria, page);

      resultsSectionController.setSource(source);

    } catch (NotFoundException e) {
      Platform.runLater(() -> Router.go("/not-found", TransitionAnimation.LEFT_SLIDE));

    } catch (DataAccessException e) {
      showError("Errore nel reperimento della libreria.", null, null);

    } catch (RemoteException e) {
      showError("Errore nella comunicazione con il server.", null, null);

    }
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