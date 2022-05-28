package helpers;

/**
 * Tracks settings set by the user.
 */
public class SettingsTracker {
  /**
   * Maximum number of event listeners that can be stored.
   */
  public static final int MAX_LISTENERS = 10;

  // Keys for referencing different settings
  public static final String WPM_ENABLED = "wpmEnabled";
  public static final String ACC_ENABLED = "accEnabled";
  public static final String TIME_ENABLED = "timeEnabled";
  public static final String HARD_MODE = "hardMode";

  private boolean wpmEnabled;
  private boolean timeEnabled;
  private boolean accEnabled;
  private boolean hardMode;

  private EventListener[] listeners;
  private int numListeners;

  public SettingsTracker() {
    this.accEnabled = false;
    this.wpmEnabled = false;
    this.timeEnabled = true;
    this.hardMode = false;

    this.numListeners = 0;
    this.listeners = new EventListener[MAX_LISTENERS];
  }

  // Accessor methods
  /**
   * Get an array of available setting keys.
   * @return
   */
  public static String[] getKeys() {
    String[] arr = {WPM_ENABLED, ACC_ENABLED, TIME_ENABLED, HARD_MODE};
    return arr;
  }

  /**
   * Get an array of labels corresponding to the keys returned in `getKeys`.
   * @return
   */
  public static String[] getLabels() {
    String[] arr = {
      "Show WPM during test            ",
      "Show accuracy during test       ",
      "Show time remaining during test ",
      "Hard mode                       "
    };

    return arr;
  }

  /**
   * Get a setting value using its key.
   * @param key
   * @return A wrapped Integer or Boolean object, or null
   * if the setting key isn't found.
   */
  public Object getSetting(String key) {
    if (key.equals(WPM_ENABLED)) {
      return wpmEnabled;
    } else if (key.equals(ACC_ENABLED)) {
      return accEnabled;
    } else if (key.equals(TIME_ENABLED)) {
      return timeEnabled;
    } else if (key.equals(HARD_MODE)) {
      return hardMode;
    }

    return null;
  }

  public boolean getWpmEnabled() {
    return this.wpmEnabled;
  }

  public boolean getAccEnabled() {
    return this.accEnabled;
  }

  public boolean getTimeEnabled() {
    return this.timeEnabled;
  }


  /**
   * Add an event listener.
   * @param listener
   */
  public void addEventListener(EventListener listener) {
    if (numListeners < MAX_LISTENERS) {
      this.listeners[numListeners++] = listener;
    }
  }

  /**
   * Update a setting and fire event listeners.
   * @param settingName
   * @param value
   */
  public void updateSetting(String settingName, boolean value) {
    boolean updated = false;

    if (settingName.equals(ACC_ENABLED)) {
      this.accEnabled = value;
      updated = true;
    } else if (settingName.equals(WPM_ENABLED)) {
      this.wpmEnabled = value;
      updated = true;
    } else if (settingName.equals(TIME_ENABLED)) {
      this.timeEnabled = value;
      updated = true;
    } else if (settingName.equals(HARD_MODE)) {
      this.hardMode = value;
      updated = true;
    }

    if (updated) {
      // Fire event listeners
      this.fireSettingChangedListeners();
    }
  }

  // Private methods
  /**
   * Fire all event listeners with a SETTING_CHANGED event.
   */
  private void fireSettingChangedListeners() {
    for (EventListener listener : listeners) {
      if (listener != null) {
        listener.actionPerformed(new Event(Event.SETTING_CHANGED));
      }
    }    
  }
}
