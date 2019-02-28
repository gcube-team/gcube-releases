package gr.cite.gaap.datatransferobjects;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountLockInfo {
	
	private static Logger logger = LoggerFactory.getLogger(AccountLockInfo.class);

	private String id;
	private long timestamp;
	private Set<String> addresses;
	private long timeToUnlock;

	public AccountLockInfo() {
	}

	public AccountLockInfo(String id, long timestamp, Set<String> addresses, long timeToUnlock) {
		logger.trace("Initializing AccountLockInfo...");
		this.id = id;
		this.timestamp = timestamp;
		this.addresses = addresses;
		this.timeToUnlock = timeToUnlock;
		logger.trace("Initialized AccountLockInfo");
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

	public Set<String> getAddress() {
		return addresses;
	}

	public void setAddress(Set<String> addresses) {
		this.addresses = addresses;
	}

	public long getTimeToUnlock() {
		return timeToUnlock;
	}

	public void setTimeToUnlock(long timeToUnlock) {
		this.timeToUnlock = timeToUnlock;
	}

}
