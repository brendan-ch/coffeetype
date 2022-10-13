package dev.bchen.helpers;

import javax.swing.*;

/**
 * Wrapper around a JComponent, with a rendering method.
 */
public interface CustomComponent {
  public JComponent getPanel();
  public void render();
}