package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchAttributeValueMessenger {
	private static Logger logger = LoggerFactory.getLogger(SearchAttributeValueMessenger.class);

	private String geographicTaxonomy = null;
	private String taxonomy = null;

	
	
	public SearchAttributeValueMessenger() {
		super();
		logger.trace("Initialized default contructor for SearchAttributeValueMessenger");

	}

	public String getGeographicTaxonomy() {
		return geographicTaxonomy;
	}

	public void setGeographicTaxonomy(String geographyTaxonomy) {
		this.geographicTaxonomy = geographyTaxonomy;
	}

	public String getTaxonomy() {
		return taxonomy;
	}

	public void setTaxonomy(String taxonomy) {
		this.taxonomy = taxonomy;
	}
}
