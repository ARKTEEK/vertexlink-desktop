package vertexlink.enums;

import java.awt.event.InputEvent;;

public enum MouseAction {
  LEFT(InputEvent.BUTTON1_DOWN_MASK),
  MIDDLE(InputEvent.BUTTON2_DOWN_MASK),
  RIGHT(InputEvent.BUTTON3_DOWN_MASK);

  private final int mask;

  MouseAction(int mask) {
    this.mask = mask;
  }

  public int getMask() {
    return this.mask;
  }
}
