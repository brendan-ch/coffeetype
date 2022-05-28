package renderers;

import javax.swing.*;
import java.awt.*;

import helpers.*;
import helpers.Event;

/**
 * Hosts all GUI content.
 */
public class MainWindow implements CustomComponent {
  // Static ints
  // References for the CardLayout manager
  public static final String WORD_DISPLAY = "wordDisplay";
  public static final String RESULTS_SCREEN = "resultsScreen";
  public static final String STATS_SCREEN = "statsScreen";
  public static final String HISTORY_SCREEN = "historyScreen";
  public static final String SETTINGS_SCREEN = "settingsScreen";
  public static final String MULTIPLAYER_SCREEN = "multiplayerScreen";
  public static final String PLAYER_SCREEN = "playerScreen";

  public static final int WIDTH = Constants.WIDTH;
  public static final int HEIGHT = Constants.HEIGHT;

  // Keep track of which menu item is selected
  
  // Panels
  private JPanel panel;
  private JPanel contentWrapper;

  // Stats
  private StatsTracker stats;
  // Settings
  private SettingsTracker settings;
  // Store login information
  private LoginTracker loginTracker;

  private NetworkManager networkManager;

  // Track the different components
  private WordDisplay wordDisplay;
  private ResultsScreen resultsScreen;
  private SettingsScreen settingsScreen;
  private HistoryScreen historyScreen;
  private NavigationBar header;
  private MultiplayerScreen multiplayerScreen;
  private PlayersScreen playersScreen;
  
  // Create stats array to keep track of runs
  // private StatsTracker[] statsArr;

  public MainWindow() {
    this.panel = new JPanel();
    this.settings = new SettingsTracker();
    this.stats = new StatsTracker(this.settings);
    this.loginTracker = new LoginTracker();
    this.networkManager = new NetworkManager();

    // this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.PAGE_AXIS));
    this.panel.setLayout(new FlowLayout(FlowLayout.LEADING));
    this.panel.setSize(WIDTH, HEIGHT);
    this.panel.setBackground(Constants.BACKGROUND);

    // Initialize the header
    this.header = new NavigationBar(this.stats, this.settings, this.loginTracker, this.networkManager);

    this.panel.add(this.header.getPanel());
    
    // Add event listeners to switch primary content renderer
    // when necessary

    // Add key listener for keystrokes

    // Use a CardLayout for the middle panel
    // More information on CardLayout: https://docs.oracle.com/javase/tutorial/uiswing/layout/card.html
    this.contentWrapper = new JPanel(new CardLayout());
    this.contentWrapper.setBackground(Constants.BACKGROUND);
    CardLayout c = (CardLayout) this.contentWrapper.getLayout();

    class HeaderListener implements EventListener {
      private CardLayout c;

      public HeaderListener(CardLayout c) {
        this.c = c;
      }

      public void actionPerformed(Event e) {
        if (e.EVENT_TYPE == Event.SETTINGS_BUTTON_PRESS) {
          // Switch to settings page
          c.show(contentWrapper, SETTINGS_SCREEN);
        } else if (e.EVENT_TYPE == Event.HISTORY_BUTTON_PRESS) {
          // Switch to history page
          c.show(contentWrapper, HISTORY_SCREEN);
        } else if (e.EVENT_TYPE == Event.INVITE_BUTTON_PRESS) {
          // Switch to multiplayer screen
          c.show(contentWrapper, MULTIPLAYER_SCREEN);
        } else if (e.EVENT_TYPE == Event.EXIT_MULTIPLAYER_BUTTON_PRESS) {
          // Exit multiplayer
          networkManager.exitRoom();
          c.show(contentWrapper, WORD_DISPLAY);
        }
      }
    }
    this.header.addEventListener(new HeaderListener(c));

    this.wordDisplay = new WordDisplay(this.stats, this.networkManager);
    c.addLayoutComponent(this.wordDisplay.getPanel(), WORD_DISPLAY);
    this.contentWrapper.add(this.wordDisplay.getPanel());

    this.resultsScreen = new ResultsScreen(this.stats);
    c.addLayoutComponent(this.resultsScreen.getPanel(), RESULTS_SCREEN);
    this.contentWrapper.add(resultsScreen.getPanel());

    this.settingsScreen = new SettingsScreen(this.settings);

    // Also use for history screen
    class SettingsScreenListener implements EventListener {
      private CardLayout c;

      public SettingsScreenListener(CardLayout c) {
        this.c = c;
      }

      public void actionPerformed(Event e) {
        if (e.EVENT_TYPE == Event.BACK_BUTTON_PRESS) {
          // Return to the word display
          c.show(contentWrapper, WORD_DISPLAY);
          wordDisplay.render();
        }
      }
    }

    class SettingsTrackerListener implements EventListener {
      public void actionPerformed(Event e) {
        if (e.EVENT_TYPE == Event.SETTING_CHANGED) {
          // Reset test
          stats.resetTest();
        }
      }
    }

    this.settings.addEventListener(new SettingsTrackerListener());
    
    this.settingsScreen.addEventListener(new SettingsScreenListener(c));

    c.addLayoutComponent(this.settingsScreen.getPanel(), SETTINGS_SCREEN);
    this.contentWrapper.add(settingsScreen.getPanel());

    this.historyScreen = new HistoryScreen(this.stats);
    this.historyScreen.addEventListener(new SettingsScreenListener(c));
    c.addLayoutComponent(this.historyScreen.getPanel(), HISTORY_SCREEN);
    this.contentWrapper.add(historyScreen.getPanel());

    this.multiplayerScreen = new MultiplayerScreen(this.networkManager);
    this.multiplayerScreen.addEventListener(new SettingsScreenListener(c));
    c.addLayoutComponent(this.multiplayerScreen.getPanel(), MULTIPLAYER_SCREEN);
    this.contentWrapper.add(multiplayerScreen.getPanel());

    this.playersScreen = new PlayersScreen(this.networkManager);
    c.addLayoutComponent(this.multiplayerScreen.getPanel(), PLAYER_SCREEN);
    this.contentWrapper.add(playersScreen.getPanel());

    class ResultsScreenListener implements EventListener {
      private CardLayout c;

      public ResultsScreenListener(CardLayout c) {
        this.c = c;
      }

      public void actionPerformed(Event e) {
        if (e.EVENT_TYPE == Event.NEXT_TEST) {
          // Reset the test
          stats.resetTest();
          
          // Switch what's displayed
          if (networkManager.getConnectionStatus()) {
            c.show(contentWrapper, PLAYER_SCREEN);
          } else {
            c.show(contentWrapper, WORD_DISPLAY);
            wordDisplay.render();
          }
        }
      }
    }
    this.resultsScreen.addEventListener(new ResultsScreenListener(c));
    
    // Event listener for when test is complete
    class StatsEventListener implements EventListener {
      private CardLayout c;

      public StatsEventListener(CardLayout c) {
        this.c = c;
      }

      public void actionPerformed(Event e) {
        // Save a new run to stats
        if (e.EVENT_TYPE == Event.TEST_FINISHED) {
          // Display the results screen 
          c.show(contentWrapper, RESULTS_SCREEN);
        }
      }
    }
    this.stats.addEventListener(new StatsEventListener(c));
    this.panel.add(this.contentWrapper);
  }

  public JPanel getPanel() {
    return this.panel;
  }
  
  public void render() {}
}