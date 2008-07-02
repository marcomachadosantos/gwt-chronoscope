/**
 * 
 */
package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.util.MathUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * A filterable always-sorted set of {@link ZoomInterval} objects, which
 * provides the model for the zoom links in the legend axis.
 * 
 * @author chad takahashi &lt;chad@timepedia.org&gt;
 */
public class ZoomIntervals implements Iterable<ZoomInterval> {
  private Collection<ZoomInterval> intervals;
  private double minInterval;
  private double maxInterval;

  public ZoomIntervals() {
    clearFilter();
    intervals = new TreeSet<ZoomInterval>();
  }
  
  public boolean add(ZoomInterval zoomInterval) {
    return this.intervals.add(zoomInterval);
  }

  /**
   * Applies the specified filter criteria to this container, which is realized
   * during element iteration.
   * 
   * @param timeStart - the earliest timestamp across all datasets.
   * @param timeEnd - the latest timestamp across all datasets.
   * @param minInterval - the smallest time interval across all datasets.
   */
  public void applyFilter(double timeStart, double timeEnd, double minInterval) {
    if (timeStart >= timeEnd) {
      throw new IllegalArgumentException("timeSmart >= timeEnd: " + timeStart
          + ", " + timeEnd);
    }
    this.maxInterval = timeEnd - timeStart;
    if (minInterval > maxInterval) {
      throw new IllegalArgumentException("minInterval > maxInterval: "
          + minInterval + ", " + maxInterval);
    }
    this.minInterval = minInterval;
  }
  
  /**
   * Clears all filter criteria.
   */
  public void clearFilter() {
    this.minInterval = 0;
    this.maxInterval = Double.MAX_VALUE;
  }
  
  /**
   * Iterates over the filtered {@link ZoomInterval} elements in this container.
   * 
   * @see #applyFilter(double, double, double)
   */
  public Iterator<ZoomInterval> iterator() {
    ArrayList<ZoomInterval> l = new ArrayList<ZoomInterval>(intervals.size());
    for (ZoomInterval zoom : intervals) {
      if (MathUtil.isBounded(zoom.getInterval(), minInterval, maxInterval)) {
        l.add(zoom);
      }
    }
    return l.iterator();
  }

}
