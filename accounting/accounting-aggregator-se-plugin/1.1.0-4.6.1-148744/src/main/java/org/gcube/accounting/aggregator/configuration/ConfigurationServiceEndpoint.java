package org.gcube.accounting.aggregator.configuration;

/**
 * @author Alessandro Pieve (ISTI - CNR) 
 *
 */

public class ConfigurationServiceEndpoint {
	
	//Static Key for Configuration from service end point
	public static final String URL_PROPERTY_KEY = "URL";
	public static final String PASSWORD_PROPERTY_KEY = "password";
	public static final String BUCKET_NAME_PROPERTY_KEY = "bucketName";

	public static final String BUCKET_STORAGE_NAME_PROPERTY_KEY="AggregatedStorageUsageRecord";
	public static final String BUCKET_SERVICE_NAME_PROPERTY_KEY="AggregatedServiceUsageRecord";
	public static final String BUCKET_PORTLET_NAME_PROPERTY_KEY="AggregatedPortletUsageRecord";
	public static final String BUCKET_JOB_NAME_PROPERTY_KEY="AggregatedJobUsageRecord";
	public static final String BUCKET_TASK_NAME_PROPERTY_KEY="AggregatedTaskUsageRecord";
	
	
	public static final String BUCKET_STORAGE_TYPE="StorageUsageRecord";
	public static final String BUCKET_SERVICE_TYPE="ServiceUsageRecord";
	public static final String BUCKET_PORTLET_TYPE="PortletUsageRecord";
	public static final String BUCKET_JOB_TYPE="JobUsageRecord";
	public static final String BUCKET_TASK_TYPE="TaskUsageRecord";
}
