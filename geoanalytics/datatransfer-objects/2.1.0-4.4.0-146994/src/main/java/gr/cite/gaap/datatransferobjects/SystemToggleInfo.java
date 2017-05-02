package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemToggleInfo {
	private static Logger logger = LoggerFactory.getLogger(SystemToggleInfo.class);

	public boolean status;
	public boolean error;

	public SystemToggleInfo(boolean status, boolean error) {
		logger.trace("Initializing SystemToggleInfo...");

		this.status = status;
		this.error = error;
		logger.trace("Initialized SystemToggleInfo");

	}
}
