package org.gcube.portlets.user.gisviewer.client.commons.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LayerItemsResult implements Serializable {
	String status = "";
	List<LayerItem> layerItems = new ArrayList<LayerItem>();
	
	public LayerItemsResult() {
	}

	
	public LayerItemsResult(String status, List<LayerItem> layerItems) {
		super();
		this.status = status;
		this.layerItems = layerItems;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public void addStatusMessage(String message) {
		status += (status=="" ? "" : "<br>") + message;
	}

	public List<LayerItem> getLayerItems() {
		return layerItems;
	}

	public void setLayerItems(List<LayerItem> layerItems) {
		this.layerItems = layerItems;
	}
	
	public void addLayerItem(LayerItem layerItem) {
		this.layerItems.add(layerItem);
	}


	public int getLayerItemsSize() {
		return layerItems.size();
	}
}
