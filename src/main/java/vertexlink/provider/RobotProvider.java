package vertexlink.provider;

import java.awt.AWTException;
import java.awt.Robot;

public class RobotProvider {
  private static Robot instance;

  public static Robot getInstance() {
    if (instance == null) {
      try {
        instance = new Robot();
      } catch (AWTException e) {
        throw new IllegalStateException("Error has occured.", e);
      }
    }

    return instance;
  }
}
