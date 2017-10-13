package gr.cite.gaap.datatransferobjects;

import java.util.List;

import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;
import gr.cite.geoanalytics.dataaccess.entities.layer.LayerTenant;

public class LayerDescription {

	private Layer layer;
	private List<LayerTenant> layerTenants;
	
	public LayerDescription(Layer layer, List<LayerTenant> layerTenants){
		this.layer = layer;
		this.layerTenants = layerTenants;
	}

	public Layer getLayer() {
		return layer;
	}

	public void setLayer(Layer layer) {
		this.layer = layer;
	}

	public List<LayerTenant> getLayerTenants() {
		return layerTenants;
	}

	public void setLayerTenants(List<LayerTenant> layerTenants) {
		this.layerTenants = layerTenants;
	}
}
