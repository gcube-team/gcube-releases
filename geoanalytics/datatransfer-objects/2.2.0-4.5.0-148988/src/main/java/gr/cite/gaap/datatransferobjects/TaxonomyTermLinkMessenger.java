package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.geoanalytics.dataaccess.entities.geocode.TaxonomyTermLink;
import gr.cite.geoanalytics.dataaccess.entities.geocode.TaxonomyTermLink.Verb;


public class TaxonomyTermLinkMessenger {
	private static Logger logger = LoggerFactory.getLogger(TaxonomyTermLinkMessenger.class);

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
		logger.trace("Initialized default contructor for TaxonomyTermLinkMessenger");
	}

	public TaxonomyTermLinkMessenger(TaxonomyTermLink ttl) {
		logger.trace("Initializing TaxonomyTermLinkMessenger...");

		this.sourceTermTaxonomy = ttl.getSourceTerm().getGeocodeSystem().getName();
		this.sourceTerm = ttl.getSourceTerm().getName();
		this.destTermTaxonomy = ttl.getDestinationTerm().getGeocodeSystem().getName();
		this.destTerm = ttl.getDestinationTerm().getName();
		this.verb = ttl.getVerb();
		logger.trace("Initialized TaxonomyTermLinkMessenger");

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
