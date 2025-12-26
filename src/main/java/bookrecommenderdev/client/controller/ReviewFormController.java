package bookrecommenderdev.client.controller;

import bookrecommenderdev.client.controller.components.RatingFieldController;
import bookrecommenderdev.model.CampoValutazione;
import bookrecommenderdev.model.Libro;
import bookrecommenderdev.model.Valutazione;
import bookrecommenderdev.routing.AppContext;
import bookrecommenderdev.routing.Router;
import bookrecommenderdev.routing.auth.AuthContext;
import bookrecommenderdev.routing.route.Routable;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.rmi.RemoteException;
import java.util.Map;

import static bookrecommenderdev.model.CampoValutazione.*;

public class ReviewFormController implements Routable {

  @FXML private Label titleLabel;
  @FXML private RatingFieldController generaleController;
  @FXML private RatingFieldController stileController;
  @FXML private RatingFieldController contenutoController;
  @FXML private RatingFieldController originalitaController;
  @FXML private RatingFieldController gradevolezzaController;
  @FXML private RatingFieldController edizioneController;
  @FXML private Label feedbackLabel;

  private Map<CampoValutazione, RatingFieldController> fields;
  Libro libro;
  AppContext context;

  @Override
  public void onRoute(Map<String, String> params, AppContext context) {
    String idLibro = params.get("id");

    this.context = context;
  }

  private void fetchBook(AppContext context, String idLibro) {



  }

  @FXML
  private void initialize() {
    // get libro

    fields = Map.of(
        GENERALE, generaleController,
        STILE, stileController,
        CONTENUTO, contenutoController,
        ORIGINALITA, originalitaController,
        GRADEVOLEZZA, gradevolezzaController,
        EDIZIONE, edizioneController
    );

    fields.get(GENERALE).setScoreBoxVisible(false);

    fields.get(GENERALE).setTitle("Generale");
    fields.get(STILE).setTitle("Stile");
    fields.get(CONTENUTO).setTitle("Contenuto");
    fields.get(GRADEVOLEZZA).setTitle("Gradevolezza");
    fields.get(ORIGINALITA).setTitle("Originalità");
    fields.get(EDIZIONE).setTitle("Edizione");
  }

  @FXML
  private void conferma() {
    if (!AuthContext.isAuthenticated()) Router.go("/login"); // temp?

    for (Map.Entry<CampoValutazione, RatingFieldController> entry : fields.entrySet()) {

      CampoValutazione campo = entry.getKey();
      RatingFieldController controller = entry.getValue();

      if (campo == CampoValutazione.GENERALE) continue;

      System.out.println(
          controller.getScore()
              + " — "
              + (controller.getTextReview() == null ? "N/A" : controller.getTextReview())
      );
    }


    Valutazione v = new Valutazione();
    v.setIdLibro(libro.getIdLibro());
    v.setIdUtente(AuthContext.getUser().getId_utente());

    for (CampoValutazione criteria: CampoValutazione.values()) {

      String text = fields.get(criteria).getTextReview();
      v.setRecensione(criteria, text.isBlank() ? null : text);
      if (criteria == GENERALE) continue;

      if (fields.get(criteria).getScore() < 0) {
        setErrorFeedback("Tutti i punteggi devono essere impostati.");
        return;
      }

      v.setPunteggio(criteria, fields.get(criteria).getScore());

    }

    try {
      boolean res = context.server().inserisciValutazione(v);
      System.out.println(res ? "Valutazione aggiunta" : "DB unaffected");

      if (res) Router.go("/book/" + libro.getIdLibro());
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }


  /** @param title Titolo del libro */
  private void setTitle(String title) {
    this.titleLabel.setText("Recensisci " + title);
  }

  private void setErrorFeedback(String feedback) {
    this.feedbackLabel.setText(feedback);
  }
}
