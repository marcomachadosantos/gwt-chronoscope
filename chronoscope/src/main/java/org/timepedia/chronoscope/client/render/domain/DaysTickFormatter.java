package org.timepedia.chronoscope.client.render.domain;

import org.timepedia.chronoscope.client.util.TimeUnit;
import org.timepedia.chronoscope.client.util.date.ChronoDate;

public class DaysTickFormatter extends TickFormatter {

  public DaysTickFormatter(TickFormatter superFormatter) {
    super("00-Xxx"); // e.g. "22-Aug"
    this.superFormatter = superFormatter;
    this.subFormatter = new HoursTickFormatter(this);
    this.possibleTickSteps = new int[] {1, 2, 7, 14};
    this.tickInterval = TimeUnit.DAY;
  }

  public String formatRelativeTick(ChronoDate d) {
    return dateFormat.dayAndMonth(d);
  }

  public int getSubTickStep(int primaryTickStep) {
    switch (primaryTickStep) {
      case 1:
        // Divide labeled day ticks into 4 subticks
        return 4;
      default:
        // If labeled ticks are more than 1 day part, then place
        // subticks at 1 day intervals.
        return primaryTickStep;
    }
  }

  /**
   * Date incrementing for {@link TimeUnit#DAY} requires special handling
   * in order to ensure stable tick label rendering.  Other formatters don't 
   * require this because their quantized intervals are all factors of N, 
   * where N is the number of time units that compose the parent time unit. 
   */
  @Override
  public int incrementDate(ChronoDate d, int numTimeUnits) {
    final int dayOfMonth = d.getDay();
    boolean doSkipToNextMonth = false;
    switch (numTimeUnits) {
      case 2:
        int daysInMonth = d.getDaysInMonth();
        boolean isEven = (daysInMonth % 2 == 0);
        int dayThreshold = daysInMonth - (isEven ? 1 : 2);
        doSkipToNextMonth = (dayOfMonth >= dayThreshold);
        break;
      case 7:
        doSkipToNextMonth = dayOfMonth > (7 * 3); 
        break;
      case 14:
        doSkipToNextMonth = dayOfMonth >= 14; 
        break;
    }

    int actualIncrement;
    if (doSkipToNextMonth) {
      actualIncrement = gotoFirstOfNextMonth(d);
    }
    else {
      actualIncrement = numTimeUnits;
      d.add(this.tickInterval, numTimeUnits);
    }
    
    return actualIncrement;
  }

  @Override
  public ChronoDate quantizeDate(double dO, int idealTickStep) {
    ChronoDate d = ChronoDate.get(dO).truncate(this.tickInterval);
    final int dayIndex = d.getDay() - 1; // convert to 0-based day
    int normalizedValue = 1 + quantize(dayIndex, idealTickStep);
    d.set(this.tickInterval, normalizedValue);
    return d;
  }

  /**
   * Increments the specified date to the first day of the following month.
   * For example, if d represents the date 'April 19, 2005', then 
   * gotoFirstOfNextMonth(d) would modify d to be May 1, 2005.
   * 
   * @return the number of days that were incremented in order to arrive at the
   * first day of the following month.
   */
  private static int gotoFirstOfNextMonth(ChronoDate d) {
    int actualIncrement = d.getDaysInMonth() - d.getDay() + 1;
    d.set(TimeUnit.DAY, 1);
    d.add(TimeUnit.MONTH, 1);
    //System.out.println("TESTING: day=" + d.getDay() + "; actualIncrement=" + actualIncrement);
    return actualIncrement;
  }
  
}
