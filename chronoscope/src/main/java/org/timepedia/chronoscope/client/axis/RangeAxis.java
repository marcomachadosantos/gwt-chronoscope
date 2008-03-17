package org.timepedia.chronoscope.client.axis;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.render.RangeAxisRenderer;
import org.timepedia.exporter.client.Exportable;

/**
 * A RangeAxis is an ValueAxis that represents values, typically on the y-axis.
 *
 * @gwt.exportPackage chronoscope
 */
public class RangeAxis extends ValueAxis implements Exportable {

  private final int axisNum;

  private final double rangeLow;

  private final double rangeHigh;

  private RangeAxisRenderer renderer = null;

  private double maxLabelWidth;

  private double maxLabelHeight;

  private double axisLabelHeight;

  private double axisLabelWidth;

  private double visRangeMin;

  private double visRangeMax;

  private boolean autoZoom = false;

  private boolean allowScientificNotation = true;

  private boolean forceScientificNotation = false;

  private int maxDigits = 4;

  public RangeAxis(Chart chart, String label, String units, int axisNum,
      double rangeLow, double rangeHigh, AxisPanel panel) {
    super(chart, label, units);
    this.axisNum = axisNum;
    setAxisPanel(panel);
    renderer = new RangeAxisRenderer(this);
    this.rangeLow = rangeLow;
    this.rangeHigh = rangeHigh;
  }

  public double dataToUser(double dataY) {
    return (dataY - getRangeLow()) / getRange();
  }

  public void drawAxis(XYPlot plot, Layer layer, Bounds axisBounds,
      boolean gridOnly) {
    renderer.drawAxis(plot, layer, axisBounds, gridOnly);
  }

  public double getAxisLabelHeight() {
    return axisLabelHeight;
  }

  public double getAxisLabelWidth() {
    return axisLabelWidth;
  }

  public int getAxisNumber() {
    return axisNum;
  }

  public double getHeight() {
    if (axisPanel.getOrientation() == AxisPanel.HORIZONTAL_AXIS) {
      return getMaxLabelHeight() + 5 + axisLabelHeight + 2;
    } else {
      return getChart().getPlotForAxis(this).getPlotBounds().height;
    }
  }

  public double getMaxLabelHeight() {
    return maxLabelHeight;
  }

  public double getMaxLabelWidth() {
    return maxLabelWidth;
  }

  public double getRangeHigh() {
    return autoZoom ? visRangeMax : rangeHigh;
  }

  public double getRangeLow() {
    return autoZoom ? visRangeMin : rangeLow;
  }

  public double getRotationAngle() {
    return
        (getAxisPanel().getPosition() == AxisPanel.RIGHT ? 1.0 : -1.0) * Math.PI
            / 2;
  }

  public double getWidth() {
    if (axisPanel.getOrientation() == AxisPanel.VERTICAL_AXIS) {
      return maxLabelWidth + 10 + axisLabelWidth;
    } else {
      return getChart().getPlotForAxis(this).getPlotBounds().width;
    }
  }

  public void init() {
    computeLabelWidths(getChart().getView());
  }

  public void initVisibleRange() {
    visRangeMin = rangeLow;
    visRangeMax = rangeHigh;
  }

  public boolean isAutoZoomVisibleRange() {
    return autoZoom;
  }

  /**
   * @gwt.export
   */
  public void setAutoZoomVisibleRange(boolean autoZoom) {

    this.autoZoom = autoZoom;
  }

  /**
   * @gwt.export
   */
  public void setLabel(String label) {
    super.setLabel(label);
    getChart().damageAxes(this);
    computeLabelWidths(getChart().getView());
  }

  /**
   * @gwt.export
   */
  public void setVisibleRange(double visRangeMin, double visRangeMax) {

    this.visRangeMin = visRangeMin;
    this.visRangeMax = visRangeMax;
  }

  public double userToData(double userValue) {
    return getRangeLow() + userValue * getRange();
  }

  private void computeLabelWidths(View view) {
    renderer.init(view);

    maxLabelWidth = renderer.getLabelWidth(view, getDummyLabel(), 0) + 10;
    maxLabelHeight = renderer.getLabelHeight(view, getDummyLabel(), 0) + 10;
    axisLabelHeight = renderer
        .getLabelHeight(view, getLabel(), getRotationAngle());
    axisLabelWidth = renderer
        .getLabelWidth(view, getLabel(), getRotationAngle());
  }

  /**
   * If enabled (true by default), when maxTickLabelDigits is exceeded, labels
   * will be rendered in scientific notation.
   *
   * @gwt.export
   */
  public void setAllowScientificNotation(boolean enable) {
    allowScientificNotation = enable;
  }

  /**
   * Force tick labels to always be rendered in scientific notation. (Default
   * false);
   *
   * @gwt.export
   */
  public void setForceScientificNotation(boolean force) {
    forceScientificNotation = force;
  }

  /**
   * The maximum number of digits allowed in a tick label, if scientific
   * notation is enabled, it will automatically switch after this limit is
   * reached. Minimum is 1 digit.
   */
  public void setMaxTickLabelDigits(int digits) {
    maxDigits = Math.max(1, digits);
  }

  public boolean isAllowScientificNotation() {
    return allowScientificNotation;
  }

  public boolean isForceScientificNotation() {
    return forceScientificNotation;
  }

  public int getMaxDigits() {
    return maxDigits;
  }

  public String getDummyLabel() {
    return "0" + (maxDigits == 1 ? ""
        : "." + "000000000".substring(0, maxDigits - 1));
  }
}
