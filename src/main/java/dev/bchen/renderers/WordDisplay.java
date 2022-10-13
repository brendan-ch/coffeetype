package dev.bchen.renderers;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import dev.bchen.assets.Assets;
import dev.bchen.helpers.Constants;
import dev.bchen.helpers.CustomComponent;
import dev.bchen.helpers.Event;
import dev.bchen.helpers.EventListener;
import dev.bchen.helpers.NetworkManager;
import dev.bchen.helpers.StatsTracker;

/**
 * Tracks the position of the character relative to the
 * character string in `StatsTracker`.
 */
class RenderedCharacter {
  public final String CHARACTER;
  public final int INDEX;

  public RenderedCharacter(String character, int index) {
    this.CHARACTER = character;
    this.INDEX = index;
  }
}

/**
 * Renders the words to type.
 */
public class WordDisplay implements CustomComponent {
  // Static ints
  /**
   * Maximum number of characters in one line.
   */
  public static final int MAX_CHARS = 50;

  /**
   * Row # to start scrolling at.
   */
  public static final int ROWS_BEFORE_SCROLL = 7;
  
  public static final int WIDTH = Constants.WIDTH;
  public static final int HEIGHT = (Constants.HEIGHT / 4);

  /**
   * Number of rows.
   */
  public static final int NUM_ROWS = 11;

  public static final Color BACKGROUND = Constants.BACKGROUND;

  // Main panel
  private JPanel panel;

  /**
   * 2D array of labels containing text.
   */
  private JLabel[] labels;
  private RenderedCharacter[][] chars;
  private int rowNumber;
  private int shift;
  
  /**
   * Number of characters before bumping the row number.
   */
  private int charsBeforeNextRow;
  
  // Stats tracker
  private StatsTracker stats; // reference to object should be passed down from MainWindow
  private NetworkManager networkManager;

  public WordDisplay(StatsTracker stats, NetworkManager networkManager) {
    this.stats = stats;
    this.networkManager = networkManager;

    class NetworkManagerListener implements EventListener {
      public void actionPerformed(Event e) {
        if (e.EVENT_TYPE == Event.NETWORK_STATUS_CHANGE) {

        } else if (e.EVENT_TYPE == Event.NETWORK_WORDS_UPDATE || e.EVENT_TYPE == Event.NETWORK_TEST_START) {
          updateWords();
          render();
        }
      }
    }
    this.networkManager.addEventListener(new NetworkManagerListener());
    
    this.panel = new JPanel();
    this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.PAGE_AXIS));
    this.panel.setBackground(Constants.BACKGROUND);

    this.shift = 0;

    // Add key listener here
    class TypingKeyListener implements KeyListener {
      private StatsTracker stats;
      private NetworkManager networkManager;

      public TypingKeyListener(StatsTracker stats, NetworkManager networkManager) {
        this.stats = stats;
        this.networkManager = networkManager;
      }

      // KeyListener requires 3 listeners
      public void keyPressed(KeyEvent e) {
        // Check if backspace
        if (e.getKeyCode() == 8) {
          // Remove a letter
          if (networkManager.getConnectionStatus()) {
            networkManager.deleteLetter();
          } else {
            stats.deleteLetter();
          }

          charsBeforeNextRow++;
        // Escape key -> quick reset
        } else if (e.getKeyCode() == 27 && !networkManager.getConnectionStatus()) {
          stats.resetTest();
          setNumCharsInRow();
        } else if ((e.getKeyCode() >= 44 && e.getKeyCode() <= 111) || e.getKeyCode() == 32 || e.getKeyCode() == 222) {
          if (networkManager.getConnectionStatus()) {
            // Update words in network manager
            networkManager.addLetter(String.valueOf(e.getKeyChar()));
          } else {
            // Add key to `typed` string
            stats.addLetter(String.valueOf(e.getKeyChar()));
          }

          charsBeforeNextRow--;

          if (charsBeforeNextRow < 0) {
            // Bump row number
            rowNumber++;

            // Recalculate charsBeforeNextRow
            // System.out.println("charsBeforeNextRow reset");
            setNumCharsInRow();
          }
        }
        
        // Re-render based on new `typed` string
        updateWords();
        render();
      }
      public void keyReleased(KeyEvent e) {}
      public void keyTyped(KeyEvent e) {}
    }
    this.panel.addKeyListener(new TypingKeyListener(this.stats, this.networkManager));

    class StatsListener implements EventListener {
      public void actionPerformed(Event e) {
        if (e.EVENT_TYPE == Event.RESTART_TEST) {
          // Reset some values
          rowNumber = 0;
          shift = 0;

          updateWords();
          setNumCharsInRow();
          render();
        }
      }
    }
    this.stats.addEventListener(new StatsListener());

    this.chars = new RenderedCharacter[NUM_ROWS][MAX_CHARS];
    this.labels = new JLabel[NUM_ROWS];
    for (int i = 0; i < NUM_ROWS; i++) {
      this.labels[i] = new JLabel("");
      this.labels[i].setFont(Assets.TYPING_FONT);
      this.labels[i].setForeground(Constants.TYPING_TEXT_COLOR);
      this.labels[i].setBorder(new EmptyBorder(3, 0, 3, 0));

      this.panel.add(this.labels[i]);
    }

    // Focus on panel
    this.panel.setFocusable(true);
    this.panel.requestFocusInWindow();

    this.rowNumber = 0;

    this.updateWords();
    this.setNumCharsInRow();

    this.render();
  }

  public JPanel getPanel() {
    return this.panel;
  }

  public void render() {
    this.panel.requestFocusInWindow();

    // Loop through chars array
    for (int r = 0; r < NUM_ROWS; r++) {
      // Get the label in the correct row
      JLabel rowLabel = this.labels[r];

      String toSet = "<html><p>";

      String typed = this.stats.getTyped();

      if (this.networkManager.getConnectionStatus()) {
        typed = this.networkManager.getTyped();
      }

      // Loop through RenderedChars
      for (int c = 0; c < MAX_CHARS; c++) {
        if (this.chars[r][c] != null) {
          // Append the character to the toSet string
          String character = this.chars[r][c].CHARACTER;
          int index = this.chars[r][c].INDEX;

          // Compare to index in StatsTracker
          if (index > typed.length()) {
            toSet += character;
          } else if (index == typed.length()) {
            // Highlight the text to create cursor
            toSet += "<span style='background-color: white; color: black'>" + character + "</span>";
          } else if (typed.substring(index, index + 1).equals(character)) {
            // Typed correctly
            toSet += "<u><font color=WHITE>" + character + "</font></u>";
          } else {
            // Typed incorrectly
            toSet += "<u><font color=RED>" + character + "</font></u>";
          }
        }
      }

      toSet += "</p></html>";
      rowLabel.setText(toSet);
    }
  }

  // Private methods
  /**
   * Read characters from `StatsTracker` object, and update
   * `stats.chars` array.
   * Run on initialization and after more words are added to `stats.chars`.
   */
  private void updateWords() {
    String characters = this.stats.getCharacters();

    if (this.networkManager.getConnectionStatus()) {
      characters = this.networkManager.getCharacters();
    }

    // Row number
    int r = 0;
    // Column number
    int c = 0;
    // Index of character
    int i = 0;

    if (this.rowNumber > ROWS_BEFORE_SCROLL) {
      this.rowNumber--;
      this.shift++;
    }

    int tempShift = shift;

    // Loop through rows and columns
    while (r < NUM_ROWS) {
      // Get the index of the next space
      int nextSpace = characters.indexOf(" ");
      // Get the word before the space
      String word = characters.substring(0, nextSpace);

      word += " ";
      // If wrapped to next line
      if (c + word.length() > MAX_CHARS) {
        if (tempShift <= 0) {
          // Set rest of row to null
          for (int tempColumn = c; tempColumn < MAX_CHARS; tempColumn++) {
            this.chars[r][tempColumn] = null;
          }
          r++;
        } else {
          tempShift--;
        }

        // Wrap to next line
        c = 0;
      }

      // Exit if row exceeds NUM_ROWS
      if (r >= NUM_ROWS) return;
      
      for (int tempColumn = c; tempColumn < c + word.length(); tempColumn++) {
        // Assign to place in chars array
        if (tempShift <= 0) {
          this.chars[r][tempColumn] = new RenderedCharacter(word.substring(tempColumn - c, tempColumn - c + 1), i);
        }
        i++;
      }
      // Add to column length (including space)
      c += word.length();
      
      // Remove word from characters string
      characters = characters.substring(nextSpace + 1);
    }
  }

  /**
   * Update the `charsBeforeNextRow` property, using the `rowNumber`.
   */
  private void setNumCharsInRow() {
    if (this.rowNumber >= NUM_ROWS) {
      return;
    }

    // Loop from end until not null
    for (int i = this.chars[this.rowNumber].length - 1; i >= 0; i--) {
      if (this.chars[this.rowNumber][i] != null) {
        this.charsBeforeNextRow = i;
        return;
      }
    }

    // this.charsBeforeNextRow = this.chars[this.rowNumber].length;
  }
}