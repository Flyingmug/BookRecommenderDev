package bookrecommenderdev.client.controller.components;

import bookrecommenderdev.client.factory.ReviewItemFactory;
import bookrecommenderdev.model.DataAccessException;
import bookrecommenderdev.model.Valutazione;
import bookrecommenderdev.routing.AppContext;
import bookrecommenderdev.routing.Router;
import bookrecommenderdev.routing.auth.AuthContext;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.rmi.RemoteException;

public class UserReviewSectionController {

  @FXML private VBox content;
  @FXML private VBox mainArea;
  @FXML private Button deleteButton;
  @FXML private Label titleLabel;
  @FXML private Button reviewButton;

  @FXML private ErrorBannerController errorBannerController;

  private int idLibro;
  private AppContext context;
  Runnable onLayoutChange;

  public void setContext(AppContext context, int idLibro) {
    this.context = context;
    this.idLibro = idLibro;

    refresh();
  }

  @FXML
  private void onReview() {
    Router.go("/book/" + idLibro + "/review");
  }

  @FXML
  private void onDeleteReview() {
    try {
      if (context == null || !AuthContext.isAuthenticated()) return;

      // valore di ritorno gestito tramite refresh del componente
      context.server().deleteValutazione(idLibro, AuthContext.getUser().getId_utente());
      refresh();
    } catch (DataAccessException | RemoteException e) {

      // no "hideSection"
      errorBannerController.show(
          "Errore nel reperimento della valutazione (DB).",
          this::refresh,
          null
      );
    }
  }

  private void refresh() {

    if (context == null || !AuthContext.isAuthenticated()) {
      hideSection();
      return;
    }

    try {
      int userId = AuthContext.getUser().getId_utente();
      Valutazione v = context.server().getValutazione(idLibro, userId);

      if (v != null) showExistingReview(v);
      else showCreateReview();

      showSection();
      notifyLayoutChange();

    } catch (DataAccessException e) {

      hideSection();
      errorBannerController.show(
          "Errore nel reperimento della valutazione (DB).",
          this::refresh,
          null
      );
    } catch (RemoteException e) {

      hideSection();
      errorBannerController.show(
          "Errore di comunicazione con il server.",
          this::refresh,
          null
      );
    }
  }

  private void showExistingReview(Valutazione v) {

    mainArea.getChildren().setAll(
        ReviewItemFactory.createReviewNode(v)
    );

    deleteButton.setVisible(true);
    deleteButton.setManaged(true);
  }

  private void showCreateReview() {
    showIntro();
    deleteButton.setVisible(false);
    deleteButton.setManaged(false);

    notifyLayoutChange();
  }

  private void showSection() {
    content.setVisible(true);
    content.setManaged(true);
  }

  private void showIntro() {
    mainArea.getChildren().clear();
    mainArea.getChildren().addAll(
        titleLabel,
        reviewButton
    );
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

  // Metodi Esposti

  /** Imposta un <i>callback</i> eseguito in seguito al cambiamento di layout.
   * @param r callback */
  public void setOnLayoutChange(Runnable r) {
    this.onLayoutChange = r;
  }


}
