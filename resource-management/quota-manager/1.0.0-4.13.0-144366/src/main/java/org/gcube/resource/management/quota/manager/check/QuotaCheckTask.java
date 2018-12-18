package org.gcube.resource.management.quota.manager.check;

import java.util.Date;
import java.util.TimerTask;

import org.gcube.accounting.analytics.persistence.AccountingPersistenceQuery;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resource.management.quota.manager.persistence.QuotaDBPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Quota Check Task 
 * Run with timerTask each DELAY_SCHEDULE_CHEK 
 * 
 * 
 *@author Alessandro Pieve (alessandro.pieve@isti.cnr.it)
 *
 */
public class QuotaCheckTask extends TimerTask {

	private static Logger log = LoggerFactory.getLogger(QuotaCheckTask.class);
	
	String context;
	String token;
	
	private QuotaUsage quotaUsage;
	private QuotaDBPersistence quotaDbPersistence;
	private AccountingPersistenceQuery apq;
	
	public QuotaCheckTask(String context, String token, QuotaUsage quotaUsage,QuotaDBPersistence quotaDbPersistence,AccountingPersistenceQuery apq) {
		super();
		this.context = context;
		this.token = token;
		this.quotaUsage = quotaUsage;
		this.quotaDbPersistence = quotaDbPersistence;
		this.apq=apq;
	}

	
	
	
	@Override
	public void run() {
		//TODO Prendere il contesto del token 
		String context=ScopeProvider.instance.get();
	
		ScopeProvider.instance.set(context);
		log.info("Timer task run at:{} in this context:{}",new Date(),context);
		try {
			QuotaCheck quotaCheck= new QuotaCheck(context,quotaUsage,quotaDbPersistence,apq);
			quotaCheck.getQuotaCheck();
			log.info("Timer finish");
		} catch (Exception e) {
			log.warn("No quota found!");
		}
		
		
		
	}
	
}