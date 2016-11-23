package gr.uoa.di.madgik.execution.report.accounting;

import gr.uoa.di.madgik.environment.accounting.AccountingSystem;
import gr.uoa.di.madgik.environment.accounting.properties.JobProperties;
import gr.uoa.di.madgik.environment.accounting.record.JobUsageRecord;
import gr.uoa.di.madgik.execution.engine.ExecutionEngine;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle.HandleState;
import gr.uoa.di.madgik.execution.report.Dispatcher;
import gr.uoa.di.madgik.execution.utils.ExecutionPlanAnalyser;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobAccountingDispatcher extends Dispatcher {
	private Logger log = LoggerFactory.getLogger(JobAccountingDispatcher.class.getName());
	private ExecutionHandle handle;
	private String adaptor = null;
	private Date jobStart = null;
	private Date jobEnd = null;
	private static final String ACTIONSCOPE= "GCubeActionScope";
	
	public JobAccountingDispatcher(ExecutionHandle handle) {
//		try {	// only for testing
//			AccountingSystem.init("gr.uoa.di.madgik.environment.gcube.GCubeAccountingFrameworkProvider");
//			ScopeProvider.instance.set("/gcube/devsec");
//		} catch (EnvironmentValidationException e1) {
//			e1.printStackTrace();
//		}
		this.handle = handle;

		try {
			this.adaptor = Thread.currentThread().getStackTrace()[5].getClassName();
		} catch (Exception e) {
		}
		
		jobStart = new Date();
	}

	private Date getJobStart() {
		if (jobStart == null)
			jobStart = new Date();
		return jobStart;
	}

	@Override
	public void run() {
		// Initialize record's common fields
		String consumerId = this.adaptor; // XXX enumeration matching?
		Date startTime = getJobStart();
		Date endTime = new Date();
		String scope = null;
		if (handle.GetPlan().EnvHints.GetHint(ACTIONSCOPE) != null)
			scope = handle.GetPlan().EnvHints.GetHint(ACTIONSCOPE).Hint.Payload;
		String owner = null;
		try {
			owner = ExecutionEngine.getLocalhost();
		} catch (Exception e) {}

		// Initialize job's specific fields
		String jobId = handle.GetPlan().Root.GetID();
		String jobQualifier = adaptor;
		String jobName = null;
		String jobStatus = handle.GetHandleState().toString().toLowerCase();
		
		if (this.jobEnd == null)
			if (jobStatus.equalsIgnoreCase(HandleState.Completed.toString()) || jobStatus.equalsIgnoreCase(HandleState.Cancel.toString()))
				this.jobEnd = new Date();

		String jobStart = String.valueOf(getJobStart().getTime());
		String jobEnd = this.jobEnd == null ? null : String.valueOf(this.jobEnd.getTime());
		int numOfBound = ExecutionPlanAnalyser.countBoundaries(handle.GetPlan());
		String vmsUsed = numOfBound >= 0 ? String.valueOf(1 + numOfBound) : null;
		String wallDuration = jobEnd != null ? String.valueOf(this.jobEnd.getTime() - this.jobStart.getTime()) : null;

		Map<String, String> props = new HashMap<String, String>();
		props.put(JobProperties.jobId.toString(), jobId);
		props.put(JobProperties.jobQualifier.toString(), jobQualifier);
		props.put(JobProperties.jobName.toString(), jobName);
		props.put(JobProperties.jobStatus.toString(), jobStatus);
		props.put(JobProperties.jobStart.toString(), jobStart);
		props.put(JobProperties.jobEnd.toString(), jobEnd);
		props.put(JobProperties.vmsUsed.toString(), vmsUsed);
		props.put(JobProperties.wallDuration.toString(), wallDuration);

		JobUsageRecord record = new JobUsageRecord(consumerId, startTime, endTime, scope, owner, props);

		try {
			AccountingSystem.send(record);
		} catch (Exception e) {
			log.warn("Could not send accounting record: ", e);
		}
	}
}
