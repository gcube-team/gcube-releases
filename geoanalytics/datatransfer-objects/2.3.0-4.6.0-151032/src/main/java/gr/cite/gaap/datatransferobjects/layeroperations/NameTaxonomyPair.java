package gr.cite.gaap.datatransferobjects.layeroperations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NameTaxonomyPair {
	private static Logger logger = LoggerFactory.getLogger(NameTaxonomyPair.class);

	public String termName = null;
	public String termTaxonomy = null;
	
	public NameTaxonomyPair() { 
		logger.trace("Initialized default contructor for NameTaxonomyPair");

	}
	
	public NameTaxonomyPair(String termName, String termTaxonomy) {
		logger.trace("Initializing NameTaxonomyPair...");

		this.termName = termName;
		this.termTaxonomy = termTaxonomy;
		logger.trace("Initialized NameTaxonomyPair");

	}

	public String getTermName() {
		return termName;
	}

	public void setTermName(String termName) {
		this.termName = termName;
	}

	public String getTermTaxonomy() {
		return termTaxonomy;
	}

	public void setTermTaxonomy(String termTaxonomy) {
		this.termTaxonomy = termTaxonomy;
	}
}