package helpers;

/**
 * Represents a custom event passed by an `EventListener`.
 */
public class Event {
  // All possible events go here
  // Constants -> ints
  public static final int TEST_FINISHED = 0;
  public static final int TIMER_TICK = 1;
  public static final int NEXT_TEST = 2;

  /**
   * Fires when a test is reset, using the restart button or
   * keybind (esc).
   */
  public static final int RESTART_TEST = 3;

  /**
   * Fires when the timer is started.
   */
  public static final int START_TEST = 4;
  
  // Indicates the screen to return to
  public static final int SETTINGS_BUTTON_PRESS = 5;
  public static final int HISTORY_BUTTON_PRESS = 6;
  public static final int INVITE_BUTTON_PRESS = 7;
  public static final int EXIT_MULTIPLAYER_BUTTON_PRESS = 8;

  public static final int BACK_BUTTON_PRESS = 9;

  public static final int SETTING_CHANGED = 10;

  public static final int NEW_ROOM_PRESSED = 11;
  public static final int JOIN_ROOM_PRESSED = 12;

  // Corresponds to network events
  public static final int NETWORK_TEST_START = 13;
  public static final int NETWORK_TEST_END = 14;
  public static final int NETWORK_WORDS_UPDATE = 15;
  public static final int NETWORK_PLAYERS_UPDATE = 16;

  public static final int NETWORK_TEST_START_DELAY_END = 17;

  // Indicates join or exit
  public static final int NETWORK_STATUS_CHANGE = 18;

  public final int EVENT_TYPE;
  
  public Event(int type) {
    this.EVENT_TYPE = type;
  }
}