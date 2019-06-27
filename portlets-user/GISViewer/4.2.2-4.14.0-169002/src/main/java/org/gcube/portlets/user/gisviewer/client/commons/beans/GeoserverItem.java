package org.gcube.portlets.user.gisviewer.client.commons.beans;

import java.util.ArrayList;
import java.util.List;

public class GeoserverItem {
	
	List<LayerItem> layerItems = new ArrayList<LayerItem>();
	String url;
	
	
	public GeoserverItem(String url) {
		super();
		this.url = url;
	}

	public void addLayerItem(LayerItem layerItem) {
		this.layerItems.add(layerItem);
	}
	
	public List<LayerItem> getLayerItems() {
		return layerItems;
	}
	
	public void setLayerItems(List<LayerItem> layerItems) {
		this.layerItems = layerItems;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	@Override
	public String toString() {
		String s = "";
		s += "GEOSERVER " + url + "\n  LAYERS " ;
		for (LayerItem l : layerItems)
			s += l.getName() + ",";
		s += "\n";
		return s;
	}
}
