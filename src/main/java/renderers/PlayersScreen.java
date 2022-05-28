package renderers;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import assets.Assets;
import helpers.Constants;
import helpers.CustomComponent;
import helpers.Event;
import helpers.EventListener;
import helpers.NetworkManager;

// Displays a list of players and their stats
public class PlayersScreen implements CustomComponent {
  public static final String PAGE_TITLE = "Players";
  public static final int COUNTDOWN_VALUE = 5;
  public static final int MAX_LISTENERS = 5;

  private JPanel panel;
  private JLabel[] items;
  private NetworkManager networkManager;

  // Countdown timer until the test starts
  private Timer countdown;
  private int countdownValue;

  private JPanel buttonWrapper;

  private EventListener[] listeners;
  private int numListeners;

  public PlayersScreen(NetworkManager networkManager) {
    this.listeners = new EventListener[MAX_LISTENERS];
    this.numListeners = 0;
    
    this.networkManager = networkManager;
    this.countdownValue = COUNTDOWN_VALUE;

    class TimerListener implements ActionListener {
      public void actionPerformed(ActionEvent e) {
        // Decrease the countdown
        countdownValue--;

        if (countdownValue <= 0) {
          // Fire event
          fireEventListeners(new Event(Event.NETWORK_TEST_START_DELAY_END));

          // Reset the countdown
          // Switch to the words screen
          resetCountdown();
        }
      }
    }

    int delay = 1000;
    this.countdown = new Timer(delay, new TimerListener());

    // Re-render when player list changed
    class NetworkManagerListener implements EventListener {
      public void actionPerformed(Event e) {
        if (e.EVENT_TYPE == Event.NETWORK_TEST_START) {
          // Begin countdown
          countdown.start();
        } else if (e.EVENT_TYPE == Event.NETWORK_PLAYERS_UPDATE || e.EVENT_TYPE == Event.NETWORK_STATUS_CHANGE) {
          // System.out.println("Re-rendering player list");
          // Re-render player list
          render();
        }
      }
    }

    this.networkManager.addEventListener(new NetworkManagerListener());

    // Display a list of players
    this.panel = new JPanel();
    this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.Y_AXIS));
    this.panel.setBorder(new EmptyBorder(0, 0, 0, 0));
    this.panel.setBackground(Constants.BACKGROUND);

    PageHeader header = new PageHeader(PAGE_TITLE, false);
    this.panel.add(header.getPanel());

    this.items = new JLabel[NetworkManager.MAX_PLAYERS];

    for (int i = 0; i < this.items.length; i++) {
      JPanel wrapper = new JPanel();
      wrapper.setBackground(Constants.BACKGROUND);
      wrapper.setLayout(new FlowLayout(FlowLayout.LEADING));
      
      this.items[i] = new JLabel(" ");
      this.items[i].setFont(Assets.TYPING_FONT);
      this.items[i].setLayout(new FlowLayout(FlowLayout.LEADING));
      
      this.items[i].setBackground(Constants.BACKGROUND);
      this.items[i].setForeground(Constants.EMPHASIS_COLOR);
      this.items[i].setAlignmentX(Component.LEFT_ALIGNMENT);
      wrapper.add(this.items[i]);

      this.panel.add(wrapper);
    }

    this.buttonWrapper = new JPanel();
    this.buttonWrapper.setLayout(new FlowLayout(FlowLayout.CENTER));
    this.buttonWrapper.setBackground(Constants.BACKGROUND);
    this.panel.add(buttonWrapper);

    // class HeaderListener implements EventListener {
    //   public void actionPerformed(Event e) {
    //     for (EventListener listener : listeners) {
    //       if (listener != null) {
    //         listener.actionPerformed(e);
    //       }
    //     }
    //   }
    // }
    // header.addEventListener(new HeaderListener());

    this.render();
  }

  public JPanel getPanel() {
    return this.panel;
  }
  
  public void render() {
    int labelIndex = 0;

    JSONArray players = this.networkManager.getPlayers();
    System.out.println(players);
    if (players == null) return;
    
    // Re-render based on players
    for (int i = players.size() - 1; i >= 0; i--) {
      if (players.get(i) != null) {
        this.items[labelIndex].setText((String) ((JSONObject) players.get(i)).get("name"));

        // String toSet = players.get(i).dateStr + " - " + tests[i].wpm + " WPM, " + tests[i].accuracy + "%";
        // if (tests[i].hardMode) {
        //   toSet += ", hard mode";
        // }
        
        // Render test information
        // this.items[labelIndex].setText(toSet);
        
        labelIndex++;
      }
    }

    // Render button panel depending on whether player is host
    // Remove item
    Component[] components = this.buttonWrapper.getComponents();
    for (Component component : components) {
      this.buttonWrapper.remove(component);
    }

    // Add item
    boolean isHost = networkManager.getIsHost();
    if (isHost) {
      // Add start button
      JButton startButton = new JButton("Start");

      class StartButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
          // Start the test
          networkManager.startTest();
        }
      }
      startButton.addActionListener(new StartButtonListener());
      startButton.setFocusable(false);

      buttonWrapper.add(startButton);
    } else {
      // Add waiting text
      JLabel waitingText = new JLabel("Waiting for host to start...");
      waitingText.setForeground(Constants.EMPHASIS_COLOR);
      waitingText.setBackground(Constants.BACKGROUND);
      buttonWrapper.add(waitingText);
    }
  }

  private void resetCountdown() {
    // Stop the timer
    this.countdown.stop();
    this.countdownValue = COUNTDOWN_VALUE;
  }

  public void addEventListener(EventListener listener) {
    if (numListeners < MAX_LISTENERS) {
      this.listeners[numListeners++] = listener; 
    }
  }

  private void fireEventListeners(Event e) {
    // Pass events up
    for (EventListener listener : listeners) {
      if (listener != null) {
        listener.actionPerformed(e);
      }
    }
  }
}
