package org.gcube.data.publishing.gCatFeeder.service.engine;

public interface LocalConfiguration {

	public static final String POOL_MAX_IDLE="db.pools.max_idle";
	public static final String POOL_MAX_TOTAL="db.pools.max_total";
	public static final String POOL_MIN_IDLE="db.pools.min_total";
	
	
	public static final String DB_ENDPOINT_NAME="db.ep.name";
	public static final String DB_ENDPOINT_CATEGORY="db.ep.category";
	
	
	
	public String getProperty(String propertyName);
	
}
