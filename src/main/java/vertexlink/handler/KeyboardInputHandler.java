package vertexlink.handler;

import vertexlink.controller.KeyboardController;

public class KeyboardInputHandler {
  private static final String KEY_COMBO_PREFIX = "KEY_COMBO:";

  private final KeyboardController keyboardController;

  public KeyboardInputHandler(KeyboardController keyboardController) {
    if (keyboardController == null) {
      throw new IllegalArgumentException("KeyboardController cannot be null");
    }

    this.keyboardController = keyboardController;
  }

  public boolean handleCommand(String data) {
    if (data == null || data.isEmpty()) {
      return false;
    }

    if (data.startsWith(KEY_COMBO_PREFIX)) {
      handleKeyCombo(data);

      return true;
    }

    return false;
  }

  private void handleKeyCombo(String data) {
    try {
      String payload = data.substring(KEY_COMBO_PREFIX.length());
      String[] parts = payload.split(",");

      int[] keyCodes = new int[parts.length];

      for (int index = 0; index < parts.length; index++) {
        keyCodes[index] = Integer.parseInt(parts[index].trim());
      }

      keyboardController.executeCombo(keyCodes);
    } catch (Exception exception) {
      System.err.println("[Network] Malformed key combo payload: " + data);
    }
  }
}
