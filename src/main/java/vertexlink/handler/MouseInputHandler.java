package vertexlink.handler;

import vertexlink.controller.MouseController;

public class MouseInputHandler {
  private static final String MOUSE_MOVE_PREFIX = "MOUSE_MOVE:";
  private static final String MOUSE_LEFT_CLICK = "MOUSE_LEFT_CLICK";
  private static final String MOUSE_RIGHT_CLICK = "MOUSE_RIGHT_CLICK";
  private static final String MOUSE_LEFT_DOWN = "MOUSE_LEFT_DOWN";
  private static final String MOUSE_LEFT_UP = "MOUSE_LEFT_UP";

  private MouseController mouseController;

  public void setMouseController(MouseController mouseController) {
    this.mouseController = mouseController;
  }

  public boolean handleCommand(String data) {
    if (data == null || data.isEmpty() || mouseController == null) {
      return false;
    }

    if (data.startsWith(MOUSE_MOVE_PREFIX)) {
      handleMouseMove(data);
      return true;
    }

    if (MOUSE_LEFT_CLICK.equals(data)) {
      mouseController.leftClick();
      return true;
    }

    if (MOUSE_RIGHT_CLICK.equals(data)) {
      mouseController.rightClick();
      return true;
    }

    if (MOUSE_LEFT_DOWN.equals(data)) {
      mouseController.leftButtonDown();
      return true;
    }

    if (MOUSE_LEFT_UP.equals(data)) {
      mouseController.leftButtonUp();
      return true;
    }

    return false;
  }

  private void handleMouseMove(String data) {
    try {
      String payload = data.substring(MOUSE_MOVE_PREFIX.length());
      String[] parts = payload.split(",", 2);

      int x = Integer.parseInt(parts[0].trim());
      int y = Integer.parseInt(parts[1].trim());

      mouseController.moveRelative(x, y);
    } catch (Exception e) {
      System.err.println("[Network] Malformed mouse move payload: " + data);
    }
  }

  public void releaseIfHeld() {
    if (mouseController != null) {
      mouseController.releaseIfHeld();
    }
  }
}
