package org.gcube.resource.management.quota.manager.check;

import java.util.ArrayList;
import java.util.List;

import org.gcube.accounting.analytics.UsageValue;
import org.gcube.accounting.analytics.persistence.AccountingPersistenceQuery;
import org.gcube.resource.management.quota.library.quotalist.QuotaType;
import org.gcube.resource.management.quota.manager.defaults.InformationSystemQuery;
import org.gcube.resource.management.quota.manager.persistence.QuotaDBPersistence;
import org.gcube.resource.management.quota.manager.util.Constants;
import org.gcube.resource.management.quota.manager.util.DiscoveryListUser;
import org.gcube.resource.management.quota.manager.util.QuotaUsageServiceValue;
import org.gcube.resource.management.quota.manager.util.QuotaUsageStorageValue;
import org.gcube.resource.management.quota.manager.util.ReadFileProperties;
import org.gcuberesource.management.quota.manager.service.exception.NotFoundQuotaExecption;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.couchbase.client.deps.io.netty.handler.timeout.TimeoutException;

/**
 * Quota check use a specify context 
 * Verify for context all quota inserted
 *  
 * @author Alessandro Pieve (alessandro.pieve@isti.cnr.it)
 *
 */
public class QuotaCheck {

	private static Logger log = LoggerFactory.getLogger(QuotaCheck.class);
	private QuotaUsage queryQuotaUsage;
	private QuotaDBPersistence quotaDbPersistence;
	
	private AccountingPersistenceQuery apq;
	private String context=null;

	/**
	 * List quota for specify context
	 * @param context
	 * @throws NotFoundQuotaExecption
	 * @throws JSONException 
	 */
	public QuotaCheck(String context,QuotaUsage queryQuotaUsage,QuotaDBPersistence quotaDbPersistence,AccountingPersistenceQuery apq) throws NotFoundQuotaExecption{
		this.context=context;
		this.queryQuotaUsage=queryQuotaUsage;
		this.quotaDbPersistence=quotaDbPersistence;
		this.apq=apq;
	}
	public void getQuotaCheck() throws Exception{
		
		log.info("QuotaCheck - init quota check task");

		QuotaCalculateUtil quotaUtility= new QuotaCalculateUtil(context,quotaDbPersistence);

		//create a list quota found on information System
		InformationSystemQuery informationSystemQuery =new InformationSystemQuery();
		log.debug("QuotaCheck - context:{}, setQuoteDefault:{}",context,informationSystemQuery.getListQuotaDefault());
		
		//set quota utility with quota default found on is
		quotaUtility.setQuoteDefault(informationSystemQuery.getListQuotaDefault());
		
		//TODO verify of not a quota default and get file properties
		if (quotaUtility.getQuoteDefault()==null){
			log.debug("QuotaCheck - No quota default found on IS");
			ReadFileProperties fileQuota = new ReadFileProperties(Constants.FILE_PROPERTIES_QUOTA);
			quotaUtility.setQuoteDefault(fileQuota.getListQuotaDefault());
		}
		
		//list of user for specify context
		DiscoveryListUser discoveryListUser= new DiscoveryListUser(this.context);	
		log.debug("QuotaCheck - for context:{} , list user:{}",context,discoveryListUser.getListUser());
		
		
		
		
		//verify for each user if have a specify quota and Overwrite if exist and traduce into object for query accounting
		quotaUtility.verifyListUser(discoveryListUser.getListUser());
		
		log.debug("QuotaCheck - quotaUtility.getUsageToBeVerified():{}",quotaUtility.getUsageToBeVerified());
			
		Boolean error=true;
		//init accounting Persistence
		
		//AccountingPersistenceQuery apq = AccountingPersistenceQueryFactory.getInstance();
		List<UsageValue> quoteChecked=new ArrayList<UsageValue>();
		try {
			quoteChecked=apq.getUsageValueQuotaTotal(quotaUtility.getUsageToBeVerified());
			error=false;
		}catch(TimeoutException te){
			log.error("error timeout on call accounting",te);
			quoteChecked=apq.getUsageValueQuotaTotal(quotaUtility.getUsageToBeVerified());
		}		
		catch (Exception e) {
			log.error("error on call accounting",e);
			quoteChecked=apq.getUsageValueQuotaTotal(quotaUtility.getUsageToBeVerified());			
		}		
		
		log.error("error:{}",error);
		
		if (!error){
			log.debug("return quota usage:{}",quoteChecked);
			//insert into db
			for(UsageValue usageIndex:quotaUtility.getUsageToBeVerified()){
				if (usageIndex.getClz()==QuotaType.STORAGE.getQuotaTypeClass()){
					log.debug("----Elaborate a identifier:{}, temporalConstraint:{} , insert a quota storage",usageIndex.getIdentifier(),usageIndex.getTemporalConstraint());
					QuotaUsageStorageValue usageStorVal=(QuotaUsageStorageValue) usageIndex;
					queryQuotaUsage.insertStorageQuota(usageStorVal);
				}
				if (usageIndex.getClz()==QuotaType.SERVICE.getQuotaTypeClass()){
					log.debug("----Elaborate a identifier:{}, temporalConstraint:{} , insert a quota service",usageIndex.getIdentifier(),usageIndex.getTemporalConstraint());					
					QuotaUsageServiceValue usageSerVal=(QuotaUsageServiceValue) usageIndex;
					queryQuotaUsage.insertServiceQuota(usageSerVal);
				}
			}
			queryQuotaUsage.SendNotificationAdmin();
	
		}
		
		//TODO  
		/*
		log.info("Checked quota:{}",quoteChecked);
		for(UsageValue usageValue:quoteChecked){
			//set in quota entity usageValue
			quotaPersistence.setUsageQuota(quoteInsert.get(usageValue.getIdentifier()),usageValue.getD());		

			log.info("For identifier:{} quota/usage is:{}/{}",usageValue.getIdentifier(),quoteInsert.get(usageValue.getIdentifier()),usageValue.getD());
			if (usageValue.getD()>quoteInsert.get(usageValue.getIdentifier()).getQuotaValue()){
				insertPolicyBlock(quoteInsert.get(usageValue.getIdentifier()));
			}
		}
		log.info("QuotaCheck end");
		 */
	}


}
