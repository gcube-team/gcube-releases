package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserLastLoginInfo {
	private static Logger logger = LoggerFactory.getLogger(UserLastLoginInfo.class);

	private String id;
	private long timestamp;

	public UserLastLoginInfo(String id, long timestamp) {
		logger.trace("Initializing UserLastLoginInfo...");

		this.id = id;
		this.timestamp = timestamp;
		logger.trace("Initialized UserLastLoginInfo");

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
