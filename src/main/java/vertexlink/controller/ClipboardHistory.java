package vertexlink.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ClipboardHistory {
  public static final int MAX_ENTRIES = 30;
  public static final int MAX_ENTRY_LENGTH = 20_000;

  public static final class Entry {
    private final String id;
    private final String text;
    private final long timestamp;

    public Entry(String id, String text, long timestamp) {
      this.id = id;
      this.text = text;
      this.timestamp = timestamp;
    }

    public String getId() {
      return this.id;
    }

    public String getText() {
      return this.text;
    }

    public long getTimestamp() {
      return this.timestamp;
    }
  }

  private final LinkedList<Entry> entries = new LinkedList<>();
  private long nextId = 1;

  public synchronized boolean add(String text) {
    if (text == null || text.trim().isEmpty() || text.length() > MAX_ENTRY_LENGTH) {
      return false;
    }

    if (!this.entries.isEmpty() && this.entries.getFirst().getText().equals(text)) {
      return false;
    }

    Iterator<Entry> iterator = this.entries.iterator();

    while (iterator.hasNext()) {
      if (iterator.next().getText().equals(text)) {
        iterator.remove();
      }
    }

    this.entries.addFirst(new Entry(String.valueOf(this.nextId++), text, System.currentTimeMillis()));

    while (this.entries.size() > MAX_ENTRIES) {
      this.entries.removeLast();
    }

    return true;
  }

  public synchronized List<Entry> snapshot() {
    return new ArrayList<>(this.entries);
  }

  public synchronized void clear() {
    this.entries.clear();
  }
}
