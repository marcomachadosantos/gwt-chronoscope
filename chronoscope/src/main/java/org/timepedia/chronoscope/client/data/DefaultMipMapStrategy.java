package org.timepedia.chronoscope.client.data;

import com.google.gwt.core.client.GWT;

import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.chronoscope.client.util.Array2D;
import org.timepedia.chronoscope.client.util.JavaArray2D;
import org.timepedia.chronoscope.client.util.MathUtil;

/**
 * 
 * @author Chad Takahashi
 */
public abstract class DefaultMipMapStrategy implements MipMapStrategy {

  public Array2D calcMultiDomain(double[] domain) {
    ArgChecker.isGT(domain.length, 0, "domain.length");
    int numSamples = domain.length;
    int numLevels = calcNumLevels(numSamples);
    double[][] multiDomain = new double[numLevels][];

    for (int level = 0; level < numLevels; level++) {
      multiDomain[level] = new double[numSamples];
      int index = 0;
      int indexStep = 1 << level; // [1, 2, 4, 8, ...]

      for (int i = 0; i < numSamples; i++) {
        multiDomain[level][i] = domain[index];
        index += indexStep;
      }

      numSamples /= 2;
    }

    return new JavaArray2D(multiDomain);
  }

  public Array2D calcMultiRange(double[] range) {
    ArgChecker.isGT(range.length, 0, "range.length");
    int numSamples = range.length;
    int numLevels = calcNumLevels(numSamples);
    double[][] multiRange = new double[numLevels][];
    double[] prevRange = range;

    for (int level = 0; level < numLevels; level++) {
      double[] currRange = new double[numSamples];
      if (level == 0) {
        for (int i = 0; i < numSamples; i++) {
          currRange[i] = range[i];
        }
      } else {
        for (int i = 0; i < numSamples; i++) {
          int prevIndex = i * 2;
          currRange[i] = calcRangeValue(prevRange[prevIndex],
              prevRange[prevIndex + 1]);
        }
      }

      multiRange[level] = currRange;
      prevRange = currRange;
      numSamples /= 2;
    }

    return new JavaArray2D(multiRange);
  }

  protected abstract double calcRangeValue(double prev1, double prev2);

  public static final MipMapStrategy MEAN = new DefaultMipMapStrategy() {
    protected double calcRangeValue(double prev1, double prev2) {
      return (prev1 + prev2) / 2.0;
    }
  };

  public static final MipMapStrategy MAX = new DefaultMipMapStrategy() {
    protected double calcRangeValue(double prev1, double prev2) {
      return Math.max(prev1, prev2);
    }
  };

  public final void setRangeValue(int pointIndex, double y, Array2D multiRange) {
    if (!MathUtil.isBounded(pointIndex, 0, multiRange.numColumns(0) - 1)) {
      throw new IndexOutOfBoundsException("Invalid datapoint index: "
          + pointIndex);
    }
    setRangeValue(pointIndex, y, multiRange, 0);
  }

  public final void appendDomainValue(double x, Array2D multiDomain) {
    appendDomainValue(x, multiDomain, 0);
  }

  public final void appendRangeValue(double y, Array2D multiRange) {
    appendRangeValue(y, multiRange, 0);
  }

  private void appendDomainValue(double x, Array2D a, int level) {
    //GWT.log("TESTING appendDomain: level=" + level + "; x=" + x, null);
    boolean levelExists = (level < a.numRows());
    if (levelExists) {
      int oldLength = a.numColumns(level);
      int newLength = oldLength + 1;
      a.set(level, newLength - 1, x);
      if (isEven(newLength)) {
        double nextLevelX = a.get(level, oldLength - 1);
        appendDomainValue(nextLevelX, a, level + 1);
      }
    } else {
      a.set(level, 0, x);
    }
  }

  private void appendRangeValue(double y, Array2D a, int level) {
    //GWT.log("TESTING appendRange: level=" + level + "; y=" + y, null);
    boolean levelExists = (level < a.numRows());
    if (levelExists) {
      int oldLength = a.numColumns(level);
      int newLength = oldLength + 1;
      a.set(level, newLength - 1, y);
      if (isEven(newLength)) {
        double nextLevelY1 = a.get(level, oldLength - 1);
        double nextLevelY2 = a.get(level, oldLength - 0);
        double nextLevelY = this.calcRangeValue(nextLevelY1, nextLevelY2);
        appendRangeValue(nextLevelY, a, level + 1);
      }
    } else {
      a.set(level, 0, y);
    }
  }

  private void setRangeValue(int pointIndex, double y, Array2D a, int level) {
    a.set(level, pointIndex, y);
    
    boolean hasMoreLevels = level < (a.numRows() - 1);
    if (!hasMoreLevels) {
      return;
    }

    if (isOdd(pointIndex)) {
      // If index is odd, then this point must have at least 1 preceding point.
      double nextLevelY1 = a.get(level, pointIndex - 1);
      double nextLevelY2 = a.get(level, pointIndex);
      double nextLevelY = this.calcRangeValue(nextLevelY1, nextLevelY2);
      setRangeValue(pointIndex / 2, nextLevelY, a, level + 1);
    } else {
      boolean hasMorePoints = pointIndex < (a.numColumns(level) - 1);
      if (hasMorePoints) {
        double nextLevelY1 = a.get(level, pointIndex);
        double nextLevelY2 = a.get(level, pointIndex + 1);
        double nextLevelY = this.calcRangeValue(nextLevelY1, nextLevelY2);
        setRangeValue(pointIndex / 2, nextLevelY, a, level + 1);
      }
      // else, this is this the first-and-only or last point in the dataset.
    }
  }

  /**
   * Calculates the number of MIP levels that will be needed for the specified
   * number of data points.
   */
  private int calcNumLevels(int numSamples) {
    return (int) MathUtil.log2(numSamples) + 1;
  }

  /**
   * Returns true only if n is an even integer.
   */
  private static boolean isEven(int n) {
    return n % 2 == 0;
  }

  /**
   * Returns true only if n is an odd integer.
   */
  private static boolean isOdd(int n) {
    return n % 2 == 1;
  }
}
