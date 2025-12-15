package bookrecommenderdev.routing;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class Animations {

  /**
   * Gestisce una transizione 'a scomparsa' tra due pagine riferite.
   * I parametri richiedono l'elemento contenitore e il nuovo nodo.
   * @param rootNode Elemento radice contenente il nodo corrente
   * @param newNode Nuovo elemento
   * @param duration Durata del'animazione
   */
  public static void fadeTransition(StackPane rootNode, Node newNode, Duration duration) {
    Node oldNode = rootNode.getChildren().isEmpty()
        ? null
        : rootNode.getChildren().getFirst();

    newNode.setOpacity(0);
    rootNode.getChildren().add(newNode);

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
      rootNode.getChildren().remove(oldNode);
      fadeIn.play();
    });

    fadeOut.play();
  }

  public static void slideTransition(StackPane rootNode, Node newNode, Direction direction, Duration duration) {
    Node oldNode = rootNode.getChildren().isEmpty()
        ? null
        : rootNode.getChildren().getFirst();

    double width  = rootNode.getWidth();
    double height = rootNode.getHeight();

    // Fallback if layout not ready yet
    if (width <= 0 || height <= 0) {
      rootNode.applyCss();
      rootNode.layout();
      width  = rootNode.getWidth();
      height = rootNode.getHeight();
    }

    double newStartX = 0, newStartY = 0;
    double oldEndX   = 0, oldEndY   = 0;

    switch (direction) {
      case LEFT -> {
        newStartX = width;
        oldEndX   = -width;
      }
      case RIGHT -> {
        newStartX = -width;
        oldEndX   = width;
      }
      case TOP -> {
        newStartY = height;
        oldEndY   = -height;
      }
      case BOTTOM -> {
        newStartY = -height;
        oldEndY   = height;
      }
    }

    newNode.setTranslateX(newStartX);
    newNode.setTranslateY(newStartY);
    newNode.setMouseTransparent(true);

    rootNode.getChildren().add(newNode);

    TranslateTransition in = new TranslateTransition(duration, newNode);
    in.setToX(0);
    in.setToY(0);

    if (oldNode == null) {
      in.setOnFinished(_ -> newNode.setMouseTransparent(false));
      in.play();
      return;
    }

    TranslateTransition out = new TranslateTransition(duration, oldNode);
    out.setToX(oldEndX);
    out.setToY(oldEndY);

    out.setOnFinished(_ -> {
      rootNode.getChildren().remove(oldNode);
      newNode.setMouseTransparent(false);
    });

    in.play();
    out.play();
  }

}
