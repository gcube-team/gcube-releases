package gr.cite.gaap.datatransferobjects.layeroperations;

public class NameTaxonomyPair {
	public String termName = null;
	public String termTaxonomy = null;
	
	public NameTaxonomyPair() { }
	
	public NameTaxonomyPair(String termName, String termTaxonomy) {
		this.termName = termName;
		this.termTaxonomy = termTaxonomy;
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