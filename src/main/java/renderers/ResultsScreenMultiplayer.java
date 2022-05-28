package renderers;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.awt.*;

import assets.Assets;

import helpers.Constants;
import helpers.CustomComponent;
import helpers.Event;
import helpers.EventListener;
import helpers.NetworkManager;


public class ResultsScreenMultiplayer implements CustomComponent {
  public static final String PAGE_TITLE = "Results";
  public static final int MAX_LISTENERS = 5;

  private EventListener[] listeners;
  private int numListeners;

  private JPanel panel;

  private NetworkManager networkManager;

  private JLabel[] items;
  
  public ResultsScreenMultiplayer(NetworkManager networkManager) {
    this.networkManager = networkManager;
    this.listeners = new EventListener[MAX_LISTENERS];
    this.numListeners = 0;

    this.panel = new JPanel();
    this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.PAGE_AXIS));
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
    
    class NextTestListener implements EventListener {
      public void actionPerformed(Event e) {
        // Loop through event listeners and call actionPerformed
        for (EventListener listener : listeners) {
          if (listener != null) {
            listener.actionPerformed(new Event(Event.NEXT_TEST));
          }
        }
      }
    }

    // For advancing to next test
    IconButton nextTestButton = new IconButton(Assets.scaleIcon(Assets.NEXT_TEST), Event.NEXT_TEST);
    nextTestButton.addEventListener(new NextTestListener());

    JPanel nextTestWrapper = new JPanel();
    nextTestWrapper.setLayout(new FlowLayout(FlowLayout.CENTER));
    nextTestWrapper.setBackground(Constants.BACKGROUND);
    nextTestWrapper.add(nextTestButton.getPanel());

    class NetworkManagerListener implements EventListener {
      public void actionPerformed(Event e) {
        // Re-render if players updated
        if (e.EVENT_TYPE == Event.NETWORK_TEST_END) {
          render();
        }
      }
    }
    this.networkManager.addEventListener(new NetworkManagerListener());
    this.panel.add(nextTestWrapper);

    this.render();
  }

  public JPanel getPanel() {
    return this.panel;
  }

  public void render() {
    // Display stats of each player
    int labelIndex = 0;

    JSONArray players = this.networkManager.getPlayers();
    System.out.println("Rendering players");
    System.out.println(players);
    if (players == null) return;
    
    // Re-render based on players
    for (int i = players.size() - 1; i >= 0; i--) {
      if (players.get(i) != null) {
        JSONObject player = (JSONObject) players.get(i);

        String toAdd = (String) player.get("name");

        Number wpm = (Number) player.get("wpm");
        Number acc = (Number) player.get("acc");

        toAdd += ": " + wpm.intValue() + "wpm, " + acc.intValue() + "%";

        this.items[labelIndex].setText(toAdd);
        
        labelIndex++;
      }
    }
  }

  public void addEventListener(EventListener listener) {
    if (this.numListeners < MAX_LISTENERS) {
      this.listeners[this.numListeners++] = listener;
    }
  }
}
