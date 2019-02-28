package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrincipalLastActionInfo {
	
	private static Logger logger = LoggerFactory.getLogger(PrincipalLastActionInfo.class);
	private String id;
	private String entityType;
	private String action;
	private long timestamp;

	public PrincipalLastActionInfo(String id, String entityType, String action, long timestamp) {
		logger.trace("Initializing PrincipalLastActionInfo...");
		this.id = id;
		this.entityType = entityType;
		this.action = action;
		this.timestamp = timestamp;
		logger.trace("Initialized PrincipalLastActionInfo");
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

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
}
