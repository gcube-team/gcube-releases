package gr.cite.gaap.datatransferobjects;

import gr.cite.geoanalytics.dataaccess.entities.taxonomy.Taxonomy;

public class TaxonomyMessenger {
	private String name = null;
	private String originalName = null;
	private String taxonomyClass = null;
	private String extraData = null;
	private boolean userTaxonomy = false;
	private boolean active = false;

	public TaxonomyMessenger() {
	}

	public TaxonomyMessenger(Taxonomy t) {
		this.name = t.getName();
		this.taxonomyClass = t.getTaxonomyClass().getName();
		this.userTaxonomy = t.getIsUserTaxonomy();
		this.active = t.getIsActive();
		this.extraData = t.getExtraData();
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
