package bookrecommenderdev.client.controller;

import bookrecommenderdev.animations.Book;
import bookrecommenderdev.animations.BookRectangle;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.LinkedList;
import java.util.List;

public class LoadingController {


  @FXML public Canvas loadingCanvas;

  List<Book> elements;

  @FXML
  public void initialize() {

    elements = new LinkedList<>();
    elements.add(new Book(
        new BookRectangle(350, 100, 250, 120, 0, 0, 0, 3, Color.DARKBLUE),
        100, 0, 0
    ));
//    elements.add(new Book(
//        new BookRectangle(350, 200, 250, 80, 15, 10, 4, 3, Color.CYAN),
//        200, 10, 0
//    ));
//    elements.add(new Book(
//        new BookRectangle(350, 300, 250, 80, 15, 10, 4, 3, Color.BLUEVIOLET),
//        150, 10, 0
//    ));
//    elements.add(new Book(
//        new BookRectangle(350, 400, 250, 80, 15, 10, 4, 3, Color.AQUAMARINE),
//        70, 10, 0
//    ));

    elements.getFirst().addRotationKeyframe(0, 90);
    elements.getFirst().addRotationKeyframe(1500, 0);
    elements.getFirst().addRotationKeyframe(3000, 0);

    GraphicsContext gc = loadingCanvas.getGraphicsContext2D();

    AnimationTimer timer = new AnimationTimer() {
      private long start = -1;

      @Override
      public void handle(long now) {
        if (start < 0) start = now;
        double elapsed = (now - start) / 1_000_000.0;

        // Clear and draw
        gc.setFill(Color.DIMGRAY);
        gc.fillRect(0, 0, loadingCanvas.getWidth(), loadingCanvas.getHeight());

        // draw
        for(Book b: elements) {
          b.draw(gc, elapsed);
        }

      }
    };
    timer.start();
  }
}
