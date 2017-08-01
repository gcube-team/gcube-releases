package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaxonomyTermLinkInfo {
	private static Logger logger = LoggerFactory.getLogger(TaxonomyTermLinkInfo.class);

	private String sourceTermTaxonomy = null;
	private String sourceTerm = null;
	private String destTermTaxonomy = null;
	private String destTerm = null;

	
	public TaxonomyTermLinkInfo() {
		super();
		logger.trace("Initialized default contructor for TaxonomyTermLinkInfo");
	}

	public String getSourceTermTaxonomy() {
		return sourceTermTaxonomy;
	}

	public void setSourceTermTaxonomy(String sourceTermTaxonomy) {
		this.sourceTermTaxonomy = sourceTermTaxonomy;
	}

	public String getSourceTerm() {
		return sourceTerm;
	}

	public void setSourceTerm(String sourceTerm) {
		this.sourceTerm = sourceTerm;
	}

	public String getDestTermTaxonomy() {
		return destTermTaxonomy;
	}

	public void setDestTermTaxonomy(String destTermTaxonomy) {
		this.destTermTaxonomy = destTermTaxonomy;
	}

	public String getDestTerm() {
		return destTerm;
	}

	public void setDestTerm(String destTerm) {
		this.destTerm = destTerm;
	}
}
