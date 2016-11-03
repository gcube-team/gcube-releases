package gr.cite.gaap.datatransferobjects;

public class UserLastUnsuccessfulLoginInfo {
	private String id;
	private long timestamp;
	private int times;

	public UserLastUnsuccessfulLoginInfo(String id, long timestamp, int times) {
		this.id = id;
		this.timestamp = timestamp;
		this.times = times;
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
