package bookrecommenderdev.routing;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class Animations {

  /**
   * Gestisce una transizione a scomparsa tra due pagine riferite.
   * @param currentNode Elemento presente
   * @param newNode Nuovo elemento
   * @param duration Durata del'animazione
   */
  public static void fadeTransition(StackPane currentNode, Node newNode, Duration duration) {
    Node oldNode = currentNode.getChildren().isEmpty()
        ? null
        : currentNode.getChildren().getFirst();

    newNode.setOpacity(0);
    currentNode.getChildren().add(newNode);

    // Se il nodo precedente è nullo, esegue solo la transizione di fade-in
    if (oldNode == null) {
      FadeTransition fadeIn = new FadeTransition(duration, newNode);
      fadeIn.setFromValue(0);
      fadeIn.setToValue(1);
      fadeIn.play();
      return;
    }

    FadeTransition fadeOut = new FadeTransition(duration, oldNode);
    fadeOut.setFromValue(1);
    fadeOut.setToValue(0);

    FadeTransition fadeIn = new FadeTransition(duration, newNode);
    fadeIn.setFromValue(0);
    fadeIn.setToValue(1);

    fadeOut.setOnFinished(e -> {
      currentNode.getChildren().remove(oldNode);
      fadeIn.play();
    });

    fadeOut.play();
  }
}
