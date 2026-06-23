package vertexlink.controller;

import java.awt.Robot;

import vertexlink.provider.RobotProvider;

public class KeyboardController {

  private final Robot robot = RobotProvider.getInstance();

  public void executeKey(int keyCode) {
    if (this.robot == null) {
      return;
    }

    this.robot.keyPress(keyCode);
    this.robot.keyRelease(keyCode);
  }
}
