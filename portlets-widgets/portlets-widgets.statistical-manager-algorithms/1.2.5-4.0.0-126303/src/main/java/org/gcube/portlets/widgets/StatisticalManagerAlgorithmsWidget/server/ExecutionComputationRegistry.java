/**
 * 
 */
package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.server;

import java.util.HashMap;
import java.util.Map;




public class ExecutionComputationRegistry {
	
	protected static ExecutionComputationRegistry instance;
	
	public static final ExecutionComputationRegistry getInstance()
	{
		if (instance == null) instance = new ExecutionComputationRegistry();
		return instance;
	}
	
	
	protected Map<String, ExecutionComputation> registry = new HashMap<String, ExecutionComputation>();
	
	
	public void add(ExecutionComputation target)
	{
//		logger.trace("add target: "+target.getId());
		ExecutionComputation old = registry.put(target.getId(), target);
//		if (old!=nul) logger.warn("A CSVTarget instance with id "+old.getId()+" and class "+old.getClass().getCanonicalName()+" was already registered. The old one has been replaced by the new one with class "+target.getClass().getCanonicalName()+".");
	}
	
	
	public  ExecutionComputation get(String targetId)
	{
		return registry.get(targetId);
	}
	
	public boolean exists(String targetId)
	{
		return registry.containsKey(targetId);
	}
	
	
	public void remove(String targetId)
	{
//		logger.trace("remove targetId: "+targetId);
		registry.remove(targetId);
	}

}
