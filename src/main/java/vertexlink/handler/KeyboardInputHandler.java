package vertexlink.handler;

import vertexlink.controller.KeyboardController;

public class KeyboardInputHandler {
  private static final String KEY_COMBO_PREFIX = "KEY_COMBO:";

  private KeyboardController keyboardController;

  public void setKeyboardController(KeyboardController keyboardController) {
    this.keyboardController = keyboardController;
  }

  public boolean handleCommand(String data) {
    if (data == null || data.isEmpty() || keyboardController == null) {
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

      for (int i = 0; i < parts.length; i++) {
        keyCodes[i] = Integer.parseInt(parts[i].trim());
      }

      keyboardController.executeCombo(keyCodes);
    } catch (Exception e) {
      System.err.println("[Network] Malformed key combo payload: " + data);
    }
  }
}
