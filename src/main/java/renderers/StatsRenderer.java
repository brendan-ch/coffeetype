package renderers;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.*;

import assets.Assets;
import helpers.*;

/**
 * Renders the stats (e.g. wpm, accuracy, time remaining).
 */
public class StatsRenderer implements CustomComponent {
  public static final String BEFORE = "<html>"
      + "<div style=\"color: white; display: flex; flex-direction: column; flex: 1\">";
  public static final String AFTER = "</div></html>";
  
  private JPanel panel;
  private JLabel headerText;
  private JLabel statsText;
  
  private StatsTracker stats;
  private SettingsTracker settings;
  
  public StatsRenderer(StatsTracker stats, SettingsTracker settings) {
    this.stats = stats;
    this.settings = settings;

    this.panel = new JPanel();
    this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.Y_AXIS));
    this.panel.setBackground(Constants.ALT_LIST_COLOR);
    // this.panel.setAlignmentX(Component.CENTER_ALIGNMENT);

    this.headerText = new JLabel("stats");
    headerText.setFont(Assets.TYPING_FONT);
    headerText.setForeground(Constants.TYPING_TEXT_COLOR);

    // Will be rendered in render() method
    this.statsText = new JLabel("");
    this.statsText.setFont(Assets.TYPING_FONT);
    this.panel.add(headerText);
    this.panel.add(statsText);

    // Add listener for stats
    class StatsListener implements EventListener {
      public void actionPerformed(Event e) {
        if (e.EVENT_TYPE == Event.TIMER_TICK || e.EVENT_TYPE == Event.TEST_FINISHED || e.EVENT_TYPE == Event.RESTART_TEST) {
          // Render info
          render();
        }
      }
    }

    this.stats.addEventListener(new StatsListener());

    class SettingsListener implements EventListener {
      public void actionPerformed(Event e) {
        if (e.EVENT_TYPE == Event.SETTING_CHANGED) {
          // Re-render
          render();
        }
      }
    }
    this.settings.addEventListener(new SettingsListener());

    this.render();
  }

  public JPanel getPanel() {
    return this.panel;
  }

  /**
   * Re-render based on properties in the StatsTracker object.
   */
  public void render() {
    // Re-set info display
    String info = "";

    // Read time and call getWPM method
    int wpm = this.stats.getWPM();
    int acc = this.stats.getAcc();
    int timeRemaining = this.stats.getTimeRemaining();

    int numLineBreaks = 0;
    
    if ((Boolean) this.settings.getSetting(SettingsTracker.WPM_ENABLED)) {
      info += "<p>" + wpm + " wpm";
      info += "</p>";
    } else {
      // info += "<p> </p>";
      // info += "<br>";
      numLineBreaks++;
    }
    if ((Boolean) this.settings.getSetting(SettingsTracker.ACC_ENABLED)) {
      info += "<p>" + acc + "%</p>";
    } else {
      // info += "<p> </p>";
      // info += "<br>";
      numLineBreaks++;
    }
    if ((Boolean) this.settings.getSetting(SettingsTracker.TIME_ENABLED)) {
      info += "<p>" + timeRemaining + "s</p>";
    } else {
      // info += "<p> </p>";
      // info += "<br>";
      numLineBreaks++;
    }

    // Change the header display if only one item is displayed
    if (numLineBreaks == 2 && (Boolean) this.settings.getSetting(SettingsTracker.WPM_ENABLED)) {
      info = "<p>" + wpm + "</p>";
      headerText.setText("wpm");
    } else if (numLineBreaks == 2 && (Boolean) this.settings.getSetting(SettingsTracker.ACC_ENABLED)) {
      headerText.setText("acc%");
    } else if (numLineBreaks == 2 && (Boolean) this.settings.getSetting(SettingsTracker.TIME_ENABLED)) {
      headerText.setText("time");
    }

    // Clear the header display if no items displayed
    if (numLineBreaks == 3) {
      // Remove stats header
      headerText.setText("<html><br></html>");
    } else if (numLineBreaks != 2) {
      headerText.setText("stats");
    }

    // Supersize the text if only one item is displayed
    if (numLineBreaks != 2) {
      this.statsText.setFont(Assets.TYPING_FONT);
      for (int i = 0; i < numLineBreaks; i++) {
        info += "<br>";
      }
      // Prevent layout shift
      this.statsText.setBorder(new EmptyBorder(0, 0, 0, 0));
    } else {
      this.statsText.setFont(Assets.TYPING_FONT_BIG);
      this.statsText.setBorder(new EmptyBorder(0, 0, 12, 0));
    }

    this.statsText.setText(BEFORE + info + AFTER);
  }
}
