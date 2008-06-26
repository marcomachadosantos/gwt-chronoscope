package org.timepedia.chronoscope.client.axis;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.chronoscope.client.render.LegendAxisRenderer;

/**
 * An implementation of ValueAxis to render a chart Legend
 */
public class LegendAxis extends ValueAxis {

  private final LegendAxisRenderer renderer;

  private DefaultXYPlot plot;

  public LegendAxis(DefaultXYPlot plot, AxisPanel panel, String title) {
    super(plot.getChart(), title, "");
    this.plot = plot;
    setAxisPanel(panel);
    renderer = new LegendAxisRenderer(this);
  }

  public boolean click(int x, int y) {
    return renderer.click(plot, x, y);
  }

  public double dataToUser(double dataValue) {
    return 0;
  }

  public void drawAxis(XYPlot plot, Layer layer, Bounds axisBounds,
      boolean gridOnly) {
    renderer.drawLegend(plot, layer, axisBounds, gridOnly);
  }

  public double getHeight() {
    View view = plot.getChart().getView();
    return renderer.getLabelHeight(view, "X") + renderer.getLegendLabelBounds(
        plot, view.getCanvas().getRootLayer(), plot.getInnerPlotBounds()).height
        + 20;
  }

  public double getRangeHigh() {
    return 0;
  }

  public double getRangeLow() {
    return 0;
  }

  public double getWidth() {
    return plot.getInnerPlotBounds().width;
  }

  public void init() {
    renderer.init(plot, this);
  }

  public double userToData(double screenPosition) {
    return 0;
  }
}