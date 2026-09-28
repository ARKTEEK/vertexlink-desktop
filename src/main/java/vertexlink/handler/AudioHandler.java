package vertexlink.handler;

import vertexlink.controller.audio.AudioController;
import vertexlink.network.server.ClientHandler;

public class AudioHandler {
  private static final String VOLUME_GET = "VOLUME_GET";
  private static final String VOLUME_SET_PREFIX = "VOLUME_SET:";
  private static final String VOLUME_MUTE_PREFIX = "VOLUME_MUTE:";
  private static final String VOLUME_STATE_PREFIX = "VOLUME_STATE:";

  private final AudioController audioController;

  public AudioHandler(AudioController audioController) {
    this.audioController = audioController;
  }

  public boolean handleCommand(String data, ClientHandler client) {
    if (data == null || data.isEmpty() || audioController == null) {
      return false;
    }

    if (VOLUME_GET.equals(data)) {
      sendState(client);
      return true;
    }

    if (data.startsWith(VOLUME_SET_PREFIX)) {
      handleVolumeSet(data);
      return true;
    }

    if (data.startsWith(VOLUME_MUTE_PREFIX)) {
      handleVolumeMute(data);
      return true;
    }

    return false;
  }

  private void sendState(ClientHandler client) {
    if (client == null) {
      return;
    }

    int volumePercent = audioController.getVolumePercent();

    if (volumePercent < 0) {
      return;
    }

    int mutedFlag = audioController.isMuted() ? 1 : 0;

    client.send(VOLUME_STATE_PREFIX + volumePercent + "," + mutedFlag);
  }

  private void handleVolumeSet(String data) {
    try {
      String payload = data.substring(VOLUME_SET_PREFIX.length());
      int volumePercent = Integer.parseInt(payload.trim());

      audioController.setVolumePercent(volumePercent);
    } catch (NumberFormatException exception) {
      System.err.println("[Audio] Malformed volume payload: " + data);
    }
  }

  private void handleVolumeMute(String data) {
    String payload = data.substring(VOLUME_MUTE_PREFIX.length()).trim();

    if ("1".equals(payload)) {
      audioController.mute();
    } else if ("0".equals(payload)) {
      audioController.unmute();
    } else {
      System.err.println("[Audio] Malformed mute payload: " + data);
    }
  }
}
