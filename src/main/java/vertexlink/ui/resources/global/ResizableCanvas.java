package vertexlink.ui.resources.global;

import javafx.scene.canvas.Canvas;

public class ResizableCanvas extends Canvas {
  @Override
  public boolean isResizable() {
    return true;
  }

  @Override
  public double minWidth(double height) {
    return 1;
  }

  @Override
  public double minHeight(double width) {
    return 1;
  }

  @Override
  public double prefWidth(double height) {
    return getWidth();
  }

  @Override
  public double prefHeight(double width) {
    return getHeight();
  }

  @Override
  public void resize(double width, double height) {
    setWidth(width);
    setHeight(height);
  }
}
