package dev.bchen.renderers;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.border.EmptyBorder;

import dev.bchen.assets.Assets;
import dev.bchen.helpers.Constants;
import dev.bchen.helpers.CustomComponent;
import dev.bchen.helpers.Event;
import dev.bchen.helpers.EventListener;
import dev.bchen.helpers.StatsTracker;
import dev.bchen.helpers.Test;

import java.awt.Component;
import java.awt.FlowLayout;

/**
 * Display user's history of tests.
 */
public class HistoryScreen implements CustomComponent {
  public static final String PAGE_TITLE = "History";
  public static final int MAX_LISTENERS = 10;
  
  private JPanel panel;

  private EventListener[] listeners;
  private int numListeners;

  private StatsTracker stats;

  private JLabel[] items;

  public HistoryScreen(StatsTracker stats) {

    this.stats = stats;
    this.numListeners = 0;
    this.listeners = new EventListener[MAX_LISTENERS];
    
    this.panel = new JPanel();
    this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.Y_AXIS));
    this.panel.setBorder(new EmptyBorder(0, 0, 0, 0));
    this.panel.setBackground(Constants.BACKGROUND);

    PageHeader header = new PageHeader(PAGE_TITLE, true);
    this.panel.add(header.getPanel());

    class HeaderListener implements EventListener {
      public void actionPerformed(Event e) {
        for (EventListener listener : listeners) {
          if (listener != null) {
            listener.actionPerformed(e);
          }
        }
      }
    }
    header.addEventListener(new HeaderListener());

    class StatsListener implements EventListener {
      public void actionPerformed(Event e) {
        // Re-render when test finished
        if (e.EVENT_TYPE == Event.TEST_FINISHED) {
          render();
        }
      }
    }
    this.stats.addEventListener(new StatsListener());

    this.items = new JLabel[StatsTracker.MAX_TESTS];
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

    this.items[0].setText("Past tests will appear here.");

    this.render();
  }
  
  public JPanel getPanel() {
    return this.panel;
  }

  public void render() {
    Test[] tests = this.stats.getTests();
    int labelIndex = 0;
    
    // Re-render based on stats
    for (int i = tests.length - 1; i >= 0; i--) {
      if (tests[i] != null) {
        String toSet = tests[i].dateStr + " - " + tests[i].wpm + " WPM, " + tests[i].accuracy + "%";
        if (tests[i].hardMode) {
          toSet += ", hard mode";
        }
        
        // Render test information
        this.items[labelIndex].setText(toSet);
        
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
