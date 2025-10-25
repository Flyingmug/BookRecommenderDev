package bookrecommenderdev.client.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class LoadingController {


  @FXML public Canvas loadingCanvas;
  public GraphicsContext graphic;

  private double rectX = 50;
  private double rectY = 150;
  private double rectW = 50;
  private double rectH = 50;

  private double dx = 2; // speed along x
  private double dy = 1; // speed along y
  private double dW = 0.5; // change in width per frame
  private double dH = 0.3; // change in height per frame


  public void presetItems() {

    double cw = loadingCanvas.getWidth();
    double ch = loadingCanvas.getHeight();

    graphic.setFill(Color.BLACK);
    graphic.fillRect(0, 0, cw, ch);

    Timeline timeline = new Timeline(new KeyFrame(Duration.millis(16), e -> {
      // Update position and size
      rectX += dx;
      rectY += dy;
      rectW += dW;
      rectH += dH;

      // Bounce or reverse direction
      if (rectX < 0 || rectX + rectW > loadingCanvas.getWidth()) dx = -dx;
      if (rectY < 0 || rectY + rectH > loadingCanvas.getHeight()) dy = -dy;
      if (rectW < 30 || rectW > 100) dW = -dW;
      if (rectH < 30 || rectH > 100) dH = -dH;

      // Clear and redraw
      graphic.setFill(Color.LIGHTGRAY);
      graphic.fillRect(0, 0, loadingCanvas.getWidth(), loadingCanvas.getHeight());

      graphic.setFill(Color.CORNFLOWERBLUE);
      graphic.fillRect(rectX, rectY, rectW, rectH);
    }));

    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();

  }


  @FXML
  public void initialize() {

    graphic = loadingCanvas.getGraphicsContext2D();
    presetItems();
  }
}
