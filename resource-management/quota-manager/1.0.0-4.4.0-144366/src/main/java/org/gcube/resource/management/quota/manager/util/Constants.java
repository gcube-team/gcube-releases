package org.gcube.resource.management.quota.manager.util;
/**
 * Constant use for schedule a check quota
 * @author pieve
 *
 */
public class Constants {
	public static final Long TIME_SCHEDULE_CHECK = 180*1000L;
	public static final Long DELAY_SCHEDULE_CHECK = (long) (30*1000);
	
	
	
	// IS Resource
	public static final String QUOTA_NAME = "QuotaDefault";
	public static final String QUOTA_CATEGORY = "Quota";
	
	
	//File properties
	public static final String FILE_PROPERTIES_QUOTA="/tmp/quota.xml";
	
	
	//Limit msg for quota
	public static final Integer LIMIT_MSG_QUOTA_PERC_USAGE_1=90;
	
	public static final Integer LIMIT_MSG_QUOTA_PERC_USAGE_2=95;
	
	
}
