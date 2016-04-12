package org.gcube.portlets.user.gisviewer.client.openlayers;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.user.gisviewer.client.commons.beans.BoundsMap;
import org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem;
import org.gwtopenmaps.openlayers.client.Bounds;
import org.gwtopenmaps.openlayers.client.layer.GMapType;
import org.gwtopenmaps.openlayers.client.layer.Google;
import org.gwtopenmaps.openlayers.client.layer.GoogleOptions;
import org.gwtopenmaps.openlayers.client.layer.Layer;
import org.gwtopenmaps.openlayers.client.layer.WMS;
import org.gwtopenmaps.openlayers.client.layer.WMSOptions;
import org.gwtopenmaps.openlayers.client.layer.WMSParams;

public class LayerItemsHandler {

	private List<Layer> layers_click = new ArrayList<Layer>(); // it was WMS instead Layer
	private List<Layer> layers_legend = new ArrayList<Layer>(); // it was WMS instead Layer
	private List<Layer> layers = new ArrayList<Layer>();

	public LayerItemsHandler(List<LayerItem> layerItems) {
		
		for (LayerItem layerItem : layerItems) {
			Layer layer = getLayerFromLayerItem(layerItem);
			if (layerItem.isOnMap())
				layers.add(layer);
			if (layerItem.isClickData())
				layers_click.add(layer);
			if (layerItem.isHasLegend())
				layers_legend.add(layer);
		}
	}

	public LayerItemsHandler(List<LayerItem> layerItems,
			HashMap<LayerItem, Layer> mappingLayerItemsToLayers,
			HashMap<Layer, LayerItem> mappingLayersToLayerItems) {
		
		for (LayerItem layerItem : layerItems) {
			Layer layer = getLayerFromLayerItem(layerItem);
			
			mappingLayerItemsToLayers.put(layerItem, layer);
			mappingLayersToLayerItems.put(layer, layerItem);
			
			if (layerItem.isOnMap())
				layers.add(layer);
			if (layerItem.isClickData())
				layers_click.add(layer);
			if (layerItem.isHasLegend())
				layers_legend.add(layer);
		}
	}

	private Layer getLayerFromLayerItem(LayerItem layerItem) {
		Layer layer;
		BoundsMap bounds = layerItem.getMaxExtent();
		if (layerItem.getName().contentEquals("Google Satellite")) {
			GoogleOptions googleOptions = new GoogleOptions();
			googleOptions.setType(GMapType.G_SATELLITE_MAP);
			googleOptions.setNumZoomLevels(12);
			googleOptions.setProjection("EPSG:4326");
			googleOptions.setDisplayOutsideMaxExtent(true);
			googleOptions.setIsBaseLayer(layerItem.isBaseLayer());			
			googleOptions.setMaxExtent(new Bounds(bounds.getLowerLeftX(), 
					bounds.getLowerLeftY(), 
					bounds.getUpperRightX(), 
					bounds.getUpperRightY()));
			
			layer = new Google("Google Satellite", googleOptions);	
			
		} else {
			WMSOptions wmso = new WMSOptions();
			wmso.setIsBaseLayer(layerItem.isBaseLayer());
			wmso.setBuffer(layerItem.getBuffer());
			wmso.setWrapDateLine(layerItem.isWrapDateLine());
			
			WMSParams wmsp = new WMSParams();
			wmsp.setLayers(layerItem.getLayer());
			wmsp.setStyles(layerItem.getStyle());
			
			
			if (bounds!=null) {
//				wmsp.setMaxExtent(new Bounds(bounds.getLowerLeftX(), 
//												bounds.getLowerLeftY(), 
//												bounds.getUpperRightX(), 
//												bounds.getUpperRightY()));
				wmso.setMaxExtent(new Bounds(bounds.getLowerLeftX(), 
					bounds.getLowerLeftY(), 
					bounds.getUpperRightX(), 
					bounds.getUpperRightY()));
			}
//			wmsp.setIsTransparent(layerItem.isTrasparent());
			wmsp.setTransparent(layerItem.isTrasparent());
			
			layer = new WMS(layerItem.getName(), layerItem.getUrl(), wmsp, wmso);
		}
		//layerItem.setOpenlayersLayer((LayerItem.Layer2)layer);
		layer.setIsVisible(layerItem.isVisible());
		layer.setOpacity((float)layerItem.getOpacity());
		return layer;
	}

//	private void setOpacity(Layer l) {
//		boolean isBrightLayer=false;
//		for (String s : Constants.brightLayers)
//			if (s.equals(l.getName()))
//				isBrightLayer=true;
//		
//		if (!isBrightLayer)
//			l.setOpacity((float) Constants.defaultOpacityLayers);
//	}

	public List<Layer> getLayersWithLegend() {
		return layers_legend;
	}

	public List<Layer> getLayersClickData() {
		return layers_click;
	}

	public List<Layer> getLayers() {
		return layers;
	}

	public static String getLayerName(WMS layer){
		return layer.getJSObject().getProperty("params").getPropertyAsString("LAYERS");
	}
	public static String getLayerStyle(WMS layer){
		return layer.getJSObject().getProperty("params").getPropertyAsString("STYLES");
	}
	public static String getLayerStyle(Layer layer){
		return layer.getJSObject().getProperty("params").getPropertyAsString("STYLES");
	}
}
