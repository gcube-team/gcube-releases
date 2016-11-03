package gr.cite.gaap.datatransferobjects;

import java.util.UUID;

public class ShapeImportInstance {
	private UUID importId = null;
	private long timestamp = 0;
	private String termTaxonomy = null;
	private String term = null;

	public ShapeImportInstance() {
	}

	public UUID getImportId() {
		return importId;
	}

	public void setImportId(UUID importId) {
		this.importId = importId;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getTermTaxonomy() {
		return termTaxonomy;
	}

	public void setTermTaxonomy(String termTaxonomy) {
		this.termTaxonomy = termTaxonomy;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}
}
