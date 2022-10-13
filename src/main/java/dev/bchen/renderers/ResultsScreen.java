package dev.bchen.renderers;

import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import dev.bchen.assets.Assets;
import dev.bchen.helpers.Constants;
import dev.bchen.helpers.CustomComponent;
import dev.bchen.helpers.Event;
import dev.bchen.helpers.EventListener;
import dev.bchen.helpers.StatsTracker;

/**
 * Displays the results after a test.
 */
public class ResultsScreen implements CustomComponent {
  public static final String PAGE_TITLE = "Results";
  public static final int RESULTS_WIDTH = 100;
  public static final int RESULTS_HEIGHT = 60;

  public static final int MAX_LISTENERS = 5;
  
  private JPanel panel;
  private EventListener[] listeners;
  private int numListeners;

  private StatsTracker stats;

  private ResultItem wpmResult;
  private ResultItem accuracyResult;
  private ResultItem timeResult;

  public ResultsScreen(StatsTracker stats) {
    this.listeners = new EventListener[MAX_LISTENERS];
    this.numListeners = 0;
    
    this.stats = stats;
    this.panel = new JPanel();
    this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.PAGE_AXIS));
    // this.panel.setLayout(new FlowLayout(FlowLayout.CENTER));
    // this.panel.setAlignmentY(Component.CENTER_ALIGNMENT);
    // this.panel.setAlignmentX(Component.CENTER_ALIGNMENT);
    this.panel.setBackground(Constants.BACKGROUND);

    PageHeader header = new PageHeader(PAGE_TITLE, false);
    this.panel.add(header.getPanel());

    // Row of ResultItem's with information
    JPanel resultItems = new JPanel(new FlowLayout(FlowLayout.CENTER));
    // resultItems.setLayout(new BoxLayout(this.panel, BoxLayout.X_AXIS));
    resultItems.setBackground(Constants.BACKGROUND);

    // Read stats and render some text
    this.wpmResult = new ResultItem();
    resultItems.add(this.wpmResult.getPanel());

    this.accuracyResult = new ResultItem();
    resultItems.add(this.accuracyResult.getPanel());

    this.timeResult = new ResultItem();
    resultItems.add(this.timeResult.getPanel());

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

    this.panel.add(resultItems);
    
    // Wrapper for the button
    nextTestWrapper.add(nextTestButton.getPanel());
    this.panel.add(nextTestWrapper);

    // Add listener to re-render the screen when test is done
    class TimerListener implements EventListener {
      public void actionPerformed(Event e) {
        if (e.EVENT_TYPE == Event.TEST_FINISHED) {
          // Re-render
          render();
        }
      }
    }

    this.stats.addEventListener(new TimerListener());

    this.render();
  }
  
  public JPanel getPanel() {
    return this.panel;
  }
  public void render() {
    this.panel.requestFocusInWindow();
    
    int wpm = this.stats.getWPM();
    int acc = this.stats.getAcc();
    int time = StatsTracker.TIMER_DEFAULT;
    
    this.wpmResult.setValues("wpm", "" + wpm);
    this.accuracyResult.setValues("acc%", "" + acc);
    this.timeResult.setValues("time", "" + time);
  }

  public void addEventListener(EventListener listener) {
    if (numListeners < MAX_LISTENERS) {
      this.listeners[numListeners++] = listener; 
    }
  }
}