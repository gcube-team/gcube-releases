package gr.cite.gaap.datatransferobjects;

public class PrincipalLastActionInfo {
	private String id;
	private String entityType;
	private String action;
	private long timestamp;

	public PrincipalLastActionInfo(String id, String entityType, String action, long timestamp) {
		this.id = id;
		this.entityType = entityType;
		this.action = action;
		this.timestamp = timestamp;
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
