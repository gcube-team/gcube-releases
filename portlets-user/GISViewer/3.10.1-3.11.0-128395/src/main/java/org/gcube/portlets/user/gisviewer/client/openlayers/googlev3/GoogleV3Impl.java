/**
 * 
 */
package org.gcube.portlets.user.gisviewer.client.openlayers.googlev3;

/**
 * @author ceras
 *
 */
import org.gwtopenmaps.openlayers.client.util.JSObject;

/**
 * @author Erdem Gunay
 * @author Aaron Novstrup - Stottler Henke Associates, Inc.
 */
class GoogleV3Impl {
        public static native JSObject create(String name)/*-{
                return new $wnd.OpenLayers.Layer.Google(name);
        }-*/;

        public static native JSObject create(String name, JSObject options)/*-{
                return new $wnd.OpenLayers.Layer.Google(name, options);
        }-*/;

        public static native JSObject forwardMercator(JSObject google, double lon, double lat)/*-{
                return google.forwardMercator(lon, lat);
        }-*/;
}