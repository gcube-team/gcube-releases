package org.gcube.portlets.user.workflowdocuments.shared;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class LockInfo implements Serializable {
	
	private boolean locked;
	private String lockedby;
	private Date expirationTime;
	
	public LockInfo() {	}

	public LockInfo(boolean locked, String lockedby, Date expirationTime) {
		super();
		this.locked = locked;
		this.lockedby = lockedby;
		this.expirationTime = expirationTime;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public String getLockedby() {
		return lockedby;
	}

	public void setLockedby(String lockedby) {
		this.lockedby = lockedby;
	}

	public Date getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(Date expirationTime) {
		this.expirationTime = expirationTime;
	}
	
}
