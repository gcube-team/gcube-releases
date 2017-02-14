package gr.cite.gaap.datatransferobjects;

public class TaxonomyTermLinkInfo {
	private String sourceTermTaxonomy = null;
	private String sourceTerm = null;
	private String destTermTaxonomy = null;
	private String destTerm = null;

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
