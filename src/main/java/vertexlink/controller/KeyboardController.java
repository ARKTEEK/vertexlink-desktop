package vertexlink.controller;

import java.awt.Robot;

import vertexlink.provider.RobotProvider;

public class KeyboardController {
  private final Robot robot = RobotProvider.getInstance();

  public void executeKey(int keyCode) {
    executeCombo(new int[] { keyCode });
  }

  public void executeCombo(int[] keyCodes) {
    if (this.robot == null || keyCodes == null || keyCodes.length == 0) {
      return;
    }

    int pressedCount = 0;

    try {
      for (int keyCode : keyCodes) {
        this.robot.keyPress(keyCode);

        pressedCount++;
      }
    } catch (Exception e) {
      System.err.println("[KeyboardController] Failed to press combo: " + e.getMessage());
    } finally {
      for (int i = pressedCount - 1; i >= 0; i--) {
        try {
          this.robot.keyRelease(keyCodes[i]);
        } catch (Exception e) {
          System.err.println("[KeyboardController] Failed to release key: " + e.getMessage());
        }
      }
    }
  }
}
