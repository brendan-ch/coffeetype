package dev.bchen.assets;


import java.awt.Font;
import java.awt.Image;

import javax.swing.ImageIcon;


/**
 * Utility class to host assets and methods to
 * scale/format them.
 */
public class Assets {
  public static final ImageIcon LOGO = new ImageIcon(Assets.class.getResource("/assets/coffeetype-logo.png"));
  public static final ImageIcon LOGO_WITH_TEXT = new ImageIcon(Assets.class.getResource("/assets/coffeetype-logo-with-text.png"));

  public static final ImageIcon RESTART_TEST = new ImageIcon(Assets.class.getResource("/assets/restart-test.png"));
  public static final ImageIcon NEXT_TEST = new ImageIcon(Assets.class.getResource("/assets/next-test.png"));
  public static final ImageIcon SETTINGS = new ImageIcon(Assets.class.getResource("/assets/settings.png"));
  public static final ImageIcon HISTORY = new ImageIcon(Assets.class.getResource("/assets/history.png"));
  public static final ImageIcon BACK = new ImageIcon(Assets.class.getResource("/assets/back.png"));
  public static final ImageIcon INVITE = new ImageIcon(Assets.class.getResource("/assets/invite.png"));
  public static final ImageIcon EXIT = new ImageIcon(Assets.class.getResource("/assets/exit.png"));
  
  public static final ImageIcon TOGGLE_SWITCH_ON = new ImageIcon(Assets.class.getResource("/assets/toggle-switch-on.png"));
  public static final ImageIcon TOGGLE_SWITCH_OFF = new ImageIcon(Assets.class.getResource("/assets/toggle-switch-off.png"));

  public static final Font DISPLAY_FONT = new Font("/assets/display-font.ttf", Font.TRUETYPE_FONT, 15);
  public static final Font DISPLAY_FONT_BIG = new Font("/assets/display-font.ttf", Font.TRUETYPE_FONT, 35);
  public static final Font TYPING_FONT = new Font("Monospaced", Font.TRUETYPE_FONT, 15);
  public static final Font TYPING_FONT_BIG = new Font("Monospaced", Font.TRUETYPE_FONT, 35);

  /**
   * Flexible scaling method for icons.
   * @param iconToScale
   * @param divideBy
   * @return
   */
  public static ImageIcon scaleIcon(ImageIcon iconToScale, int divideBy) {;
    Image newIcon = iconToScale.getImage().getScaledInstance(iconToScale.getIconWidth() / divideBy, iconToScale.getIconHeight() / divideBy, Image.SCALE_SMOOTH);
    return new ImageIcon(newIcon);
  }

  /**
   * Convenience overload, divides size by 6. Suitable for most icons.
   */
  public static ImageIcon scaleIcon(ImageIcon iconToScale) {
    return Assets.scaleIcon(iconToScale, 6);
  }
}