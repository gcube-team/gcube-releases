package org.gcube.portlets.user.gisviewer.client.commons.beans;

import java.util.HashMap;

import org.gwtopenmaps.openlayers.client.layer.Layer;

public class LayersList {
	HashMap<LayerItem, Layer> hashLayerItems = new HashMap<LayerItem, Layer>();
	HashMap<Layer, LayerItem> hashLayers = new HashMap<Layer, LayerItem>();
	
	public LayersList() {
	}
	
	public void addLayer(LayerItem layerItem, Layer layer) {
		this.hashLayerItems.put(layerItem, layer);
		this.hashLayers.put(layer, layerItem);
	}
	
	public Layer getLayerByLayerItem(LayerItem layerItem) {
		return this.hashLayerItems.get(layerItem);
	}

	public LayerItem getLayerItemByLayer(Layer layer) {
		return this.hashLayers.get(layer);
	}
	
	public void remove(LayerItem layerItem) {
		this.hashLayerItems.remove(layerItem);
	}

	public void remove(Layer layer) {
		hashLayers.remove(layer);
	}
}
