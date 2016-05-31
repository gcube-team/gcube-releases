/**
 * 
 */
package org.gcube.portlets.user.geoexplorerportlet.client.resources;

import org.gcube.portlets.user.geoexplorerportlet.client.GeoExplorerPortlet;

import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * @author ceras
 *
 */
public class Images {

	public static AbstractImagePrototype iconLayers() {
		return AbstractImagePrototype.create(GeoExplorerPortlet.resources.iconLayers());
	}

	public static AbstractImagePrototype iconTrueMarble() {
		return AbstractImagePrototype.create(GeoExplorerPortlet.resources.iconTrueMarble());
	}

	public static AbstractImagePrototype iconOpen() {
		return AbstractImagePrototype.create(GeoExplorerPortlet.resources.iconOpen());
	}
	
	public static AbstractImagePrototype iconRemove() {
		return AbstractImagePrototype.create(GeoExplorerPortlet.resources.iconRemove());
	}

	public static AbstractImagePrototype iconAddWms() {
		return AbstractImagePrototype.create(GeoExplorerPortlet.resources.iconAddWms());
	}

	public static AbstractImagePrototype logoGeoExplorer() {
		return AbstractImagePrototype.create(GeoExplorerPortlet.resources.logoGeoExplorer());
	}
	
	public static AbstractImagePrototype settingsRefresh() {
		return AbstractImagePrototype.create(GeoExplorerPortlet.resources.settingsRefresh());
	}
	
	public static AbstractImagePrototype closeWindow() {
		return AbstractImagePrototype.create(GeoExplorerPortlet.resources.closeWindow());
	}
}
