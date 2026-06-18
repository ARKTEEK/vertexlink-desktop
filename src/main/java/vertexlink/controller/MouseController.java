package vertexlink.controller;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;

import vertexlink.enums.MouseAction;

public class MouseController {
  private final Robot robot;

  public MouseController() throws AWTException {
    this.robot = new Robot();
  }

  public void moveRelative(int x, int y) {
    if (this.robot == null) {
      return;
    }

    Point currentPosition = MouseInfo.getPointerInfo().getLocation();

    int newX = currentPosition.x + x;
    int newY = currentPosition.y + y;
    this.robot.mouseMove(newX, newY);
  }

  public void executeClick(MouseAction action) {
    if (this.robot == null || action == null) {
      return;
    }

    int mask = action.getMask();
    this.robot.mousePress(mask);
    this.robot.mouseRelease(mask);
  }

  public void executeScroll(int amount) {
    if (this.robot == null) {
      return;
    }

    if (amount != 0) {
      this.robot.mouseWheel(amount);
    }
  }

}
