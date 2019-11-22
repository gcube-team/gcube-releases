package gr.cite.gaap.datatransferobjects.layeroperations;

import gr.cite.gaap.datatransferobjects.GeocodeMessenger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.gaap.datatransferobjects.GeoLocationTag;
import gr.cite.gaap.datatransferobjects.PrincipalMessenger;

public class ShapeBoundaryRequest {
	private static Logger logger = LoggerFactory.getLogger(ShapeBoundaryRequest.class);

	private GeocodeMessenger layerTerm = null;
	private GeocodeMessenger boundaryTerm = null;
	private PrincipalMessenger principalMessenger = null;
	
	public ShapeBoundaryRequest() { 
		logger.trace("Initialized default contructor for ShapeBoundaryRequest");

	}
	
	public ShapeBoundaryRequest(GeocodeMessenger layerTerm, GeocodeMessenger boundaryTerm, PrincipalMessenger principalMessenger) {
		logger.trace("Initializing ShapeBoundaryRequest...");

		this.layerTerm = layerTerm;
		this.boundaryTerm = boundaryTerm;
		this.principalMessenger = principalMessenger;
		logger.trace("Initialized ShapeBoundaryRequest");

	}
	
	public GeocodeMessenger getLayerTerm() {
		return layerTerm;
	}

	public void setLayerTerm(GeocodeMessenger layerTerm) {
		this.layerTerm = layerTerm;
	}

	public GeocodeMessenger getBoundaryTerm() {
		return boundaryTerm;
	}

	public void setBoundaryTerm(GeocodeMessenger boundaryTerm) {
		this.boundaryTerm = boundaryTerm;
	}

	public PrincipalMessenger getUserMessenger() {
		return principalMessenger;
	}

	public void setUserMessenger(PrincipalMessenger principalMessenger) {
		this.principalMessenger = principalMessenger;
	}
}