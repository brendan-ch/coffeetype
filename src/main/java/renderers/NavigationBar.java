package renderers;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;

import helpers.*;
import helpers.Event;
import assets.*;

/**
 * Vertical navigation bar with StatsRenderer object
 * and buttons for navigation
 */
public class NavigationBar implements CustomComponent {
  // To identify different button types
  public static final String MULTIPLAYER_BUTTONS = "multiplayerButtons";
  public static final String TEST_INACTIVE_BUTTONS = "testInactiveButtons";
  public static final String TEST_ACTIVE_BUTTONS = "testActiveButtons";

  public static final int MAX_LISTENERS = 10;
  
  private StatsTracker stats;
  private NetworkManager networkManager;

  private JPanel panel;

  private JPanel buttonsPanel;

  // Swap between these depending on test state
  private JPanel testInactiveButtons;
  private JPanel testActiveButtons;
  private JPanel multiplayerActiveButtons;
  
  // Determines when to switch the CardLayout display
  private boolean testActiveRendered;

  // Holds stats renderer and login button
  private JPanel statsWrapper;
  private StatsRenderer statsRenderer;
  // private LoginInfo loginInfo;

  // Keep track of event listeners
  private EventListener[] listeners;
  private int listenerCount;

  public NavigationBar(StatsTracker stats, SettingsTracker settings, LoginTracker loginTracker, NetworkManager networkManager) {
    this.stats = stats;
    this.networkManager = networkManager;

    this.panel = new JPanel();
    this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.PAGE_AXIS));
    // Make sure that size is set correctly
    this.panel.setBackground(Constants.ALT_LIST_COLOR);
    // Set a "padding"
    this.panel.setBorder(new EmptyBorder(10, 10, 10, 10));

    // Get the logo
    JLabel picLabel = new JLabel(Assets.scaleIcon(Assets.LOGO_WITH_TEXT));
    picLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    this.panel.add(picLabel);

    // For swapping between the two sets of buttons
    this.buttonsPanel = new JPanel();
    buttonsPanel.setLayout(new CardLayout());
    CardLayout c = (CardLayout) this.buttonsPanel.getLayout();

    this.testInactiveButtons = new JPanel();
    c.addLayoutComponent(this.testInactiveButtons, TEST_INACTIVE_BUTTONS);
    this.testInactiveButtons.setLayout(new BoxLayout(this.testInactiveButtons, BoxLayout.Y_AXIS));
    this.testInactiveButtons.setBackground(Constants.ALT_LIST_COLOR);
    this.testInactiveButtons.setBorder(new EmptyBorder(5, 0, 5, 0));

    // Create the icon button
    IconButton historyButton = new IconButton(Assets.scaleIcon(Assets.HISTORY), Event.HISTORY_BUTTON_PRESS);
    historyButton.getPanel().setAlignmentX(Component.CENTER_ALIGNMENT);
    historyButton.getPanel().setBackground(Constants.ALT_LIST_COLOR);
    testInactiveButtons.add(historyButton.getPanel());

    // Create the settings button
    IconButton settingsButton = new IconButton(Assets.scaleIcon(Assets.SETTINGS), Event.SETTINGS_BUTTON_PRESS);
    settingsButton.getPanel().setAlignmentX(Component.CENTER_ALIGNMENT);
    settingsButton.getPanel().setBackground(Constants.ALT_LIST_COLOR);
    testInactiveButtons.add(settingsButton.getPanel());

    IconButton inviteButton = new IconButton(Assets.scaleIcon(Assets.INVITE), Event.INVITE_BUTTON_PRESS);
    inviteButton.getPanel().setAlignmentX(Component.CENTER_ALIGNMENT);
    inviteButton.getPanel().setBackground(Constants.ALT_LIST_COLOR);
    testInactiveButtons.add(inviteButton.getPanel());

    // Create the button listener
    // Passes events back up to the MainWindow class
    class ButtonListener implements EventListener {
      public void actionPerformed(Event e) {
        for (EventListener listener : listeners) {
          if (listener != null) {
            listener.actionPerformed(e);
          }
        }
      }
    }
    // Pass events back up
    settingsButton.addEventListener(new ButtonListener());
    historyButton.addEventListener(new ButtonListener());
    inviteButton.addEventListener(new ButtonListener());

    // Create a different panel for the restart button
    this.testActiveButtons = new JPanel();
    c.addLayoutComponent(this.testActiveButtons, TEST_ACTIVE_BUTTONS);
    this.testActiveButtons.setLayout(new BoxLayout(this.testActiveButtons, BoxLayout.PAGE_AXIS));
    this.testActiveButtons.setBackground(Constants.ALT_LIST_COLOR);
    this.testActiveButtons.setBorder(new EmptyBorder(5, 0, 5, 0));

    // Add the restart button
    IconButton restartButton = new IconButton(Assets.scaleIcon(Assets.RESTART_TEST), Event.RESTART_TEST);
    restartButton.getPanel().setAlignmentX(Component.CENTER_ALIGNMENT);
    restartButton.getPanel().setBackground(Constants.ALT_LIST_COLOR);
    this.testActiveButtons.add(restartButton.getPanel());

    class RestartListener implements EventListener {
      private StatsTracker stats;

      public RestartListener(StatsTracker stats) {
        this.stats = stats;
      }

      public void actionPerformed(Event e) {
        stats.resetTest();
      }
    }

    restartButton.addEventListener(new RestartListener(this.stats));

    this.multiplayerActiveButtons = new JPanel();
    c.addLayoutComponent(this.multiplayerActiveButtons, MULTIPLAYER_BUTTONS);
    this.multiplayerActiveButtons.setLayout(new BoxLayout(this.multiplayerActiveButtons, BoxLayout.PAGE_AXIS));
    this.multiplayerActiveButtons.setBackground(Constants.ALT_LIST_COLOR);
    this.multiplayerActiveButtons.setBorder(new EmptyBorder(5, 0, 5, 0));
    
    // Add the exit button
    IconButton exitButton = new IconButton(Assets.scaleIcon(Assets.EXIT), Event.EXIT_MULTIPLAYER_BUTTON_PRESS);
    exitButton.getPanel().setAlignmentX(Component.CENTER_ALIGNMENT);
    exitButton.getPanel().setBackground(Constants.ALT_LIST_COLOR);
    this.multiplayerActiveButtons.add(exitButton.getPanel());

    // Pass events back up
    exitButton.addEventListener(new ButtonListener());

    // Keep track of current test state
    this.testActiveRendered = false;

    // Add event listener to switch between testInactiveButtons
    // and testActiveButtons
    class StatsListener implements EventListener {
      public void actionPerformed(Event e) {
        if (e.EVENT_TYPE == Event.START_TEST || e.EVENT_TYPE == Event.TEST_FINISHED || e.EVENT_TYPE == Event.RESTART_TEST) {
          // Re-render
          render();
        }
      }
    }
    stats.addEventListener(new StatsListener());

    this.statsWrapper = new JPanel();
    this.statsWrapper.setLayout(new CardLayout());

    this.statsRenderer = new StatsRenderer(stats, settings);
    this.statsRenderer.getPanel().setAlignmentX(Component.CENTER_ALIGNMENT);

    // this.loginInfo = new LoginInfo(loginTracker);
    
    this.buttonsPanel.add(this.testInactiveButtons);
    this.buttonsPanel.add(this.testActiveButtons);
    this.buttonsPanel.add(this.multiplayerActiveButtons);

    // this.statsWrapper.add(this.loginInfo.getPanel());
    this.statsWrapper.add(this.statsRenderer.getPanel());
    
    this.panel.add(buttonsPanel);
    this.panel.add(statsWrapper);

    // Add a listener for the network manager
    class NetworkManagerListener implements EventListener {
      public void actionPerformed(Event e) {
        // Re-render based on network status
        if (e.EVENT_TYPE == Event.NETWORK_STATUS_CHANGE) {
          render();
        }
      }
    }
    this.networkManager.addEventListener(new NetworkManagerListener());

    this.listeners = new EventListener[MAX_LISTENERS];
    this.listenerCount = 0;
  }

  public JPanel getPanel() {
    return this.panel;
  }
  
  public void addEventListener(EventListener listener) {
    if (listenerCount < MAX_LISTENERS) {
      this.listeners[listenerCount++] = listener; 
    }
  }

  /**
   * Update buttons according to whether test is active.
   */
  public void render() {
    CardLayout c = (CardLayout) this.buttonsPanel.getLayout();

    if (this.networkManager.getConnectionStatus()) {
      // Show multiplayer buttons
      c.show(this.buttonsPanel, MULTIPLAYER_BUTTONS);
    } else if (this.stats.getTimerStatus()) {
      // If test is running, and correct buttons not displayed already
      // Switch the layout
      c.show(this.buttonsPanel, TEST_ACTIVE_BUTTONS);
    } else if (!this.stats.getTimerStatus()) {
      c.show(this.buttonsPanel, TEST_INACTIVE_BUTTONS);
    }
  }
}