package bookrecommenderdev.client.controller.components;

import bookrecommenderdev.client.controller.components.controls.ConfirmActionDialogController;
import bookrecommenderdev.client.controller.errors.components.ErrorBannerController;
import bookrecommenderdev.client.factory.ReviewItemFactory;
import bookrecommenderdev.model.exceptions.DataAccessException;
import bookrecommenderdev.model.exceptions.NotFoundException;
import bookrecommenderdev.model.Valutazione;
import bookrecommenderdev.routing.AppContext;
import bookrecommenderdev.routing.Router;
import bookrecommenderdev.routing.auth.AuthContext;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

import java.rmi.RemoteException;

public class UserReviewSectionController {

  @FXML private VBox content;
  @FXML private VBox mainArea;
  @FXML private Parent deleteControl;
  @FXML private ConfirmActionDialogController deleteControlController;
  @FXML private VBox createReviewSection;

  @FXML private ErrorBannerController errorBannerController;

  private int idLibro;
  private AppContext context;
  Runnable onLayoutChange;

  /**Imposta il contesto e l'id del libro riferito.
   * @param idLibro id libro*/
  public void setContext(AppContext context, int idLibro) {
    this.context = context;
    this.idLibro = idLibro;

    refresh();
  }

  @FXML
  private void initialize() {

    // Configurazione del dialog di conferma
    if (deleteControlController != null) {
      deleteControlController.setOnConfirm(this::deleteReviewConfirmed);
      deleteControlController.setOnCancel(() -> {});
    }
  }

  /**Reindirizza al form di creazione di valutazione {@code /book/:id/review}*/
  @FXML
  private void onReview() {
    Router.go("/book/" + idLibro + "/review");
  }

  /**<p>Gestisce l'aggiornamento dello stato della pagina.
   * <p>Verifica lo stato di autenticazione, per poi eventualmente verificare
   * la presenza di una valutazione dell'utente per il libro corrente.
   * <p>Questa viene mostrata se esiste, altrimenti viene caricato il link al form di recensione.
   * <p>Notifica un cambiamento di layout.
   * <p>In caso di errore viene mostrato un banner con l'opzione di refresh della sezione. */
  private void refresh() {

    if (context == null || !AuthContext.isAuthenticated()) {
      hideSection();
      return;
    }

    try {
      int userId = AuthContext.getUser().idUtente();
      Valutazione v = context.server().getValutazione(idLibro, userId);

      if (v != null) showExistingReview(v);
      else showCreateReview();

      showSection();
      notifyLayoutChange();

    } catch (DataAccessException e) {
      hideSection();
      showError(
          "Errore nel reperimento della valutazione (DB).",
          this::refresh,
          null
      );

    } catch (RemoteException e) {
      hideSection();
      showError(
          "Errore di comunicazione con il server.",
          this::refresh,
          null
      );

    }
  }

  /**
   * todo doc
   */
  private void deleteReviewConfirmed() {
    if (deleteControlController != null) deleteControlController.setDisabled(false);

    try {
      if (context == null || !AuthContext.isAuthenticated()) return;

      context.server().deleteValutazione(idLibro, AuthContext.getUser().idUtente());
      refresh();

    } catch (NotFoundException e) {
      if (deleteControlController != null) deleteControlController.setDisabled(false);
      showError(
          "Errore nell'identificare la valutazione.",
          this::refresh, null
      );

    } catch (DataAccessException | RemoteException e) {
      if (deleteControlController != null) deleteControlController.setDisabled(false);
      showError(
          "Errore nel reperimento della valutazione (DB).",
          this::refresh,null
      );

    }
  }

  private void showExistingReview(Valutazione v) {

    mainArea.getChildren().setAll(
        ReviewItemFactory.create(v)
    );
    deleteControl.setVisible(true);
    deleteControl.setManaged(true);
  }

  private void showCreateReview() {
    showIntro();
    deleteControl.setVisible(false);
    deleteControl.setManaged(false);

    notifyLayoutChange();
  }

  private void showIntro() {
    mainArea.getChildren().clear();
    mainArea.getChildren().add(createReviewSection);
  }

  private void showSection() {
    content.setVisible(true);
    content.setManaged(true);
  }

  private void hideSection() {
    content.setVisible(false);
    content.setManaged(false);
  }

  /** Notifica un cambiamento di layout. */
  private void notifyLayoutChange() {
    if (onLayoutChange != null) {
      onLayoutChange.run();
    }
  }

  /** Mostra il banner d'errore.
   * @param message Messaggio
   * @param retry callback
   * @param back callback*/
  private void showError(String message, Runnable retry, Runnable back) {
    errorBannerController.show(message, retry, back);
  }

  // Metodi Esposti

  /** Imposta un <i>callback</i> eseguito in seguito al cambiamento di layout.
   * @param r callback */
  public void setOnLayoutChange(Runnable r) {
    this.onLayoutChange = r;
  }
}
