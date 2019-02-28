package org.gcube.resource.management.quota.manager.check;

import java.util.Timer;

import org.gcube.accounting.analytics.persistence.AccountingPersistenceQuery;
import org.gcube.accounting.analytics.persistence.AccountingPersistenceQueryFactory;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resource.management.quota.manager.persistence.QuotaDBPersistence;
import org.gcube.resource.management.quota.manager.util.Constants;
import org.gcube.resource.management.quota.manager.util.DiscoveryConfiguration;
import org.gcube.smartgears.ApplicationManager;
import org.gcube.smartgears.ContextProvider;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MyApp Manager
 *  
 * @author Alessandro Pieve (alessandro.pieve@isti.cnr.it)
 *
 */
public class MyAppManager implements ApplicationManager {

	private static Logger logger = LoggerFactory.getLogger(MyAppManager.class);

	ApplicationContext ctx = ContextProvider.get();

	Timer timer = null;
	
	public String dbName;
	
	private QuotaUsage quotaUsage;
	private QuotaDBPersistence quotaDbPersistence;
	
	@Override
	public void onInit() {
		//do something on init
		logger.info("QuotaCheckTask on init");
		String token = SecurityTokenProvider.instance.get();
		String context = ScopeProvider.instance.get();
		logger.info("QuotaCheckTask on discovery");
		DiscoveryConfiguration discoveryCheck =new DiscoveryConfiguration(context);
		logger.info("QuotaCheckTask DiscoveryConfiguration complete");
		dbName=discoveryCheck.getDbname();
		
		quotaDbPersistence= new QuotaDBPersistence(discoveryCheck.getDatabasePath(),discoveryCheck.getDbname(),
				discoveryCheck.getUsernameDb(),discoveryCheck.getPwdnameDb());
		quotaUsage =new QuotaUsage(discoveryCheck);
		
		AccountingPersistenceQuery apq = AccountingPersistenceQueryFactory.getInstance();
		
		QuotaCheckTask task = new QuotaCheckTask(context, token,quotaUsage,quotaDbPersistence,apq);
		
		Integer refreshTimeQuotaConfiguration=discoveryCheck.getRefreshTimeQuota();
		Long refreshTimeQuota;
		if (refreshTimeQuotaConfiguration==0){
			refreshTimeQuota=Constants.TIME_SCHEDULE_CHECK;
		}
		else{
			refreshTimeQuota=(long) (refreshTimeQuotaConfiguration*60*1000);
		}		
		timer = new Timer(true);
		timer.scheduleAtFixedRate(task,Constants.DELAY_SCHEDULE_CHECK,  refreshTimeQuota);
		logger.info("QuotaCheckTask started in context {}", context);
		logger.info("QuotaCheckTask schedule task on:{} milliseconds", refreshTimeQuota);
		logger.info("QuotaCheckTask notifierUser :{} notifierAdmin:{}", discoveryCheck.getNotifierUser(),discoveryCheck.getNotifierAdmin());
	}

	@Override
	public void onShutdown() {
		timer.cancel();
	}

	public QuotaUsage getQuotaUsage() {
		return quotaUsage;
	}

	public QuotaDBPersistence getQuotaDbPersistence() {
		return quotaDbPersistence;
	}

	

	
	
	
}
