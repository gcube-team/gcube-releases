package gr.cite.gaap.datatransferobjects;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeocodeSystemSearchSelection {
	private static Logger logger = LoggerFactory.getLogger(GeocodeSystemSearchSelection.class);

	public List<String> geocodeSystemNames = Collections.emptyList();
	public List<String> geocodeNames = Collections.emptyList();
	public boolean activeGeocodeSystems;
	public boolean activeGeocodes;
	

	public GeocodeSystemSearchSelection() {
		super();
		logger.trace("Initialized default contructor for TaxonomySearchSelection");

	}

	public List<String> getGeocodeSystemNames() {
		return geocodeSystemNames;
	}

	public void setGeocodeSystemNames(List<String> taxonomyNames) {
		this.geocodeSystemNames = taxonomyNames;
	}

	public List<String> getGeocodeNames() {
		return geocodeNames;
	}

	public void setGeocodeNames(List<String> termNames) {
		this.geocodeNames = termNames;
	}

	public boolean isActiveGeocodeSystems() {
		return activeGeocodeSystems;
	}

	public void setActiveGeocodeSystems(boolean activeTaxonomies) {
		this.activeGeocodeSystems = activeTaxonomies;
	}

	public boolean isActiveGeocodes() {
		return activeGeocodes;
	}

	public void setActiveGeocodes(boolean activeTerms) {
		this.activeGeocodes = activeTerms;
	}

}
