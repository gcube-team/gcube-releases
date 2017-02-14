package gr.cite.gaap.datatransferobjects;

import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTermLink;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTermLink.Verb;

public class TaxonomyTermLinkMessenger {
	private String sourceTermTaxonomy = null;
	private String sourceTerm = null;
	private String destTermTaxonomy = null;
	private String destTerm = null;

	private String origSourceTermTaxonomy = null;
	private String origSourceTerm = null;
	private String origDestTermTaxonomy = null;
	private String origDestTerm = null;

	private Verb verb = null;

	public TaxonomyTermLinkMessenger() {
	}

	public TaxonomyTermLinkMessenger(TaxonomyTermLink ttl) {
		this.sourceTermTaxonomy = ttl.getSourceTerm().getTaxonomy().getName();
		this.sourceTerm = ttl.getSourceTerm().getName();
		this.destTermTaxonomy = ttl.getDestinationTerm().getTaxonomy().getName();
		this.destTerm = ttl.getDestinationTerm().getName();
		this.verb = ttl.getVerb();
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

	public String getOrigSourceTermTaxonomy() {
		return origSourceTermTaxonomy;
	}

	public void setOrigSourceTermTaxonomy(String origSourceTermTaxonomy) {
		this.origSourceTermTaxonomy = origSourceTermTaxonomy;
	}

	public String getOrigSourceTerm() {
		return origSourceTerm;
	}

	public void setOrigSourceTerm(String origSourceTerm) {
		this.origSourceTerm = origSourceTerm;
	}

	public String getOrigDestTermTaxonomy() {
		return origDestTermTaxonomy;
	}

	public void setOrigDestTermTaxonomy(String origDestTermTaxonomy) {
		this.origDestTermTaxonomy = origDestTermTaxonomy;
	}

	public String getOrigDestTerm() {
		return origDestTerm;
	}

	public void setOrigDestTerm(String origDestTerm) {
		this.origDestTerm = origDestTerm;
	}

	public Verb getVerb() {
		return verb;
	}

	public void setVerb(Verb verb) {
		this.verb = verb;
	}

}
