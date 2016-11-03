package gr.cite.gaap.datatransferobjects.layeroperations;

import gr.cite.gaap.datatransferobjects.TaxonomyTermMessenger;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape.Attribute;

public class TaxonomyTermAttributePair {
	private TaxonomyTermMessenger layerTerm;
	private Attribute attr;
	
	public TaxonomyTermAttributePair() { }
	
	public TaxonomyTermAttributePair(TaxonomyTermMessenger layerTerm, Attribute attr) {
		this.layerTerm = layerTerm;
		this.attr = attr;
	}

	public TaxonomyTermMessenger getLayerTerm() {
		return layerTerm;
	}

	public void setLayerTerm(TaxonomyTermMessenger layerTerm) {
		this.layerTerm = layerTerm;
	}

	public Attribute getAttr() {
		return attr;
	}

	public void setAttr(Attribute attr) {
		this.attr = attr;
	}
	
}
