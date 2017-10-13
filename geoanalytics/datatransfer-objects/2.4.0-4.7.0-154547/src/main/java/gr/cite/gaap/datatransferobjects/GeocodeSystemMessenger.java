package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.geoanalytics.dataaccess.entities.geocode.GeocodeSystem;

public class GeocodeSystemMessenger {
	private static Logger logger = LoggerFactory.getLogger(GeocodeSystemMessenger.class);

	private String name = null;
	private String originalName = null;
	private String taxonomyClass = null;
	private String extraData = null;
	private boolean userTaxonomy = false;
	private boolean active = false;

	public GeocodeSystemMessenger() {
		logger.trace("Initialized default contructor for TaxonomyMessenger");

	}

	public GeocodeSystemMessenger(GeocodeSystem t) {
		logger.trace("Initializing TaxonomyMessenger...");

		this.name = t.getName();
		this.taxonomyClass = t.getTaxonomyClass().getName();
		this.userTaxonomy = t.getIsUserTaxonomy();
		this.active = t.getIsActive();
		this.extraData = t.getExtraData();
		logger.trace("Initialized TaxonomyMessenger");

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	public String getTaxonomyClass() {
		return taxonomyClass;
	}

	public void setTaxonomyClass(String taxonomyClass) {
		this.taxonomyClass = taxonomyClass;
	}

	public String getExtraData() {
		return extraData;
	}

	public void setExtraData(String extraData) {
		this.extraData = extraData;
	}

	public boolean isUserTaxonomy() {
		return userTaxonomy;
	}

	public void setUserTaxonomy(boolean userTaxonomy) {
		this.userTaxonomy = userTaxonomy;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
