package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IllegalAccessAuditingInfo {
	
	private static Logger logger = LoggerFactory.getLogger(IllegalAccessAuditingInfo.class);
	private String id;
	private long timestamp;
	private String info;

	public IllegalAccessAuditingInfo() {
		logger.trace("Initialized default contructor for IllegalAccessAuditingInfo");
	}

	public IllegalAccessAuditingInfo(String id, long timestamp, String info) {
		logger.trace("Initializing IllegalAccessAuditingInfo...");
		this.id = id;
		this.timestamp = timestamp;
		this.info = info;
		logger.trace("Initialized IllegalAccessAuditingInfo");
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

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

}
