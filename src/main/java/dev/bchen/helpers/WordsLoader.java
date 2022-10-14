package dev.bchen.helpers;

// For text file loading
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.*;

/**
 * Utility class to load words from a file.
 */
public class WordsLoader {
  // Folder name of the word files
  public static final String PATH_NORMAL = "/words/normal.txt";
  public static final String PATH_HARD = "/words/hard.txt";

  // Store num lines as an int instead of calculating
  // from text file
  // Use to generate random line to get word from
  public static final int NUM_LINES_NORMAL = 500;
  public static final int NUM_LINES_HARD = 500;

  /**
   * Return a random int between 1 and `NUM_LINES_NORMAL`.
   */
  public static int generateRandomLine() {
    return (int) (Math.random() * NUM_LINES_NORMAL) + 1;
  }

  /**
   * Return a random int between 1 and `NUM_LINES_NORMAL` (or `NUM_LINES_HARD` if `hardMode` is `true`);
   * @param hardMode
   */
  public static int generateRandomLine(boolean hardMode) {
    if (hardMode) {
      return (int) (Math.random() * NUM_LINES_HARD) + 1;
    }
    return generateRandomLine();
  }

  /**
   * Return a string of words separated by spaces.
   * @param numToLoad
   * @param hardMode
   */
  public static String getWords(int numToLoad, boolean hardMode) {
    if (numToLoad <= 0) return null; // prevent infinite loop
    int numLoaded = 0;

    String path = PATH_NORMAL;
    if (hardMode) {
      path = PATH_HARD;
    }

    Map<String, String> env = new HashMap<>(); 
    env.put("create", "true");
    URI uri;
    FileSystem zipfs;
    try {
      uri = WordsLoader.class.getResource(path).toURI();
      zipfs = FileSystems.newFileSystem(uri, env);
    } catch(URISyntaxException e) {
      System.out.println(e);
      return "";
    } catch(IOException e) {
      System.out.println(e);
      return "";
    }

    String characters = "";

    while (numLoaded < numToLoad) {
      // Generate a random line
      int lineToGet = WordsLoader.generateRandomLine(hardMode);
      String line;

      // Get the word from that line
      // https://www.educative.io/edpresso/reading-the-nth-line-from-a-file-in-java
      // To-do: use WordsLoader.class.getResource() to retrieve asset
      try (Stream<String> lines = Files.lines(Paths.get(uri))) {
        line = lines.skip(lineToGet - 1).findFirst().get();

        // Loop through characters
        for (int i = 0; i < line.length(); i++) {
          characters += (line.substring(i, i + 1));
        }

        characters += " ";
      } catch(IOException e){
        System.out.println(e);
      }
      
      numLoaded++;
    }

    return characters;
  }
}