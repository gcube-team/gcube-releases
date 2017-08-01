package gr.cite.gaap.datatransferobjects.layeroperations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.gaap.datatransferobjects.GeoLocationTag;
import gr.cite.gaap.datatransferobjects.GeocodeMessenger;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape.Attribute;

public class GeocodeAttributePair {
	private static Logger logger = LoggerFactory.getLogger(GeocodeAttributePair.class);

	private GeocodeMessenger layerTerm;
	private Attribute attr;
	
	public GeocodeAttributePair() { 
		logger.trace("Initialized default contructor for GeocodeAttributePair");

	}
	
	public GeocodeAttributePair(GeocodeMessenger layerTerm, Attribute attr) {
		logger.trace("Initializing GeocodeAttributePair...");

		this.layerTerm = layerTerm;
		this.attr = attr;
		logger.trace("Initialized GeocodeAttributePair");

	}

	public GeocodeMessenger getLayerTerm() {
		return layerTerm;
	}

	public void setLayerTerm(GeocodeMessenger layerTerm) {
		this.layerTerm = layerTerm;
	}

	public Attribute getAttr() {
		return attr;
	}

	public void setAttr(Attribute attr) {
		this.attr = attr;
	}
	
}
