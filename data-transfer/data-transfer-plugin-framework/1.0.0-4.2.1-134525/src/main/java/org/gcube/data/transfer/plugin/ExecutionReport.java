package org.gcube.data.transfer.plugin;

import org.gcube.data.transfer.model.PluginInvocation;
import org.gcube.data.transfer.plugin.fails.ParameterException;
import org.gcube.data.transfer.plugin.fails.PluginCleanupException;
import org.gcube.data.transfer.plugin.fails.PluginException;
import org.gcube.data.transfer.plugin.fails.PluginExecutionException;

import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@AllArgsConstructor
public class ExecutionReport {

	public enum ExecutionReportFlag{
		SUCCESS,
		WRONG_PARAMETER,
		UNABLE_TO_EXECUTE,
		FAILED_EXECUTION,
		FAILED_CLEANUP
	}
	
	
	public static ExecutionReport fromException(PluginInvocation invocation,Throwable t){
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
	
	
	private PluginInvocation invocation;
	private String message;
	private ExecutionReportFlag flag;
	
		
}
