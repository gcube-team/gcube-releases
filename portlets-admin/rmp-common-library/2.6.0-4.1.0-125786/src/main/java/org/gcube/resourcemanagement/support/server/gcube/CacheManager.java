package org.gcube.resourcemanagement.support.server.gcube;

public class CacheManager {
	private boolean useCache = false;


	public CacheManager() {
		// for serialization only
	}

	public boolean isUsingCache() {
		return this.useCache;
	}

	public void setUseCache(final boolean useCache) {
		this.useCache = useCache;
	}
}
