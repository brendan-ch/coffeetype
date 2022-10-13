package dev.bchen.helpers;

// For the timer
import javax.swing.*;
// import java.awt.*;
import java.awt.event.*;

// Object that tracks stats and words during a test
public class StatsTracker {
  // Store this as an int instead of calc1ulating
  // from text file
  // Use to generate random line to get word from
  public static final int NUM_LINES_IN_TEXT_FILE = 1000;

  // Duration of test in seconds
  public static final int TIMER_DEFAULT = 30;
  // Timer will "tick" every second
  public static final int TIMER_TICK = 1000;

  public static final int MAX_LISTENERS = 10;
  public static final int MAX_TESTS = 8;
  
  /**
   * Generated characters to display and compare typed results against.
   */
  private String characters;

  /**
   * Typed characters to compare generated characters against for error-checking.
   */
  private String typed;

  /**
   * Number of characters before populating more words
   */
  private int charsBeforeNext;

  /**
   * Number of seconds remaining in the test.
   */
  private int timeRemaining;

  /**
   * Timer object to control `timeRemaining` and ending the test.
   */
  private Timer timer;

  // Event listeners
  private EventListener[] listeners;
  private int numListeners;

  /**
   * Stores user's test history, up to `MAX_TESTS` tests. Most recent tests are at the end of the array.
   */
  private Test[] tests;
  private int numTests;

  private SettingsTracker settings;

  public StatsTracker(SettingsTracker settings) {
    this.settings = settings;
    this.tests = new Test[MAX_TESTS];

    // this.characters = new ArrayList<String>();
    this.characters = WordsLoader.getWords(200, (Boolean) settings.getSetting(SettingsTracker.HARD_MODE));

    // this.typed = new ArrayList<String>();
    this.typed = "";

    this.charsBeforeNext = 30;

    // Initialize time remaining
    this.timeRemaining = TIMER_DEFAULT;

    this.listeners = new EventListener[MAX_LISTENERS];
    this.numListeners = 0;

    // Set timer action listener to fire event listener
    class TimerListener implements ActionListener {
      private EventListener[] listeners;
      private SettingsTracker settings;

      public TimerListener(EventListener[] listeners, SettingsTracker settings) {
        this.listeners = listeners;
        this.settings = settings;
      }
      
      public void actionPerformed(ActionEvent e) {
        // Decrement the timer
        timeRemaining--;
        // Fire event listener to indicate timer run
        for (EventListener listener : this.listeners) {
          if (listener != null) {
            listener.actionPerformed(new Event(Event.TIMER_TICK)); 
          }
        }

        // If timeRemaining below 0, fire event listener and reset timer
        if (timeRemaining < 0) {
          // Add the test to history
          Test newTest = new Test(getWPM(), getAcc(), TIMER_DEFAULT, (Boolean) settings.getSetting(SettingsTracker.HARD_MODE));
          addTest(newTest);

          // Fire event listener
          for (EventListener listener : this.listeners) {
            // Fire the actionPerformed method
            if (listener != null) {
              listener.actionPerformed(new Event(Event.TEST_FINISHED)); 
            }
          }

          resetTest();
      }
    }
    }
    
    // Set the timer object, don't start it yet
    this.timer = new Timer(TIMER_TICK, new TimerListener(this.listeners, this.settings));
  }

  // Accessors
  /**
   * Calculate the current WPM, based on the time remaining.
   */
  public int getWPM() {
    // Count number of words typed correctly
    int numWordsTypedCorrectly = 0;
    // Calculate how much time has passed
    double minutes = (double) (TIMER_DEFAULT - timeRemaining) / 60;

    // Avoid dividing by 0
    if (minutes == (double) 0) {
      return 0;
    }

    String actualWord = "";
    String typedWord = "";
    
    // Loop through typed characters
    for (int i = 0; i < this.typed.length(); i++) {
      String character = this.typed.substring(i, i + 1);

      if (character.equals(" ")) {
        // Bump number of words typed correctly
        if (actualWord.equals(typedWord)) {
          numWordsTypedCorrectly++;
        }

        // Clear the typed words
        actualWord = "";
        typedWord = "";
      } else {
        actualWord += this.characters.substring(i, i + 1);
        typedWord += character;
      }
    }
    
    return (int) (numWordsTypedCorrectly / minutes);
  }

  /**
   * Calculate the current accuracy % based on characters typed correctly / total characters typed.
   */
  public int getAcc() {
    // Count number of characters typed correctly
    int numCharsTypedCorrectly = 0;

    for (int i = 0; i < this.typed.length(); i++) {
      if (this.characters.substring(i, i + 1).equals(this.typed.substring(i, i + 1))) {
        numCharsTypedCorrectly++;
      }
    }

    return (int) (((double) numCharsTypedCorrectly / this.typed.length()) * 100);
  }

  /**
   * Get all generated characters.
   */
  public String getCharacters() {
    return this.characters;
  }

  /**
   * Get all typed characters.
   */
  public String getTyped() {
    return this.typed;
  }

  /**
   * Returns true if the timer is running, and false otherwise.
   */
  public boolean getTimerStatus() {
    return this.timer.isRunning();
  }

  // Get the time remaining, in seconds
  public int getTimeRemaining() {
    return this.timeRemaining;
  }

  public Test[] getTests() {
    return this.tests;
  }

  // Mutator methods
  // "Add" an event listener
  public void addEventListener(EventListener listener) {
    if (numListeners < MAX_LISTENERS) {
      this.listeners[numListeners++] = listener; 
    }
  }

  /**
   * Add a test to the end of the `tests` array.
   * If number of tests exceeds `MAX_TESTS`, remove the oldest test
   * and shift all values left.
   * @param toAdd
   */
  public void addTest(Test toAdd) {
    if (this.numTests < MAX_TESTS) {
      this.tests[this.numTests++] = toAdd;
    } else {
      // Shift all values over
      // End of array - most recent
      // Beginning of array - oldest

      for (int i = 1; i < MAX_TESTS; i++) {
        this.tests[i - 1] = this.tests[i];
      }
      this.tests[MAX_TESTS - 1] = toAdd;
    }
  }

  /**
   * Add a letter to the `typed` string.
   */
  public void addLetter(String toAdd) {
    // When the test starts, start the 30-second timer
    if (!this.timer.isRunning()) {
      this.timer.start();

      // Fire event listener for test starting
      for (EventListener listener : listeners) {
        if (listener != null) {
          listener.actionPerformed(new Event(Event.START_TEST));
        }
      }
    }
    
    this.typed += toAdd;
    this.charsBeforeNext--;

    // Check whether it's time to add more words
    if (this.charsBeforeNext <= 0) {
      characters += WordsLoader.getWords(5, (Boolean) settings.getSetting(SettingsTracker.HARD_MODE));
      this.charsBeforeNext = 30;
    }
  }

  /**
   * Delete a letter from the `typed` string.
   */
  public void deleteLetter() {
    if (this.typed.length() > 0) {
      this.typed = this.typed.substring(0, this.typed.length() - 1);
      this.charsBeforeNext++;
    }
  }

  /**
   * Reset the test.
   */
  public void resetTest() {
    // Reset values
    timer.stop();
    timeRemaining = TIMER_DEFAULT;
    // Reset typed string
    typed = "";
    // Re-generate the string
    characters = WordsLoader.getWords(200, (Boolean) settings.getSetting(SettingsTracker.HARD_MODE));

    // Fire event listener
    for (EventListener listener : listeners) {
      if (listener != null) {
        listener.actionPerformed(new Event(Event.RESTART_TEST));
      }
    }
  }
}