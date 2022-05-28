package helpers;

/**
 * (UNUSED) Store login information.
 */
public class LoginTracker {
  public static final int MAX_LISTENERS = 10;
  
  /**
   * Username of the logged in user, `null` if not logged in.
   */
  private String username;

  private EventListener[] listeners;
  private int numListeners;

  public LoginTracker() {
    this.username = null;

    this.listeners = new EventListener[MAX_LISTENERS];
  }

  public String getUsername() {
    return this.username;
  }
  
  public void addEventListener(EventListener listener) {
    if (numListeners < MAX_LISTENERS) {
      this.listeners[numListeners++] = listener;
    }
  }
  
  public void setUsername(String username) {
    this.username = username;
  }

  public void clearUsername() {
    this.username = null;
  }
}