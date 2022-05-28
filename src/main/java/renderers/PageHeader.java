package renderers;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import assets.Assets;
import helpers.Constants;
import helpers.CustomComponent;
import helpers.Event;
import helpers.EventListener;

/**
 * Page header with a title and an optional back button.
 */
public class PageHeader implements CustomComponent {
  public static final int MAX_LISTENERS = 5;
  
  private JPanel panel;

  private EventListener[] listeners;
  private int numListeners;

  public PageHeader(String title, boolean showBackButton) {
    this.panel = new JPanel();
    this.panel.setLayout(new FlowLayout(FlowLayout.LEADING));

    this.panel.setBackground(Constants.BACKGROUND);

    this.listeners = new EventListener[MAX_LISTENERS];
    this.numListeners = 0;
    
    if (showBackButton) {
      class ButtonListener implements EventListener {
        public void actionPerformed(Event e) {
          for (EventListener listener : listeners) {
            if (listener != null) {
              listener.actionPerformed(e);
            }
          }
        }
      }

      IconButton backButton = new IconButton(Assets.scaleIcon(Assets.BACK, 8), Event.BACK_BUTTON_PRESS);
      backButton.addEventListener(new ButtonListener());
      this.panel.add(backButton.getPanel());
    }
    JLabel titleLabel = new JLabel(title);
    titleLabel.setFont(Assets.TYPING_FONT);
    titleLabel.setForeground(Constants.EMPHASIS_COLOR);
    this.panel.add(titleLabel);

  }

  public JPanel getPanel() {
    return this.panel;
  }

  /**
   * Add an event listener.
   * @param listener
   */
  public void addEventListener(EventListener listener) {
    if (this.numListeners < MAX_LISTENERS) {
      this.listeners[numListeners++] = listener;
    }
  }
  
  public void render() {}
}
