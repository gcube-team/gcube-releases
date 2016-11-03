package gr.cite.gaap.datatransferobjects;

public class IllegalAccessAuditingInfo {
	private String id;
	private long timestamp;
	private String info;

	public IllegalAccessAuditingInfo() {
	}

	public IllegalAccessAuditingInfo(String id, long timestamp, String info) {
		this.id = id;
		this.timestamp = timestamp;
		this.info = info;
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
