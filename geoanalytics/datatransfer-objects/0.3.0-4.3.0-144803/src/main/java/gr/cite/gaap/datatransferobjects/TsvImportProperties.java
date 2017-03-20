package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TsvImportProperties {

	private static Logger logger = LoggerFactory.getLogger(TsvImportProperties.class);

	private String newLayerName;
	private String geocodeSystemName;	
	
	public TsvImportProperties() {
		super();
		logger.trace("Initialized default contructor for TsvImportProperties");
	}

	public String getNewLayerName() {
		return newLayerName;
	}

	public void setNewLayerName(String newLayerName) {
		this.newLayerName = newLayerName;
	}

	public String getGeocodeSystemName() {
		return geocodeSystemName;
	}

	public void setGeocodeSystemName(String geocodeSystemName) {
		this.geocodeSystemName = geocodeSystemName;
	}
	
	@Override
	public String toString(){
		return 	"\n" +
				"New Layer = " + newLayerName +  "\n" +
				"GeocodeSystem = " + geocodeSystemName;
	}
}
