package gr.uoa.di.madgik.environment.gcube;

import gr.uoa.di.madgik.environment.accounting.IAccountingFrameworkProvider;
import gr.uoa.di.madgik.environment.accounting.record.ExecutionUsageRecord;

import java.util.Calendar;

import org.gcube.accounting.datamodel.UsageRecord;
import org.gcube.accounting.datamodel.usagerecords.JobUsageRecord;
import org.gcube.accounting.datamodel.usagerecords.TaskUsageRecord;
import org.gcube.accounting.persistence.AccountingPersistence;
import org.gcube.accounting.persistence.AccountingPersistenceFactory;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GCubeAccountingFrameworkProvider implements IAccountingFrameworkProvider {

	private static Logger logger = LoggerFactory.getLogger(GCubeAccountingFrameworkProvider.class);

	private static AccountingPersistence accountingPersistence = null;

	public void SessionInit() throws Exception {
		try {
			accountingPersistence = AccountingPersistenceFactory.getPersistence();
		} catch (Exception e) {
			logger.warn("Could not initialise accounting propeprly", e);
		}
	}

	public void Send(ExecutionUsageRecord record) throws Exception {
		if (accountingPersistence == null) {
			logger.warn("Accounting not initialized");
			return;
		}
		
		String recordType = record.getResourceType();
		UsageRecord ur;
		if(recordType.compareTo("job")==0){
			JobUsageRecord jur = new JobUsageRecord();
			Calendar startTime = Calendar.getInstance();
			startTime.setTime(record.getStartTime());
			jur.setJobStartTime(startTime);
			Calendar endTime = Calendar.getInstance();
			endTime.setTime(record.getEndTime());
			jur.setJobEndTime(endTime);
			ur = jur;
		}else if(recordType.compareTo("task")==0){
			TaskUsageRecord tur = new TaskUsageRecord();
			Calendar startTime = Calendar.getInstance();
			startTime.setTime(record.getStartTime());
			tur.setTaskStartTime(startTime);
			Calendar endTime = Calendar.getInstance();
			endTime.setTime(record.getEndTime());
			tur.setTaskEndTime(endTime);
			
			ur = tur;
		}else{
			throw new Exception("Invalid Usage Record type");
		}
		
		ur.setConsumerId(record.getConsumerId());
		ur.setScope(record.getResourceScope());

		
		logger.debug("Sending accounting message: " + ur);
		
		if(ur.getScope() != null)
			ScopeProvider.instance.set(ur.getScope());
		else {
			logger.warn("Scope is not specified");
			return;
		}
		
		accountingPersistence.account(ur);
	}
}