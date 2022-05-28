package renderers;

import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.*;

import helpers.*;
import helpers.Event;

/**
 * Represents an icon button of a fixed size.
 */
public class IconButton implements CustomComponent {
  public static final int MAX_LISTENERS = 10;

  // Event that gets emitted when the button is clicked
  public final int EMITTED_EVENT;
  
  private JButton button;
  private EventListener[] listeners;
  private int numListeners;
  
  public IconButton(ImageIcon icon, int emittedEvent) {
    this.button = new JButton(icon);
    this.listeners = new EventListener[MAX_LISTENERS];
    this.numListeners = 0;
    this.EMITTED_EVENT = emittedEvent;

    this.button.setBackground(Constants.BACKGROUND);
    this.button.setBorder(new EmptyBorder(0, 0, 0, 0));

    class ButtonListener implements ActionListener {
      public void actionPerformed(ActionEvent e) {
        // Run event listeners
        for (EventListener listener : listeners) {
          if (listener != null) {
            listener.actionPerformed(new Event(EMITTED_EVENT));
          }
        }
      }
    }

    this.button.addActionListener(new ButtonListener());
    this.button.setFocusable(false);
  }

  public void addEventListener(EventListener listener) {
    if (numListeners < MAX_LISTENERS) {
      this.listeners[numListeners++] = listener;
    }
  }

  public JButton getPanel() {
    return this.button;
  }

  public void render() {}
}