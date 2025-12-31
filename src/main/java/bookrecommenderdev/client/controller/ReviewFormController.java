package bookrecommenderdev.client.controller;

import bookrecommenderdev.client.controller.errors.components.ErrorBannerController;
import bookrecommenderdev.client.controller.components.RatingFieldController;
import bookrecommenderdev.model.base.Libro;
import bookrecommenderdev.model.base.Valutazione;
import bookrecommenderdev.model.exceptions.DataAccessException;
import bookrecommenderdev.model.exceptions.NotFoundException;
import bookrecommenderdev.client.routing.AppContext;
import bookrecommenderdev.client.routing.Router;
import bookrecommenderdev.client.auth.AuthContext;
import bookrecommenderdev.client.routing.route.Routable;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.rmi.RemoteException;
import java.util.Map;

public class ReviewFormController implements Routable {

  @FXML private Label titleLabel;
  @FXML private RatingFieldController generaleController;
  @FXML private RatingFieldController stileController;
  @FXML private RatingFieldController contenutoController;
  @FXML private RatingFieldController originalitaController;
  @FXML private RatingFieldController gradevolezzaController;
  @FXML private RatingFieldController edizioneController;
  @FXML private Label feedbackLabel;

  @FXML private ErrorBannerController errorBannerController;

  Libro libro;
  AppContext context;

  @Override
  public void onRoute(Map<String, String> params, AppContext context, Object state) {
    this.context = context;

    String idLibro = params.get("id");

    if (!AuthContext.isAuthenticated()) {
      Platform.runLater(() -> Router.go("/login"));
      return;
    }

    fetchBook(idLibro);
  }

  /**
   * Ottiene il libro corrispondente all'id dato e imposta il titolo di pagina al titolo del libro.
   * @param idLibro Id libro
   */
  private void fetchBook(String idLibro) {
    try {
      libro = context.server().getLibro(Integer.parseInt(idLibro));
      setTitle(libro.getTitolo());
    } catch (NotFoundException e) {

      Platform.runLater(() -> Router.go("/not-found"));
    } catch (DataAccessException e) {

      showError("Errore database nel reperimento del titolo.");
    } catch (RemoteException e) {

      showError("Server non raggiungibile.");
    }

  }

  @FXML
  private void initialize() {
    generaleController.setScoreBoxVisible(false);

    generaleController.setTitle("Generale");
    stileController.setTitle("Stile");
    contenutoController.setTitle("Contenuto");
    gradevolezzaController.setTitle("Gradevolezza");
    originalitaController.setTitle("Originalità");
    edizioneController.setTitle("Edizione");
  }

  @FXML
  private void conferma() {
    errorBannerController.hide();

    Valutazione v = buildOrShowError();
    if (v == null) return;

    try {
      context.server().inserisciValutazione(v);
      javafx.application.Platform.runLater(() ->
          Router.go("/book/" + libro.getIdLibro())
      );

    } catch (DataAccessException e) {
      showError("Errore database. Riprova.");

    } catch (java.rmi.RemoteException e) {
      showError("Server non raggiungibile. Riprova.");

    }

  }

  /**
   * todo doc
   * */
  private Valutazione buildOrShowError() {
    Integer stile = stileController.getScore();
    Integer contenuto = contenutoController.getScore();
    Integer gradevolezza = gradevolezzaController.getScore();
    Integer originalita = originalitaController.getScore();
    Integer edizione = edizioneController.getScore();

    if (stile == null || contenuto == null || gradevolezza == null || originalita == null || edizione == null) {
      setErrorFeedback("Seleziona un voto (1–5) per tutti i campi.");
      return null;
    }

    // Errore di fallback
    if (!AuthContext.isAuthenticated()) {
      showError("Devi effettuare l'accesso per inviare una recensione.");
      return null;
    }

    int userId = AuthContext.getUser().idUtente();

    return new Valutazione(
        libro.getIdLibro(),
        userId,
        stile, contenuto, gradevolezza, originalita, edizione,
        stileController.getTextReview(),
        contenutoController.getTextReview(),
        gradevolezzaController.getTextReview(),
        originalitaController.getTextReview(),
        edizioneController.getTextReview(),
        generaleController.getTextReview()
    );
  }

  /** @param title Titolo del libro */
  private void setTitle(String title) {
    this.titleLabel.setText("Recensisci " + title);
  }

  private void showError(String s) {
    errorBannerController.show(s, null, null);
  }

  private void setErrorFeedback(String feedback) {
    this.feedbackLabel.setText(feedback);
  }
}
