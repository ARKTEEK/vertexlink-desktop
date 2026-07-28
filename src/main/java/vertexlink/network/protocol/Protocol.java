package vertexlink.network.protocol;

import java.util.LinkedHashMap;
import java.util.Map;

public class Protocol {
  private static final String FIELD_SEP_REGEX = "\\|";
  private static final String FIELD_SEP_RAW = "|";
  private static final String KV_SEP = "=";

  public static String encode(String type, Map<String, String> fields) {
    StringBuilder sb = new StringBuilder(type);

    for (Map.Entry<String, String> e : fields.entrySet()) {
      sb.append(FIELD_SEP_RAW).append(e.getKey()).append(KV_SEP).append(escape(e.getValue()));
    }

    return sb.toString();
  }

  public static class Decoded {
    public final String type;
    public final Map<String, String> fields;

    Decoded(String type, Map<String, String> fields) {
      this.type = type;
      this.fields = fields;
    }
  }

  public static Decoded decode(String message) {
    String[] parts = message.split(FIELD_SEP_REGEX);
    Map<String, String> fields = new LinkedHashMap<>();

    for (int i = 1; i < parts.length; i++) {
      int idx = parts[i].indexOf(KV_SEP);

      if (idx >= 0) {
        fields.put(parts[i].substring(0, idx), unescape(parts[i].substring(idx + 1)));
      }
    }

    return new Decoded(parts.length > 0 ? parts[0] : "", fields);
  }

  private static String escape(String value) {
    return value.replace("\\", "\\\\").replace("|", "\\p").replace("\n", "\\n");
  }

  private static String unescape(String value) {
    return value.replace("\\n", "\n").replace("\\p", "|").replace("\\\\", "\\");
  }
}
