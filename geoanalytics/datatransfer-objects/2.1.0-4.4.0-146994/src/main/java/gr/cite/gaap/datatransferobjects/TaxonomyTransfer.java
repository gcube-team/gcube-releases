package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaxonomyTransfer {
	private static Logger logger = LoggerFactory.getLogger(TaxonomyTransfer.class);

	private boolean active = false;
	
	public TaxonomyTransfer() {
		super();
		logger.trace("Initialized default contructor for TaxonomyTransfer");

	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}

}