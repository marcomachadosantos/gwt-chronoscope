package org.timepedia.chronoscope.client.browser;

import com.google.gwt.core.client.JavaScriptObject;
import org.timepedia.chronoscope.client.canvas.RadialGradient;

/**
 * An implementation of RadialGradient on the Javascript CANVAS
 */
public class BrowserRadialGradient implements RadialGradient {
    private final JavaScriptObject nativeGradient;


    public BrowserRadialGradient(BrowserLayer layer, double x0, double y0, double r0, double x1, double y1, double r1) {

        nativeGradient = createNativeGradient(layer.getContext(), x0, y0, 1, x1, y1, r1);

    }

    public void addColorStop(double position, String color) {
        addColorStop0(nativeGradient, position, color);
    }

    private native void addColorStop0(JavaScriptObject nativeGradient, double position, String color) /*-{
       nativeGradient.addColorStop(position, color);
    }-*/;

    private native JavaScriptObject createNativeGradient(JavaScriptObject ctx, double x0, double y0, double r0,
                                                         double x1, double y1, double r1) /*-{
         return ctx.createRadialGradient(x0, y0, r0, x1, y1, r1);
    }-*/;

    public JavaScriptObject getNative() {
        return nativeGradient;
    }

}
