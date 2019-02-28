package gr.cite.gaap.datatransferobjects;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShapeImportInstance {
	private static Logger logger = LoggerFactory.getLogger(ShapeImportInstance.class);

	private UUID importId = null;
	private long timestamp = 0;
	private String termTaxonomy = null;
	private String term = null;

	public ShapeImportInstance() {
		logger.trace("Initialized default contructor for ShapeImportInstance");
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
