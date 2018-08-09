package org.gcube.data.spd.executor.jobs;

import java.util.Calendar;

import org.gcube.accounting.datamodel.UsageRecord.OperationResult;
import org.gcube.accounting.datamodel.usagerecords.JobUsageRecord;
import org.gcube.accounting.persistence.AccountingPersistence;
import org.gcube.accounting.persistence.AccountingPersistenceFactory;
import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.utils.Caller;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.model.service.types.JobStatus;
import org.gcube.smartgears.ContextProvider;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SpeciesJob implements  Runnable {

	private static Logger log = LoggerFactory.getLogger(SpeciesJob.class);

	public abstract JobStatus getStatus() ;

	public abstract void setStatus(JobStatus status) ;

	public abstract String getId();

	public abstract boolean validateInput(String input);

	public abstract int getCompletedEntries();

	public abstract Calendar getStartDate();

	public abstract Calendar getEndDate();

	public abstract void execute();

	public abstract boolean isResubmitPermitted();

	private ApplicationContext ctx = ContextProvider.get();
	
	public final void run(){
		if (getStatus()!=JobStatus.PENDING && !isResubmitPermitted()){
			log.warn("the job with id {} cannot be resubmitted",getId());
			throw new IllegalStateException("this job cannot be resubmitted");
		}
		try{
			log.debug("running job with id {}",this.getId());
			execute();
			log.debug("job with id {} executed",this.getId());
		}catch(Exception e){
			log.error("unexpected exception in job with id {}, setting status to FAILED",this.getId(),e);
			this.setStatus(JobStatus.FAILED);
		}
		generateAccounting();
	}


	
	private final void generateAccounting(){
		AccountingPersistence persistence = AccountingPersistenceFactory.getPersistence();
		JobUsageRecord jobUsageRecord = new JobUsageRecord();
		try{
			Caller caller = AuthorizationProvider.instance.get();
			String consumerId = caller!= null ?
				caller.getClient().getId() : "UNKNOWN";
			String qualifier = 	 caller!= null ?
					caller.getTokenQualifier() : "UNKNOWN";
				
			jobUsageRecord.setConsumerId(consumerId);
			jobUsageRecord.setScope(ScopeProvider.instance.get());
			jobUsageRecord.setJobName(this.getClass().getSimpleName());			
			jobUsageRecord.setOperationResult(getStatus()==JobStatus.COMPLETED?OperationResult.SUCCESS:OperationResult.FAILED);
			jobUsageRecord.setDuration(this.getEndDate().getTime().getTime()-this.getStartDate().getTime().getTime());
			jobUsageRecord.setServiceName(ctx.configuration().name());
			jobUsageRecord.setServiceClass(ctx.configuration().serviceClass());
			jobUsageRecord.setHost(ctx.container().configuration().hostname());
			jobUsageRecord.setCallerQualifier(qualifier);
						
			persistence.account(jobUsageRecord);
			log.info("Job {} accounted successfully",getId());
		}catch(Exception ex){
			log.warn("invalid record passed to accounting ",ex);
		}
	}
	
}
