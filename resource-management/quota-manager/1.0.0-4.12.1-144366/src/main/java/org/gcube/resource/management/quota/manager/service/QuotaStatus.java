package org.gcube.resource.management.quota.manager.service;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.utils.Caller;
import org.gcube.resource.management.quota.library.quotalist.TimeInterval;
import org.gcube.resource.management.quota.library.status.QuotaStorageStatus;
import org.gcube.resource.management.quota.manager.check.MyAppManager;
import org.gcube.resource.management.quota.manager.check.QuotaUsage;
import org.gcube.smartgears.ApplicationManagerProvider;
import org.gcube.smartgears.annotations.ManagedBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Query quota Status
 * @author Alessandro Pieve (alessandro.pieve@isti.cnr.it)
 *
 */
@Path("quotaStatus")
@ManagedBy(MyAppManager.class)

public class QuotaStatus {

	private static Logger log = LoggerFactory.getLogger(QuotaStatus.class);

	private MyAppManager appManager = (MyAppManager)ApplicationManagerProvider.get(MyAppManager.class);

	//http://.....quota-manager/gcube/service/quotaStatus/detail/?timeinterval=MONTHLY&gcube-token=...
	@GET
	@Path("/detail/")	
	@Produces(MediaType.APPLICATION_XML)
	public QuotaStorageStatus getQuotaStorageStatus(@QueryParam("timeinterval") String timeinterval) {

		Caller caller=AuthorizationProvider.instance.get();
		String identifier = caller.getClient().getId();

		QuotaUsage queryUsage =appManager.getQuotaUsage();

		QuotaStorageStatus quotaStorageStatus;
		try{
			log.info("retrieving quote for identifier:{} and time:{}",identifier,timeinterval);
			quotaStorageStatus =queryUsage.selectStorageQuota(identifier,TimeInterval.valueOf(timeinterval) );

		}catch(Exception e){
			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
					.entity("Error : "+e.getMessage()).type(MediaType.TEXT_PLAIN).build()); 
		}
		return quotaStorageStatus;
	}



	//http://.....quota-manager/gcube/service/quotaStatus/list/?timeinterval=MONTHLY&gcube-token=...
	@GET
	@Path("/list/")	
	@Produces(MediaType.APPLICATION_XML)
	public List<QuotaStorageStatus> getQuotaStorageStatusList(@QueryParam("timeinterval") String timeinterval) {
		QuotaUsage queryUsage =appManager.getQuotaUsage();

		List<QuotaStorageStatus> quotaStorageStatusList;
		try{
			quotaStorageStatusList=queryUsage.selectStorageQuotaList(TimeInterval.valueOf(timeinterval));
			//	log.info("retrieving quote for identifier:{} and time:{}",identifier,timeinterval);
			//	quotaStorageStatusList.add(queryUsage.selectStorageQuota(identifier,TimeInterval.valueOf(timeinterval)));

		}catch(Exception e){
			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
					.entity("Error : "+e.getMessage()).type(MediaType.TEXT_PLAIN).build()); 
		}
		return quotaStorageStatusList;
	}

}


