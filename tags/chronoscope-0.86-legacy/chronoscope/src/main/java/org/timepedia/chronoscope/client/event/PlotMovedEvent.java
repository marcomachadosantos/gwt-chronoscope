package org.timepedia.chronoscope.client.event;

import com.google.gwt.gen2.event.shared.AbstractEvent;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.util.Interval;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Export;

/**
 * Fired by plot implementations when the chart domain changes.
 */
@ExportPackage("chronoscope")
public class PlotMovedEvent extends PlotEvent implements Exportable {
  
  public enum MoveType {
    DRAGGED, PAGED, ZOOMED, CENTERED;
  }

  public static Type<PlotMovedEvent, PlotMovedHandler> TYPE
      = new Type<PlotMovedEvent, PlotMovedHandler>() {
    @Override
    protected void fire(PlotMovedHandler plotMovedHandler,
        PlotMovedEvent event) {
      plotMovedHandler.onMoved(event);
    }
  };

  private Interval domain;

  private MoveType moveType;

  public PlotMovedEvent(XYPlot plot, Interval domain, MoveType moveType) {
    super(plot);

    this.domain = domain;
    this.moveType = moveType;
  }

  @Export
  public Interval getDomain() {
    return domain;
  }

  public MoveType getMoveType() {
    return moveType;
  }

  protected Type getType() {
    return TYPE;
  }
}