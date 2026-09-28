package vertexlink.controller;

import vertexlink.controller.audio.AudioController;

public final class PeripheralControllers {
  public static MouseController createMouseController() {
    try {
      return new MouseController();
    } catch (Exception e) {
      System.err.println("[Dashboard] Mouse control unavailable: " + e.getMessage());

      return null;
    }
  }

  public static KeyboardController createKeyboardController() {
    try {
      return new KeyboardController();
    } catch (Exception e) {
      System.err.println("[Dashboard] Keyboard control unavailable: " + e.getMessage());

      return null;
    }
  }

  public static AudioController creaAudioController() {
    try {
      return new AudioController();
    } catch (Exception e) {
      System.err.println("[Dashboard] Audio control unavailable: " + e.getMessage());

      return null;
    }
  }
}
