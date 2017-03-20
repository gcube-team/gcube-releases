package gr.cite.gaap.datatransferobjects;

import java.util.List;

import gr.cite.geoanalytics.dataaccess.entities.geocode.TaxonomyLayer;
import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;
import gr.cite.geoanalytics.dataaccess.entities.layer.LayerTenant;

public class LayerDescription {

	private Layer layer;
	private List<LayerTenant> layerTenants;
	private List<TaxonomyLayer> taxonomies;
	
	public LayerDescription(Layer layer, List<LayerTenant> layerTenants, List<TaxonomyLayer> taxonomies){
		this.layer = layer;
		this.layerTenants = layerTenants;
		this.taxonomies = taxonomies;
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

	public List<TaxonomyLayer> getTaxonomies() {
		return taxonomies;
	}

	public void setTaxonomies(List<TaxonomyLayer> taxonomies) {
		this.taxonomies = taxonomies;
	}
	
	
	
}
