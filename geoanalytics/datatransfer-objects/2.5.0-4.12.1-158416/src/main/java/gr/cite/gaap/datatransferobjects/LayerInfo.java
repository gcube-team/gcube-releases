package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LayerInfo {
	private static Logger logger = LoggerFactory.getLogger(LayerInfo.class);

	private String geocodeSystem = null;
	private String layerName = null;
	private String layerID = null;
	
	public LayerInfo() {
		super();
		logger.trace("Initialized default contructor for GeocodeInfo");
	}

	
	public String getLayerID() {
		return layerID;
	}

	public void setLayerID(String layerID) {
		this.layerID = layerID;
	}

	public String getGeocodeSystem() {
		return geocodeSystem;
	}

	public void setGeocodeSystem(String geocodeSystem) {
		this.geocodeSystem = geocodeSystem;
	}

	public String getlayerName() {
		return layerName;
	}

	public void setlayerName(String layerName) {
		this.layerName = layerName;
	}
}
