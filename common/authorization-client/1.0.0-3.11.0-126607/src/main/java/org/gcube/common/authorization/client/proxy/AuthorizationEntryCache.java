package org.gcube.common.authorization.client.proxy;

import org.gcube.common.authorization.client.Constants;
import org.gcube.common.authorization.library.AuthorizationEntry;

public class AuthorizationEntryCache {
		
	private AuthorizationEntry entry;
	private long creationDate;
	
	public AuthorizationEntryCache(AuthorizationEntry entry) {
		super();
		this.entry = entry;
		this.creationDate = System.currentTimeMillis();
	}
	public AuthorizationEntry getEntry() {
		return entry;
	}
		
	public boolean isValid(){
		return (System.currentTimeMillis()-Constants.TIME_TO_LIVE_CACHE_IN_MILLIS)<this.creationDate;
	}
}
