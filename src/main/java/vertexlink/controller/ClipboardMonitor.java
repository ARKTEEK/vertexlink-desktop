package vertexlink.controller;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClipboardMonitor {
  private static final long POLL_INTERVAL_MS = 500;

  private final ClipboardController clipboardController;
  private final ClipboardHistory history;

  private ScheduledExecutorService executor;
  private String lastSeenText;

  public ClipboardMonitor(ClipboardController clipboardController, ClipboardHistory history) {
    this.clipboardController = clipboardController;
    this.history = history;
  }

  public synchronized void start() {
    if (this.executor != null) {
      return;
    }

    this.executor = Executors.newSingleThreadScheduledExecutor(runnable -> {
      Thread thread = new Thread(runnable, "clipboard-monitor");
      thread.setDaemon(true);

      return thread;
    });

    this.executor.scheduleWithFixedDelay(this::poll, POLL_INTERVAL_MS, POLL_INTERVAL_MS, TimeUnit.MILLISECONDS);
  }

  public synchronized void stop() {
    if (this.executor == null) {
      return;
    }

    this.executor.shutdownNow();
    this.executor = null;
  }

  private synchronized void poll() {
    try {
      String text = this.clipboardController.peekText();

      if (text == null || text.equals(this.lastSeenText)) {
        return;
      }

      this.lastSeenText = text;
      this.history.add(text);
    } catch (RuntimeException exception) {
      System.err.println("[ClipboardMonitor] Poll failed: " + exception.getMessage());
    }
  }
}
