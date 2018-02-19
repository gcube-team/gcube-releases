package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeocodeSystemNameTransfer {
	private static Logger logger = LoggerFactory.getLogger(GeocodeSystemNameTransfer.class);

	private String geocodeSystemName = null;
	
	public GeocodeSystemNameTransfer() {
		super();
		logger.trace("Initialized default contructor for TaxonomyNameTrasnfer");
	}
	public String getGeocodeSystemName() {
		return geocodeSystemName;
	}
	public void setGeocodeSystemName(String taxonomyName) {
		this.geocodeSystemName = taxonomyName;
	}

}