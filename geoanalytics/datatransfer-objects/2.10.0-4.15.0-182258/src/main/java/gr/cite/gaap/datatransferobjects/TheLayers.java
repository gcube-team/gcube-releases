package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TheLayers {
	private static Logger logger = LoggerFactory.getLogger(TheLayers.class);

	private String[] jstreeLayers;
	private boolean skipped;
	
	public TheLayers() {
		super();
		logger.trace("Initialized default contructor for TheLayers");

	}
	public String[] getJstreeLayers() {
		return jstreeLayers;
	}
	public void setJstreeLayers(String[] jstreeLayers) {
		this.jstreeLayers = jstreeLayers;
	}
	public boolean isSkipped() {
		return skipped;
	}
	public void setSkipped(boolean skipped) {
		this.skipped = skipped;
	}
}
