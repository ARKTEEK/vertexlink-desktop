package vertexlink.handler;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import vertexlink.controller.ClipboardController;
import vertexlink.controller.ClipboardHistory;
import vertexlink.controller.ClipboardMonitor;
import vertexlink.network.server.ClientHandler;

public class ClipboardHandler {
  private static final String CLIPBOARD_GET = "CLIPBOARD_GET";
  private static final String CLIPBOARD_SET_PREFIX = "CLIPBOARD_SET:";
  private static final String CLIPBOARD_APPEND_PREFIX = "CLIPBOARD_APPEND:";
  private static final String CLIPBOARD_STATE_PREFIX = "CLIPBOARD_STATE:";

  private static final String CLIPBOARD_HISTORY_GET = "CLIPBOARD_HISTORY_GET";
  private static final String CLIPBOARD_HISTORY_CLEAR = "CLIPBOARD_HISTORY_CLEAR";
  private static final String CLIPBOARD_ENTRY_PREFIX = "CLIPBOARD_ENTRY:";
  private static final String CLIPBOARD_HISTORY_END = "CLIPBOARD_HISTORY_END";

  private final ClipboardController clipboardController;
  private final ClipboardHistory clipboardHistory;
  private final ClipboardMonitor clipboardMonitor;

  public ClipboardHandler(ClipboardController clipboardController) {
    this.clipboardController = clipboardController;
    this.clipboardHistory = new ClipboardHistory();
    this.clipboardMonitor = new ClipboardMonitor(clipboardController, this.clipboardHistory);
  }

  public void start() {
    this.clipboardMonitor.start();
  }

  public void stop() {
    this.clipboardMonitor.stop();
  }

  public boolean handleCommand(String data, ClientHandler client) {
    if (data == null || data.isEmpty()) {
      return false;
    }

    if (CLIPBOARD_GET.equals(data)) {
      sendState(client);

      return true;
    }

    if (CLIPBOARD_HISTORY_GET.equals(data)) {
      this.clipboardMonitor.start();
      sendHistory(client);

      return true;
    }

    if (CLIPBOARD_HISTORY_CLEAR.equals(data)) {
      this.clipboardHistory.clear();

      return true;
    }

    if (data.startsWith(CLIPBOARD_SET_PREFIX)) {
      handleSet(data);

      return true;
    }

    if (data.startsWith(CLIPBOARD_APPEND_PREFIX)) {
      handleAppend(data);

      return true;
    }

    return false;
  }

  private void sendState(ClientHandler client) {
    if (client == null) {
      return;
    }

    String text = clipboardController.getText();

    if (text == null) {
      return;
    }

    client.send(CLIPBOARD_STATE_PREFIX + encode(text));
  }

  private void sendHistory(ClientHandler client) {
    if (client == null) {
      return;
    }

    for (ClipboardHistory.Entry entry : this.clipboardHistory.snapshot()) {
      client.send(CLIPBOARD_ENTRY_PREFIX + entry.getId() + "," + entry.getTimestamp() + "," + encode(entry.getText()));
    }

    client.send(CLIPBOARD_HISTORY_END);
  }

  private void handleSet(String data) {
    String text = decode(data.substring(CLIPBOARD_SET_PREFIX.length()));

    if (text != null) {
      clipboardController.setText(text);
    }
  }

  private void handleAppend(String data) {
    String text = decode(data.substring(CLIPBOARD_APPEND_PREFIX.length()));

    if (text != null) {
      clipboardController.appendText(text);
    }
  }

  private String encode(String text) {
    return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
  }

  private String decode(String payload) {
    try {
      return new String(Base64.getDecoder().decode(payload), StandardCharsets.UTF_8);
    } catch (IllegalArgumentException exception) {
      System.err.println("[Clipboard] Malformed payload: " + payload);

      return null;
    }
  }
}
