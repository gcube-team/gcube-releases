package gr.cite.gaap.datatransferobjects;

public class UserLastLoginInfo {
	private String id;
	private long timestamp;

	public UserLastLoginInfo(String id, long timestamp) {
		this.id = id;
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
}
