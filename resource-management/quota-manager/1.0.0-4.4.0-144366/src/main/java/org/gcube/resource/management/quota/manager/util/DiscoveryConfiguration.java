package org.gcube.resource.management.quota.manager.util;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.security.Key;
import java.util.List;
import java.util.Map;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.common.resources.gcore.utils.Group;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscoveryConfiguration {
	
	private String databasePath = null;
	private String usernameDb = null;
	private String pwdnameDb = null;
	private String dbname=null;
	private Boolean notifierUser=false;
	private Boolean notifierAdmin=false;
	private Integer refreshTimeQuota=0;
	
	private String textQuotaUserWarning="Hi {identifier}, your storage space is almost full, Your quota occupation is {percent} %, you are using {quotaUsage} out of {quotaAssigned} available.Please be adviced.";
	private String textQuotaUserWarningSubject="WARNING Your storage space is about to exceed quota capacity";
	
	private String textQuotaUserExceedSubject="ATTENTION  Your storage space exceeds the assigned quota capacity";
	private String textQuotaUserExceed="Hi {identifier}, your storage space is full, Your quota occupation is {percent}% , you are using {quotaUsage} out of {quotaAssigned} available. Please free up some space.";
	
	private String textQuotaUserChangeSubject="INFO your quota has changed";
	private String textQuotaUserChange="Hi {identifier},your quota has changed to {quotaAssigned}. Your are currently occupying {percent}% of the available space.";
			
	
	private String textQuotaAdminExceedSubject="INFO  List User quota Exceed";
	private String textQuotaAdminExceed="Dear, the following users have exceeded their quota: {listuser}";
	
	private String textQuotaUserReset="Hi {identifier}, Your quota occupation decreased and is now {percent}%, you are using {quotaUsage}, out of  {quotaAssigned} available. Good job!";
	private String textQuotaUserResetSubject="INFO Your storage space is now sufficiently below the quota capacity";

	
	
	private String roleNotifier="Administrator";

	private static Logger logger = LoggerFactory.getLogger(DiscoveryConfiguration.class);
	private final static String RUNTIME_RESOURCE_NAME = "Persistence";
	private final static String CATEGORY = "Quota";
	private final static String ACCESS_POINT_NAME="postgresql Server";
	private final static String DB_NAME="dbname";
	private final static String NOTIFIER_USER="notifierUser";
	private final static String NOTIFIER_ADMINISTRATOR="notifierAdministrator";
	private final static String REFRESH_TIME_QUOTA="refreshTimeQuota";
	
	private final static String TEXT_QUOTA_USER_WARNING_SUBJECT="textQuotaUserWarningSubject";
	private final static String TEXT_QUOTA_USER_WARNING="textQuotaUserWarning";
	
	private final static String TEXT_QUOTA_USER_EXCEED_SUBJECT="textQuotaUserExceedSubject";
	private final static String TEXT_QUOTA_USER_EXCEED="textQuotaUserExceed";
	
	private final static String TEXT_QUOTA_USER_CHANGE_SUBJECT="textQuotaUserChangeSubject";
	private final static String TEXT_QUOTA_USER_CHANGE="textQuotaUserChange";
	
	private final static String TEXT_QUOTA_ADMIN_EXCEED_SUBJECT="textQuotaAdminExceedSubject";
	private final static String TEXT_QUOTA_ADMIN_EXCEED="textQuotaAdminExceed";
	
	private final static String TEXT_QUOTA_USER_RESET_SUBJECT="textQuotaUserResetSubject";
	private final static String TEXT_QUOTA_USER_RESET_EXCEED="textQuotaUserReset";
	
	private final static String ROLE_NOTIFIER="roleNotifier";
	
	
	public DiscoveryConfiguration(String context){
		if(context == null || context.isEmpty())
			throw new IllegalArgumentException("A valid context is needed to discover the service");
		logger.debug("find a resources from service end point in context:{}",context);
		String oldContext = ScopeProvider.instance.get();
		logger.debug("find a resources from service end point oldContext:{}",oldContext);
		ScopeProvider.instance.set(context);
		try{
			ServiceEndpoint resources =getServiceEndpoint(context);
			logger.debug("find a resources from service end point");
			setValues(resources);
		}catch(Exception e){
			logger.error("Unable to retrieve such service endpoint information!", e);

		}finally{
			if(oldContext != null && !oldContext.equals(context))
				ScopeProvider.instance.set(oldContext);

		}
		logger.info("Found base path " + databasePath + " for the service");

	}

	/**
	 * Retrieve endpoints information from IS for DB
	 * @return list of endpoints 
	 * @throws Exception
	 */
	protected ServiceEndpoint getServiceEndpoint(String context){
		
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		
		query.addCondition("$resource/Profile/Name/text() eq '"+ RUNTIME_RESOURCE_NAME +"'");
		query.addCondition("$resource/Profile/Category/text() eq '"+ CATEGORY +"'");
		query.setResult("$resource");
		logger.debug("DiscoveryConfiguration query:{}",query.toString());
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> serviceEndpoints = client.submit(query);
	
		return serviceEndpoints.get(0);
	}
	
	protected void setValues(ServiceEndpoint serviceEndpoint) throws Exception{
		Group<AccessPoint> accessPoints = serviceEndpoint.profile().accessPoints();
		for(AccessPoint accessPoint : accessPoints){
			logger.debug("accessPoint:{}",accessPoint.name());
			if(accessPoint.name().compareTo(ACCESS_POINT_NAME)==0){

				databasePath=accessPoint.address();
				usernameDb=accessPoint.username();
				pwdnameDb=decrypt(accessPoint.password());
				Map<String, Property> propertyMap = accessPoint.propertyMap();
				for(String key : propertyMap.keySet()){
					Property property = propertyMap.get(key);
					String value = property.value();
					if(property.isEncrypted()){
						value = decrypt(value);
					}
					
					
					
					switch (key) {

						case DB_NAME:
							dbname=value;
							break;
						case NOTIFIER_USER:
							notifierUser=Boolean.valueOf(value);
							break;
						case NOTIFIER_ADMINISTRATOR:
							notifierAdmin=Boolean.valueOf(value);
							break;
						case REFRESH_TIME_QUOTA:
							refreshTimeQuota=Integer.valueOf(value);
							break;
						case TEXT_QUOTA_USER_WARNING_SUBJECT:
							textQuotaUserWarningSubject=value;
							break;
						case TEXT_QUOTA_USER_WARNING:
							textQuotaUserWarning=value;
							break;
						case TEXT_QUOTA_USER_EXCEED_SUBJECT:
							textQuotaUserExceedSubject=value;
							break;
						case TEXT_QUOTA_USER_EXCEED:
							textQuotaUserExceed=value;
							break;
						case TEXT_QUOTA_USER_CHANGE_SUBJECT:
							textQuotaUserChangeSubject=value;
							break;
						case TEXT_QUOTA_USER_CHANGE:
							textQuotaUserChange=value;
							break;
						case TEXT_QUOTA_ADMIN_EXCEED_SUBJECT:
							textQuotaAdminExceedSubject=value;
							break;
						case TEXT_QUOTA_ADMIN_EXCEED:
							textQuotaAdminExceed=value;
							break;
						case TEXT_QUOTA_USER_RESET_SUBJECT:
							textQuotaUserResetSubject=value;
							break;
						case TEXT_QUOTA_USER_RESET_EXCEED:
							textQuotaUserReset=value;
							break;
						case ROLE_NOTIFIER:
							roleNotifier=value;
							break;
					}

				}
			}
		}
	}
	
	
	private static String decrypt(String encrypted, Key... key) throws Exception {
		return StringEncrypter.getEncrypter().decrypt(encrypted);
	}
	
	


	@Override
	public String toString() {
		return "DiscoveryConfiguration [databasePath=" + databasePath
				+ ", usernameDb=" + usernameDb + ", pwdnameDb=" + pwdnameDb
				+ ", dbname=" + dbname + ", notifierUser=" + notifierUser
				+ ", notifierAdmin=" + notifierAdmin + ", refreshTimeQuota="
				+ refreshTimeQuota + ", textQuotaUserWarning="
				+ textQuotaUserWarning + ", textQuotaUserWarningSubject="
				+ textQuotaUserWarningSubject + ", textQuotaUserExceedSubject="
				+ textQuotaUserExceedSubject + ", textQuotaUserExceed="
				+ textQuotaUserExceed + ", textQuotaUserChangeSubject="
				+ textQuotaUserChangeSubject + ", textQuotaUserChange="
				+ textQuotaUserChange + ", textQuotaAdminExceedSubject="
				+ textQuotaAdminExceedSubject + ", textQuotaAdminExceed="
				+ textQuotaAdminExceed + ", textQuotaUserReset="
				+ textQuotaUserReset + ", textQuotaUserResetSubject="
				+ textQuotaUserResetSubject + ", roleNotifier=" + roleNotifier
				+ "]";
	}

	public String getDatabasePath() {
		return databasePath;
	}
	
	public String getUsernameDb() {
		return usernameDb;
	}
	
	public String getPwdnameDb() {
		return pwdnameDb;
	}

	public String getDbname() {
		return dbname;
	}

	public Boolean getNotifierUser() {
		return notifierUser;
	}

	public Boolean getNotifierAdmin() {
		return notifierAdmin;
	}

	public Integer getRefreshTimeQuota() {
		return refreshTimeQuota;
	}

	public String getTextQuotaUserWarningSubject() {
		return textQuotaUserWarningSubject;
	}
	public String getTextQuotaUserWarning() {
		return textQuotaUserWarning;
	}
	
	public String getTextQuotaUserExceedSubject() {
		return textQuotaUserExceedSubject;
	}
	public String getTextQuotaUserExceed() {
		return textQuotaUserExceed;
	}

	public String getTextQuotaAdminExceedSubject() {
		return textQuotaAdminExceedSubject;
	}
	public String getTextQuotaAdminExceed() {
		return textQuotaAdminExceed;
	}

	public String getTextQuotaUserReset() {
		return textQuotaUserReset;
	}

	public String getTextQuotaUserResetSubject() {
		return textQuotaUserResetSubject;
	}

	public String getRoleNotifier() {
		return roleNotifier;
	}

	public String getTextQuotaUserChangeSubject() {
		return textQuotaUserChangeSubject;
	}

	public String getTextQuotaUserChange() {
		return textQuotaUserChange;
	}

	
	
	
}
