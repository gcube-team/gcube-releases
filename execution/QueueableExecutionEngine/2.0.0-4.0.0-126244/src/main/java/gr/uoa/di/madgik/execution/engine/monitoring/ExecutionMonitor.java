package gr.uoa.di.madgik.execution.engine.monitoring;

import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.engine.monitoring.consumer.ExecutionNodesLoadConsumer;
import gr.uoa.di.madgik.execution.engine.monitoring.resource.ExecutionNodesLoad;
import gr.uoa.di.madgik.execution.engine.utilities.Helper;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A monitor of Execution nodes infrastructure resources.
 * 
 * @author john.gerbesiotis - DI NKUA
 *
 */
public class ExecutionMonitor {
	private Logger log = LoggerFactory.getLogger(ExecutionMonitor.class.getName());

	private static ExecutionNodesLoad hostingNodesLoad;
	private static ExecutionNodesLoadConsumer executionNodesLoadConsumer;
	
	public ExecutionMonitor(String resourceID) {
		hostingNodesLoad = new ExecutionNodesLoad();
		executionNodesLoadConsumer = new ExecutionNodesLoadConsumer(resourceID, hostingNodesLoad);
	}
	
	public void init() {
		executionNodesLoadConsumer.subscribeForExecutionNodeLoad();
	}
	
	public boolean evaluate(ExecutionHandle handle, float util) {
		Set<String> hostingNodes = Helper.getHostingNodes(handle);
		
		for (String node : hostingNodes) {
			float percUsage;
			if (hostingNodesLoad.get(node) == null){
				log.info("No status has been reported for node " + node + " submiting anyway. And setting node ustilization to maximum.");
				hostingNodesLoad.put(node, 1.0f);
				percUsage = 0.0f;
			} else
				percUsage = hostingNodesLoad.get(node);
			if (!evaluate(percUsage, util)){
				log.debug("Execution plan with required utilization " + util + " did not fulfil requirements on node " + node + " with utilization " + percUsage);
				return false;
			}else {
				log.debug("Execution plan with required utilization " + util + " did fulfil requirements on node " + node +  " with utilization " + percUsage);
			}
		}
		
		// Proactively increase usage of the nodes that will be used before message utilization arrives.
		// XXX increase only root?
		for (String node : hostingNodes) {
			Float percUsage = hostingNodesLoad.get(node);
			if (percUsage + util <= 1.0f){
				float prevPerc = hostingNodesLoad.put(node, percUsage + util);
				log.trace("Node: " + node + " with utilization: " + prevPerc + " set to: " + (percUsage + util));
			}
		}
		
		return true;
	}
	
	private boolean evaluate(float perc, float util){
		if (perc + util <= 1.0f)
			return true;
		else
			return false;
	}
	
	public void terminate() {
		executionNodesLoadConsumer.UnregisterFromExecutionNodeLoad();
	}
	
	public Object getLoadUpdatesAvailable() {
		return hostingNodesLoad.getUpdateAvailable();
	}
}
