package org.timepedia.chronoscope.client.render;

import com.google.gwt.core.client.GWT;

import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.axis.OverviewAxis;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;

/**
 * Rendering code used to render OverviewAxis
 */
public class OverviewAxisRenderer implements AxisRenderer, GssElement {

  private static final int MIN_OVERVIEW_HEIGHT = 60;

  private int overviewHeight;

  private GssProperties axisProperties;

  private OverviewAxis axis;
  
  // The singleton avoids excess creation of Bounds objects
  private Bounds highlightBounds, highlightBoundsSingleton;
  
  public OverviewAxisRenderer() {
    highlightBoundsSingleton = new Bounds();
  }
  
  /**
   * Returns the bounds of the highlighted area of the overview axis, or
   * null if nothing is highlighted.
   */
  public Bounds getHighlightBounds() {
    return highlightBounds;
  }
  
  public void drawOverview(XYPlot plot, Layer layer, Bounds axisBounds,
      boolean gridOnly) {
    
    Layer overviewLayer = plot.getOverviewLayer();
    clearAxis(layer, axis, axisBounds);

    layer.drawImage(overviewLayer, 0, 0, overviewLayer.getWidth(),
        overviewLayer.getHeight(), axisBounds.x, axisBounds.y, axisBounds.width,
        axisBounds.height);
    
    highlightBounds = calcHighlightBounds(plot, axisBounds);
    //GWT.log("TESTING: OverviewAxisRenderer: highlightBounds = " + highlightBounds, null);
    
    if (highlightBounds != null) {
      layer.save();
      layer.setFillColor(axisProperties.bgColor);
      layer.setTransparency((float) Math.max(0.5f, axisProperties.transparency));
      layer.fillRect(highlightBounds.x, highlightBounds.y,
          highlightBounds.width, highlightBounds.height);
      layer.setStrokeColor(axisProperties.color);
      layer.setTransparency(1.0f);
      layer.setLineWidth(axisProperties.lineThickness);
      layer.beginPath();
      // fix for Opera, on Firefox/Safari, rect() has implicit moveTo
      layer.moveTo(highlightBounds.x, highlightBounds.y + 1);  
      layer.rect(highlightBounds.x, highlightBounds.y + 1,
          highlightBounds.width, highlightBounds.height);
      layer.stroke();
      layer.setLineWidth(1);
      layer.restore();
      
      //plot.getChart().setCursor(Cursor.SELECTING);
    }
    else {
      //plot.getChart().setCursor(Cursor.DEFAULT);
    }
  }
  
  public int getOverviewHeight() {
    return overviewHeight;
  }

  public GssElement getParentGssElement() {
    return axis.getAxisPanel();
  }

  public String getType() {
    return "overview";
  }

  public String getTypeClass() {
    return null;
  }

  public void init(XYPlot plot, OverviewAxis overviewAxis) {
    if (axisProperties == null) {
      axis = overviewAxis;

      axisProperties = plot.getChart().getView().getGssProperties(this, "");
      overviewHeight = axisProperties.height;
      if (overviewHeight < MIN_OVERVIEW_HEIGHT) {
        overviewHeight = MIN_OVERVIEW_HEIGHT;
      }
    }
  }

  private void clearAxis(Layer layer, OverviewAxis axis, Bounds bounds) {
  }
  
  /*
   * Calculates the bounds of the highlighted area of the overview axis.
   * 
   * @return the bounds of the highlighted area, or <tt>null</tt> if no highlight
   * should be drawn.
   */
  private Bounds calcHighlightBounds(XYPlot plot, Bounds axisBounds) {
    double globalDomainMin = plot.getDomainMin();
    double visibleDomainMin = plot.getDomainOrigin();
    double globalDomainWidth = plot.getDomainMax() - globalDomainMin;
    double visibleDomainWidth = plot.getCurrentDomain();
    
    Bounds b;
    
    if (globalDomainWidth <= visibleDomainWidth) {
      // The viewport (i.e. the portion of the domain that is visible within the
      // plot area) is at least as wide as the global domain, so don't highlight.
      b = null;
    }
    else {
      double beginHighlight = axisBounds.x +
          ((visibleDomainMin - globalDomainMin) / globalDomainWidth * axisBounds.width);
      beginHighlight = Math.max(beginHighlight, axisBounds.x);
      
      double endHighlight = axisBounds.x +
          ((visibleDomainMin - globalDomainMin + visibleDomainWidth) / globalDomainWidth * axisBounds.width);
      endHighlight = Math.min(endHighlight, axisBounds.x + axisBounds.width);
      
      b = highlightBoundsSingleton;
      b.x = beginHighlight;
      b.y = axisBounds.y;
      b.width = endHighlight - beginHighlight;
      b.height = axisBounds.height;
    }
    
    return b;
  }
}
