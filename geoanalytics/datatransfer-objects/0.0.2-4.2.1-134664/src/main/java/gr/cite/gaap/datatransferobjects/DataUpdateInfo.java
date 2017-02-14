package gr.cite.gaap.datatransferobjects;

public class DataUpdateInfo {
	private String updater;
	private String entity;
	private String entityType;
	private long timestamp;

	public DataUpdateInfo(String entity, String entityType, long timestamp, String updater) {
		this.updater = updater;
		this.entity = entity;
		this.entityType = entityType;
		this.timestamp = timestamp;
	}

	public DataUpdateInfo(String entity, String entityType, long timestamp) {
		this.entity = entity;
		this.entityType = entityType;
		this.timestamp = timestamp;
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
