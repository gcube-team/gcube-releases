package org.gcube.portlets.user.gisviewer.client.openlayers;

import org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem;

public interface CqlFilterHandler {
	
	public void removeCqlFilter(LayerItem layerItem);
	
	public void setCQLFilter(LayerItem layerItem, String filter);
	
}
