package org.timepedia.chronoscope.client.render.domain;

import org.timepedia.chronoscope.client.util.TimeUnit;
import org.timepedia.chronoscope.client.util.date.ChronoDate;

public class YearsTickFormatter extends TickFormatter {

  public YearsTickFormatter() {
    super("XXXX");
    this.superFormatter = null;
    this.subFormatter = new MonthsTickFormatter(this);
    // TODO: Really need to define a millennium formatter, and so on... 
    this.possibleTickSteps = 
      new int[] {1, 5, 10, 20, 25, 50, 100, 250, 500, 1000, 2500, 5000, 10000, 100000};
    this.tickInterval = TimeUnit.YEAR;
  }

  public String formatRelativeTick(ChronoDate d) {
    return String.valueOf(d.getYear());
  }

  public int getSubTickStep(int primaryTickStep) {
    int x = primaryTickStep;
    if (x == 1) {
      return 4;
    }
    else if (x < 10) {
      return x;
    }
    else if (x == 10) {
      return 2;
    }
    else if (x == 20) {
      return 8;
    }
    else if (x == 25) {
      return 1;
    }
    else if (x == 50) {
      return 5;
    }
    else if (x == 100) {
      return 4;
    }
    else {
      // Catch-all: remaining tick steps will be (they better be) multiples of 2. 
      return 2;
    }
  }
  
}