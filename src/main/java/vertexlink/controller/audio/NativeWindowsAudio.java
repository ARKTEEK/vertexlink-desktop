package vertexlink.controller.audio;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class NativeWindowsAudio {
  private static final String LIBRARY_NAME = "audio";
  private static final String LIBRARY_RESOURCE = "/native/windows/audio.dll";

  private static boolean isLoaded = false;

  private NativeWindowsAudio() {
  }

  public static synchronized void load() throws IOException {
    if (isLoaded) {
      return;
    }

    try (InputStream libraryStream = NativeWindowsAudio.class.getResourceAsStream(LIBRARY_RESOURCE)) {
      if (libraryStream == null) {
        System.loadLibrary(LIBRARY_NAME);
      } else {
        Path libraryFile = Files.createTempFile(LIBRARY_NAME, ".dll");

        Files.copy(libraryStream, libraryFile, StandardCopyOption.REPLACE_EXISTING);
        libraryFile.toFile().deleteOnExit();

        System.load(libraryFile.toAbsolutePath().toString());
      }
    } catch (UnsatisfiedLinkError error) {
      throw new IOException("Native audio library unavailable: " + error.getMessage());
    }

    isLoaded = true;
  }

  public static native int getVolumePercent() throws IOException;

  public static native void setVolumePercent(int percent) throws IOException;

  public static native boolean isMuted() throws IOException;

  public static native void setMuted(boolean isMuted) throws IOException;
}
