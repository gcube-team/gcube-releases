package org.gcube.data.transfer.plugin;

import org.gcube.data.transfer.model.PluginInvocation;
import org.gcube.data.transfer.plugin.fails.PluginCleanupException;
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
			return ExecutionReport.fromException(this.invocation,t);
		}
	}
	
			
	
		
		// Execution
		public abstract ExecutionReport run()throws PluginExecutionException; 
		
		
		
		public abstract void cleanup() throws PluginCleanupException;
}
