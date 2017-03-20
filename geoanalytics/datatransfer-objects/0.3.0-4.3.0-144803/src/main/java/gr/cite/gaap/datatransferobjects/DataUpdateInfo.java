package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataUpdateInfo {
	private static Logger logger = LoggerFactory.getLogger(DataUpdateInfo.class);
	private String updater;
	private String entity;
	private String entityType;
	private long timestamp;

	public DataUpdateInfo(String entity, String entityType, long timestamp, String updater) {
		logger.trace("Initializing DataUpdateInfo...");
		this.updater = updater;
		this.entity = entity;
		this.entityType = entityType;
		this.timestamp = timestamp;
		logger.trace("Initialized DataUpdateInfo");
	}

	public DataUpdateInfo(String entity, String entityType, long timestamp) {
		logger.trace("Initializing DataUpdateInfo...");
		this.entity = entity;
		this.entityType = entityType;
		this.timestamp = timestamp;
		logger.trace("Initialized DataUpdateInfo");
	}

	public String getUpdater() {
		return updater;
	}

	public void setUpdater(String updater) {
		this.updater = updater;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}
