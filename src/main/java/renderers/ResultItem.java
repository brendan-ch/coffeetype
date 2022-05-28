package renderers;

import javax.swing.*;
import javax.swing.border.*;

import assets.Assets;

import java.awt.*;

import helpers.*;

/**
 * Render a single result item.
 */
public class ResultItem implements CustomComponent {
  public static final Color BACKGROUND_COLOR = new Color(53, 53, 53);

  public static final String BEFORE = "<html><div style=\"color: white; display: flex; flex-direction: column;\">";
  public static final String AFTER = "</div></html>";
  
  private JPanel panel;
  
  private JLabel title;

  private String titleStr;
  private String valueStr;

  public ResultItem() {
    this.panel = new JPanel();
    this.panel.setBackground(BACKGROUND_COLOR);
    this.panel.setBorder(new EmptyBorder(10, 10, 10, 10));

    this.titleStr = "";
    this.title = new JLabel("");
    // this.title.setFont(new Font("RobotoMono-Regular.ttf", Font.TRUETYPE_FONT));
    this.panel.add(this.title);

    // this.valueStr = "";
    // this.value = new JLabel("");
    // this.panel.add(this.value);

    this.render();
  }
  
  public ResultItem(String title, String value) {
    this.panel = new JPanel();
    this.panel.setBackground(BACKGROUND_COLOR);
    this.panel.setBorder(new EmptyBorder(10, 10, 10, 10));

    this.title = new JLabel(title);
    this.title.setFont(Assets.TYPING_FONT);
    this.panel.add(this.title);

    // this.value = new JLabel(value);
    // this.panel.add(this.value);
  }

  public JPanel getPanel() {
    return this.panel;
  }

  // Update the values of the result item, and re-render
  public void setValues(String title, String value) {
    this.titleStr = title;
    this.valueStr = value;

    this.render();
  }

  // Re-render
  public void render() {
    this.title.setText(BEFORE + "<p>" + this.titleStr + "</p><p style=\"font-size: 30px\">" + this.valueStr + "</p>" + AFTER);
    // this.value.setText(BEFORE + "<p>" + this.valueStr + "</p>" + AFTER);
  }
}