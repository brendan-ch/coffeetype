package dev.bchen.helpers;

// For text file loading
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.*;

/**
 * Utility class to load words from a file.
 */
public class WordsLoader {
  // Folder name of the word files
  public static final String PATH_NORMAL = "src/main/resources/words/normal.txt";
  public static final String PATH_HARD = "src/main/resources/words/hard.txt";

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

    String characters = "";

    while (numLoaded < numToLoad) {
      // Generate a random line
      int lineToGet = WordsLoader.generateRandomLine(hardMode);
      String line;

      // Get the word from that line
      // https://www.educative.io/edpresso/reading-the-nth-line-from-a-file-in-java
      try (Stream<String> lines = Files.lines(Paths.get(path))) {
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