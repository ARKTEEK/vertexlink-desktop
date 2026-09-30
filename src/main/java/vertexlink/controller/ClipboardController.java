package vertexlink.controller;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class ClipboardController {
  private final Clipboard clipboard;

  public ClipboardController() {
    this.clipboard = initializeClipboard();
  }

  private Clipboard initializeClipboard() {
    try {
      return Toolkit.getDefaultToolkit().getSystemClipboard();
    } catch (HeadlessException exception) {
      System.err.println("[ClipboardController] Failed to access system clipboard: " + exception.getMessage());

      return null;
    }
  }

  public String getText() {
    return readText(true);
  }

  public String peekText() {
    return readText(false);
  }

  private String readText(boolean logErrors) {
    if (this.clipboard == null) {
      return null;
    }

    try {
      if (!this.clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
        return null;
      }

      Object content = this.clipboard.getData(DataFlavor.stringFlavor);

      return content instanceof String ? (String) content : null;
    } catch (UnsupportedFlavorException | IOException | IllegalStateException exception) {
      if (logErrors) {
        System.err.println("[ClipboardController] Failed to read clipboard: " + exception.getMessage());
      }

      return null;
    }
  }

  public void setText(String text) {
    if (this.clipboard == null) {
      return;
    }

    try {
      this.clipboard.setContents(new StringSelection(text), null);
    } catch (IllegalStateException exception) {
      System.err.println("[ClipboardController] Failed to write clipboard: " + exception.getMessage());
    }
  }

  public void appendText(String text) {
    String existingText = getText();
    String combinedText = (existingText == null || existingText.isEmpty())
        ? text
        : existingText + "\n" + text;

    setText(combinedText);
  }
}
