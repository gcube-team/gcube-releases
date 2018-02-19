package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserLastPasswordRequestInfo {
	private static Logger logger = LoggerFactory.getLogger(UserLastPasswordRequestInfo.class);

	private String id;
	private long timestamp;
	private int times;

	public UserLastPasswordRequestInfo(String id, long timestamp, int times) {
		logger.trace("Initializing UserLastPasswordRequestInfo...");

		this.id = id;
		this.timestamp = timestamp;
		this.times = times;
		logger.trace("Initialized UserLastPasswordRequestInfo");
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

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}
}
