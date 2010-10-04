package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.gen2.event.dom.client.MouseDownEvent;
import com.google.gwt.gen2.event.dom.client.MouseDownHandler;
import com.google.gwt.gen2.event.dom.client.MouseEvent;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.Overlay;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.event.ChartDragStartEvent;

/**
 * Handles the event where the user depresses a mouse button.
 *
 * @author Chad Takahashi
 */
public final class ChartMouseDownHandler
    extends AbstractEventHandler<MouseDownHandler> implements MouseDownHandler {

  public void onMouseDown(MouseDownEvent event) {
    ChartState chartInfo = getChartState(event);
    Chart chart = chartInfo.chart;
    int x = getLocalX(event);
    int y = getLocalY(event);

    boolean handled;

    if (event.getButton() == MouseEvent.Button.RIGHT) {
      // Ignore mouse right-click -- let browser handle event.
      handled = false;
    } else {
      // Set the UI component that initiated the drag or select
      CompoundUIAction uiAction = chartInfo.getCompoundUIAction();
      uiAction.setSource(getComponent(x, y, chart.getPlot()));
      if (event.isShiftKeyDown()) {
        chart.setCursor(Cursor.SELECTING);
        uiAction.setSelectAction(true);
      } else {
        chart.setCursor(Cursor.DRAGGING);
        uiAction.setSelectAction(false);
      }
      uiAction.setStartX(x);
      if (uiAction.getSource() instanceof Overlay) {
      
        handled = true;
      } else {
        chart.setPlotFocus(x, y);
        handled = true;
      }
    }

    chartInfo.setHandled(handled);
  }
}
