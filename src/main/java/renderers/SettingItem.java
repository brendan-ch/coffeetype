package renderers;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import assets.Assets;

import java.awt.FlowLayout;
import java.awt.event.*;

import helpers.Constants;
import helpers.CustomComponent;
import helpers.SettingsTracker;

/**
 * Settings item that renders a setting
 * and updates a `SettingsTracker` object.
 */
public class SettingItem implements CustomComponent {
  // Settings item types
  // public static final int NUMBER = 0;
  public static final int TOGGLE = 1;

  private JPanel panel;
  private JLabel label;

  private SettingsTracker settings;

  /**
   * Setting value - should be a wrapped Integer or Boolean.
   */
  private Object value;
  /**
   * Key of the setting in `SettingsTracker`.
   */
  private String key;

  /**
   * Setting item that renders a setting and updates a `SettingsTracker` object.
   * @param settings
   * @param settingsKey
   */
  public SettingItem(
    SettingsTracker settings,
    String settingsKey,
    String settingsLabel,
    int type
  ) {
    this.settings = settings;
    this.key = settingsKey;
    
    this.panel = new JPanel();
    this.panel.setBackground(Constants.BACKGROUND);
    this.panel.setLayout(new FlowLayout(FlowLayout.LEADING));
    ((FlowLayout) this.panel.getLayout()).setHgap(30);
    
    this.label = new JLabel(settingsLabel);
    this.label.setFont(Assets.TYPING_FONT);
    this.label.setForeground(Constants.EMPHASIS_COLOR);
    this.panel.add(this.label);
    
    this.value = settings.getSetting(settingsKey);
    // if (type == NUMBER) {
      // // Add a JTextField
      // JTextField textField = new JTextField();
      // textField.setText("" + this.value);

      // class NumboxListener implements ActionListener {
      //   public void actionPerformed(ActionEvent e) {
      //     // Update the respective setting
      //     settings.updateSetting(settingsKey, Integer.parseInt(textField.getText()));
      //   }
      // }
      // textField.addActionListener(new NumboxListener());

      // this.panel.add(textField);
    // } else {
      // Add a checkbox
    JCheckBox checkBox = new JCheckBox();
    checkBox.setBackground(Constants.BACKGROUND);
    checkBox.setIcon(Assets.scaleIcon(Assets.TOGGLE_SWITCH_OFF, 3));
    checkBox.setSelectedIcon(Assets.scaleIcon(Assets.TOGGLE_SWITCH_ON, 3));
    checkBox.setSelected((Boolean) settings.getSetting(settingsKey));

    class CheckboxListener implements ActionListener {
      private SettingsTracker settings;
      private String settingsKey;

      public CheckboxListener(SettingsTracker settings, String settingsKey) {
        this.settings = settings;
        this.settingsKey = settingsKey;
      }
      
      public void actionPerformed(ActionEvent e) {
        // Toggle the respective setting
        settings.updateSetting(settingsKey, !((Boolean) value));
        render();
      }
    }
    checkBox.addActionListener(new CheckboxListener(settings, settingsKey));

    this.panel.add(checkBox);
    // }
  }

  public JPanel getPanel() {
    return this.panel;
  }

  public void render() {
    // Set the updated value
    this.value = settings.getSetting(this.key);
  }
}
