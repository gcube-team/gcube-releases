package org.gcube.searchsystem.workflow;

import java.util.Set;

import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.PlanNode;

/**
 * Classes that implement this interface are given a search plan and are able 
 * to create a plan for an execution engine. They can also execute the search 
 * plan, directly, and return a resultSet epr containing the final outcome of 
 * the search operation.
 * 
 * @author vasilis verroios
 */
public interface WorkflowEngineAdaptor {
	
	/**
	 * execute a search plan and return a resultSet epr containing the final 
	 * outcome of the search operation.
	 * @param plan - the search plan
	 * @return resultSet epr containing the final outcome of the search 
	 * operation.
	 * @throws Exception in case of an error during the creation of the 
	 * Execution plan, or in case of an error during the execution
	 */
	public String getExecutionResult(PlanNode plan) throws Exception;
	
	/**
	 * create an execution plan for the search plan provided.
	 * @param plan - the search plan
	 * @return the execution plan
	 * @throws Exception in case of an error during the creation of the 
	 * Execution plan.
	 */
	public Object getExecutionPlan(PlanNode plan) throws Exception;

	public void setSids(Set<String> sids);	

}
