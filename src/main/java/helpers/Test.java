package helpers;

import java.util.Date;
import java.text.DateFormat;

/**
 * Represents data from an individual test.
 */
public class Test {
  public final int wpm;
  public final double accuracy;
  public final int time;

  public final boolean hardMode;

  public final Date timestamp;
  public final String dateStr;

  public final int testNumber;
  public static int nextNumber = 1;

  public Test(int wpm, double accuracy, int time, boolean hardMode) {
    this.wpm = wpm;
    this.accuracy = accuracy;
    this.time = time;
    this.hardMode = hardMode;

    this.timestamp = new Date();
    this.dateStr = DateFormat.getDateInstance(DateFormat.SHORT).format(this.timestamp)
      + " " + DateFormat.getTimeInstance(DateFormat.SHORT).format(this.timestamp);

    this.testNumber = nextNumber++;
  }
}