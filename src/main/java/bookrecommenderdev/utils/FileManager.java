package bookrecommenderdev.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileManager {

  private FileManager() {
    // private constructor to prevent instantiation
  }

  /**
   * Writes the given text to the specified file.
   * If the file exists, it will be overwritten.
   */
  public static void write(String filePath, String text) {
    Path path = Path.of(filePath);
    try {
      Files.writeString(path, text, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException _) {}
  }

  /**
   * Reads the entire contents of the specified file as a string.
   * Returns an empty string if the file does not exist.
   */
  public static String read(String filePath) {
    try {
      Path path = Path.of(filePath);
      if (Files.exists(path)) {
        return Files.readString(path);
      } else {
        return "";
      }
    } catch (IOException e) {
      return null;
    }
  }

  /**
   * Checks if the specified file exists.
   */
  public static boolean exists(String filePath) {
    return Files.exists(Path.of(filePath));
  }

  /**
   * Deletes the specified file.
   * Returns true if deletion was successful, false otherwise.
   */
  public static boolean delete(String filePath) {
    Path path = Path.of(filePath);
    if (Files.exists(path)) {
      try {
        Files.delete(path);
      } catch (IOException _) {}
      return true;
    }
    return false;
  }
}