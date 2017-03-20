package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserLastUnsuccessfulLoginInfo {
	private static Logger logger = LoggerFactory.getLogger(UserLastUnsuccessfulLoginInfo.class);

	private String id;
	private long timestamp;
	private int times;

	public UserLastUnsuccessfulLoginInfo(String id, long timestamp, int times) {
		logger.trace("Initializing UserLastUnsuccessfulLoginInfo...");

		this.id = id;
		this.timestamp = timestamp;
		this.times = times;
		logger.trace("Initialized UserLastUnsuccessfulLoginInfo");

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
