package dev.bchen.helpers;

/**
 * Represents a custom version of an `ActionListener`
 * that uses custom `Event` objects.
 */
public interface EventListener {
  public void actionPerformed(Event e);
}