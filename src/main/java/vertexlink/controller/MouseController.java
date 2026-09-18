package vertexlink.controller;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;

import vertexlink.provider.RobotProvider;

public class MouseController {
  private final Robot robot = RobotProvider.getInstance();
  private volatile boolean leftButtonHeld = false;

  public synchronized void moveRelative(int x, int y) {
    Point currentPosition = MouseInfo.getPointerInfo().getLocation();

    int newX = currentPosition.x + x;
    int newY = currentPosition.y + y;

    robot.mouseMove(newX, newY);
  }

  public synchronized void leftClick() {
    robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
    robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
  }

  public synchronized void rightClick() {
    robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
    robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
  }

  public synchronized void leftButtonDown() {
    if (!leftButtonHeld) {
      robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
      leftButtonHeld = true;
    }
  }

  public synchronized void leftButtonUp() {
    if (leftButtonHeld) {
      robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
      leftButtonHeld = false;
    }
  }

  public synchronized void releaseIfHeld() {
    leftButtonUp();
  }
}
