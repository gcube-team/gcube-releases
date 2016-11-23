package gr.uoa.di.madgik.execution.report.accounting;

import gr.uoa.di.madgik.environment.accounting.AccountingSystem;
import gr.uoa.di.madgik.environment.accounting.properties.TaskProperties;
import gr.uoa.di.madgik.environment.accounting.record.TaskUsageRecord;
import gr.uoa.di.madgik.environment.exception.EnvironmentValidationException;
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

public class TaskAccountingDispatcher extends Dispatcher {
	private Logger log = LoggerFactory.getLogger(TaskAccountingDispatcher.class.getName());
	private ExecutionHandle handle;
	private String adaptor = null;
	private String cores = null;
	private Date jobStart = null;
	private Date jobEnd = null;
	private static final String ACTIONSCOPE= "GCubeActionScope";
	
	public TaskAccountingDispatcher(ExecutionHandle handle) {
//		try {	// only for testing
//			AccountingSystem.init("gr.uoa.di.madgik.environment.gcube.GCubeAccountingFrameworkProvider");
//		} catch (EnvironmentValidationException e1) {
//			e1.printStackTrace();
//		}
		this.handle = handle;

		try {
			this.adaptor = Thread.currentThread().getStackTrace()[2].getClassName();
			cores = String.valueOf(handle.GetActionsRunning() > 0? handle.GetActionsRunning() : 1);
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
		// Wait until it get completed or reach maximum time. reconsider that?
		int sleepTime = 50;
		while(sleepTime < 10000 && handle.GetHandleState().equals(HandleState.Running)) {
			sleepTime *= 2;
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {}
		}
		
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
		String usagePhase = handle.GetHandleState().toString().toLowerCase();
		
		if (this.jobEnd == null)
			if (usagePhase.equalsIgnoreCase(HandleState.Completed.toString()) || usagePhase.equalsIgnoreCase(HandleState.Cancel.toString()))
				this.jobEnd = new Date();

		String usageStart = String.valueOf(getJobStart().getTime());
		String usageEnd = this.jobEnd == null ? null : String.valueOf(this.jobEnd.getTime());

		Map<String, String> props = new HashMap<String, String>();
		props.put(TaskProperties.jobId.toString(), jobId);
		props.put(TaskProperties.refHost.toString(), owner.split(":")[0]);
		props.put(TaskProperties.refVM.toString(), owner);
		props.put(TaskProperties.domain.toString(), owner.split(":")[0]);
		props.put(TaskProperties.usageStart.toString(), usageStart);
		props.put(TaskProperties.usageEnd.toString(), usageEnd);
		props.put(TaskProperties.usagePhase.toString(), usagePhase);
		props.put(TaskProperties.inputFilesNumber.toString(), String.valueOf(ExecutionPlanAnalyser.countInputFiles(handle.GetPlan())));
		props.put(TaskProperties.inputFilesSize.toString(), null);
		props.put(TaskProperties.outputFilesNumber.toString(), String.valueOf(ExecutionPlanAnalyser.countOutputFiles(handle.GetPlan())));
		props.put(TaskProperties.outputFilesSize.toString(), null);
		props.put(TaskProperties.overallNetworkIn.toString(), null);
		props.put(TaskProperties.overallNetworkOut.toString(), null);
		props.put(TaskProperties.cores.toString(), cores);
		props.put(TaskProperties.processors.toString(), null);

		TaskUsageRecord record = new TaskUsageRecord(consumerId, startTime, endTime, scope, owner, props);

		try {
			AccountingSystem.send(record);
		} catch (Exception e) {
			log.warn("Could not send accounting record: ", e);
		}
	}
}
