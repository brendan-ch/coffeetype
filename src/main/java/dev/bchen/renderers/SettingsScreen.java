package dev.bchen.renderers;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import dev.bchen.helpers.Constants;
import dev.bchen.helpers.CustomComponent;
import dev.bchen.helpers.Event;
import dev.bchen.helpers.EventListener;
import dev.bchen.helpers.SettingsTracker;

/**
 * Renders settings options.
 */
public class SettingsScreen implements CustomComponent {
  public static final String PAGE_TITLE = "Settings";
  public static final int MAX_LISTENERS = 10;

  private JPanel panel;
  private SettingsTracker settings;

  private SettingItem[] items;

  private EventListener[] listeners;
  private int numListeners;

  public SettingsScreen(SettingsTracker settings) {
    this.settings = settings;

    this.panel = new JPanel();
    this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.PAGE_AXIS));
    this.panel.setBorder(new EmptyBorder(0, 0, 0, 0));
    this.panel.setBackground(Constants.BACKGROUND);

    PageHeader header = new PageHeader(PAGE_TITLE, true);

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

    this.panel.add(header.getPanel());

    this.listeners = new EventListener[MAX_LISTENERS];
    this.numListeners = 0;

    String[] keys = SettingsTracker.getKeys();
    this.items = new SettingItem[keys.length];

    String[] labels = SettingsTracker.getLabels();

    // Iterate through keys
    for (int i = 0; i < keys.length; i++) {
      // int type = SettingItem.NUMBER;

      // if (settings.getSetting(keys[i]) instanceof Boolean) {
      //   type = SettingItem.TOGGLE;
      // }

      // Create a new SettingItem
      SettingItem newItem = new SettingItem(this.settings, keys[i], labels[i], SettingItem.TOGGLE);
      items[i] = newItem;

      this.panel.add(newItem.getPanel());
    }

    render();
  }

  public JPanel getPanel() {
    return this.panel;
  }

  public void addEventListener(EventListener listener) {
    if (this.numListeners < MAX_LISTENERS) {
      this.listeners[numListeners++] = listener;
    }
  }

  public void render() {
  }
}
