package org.gcube.data.transfer.plugin;

import org.gcube.data.transfer.model.ExecutionReport;
import org.gcube.data.transfer.model.ExecutionReport.ExecutionReportFlag;
import org.gcube.data.transfer.model.PluginInvocation;
import org.gcube.data.transfer.plugin.fails.ParameterException;
import org.gcube.data.transfer.plugin.fails.PluginCleanupException;
import org.gcube.data.transfer.plugin.fails.PluginException;
import org.gcube.data.transfer.plugin.fails.PluginExecutionException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractPlugin{


	protected PluginInvocation invocation;
	
	public AbstractPlugin(PluginInvocation invocation) {
		this.invocation=invocation;
	}
	
	
	public ExecutionReport execute() {
		log.trace("Executing : {}",invocation);
		try{
			log.debug("Calling run method, invocation is {} ",invocation);
			ExecutionReport report=this.run();
			log.debug("Calling cleanup, report was {}",report);
			this.cleanup();
			log.trace("Returning report {} for invocation {} ",report,invocation);
			return report;
		}catch(Throwable t){			
			log.debug("Thrown exception",t);
			return fromException(this.invocation,t);
		}
	}
	
			
	
		
		// Execution
		public abstract ExecutionReport run()throws PluginExecutionException; 
		
		
		
		public abstract void cleanup() throws PluginCleanupException;
		
		
		
		protected static ExecutionReport fromException(PluginInvocation invocation,Throwable t){
			if(t instanceof PluginException){
				if(t instanceof ParameterException) return new ExecutionReport(invocation, t.getMessage(), ExecutionReportFlag.WRONG_PARAMETER); 
				if(t instanceof PluginCleanupException) return new ExecutionReport(invocation, t.getMessage(), ExecutionReportFlag.FAILED_CLEANUP);
				if(t instanceof PluginExecutionException) return new ExecutionReport(invocation, t.getMessage(), ExecutionReportFlag.FAILED_EXECUTION);
				if(t instanceof PluginCleanupException) return new ExecutionReport(invocation, t.getMessage(), ExecutionReportFlag.FAILED_CLEANUP);
				else {
					log.error("Unable to handle Plugin exception {}, invocation was {}",t.getMessage(),invocation);
					log.debug("Exception was ",t);
					throw new RuntimeException("Unhandled case : ",t);
				}			
			}else {
				return new ExecutionReport(invocation, t.getMessage(), ExecutionReportFlag.UNABLE_TO_EXECUTE);
			}
			
		}
}
