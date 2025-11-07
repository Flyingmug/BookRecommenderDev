package bookrecommenderdev.animations;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class RectShape {

  double x, y, w, h;
  Color color;
  int phase;

  public RectShape(double x, double y, double w, double h, Color color) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.color = color;
    phase = 0;
  }

  public abstract void draw(GraphicsContext gc);

//  public void draw(GraphicsContext gc) {
//    gc.setFill(color);
//    gc.fillRect(x, y, w, h);
//  }
}
