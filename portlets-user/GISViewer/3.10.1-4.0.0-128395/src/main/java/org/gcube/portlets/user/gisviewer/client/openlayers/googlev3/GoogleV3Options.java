/**
 * 
 */
package org.gcube.portlets.user.gisviewer.client.openlayers.googlev3;

import org.gwtopenmaps.openlayers.client.layer.EventPaneOptions;

/**
 * @author ceras
 *
 */
public class GoogleV3Options extends EventPaneOptions {
    public void setType(GoogleV3MapType type) {
            getJSObject().setProperty("type", type.getNativeType());
    }

    public void setSphericalMercator(boolean value) {
            getJSObject().setProperty("sphericalMercator", value);
    }
}