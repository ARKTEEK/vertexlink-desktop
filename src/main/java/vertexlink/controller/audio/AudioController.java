package vertexlink.controller.audio;

import java.io.IOException;

public class AudioController {
  private static final int MIN_VOLUME_PERCENT = 0;
  private static final int MAX_VOLUME_PERCENT = 100;
  private static final int UNAVAILABLE = -1;

  private boolean isBackendReady = false;

  public AudioController() {
    try {
      NativeWindowsAudio.load();

      this.isBackendReady = true;
    } catch (IOException exception) {
      System.err.println("[AudioController] Failed to start audio backend: " + exception.getMessage());
    }
  }

  public void volumeUp(float step) {
    int currentVolume = getVolumePercent();

    if (currentVolume == UNAVAILABLE) {
      return;
    }

    setVolumePercent(currentVolume + Math.round(step));
  }

  public void volumeDown(float step) {
    int currentVolume = getVolumePercent();

    if (currentVolume == UNAVAILABLE) {
      return;
    }

    setVolumePercent(currentVolume - Math.round(step));
  }

  public void mute() {
    setMuteState(true);
  }

  public void unmute() {
    setMuteState(false);
  }

  private void setMuteState(boolean isMuted) {
    if (!this.isBackendReady) {
      return;
    }

    try {
      NativeWindowsAudio.setMuted(isMuted);
    } catch (IOException exception) {
      System.err.println("[AudioController] Failed to set mute state: " + exception.getMessage());
    }
  }

  public float getCurrentVolume() {
    return Math.max(MIN_VOLUME_PERCENT, getVolumePercent());
  }

  public void setVolumePercent(int percent) {
    if (!this.isBackendReady) {
      return;
    }

    int clampedPercent = Math.max(MIN_VOLUME_PERCENT, Math.min(MAX_VOLUME_PERCENT, percent));

    try {
      NativeWindowsAudio.setVolumePercent(clampedPercent);
    } catch (IOException exception) {
      System.err.println("[AudioController] Failed to set volume: " + exception.getMessage());
    }
  }

  public int getVolumePercent() {
    if (!this.isBackendReady) {
      return UNAVAILABLE;
    }

    try {
      int volumePercent = NativeWindowsAudio.getVolumePercent();

      return Math.max(MIN_VOLUME_PERCENT, Math.min(MAX_VOLUME_PERCENT, volumePercent));
    } catch (IOException exception) {
      System.err.println("[AudioController] Failed to get volume percent: " + exception.getMessage());

      return UNAVAILABLE;
    }
  }

  public boolean isMuted() {
    if (!this.isBackendReady) {
      return false;
    }

    try {
      return NativeWindowsAudio.isMuted();
    } catch (IOException exception) {
      System.err.println("[AudioController] Failed to get mute state: " + exception.getMessage());

      return false;
    }
  }
}
