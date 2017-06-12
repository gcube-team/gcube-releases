package org.gcube.resource.management.quota.manager.check;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.gcube.accounting.analytics.TemporalConstraint;
import org.gcube.accounting.analytics.UsageValue;
import org.gcube.accounting.datamodel.AggregatedUsageRecord;
import org.gcube.common.authorization.library.policies.Policy;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.resource.management.quota.library.quotalist.Quota;
import org.gcube.resource.management.quota.library.quotalist.QuotaType;
import org.gcube.resource.management.quota.library.quotalist.ServiceQuota;
import org.gcube.resource.management.quota.library.quotalist.StorageQuota;
import org.gcube.resource.management.quota.library.quotalist.TimeInterval;
import org.gcube.resource.management.quota.library.quotedefault.QuotaDefault;
import org.gcube.resource.management.quota.library.quotedefault.ServiceQuotaDefault;
import org.gcube.resource.management.quota.library.quotedefault.StorageQuotaDefault;
import org.gcube.resource.management.quota.manager.persistence.QuotaDBPersistence;
import org.gcube.resource.management.quota.manager.util.QuotaUsageStorageValue;
import org.gcuberesource.management.quota.manager.service.exception.NotFoundQuotaPackageExecption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * QuotaCalculateUtil
 *  
 * @author Alessandro Pieve (alessandro.pieve@isti.cnr.it)
 *
 */
public class QuotaCalculateUtil {

	private static Logger log = LoggerFactory.getLogger(QuotaCalculateUtil.class);
	 
	private QuotaDBPersistence quotaDbPersistence;
	
	private List<QuotaDefault> quoteDefault=null;
	
	public void setQuoteDefault(List<QuotaDefault> quoteDefault) {
		this.quoteDefault = quoteDefault;
	}
	public List<QuotaDefault> getQuoteDefault() {
		return quoteDefault;
	}

	private String  context;
	private List<UsageValue> usageToBeVerified=new ArrayList<UsageValue>();
		
	public List<UsageValue> getUsageToBeVerified() {
		return usageToBeVerified;
	}
	
	
	public QuotaCalculateUtil(String context,QuotaDBPersistence quotaDbPersistence){		
		this.context=context;
		this.quotaDbPersistence=quotaDbPersistence;
	}

	/**
	 * Prepare list for of usage value for accounting from Quota
	 * @param quota
	 * @throws NotFoundQuotaPackageExecption
	 */
	public void verifyListUser(List<String> listUser){
		for (String identifier: listUser){
			log.trace("verify for user:{} in context:{},if have a no default quota",identifier,context);
			//loop for each quota default 
			for (QuotaDefault quotaDefault:quoteDefault){
				Quota quotaSpecified=null;
				//search a quota specified			
				quotaSpecified=quotaDbPersistence.getQuotaSpecified(identifier, context,quotaDefault.getQuotaType(),quotaDefault.getTimeInterval(), quotaDefault.getQuotaValue());
				log.debug("quota Specified:{} quotaDefault:{}",quotaSpecified,quotaDefault);
				
				
				//if not have a quota specified, use a default quota 
				if (quotaSpecified==null){ 
					//log.debug("identifier:{} used quota default for quota type:{}-{}-",identifier,quotaDefault.getQuotaType(),QuotaType.STORAGE.toString());
					if (quotaDefault.getQuotaType()==QuotaType.SERVICE){
						log.debug("identifier:{} used quota SERVICE",identifier);
						ServiceQuotaDefault quotaDefaultService = (ServiceQuotaDefault)quotaDefault;
						quotaSpecified = new ServiceQuota(context,identifier,quotaDefaultService.getCallerType(),
								quotaDefaultService.getServiceId(),quotaDefaultService.getTimeInterval(),
								quotaDefaultService.getQuotaValue(),quotaDefaultService.getAccessType());
					}
					if (quotaDefault.getQuotaType()==QuotaType.STORAGE){
						log.debug("identifier:{} used quota STORAGE",identifier);
						StorageQuotaDefault quotaDefaultStorage = (StorageQuotaDefault)quotaDefault;
						quotaSpecified = new StorageQuota(context,identifier,quotaDefaultStorage.getCallerType(),
								quotaDefaultStorage.getTimeInterval(),quotaDefaultStorage.getQuotaValue());
					}
				}
				
				try {
					log.debug("add quota into list for accounting usage value"+quotaSpecified.toString());
					//traduce a quota into list usageValue
					AddList(quotaSpecified);
				} catch (NotFoundQuotaPackageExecption e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		//TODO ADD quote non comprese nel default
	}

	private void AddList(Quota quota) throws NotFoundQuotaPackageExecption{

		TimeInterval interval= quota.getTimeInterval();
		
		QuotaType type =quota.getQuotaType();
		//log.info("Quote type:{}, class:{}",type,type.getQuotaTypeClass().getName());		
		Class<? extends AggregatedUsageRecord<?, ?>> clz=type.getQuotaTypeClass();

		log.debug("time interval get Value:{}",interval.getValue());
		TemporalConstraint temporalConstraint =null;
		if (interval.getValue()!=0){
			
			Calendar endTime = Calendar.getInstance();
			Calendar startTime = Calendar.getInstance();
			startTime.add(Calendar.DATE,- interval.getValue());
			temporalConstraint = new TemporalConstraint(startTime.getTimeInMillis(),
					endTime.getTimeInMillis(),interval.getAggregationMode());
		}

		if (quota.getQuotaType().equals(QuotaType.SERVICE)){
			/*
			List<FiltersValue> filtersList=null;
			filtersList=new ArrayList<FiltersValue>();
			ServiceQuota quotaService = (ServiceQuota)quota;
			
			String serviceIdentifier=quotaService.getServiceId();
			
			//translate serviceIdentifier into string 
			String[] parts = serviceIdentifier.split(":");
			String serviceClass = parts[0]; 
			String serviceName = parts[1]; 
			String serviceId = parts[2]; 
			
			List<Filter> filters =new ArrayList<Filter>();
			filters.add(new Filter("serviceClass", serviceClass));
			filters.add(new Filter("serviceName", serviceName));
			QuotaUsageServiceValue totalFilterQuota =new QuotaUsageServiceValue
					(quotaService.getContext(),quotaService.getIdentifier(),clz,temporalConstraint,filters);
			totalFilterQuota.setdQuota(quotaService.getQuotaValue());
			totalFilterQuota.setCallerType(quotaService.getCallerType());
			totalFilterQuota.setAccessType(quotaService.getAccessType());
			
			log.debug("totalfilter:{}",totalFilterQuota.toString());			
			this.usageToBeVerified.add(totalFilterQuota);
			*/
		}
		else
		{
			QuotaUsageStorageValue totalFilterQuota =new QuotaUsageStorageValue(quota.getContext(),quota.getIdentifier(),clz,temporalConstraint);
			totalFilterQuota.setdQuota(quota.getQuotaValue());
			log.debug("totalfilter:{}",totalFilterQuota.toString());
			this.usageToBeVerified.add(totalFilterQuota);
		}
		
		
	}

	
	/**
	 * If your quota is complete insert a policy Block
	 * 
	 * @param quota
	 */
	public void insertPolicyBlock(Quota quotaBlocked){
		log.info("Quota Execed insert a policy :{}",quotaBlocked.toString());
		
		String token = null;
		try {
			token = authorizationService().generateUserToken(new UserInfo(quotaBlocked.getContext(), new ArrayList<String>()), quotaBlocked.getContext());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		SecurityTokenProvider.instance.set(token);
		List<Policy> policies = new ArrayList<Policy>();
		if (quotaBlocked.getQuotaType().equals(QuotaType.SERVICE)){
			//TODO todo complete
			/*
			ServiceQuota quotaService = (ServiceQuota)quotaBlocked;
			//AccessType accessType=quotaService.getAccessType();
			Action access =Action.valueOf(quotaService.getAccessType().toString());

			Long servicePackgeId=quotaService.getServicePackageId();
			try {
				ServicePackage servicepackage=quotaPackagePersistence.getPackage(servicePackgeId);

				for (ServicePackageDetail servicePackageDetail:servicepackage.getServicesPackageDetail()){

					String contentService=servicePackageDetail.getContent();
					String[] serviceFilter=contentService.split(":");
					String serviceClass=serviceFilter[0];
					String serviceName="*";
					if (serviceFilter.length>1){
						serviceName=serviceFilter[1];

					}
					String serviceId="*";
					ServiceAccess service=new ServiceAccess(serviceName, serviceClass, serviceId);

					log.info("Quota Execed caller type :{}",quotaBlocked.getCallerType());
					log.info("Quota Execed caller type string :{}",quotaBlocked.getCallerType().toString());
					if (quotaBlocked.getCallerType().equals(CallerType.USER)){
						log.debug("add policy with user"+quotaBlocked.toString());
						//verificare se non e' gia presente
						//altrimenti non inserire
						policies.add(new User2ServicePolicy(quotaBlocked.getContext(), 
								service, Users.one(quotaBlocked.getIdentifier()), access  ));
					}
					if (quotaBlocked.getCallerType().equals(CallerType.ROLE)){
						log.debug("add policy with role"+quotaBlocked.toString());
						//verificare se non e' gia presente
						//altrimenti non inserire
						policies.add(new User2ServicePolicy(quotaBlocked.getContext(), 
								service, Roles.one(quotaBlocked.getIdentifier()), access  ));
					}



				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 */
		}
		if (quotaBlocked.getQuotaType().equals(QuotaType.STORAGE)){

		}

		try {
			log.debug("insert policy for quota:"+policies.toString());
			authorizationService().addPolicies(policies);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
