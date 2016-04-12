package org.gcube.searchsystem.planning;

import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.Constants;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.PlanNode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.gcube.searchsystem.cache.PlanCache;
import org.gcube.searchsystem.cache.PlanCacheManager;
import org.gcube.searchsystem.environmentadaptor.EnvironmentAdaptor;
import org.gcube.searchsystem.planning.maxsubtree.MaxSubtreePlanner;
import org.gcube.searchsystem.planning.preprocessing.Preprocessor;
import org.gcube.searchsystem.workflow.PE2ngWorkflowAdaptor;
import org.gcube.searchsystem.workflow.WorkflowEngineAdaptor;

import search.library.util.cql.query.tree.GCQLNode;
import search.library.util.cql.query.tree.GCQLProjectNode;
import search.library.util.cql.query.tree.GCQLQueryTreeManager;
import search.library.util.cql.query.tree.GCQLFuseNode;
import search.library.util.cql.query.tree.GCQLSortNode;

/**
 * This class is the entrypoint for answering CQL queries.
 * It triggers the preprocessing, planning and execution 
 * for answering a CQL query. The outcome provided by this 
 * class is the ResultSet EPR containing the final outcome,
 * and the warnings produced during the whole search process.
 * 
 * @author vasilis verroios - NKUA DI
 *
 */
public class Orchestrator {
	
	/**
	 * the logger for this class
	 */
	private Logger logger = LoggerFactory.getLogger(Orchestrator.class.getName());
	
	/**
	 * the list of processors to apply before planning
	 */
	private ArrayList<Preprocessor> preprocessors = new ArrayList<Preprocessor>();
	
	/**
	 * the list of priorities for the planning stage
	 */
	private ArrayList<String> priorities = new ArrayList<String>();
	
	/**
	 * the list of warnings for the last search
	 */
	private ArrayList<String> warnings = new ArrayList<String>();

	private Set<String> sids = null;
	
	public void setSids(Set<String> sids) {
		this.sids = sids;
	}
	/**
	 * receives a GCQL query and returns a ResultSet EPR containing the results
	 * @param GCQL query
	 * @param environmentAdaptor - the adaptor for the environment of this search operation
	 * @param workflowAdaptor - the adaptor for the workflow engine used for this search operation 
	 * @return ResultSet EPR containing the results
	 */
	public String search(String cqlQuery, EnvironmentAdaptor environmentAdaptor, 
			WorkflowEngineAdaptor workflowAdaptor, PlanCache pCache) throws Exception{
		
		try{
			logger.info("Received query: " + cqlQuery);	
			
			if (PlanCacheManager.checkInitializationError()) {
				logger.warn(PlanCacheManager.getInitializationError());
				throw new Exception("Search could not complete due to cache plan cache manager initialization error " + PlanCacheManager.getInitializationError());
			}
			
			//parse the query received
			long before = System.currentTimeMillis();
			GCQLNode head = GCQLQueryTreeManager.parseGCQLString(cqlQuery);
			long after = System.currentTimeMillis();
			logger.info("parseGCQLString returned after: " + (after - before) + " millis");
			
			before = System.currentTimeMillis();
			PlanNode cachedPlan = pCache.searchForCachedPlans(cqlQuery, head);
			after = System.currentTimeMillis();
			logger.info("searchForCachedPlans returned after: " + (after - before) + " millis");
			
			Exception exceptionFromCached = null;
			
			if(cachedPlan != null) {
				logger.info("Found cached plans for query: " + cqlQuery 
						+ " ---------> " + cachedPlan.toString());
				try {
					String rsEpr = execute(cachedPlan, workflowAdaptor);
					return rsEpr;
				} catch (Exception e) {
					logger.warn("Exception while executing cached plan." 
							+ " We will create a new plan and try to execute it", e);
					exceptionFromCached = e;
					pCache.clear();
				}
			}
			
			//try to create a new plan
			before = System.currentTimeMillis();
			PlanNode newPlan = null;
			try {
				newPlan = preprocessAndPlan(head, environmentAdaptor);
			} catch (Exception e) {
				logger.error("something went wrong on plan creation. trying to clear the cache and reconstruct a new plan", e);
				MaxSubtreePlanner.cache.invalidateAll();
				newPlan = preprocessAndPlan(head, environmentAdaptor);
			}
			after = System.currentTimeMillis();
			logger.info("profiling: search plan created in : " + (after - before) + " millis");
			
			before = System.currentTimeMillis();
			//check if the new plan is the same with the previous one
			if(cachedPlan != null && newPlan.equals(cachedPlan)) {
				//if it is there is no meaning to execute the plan again
				logger.info("newplan: " + newPlan.toString() 
						+ "---- is equal to the cached one: " + cachedPlan.toString());
				throw exceptionFromCached;
			}
			after = System.currentTimeMillis();
			logger.info("checking for plan equality returned after: " + (after - before) + " millis");
			
			//store the new plan in the cache
			before = System.currentTimeMillis();
			pCache.addPlan(cqlQuery, head, newPlan);
			after = System.currentTimeMillis();
			logger.info("adding plan to cache returned after: " + (after - before) + " millis");			
			
			logger.info("new plan is : " + cqlQuery 
					+ " ---------> " + newPlan.toString());
			
			//execute the new plan
			try {
				return execute(newPlan, workflowAdaptor);			
			} catch (Exception e) {
				logger.error("something went wrong on search execution. trying to reconstruct new plan and execute again", e);
				
				return retryExecute(cqlQuery, head, environmentAdaptor, workflowAdaptor, pCache);
			}
		} catch (Exception e) {
			logger.error("Could not answer query, exception: ", e);
			throw e;
		}
	}
	
	private String retryExecute(String cqlQuery, GCQLNode head, EnvironmentAdaptor environmentAdaptor, 
			WorkflowEngineAdaptor workflowAdaptor, PlanCache pCache) throws Exception{
		//we clear all the caches because caches plans might have cause the error
		MaxSubtreePlanner.cache.invalidateAll();
		PE2ngWorkflowAdaptor.planCache.invalidateAll();
		pCache.clear();
		
		long before = System.currentTimeMillis();
		PlanNode newPlan = preprocessAndPlan(head, environmentAdaptor);
		long after = System.currentTimeMillis();
		logger.info("profiling: search plan created in : " + (after - before) + " millis");
		before = System.currentTimeMillis();
		pCache.addPlan(cqlQuery, head, newPlan);
		after = System.currentTimeMillis();
		logger.info("adding plan to cache returned after: " + (after - before) + " millis");			
		
		logger.info("new plan is : " + cqlQuery 
				+ " ---------> " + newPlan.toString());
		
		//execute the new plan
		return execute(newPlan, workflowAdaptor);
		
	}
	
	private PlanNode preprocessAndPlan(GCQLNode head, EnvironmentAdaptor environmentAdaptor) throws Exception{
		//apply the preprocessors
		long before = System.currentTimeMillis();
		for(Preprocessor preprocessor : preprocessors) {
			head = preprocessor.preprocess(head);
		}
		long after = System.currentTimeMillis();
		logger.info("preprocessing returned after: " + (after - before) + " millis");
		
		logger.info("After preprocessing: " + head.toCQL());
		
		//planning stage
		before = System.currentTimeMillis();
		
		before = System.currentTimeMillis();
		MaxSubtreePlanner planner = new MaxSubtreePlanner(this.priorities, environmentAdaptor);
		after = System.currentTimeMillis();
		logger.info("profiling: MaxSubtreePlanner creation : " + (after - before) + " millis");
		
		String query = getRankMode(head);
		
		logger.error("Query : " + query);
		
		
		if (query != null && !query.trim().equals("") && !query.equals(Constants.DEFAULT)){
			planner.getPriorities().add("fuse");
			planner.setQuery(query);
			head = removeRankNode(head);
		}
		else
			planner.getPriorities().add(MaxSubtreePlanner.DEFAULTPRIORITY);

//		planner.getPriorities().add(getRankMode(head));
		
		before = System.currentTimeMillis();
		PlanNode plan = planner.plan(head);
		after = System.currentTimeMillis();
		logger.info("profiling: planning stage. planning returned after: " + (after - before) + " millis");
		
		if(plan == null) {
			throw new Exception("The infrastructure resources are not enough for answering this query. A query plan could not be created. ");
		}
		
		logger.info("After planning: " + plan.toString());
		
		//store all the warnings
		warnings = planner.getWarnings();
		
		if(warnings.size() > 0) {
			logger.info("Planner finished with warnings: ");
			for(String warning : warnings) {
				logger.info("*** " + warning);
			}
		}
		
		return plan;
	}
	
	private String execute(PlanNode plan, WorkflowEngineAdaptor workflowAdaptor) throws Exception{
		
		logger.info("Starting execution stage");
		
		//execution stage
		//return the ResultSet from execution
		long before = System.currentTimeMillis();
		workflowAdaptor.setSids(this.sids);
		String rsEpr = workflowAdaptor.getExecutionResult(plan);
		long after = System.currentTimeMillis();
		logger.info("profiling: workflowAdaptor stage. execution returned after: " + (after - before) + " millis");
		
		
		logger.info("Execution finished. ResultSet EPR output: " + rsEpr);
		
		return rsEpr;
	}
	
	/**
	 * setter for the list of processors to apply before planning
	 * @param preprocessors
	 */
	public void setPreprocessors(ArrayList<Preprocessor> preprocessors) {
		this.preprocessors = preprocessors;
	}

	/**
	 * getter for the list of processors to apply before planning
	 * @param preprocessors
	 */
	public ArrayList<Preprocessor> getPreprocessors() {
		return preprocessors;
	}
	
	/**
	 * getter for the list of priorities for the planning stage
	 * @return priorities
	 */
	public ArrayList<String> getPriorities() {
		return priorities;
	}

	/**
	 * setter for the list of priorities for the planning stage
	 * @param priorities
	 */
	public void setPriorities(ArrayList<String> priorities) {
		this.priorities = priorities;
	}

	/**
	 * getter for the list of warnings for the last search
	 * @param warnings
	 */
	public ArrayList<String> getWarnings() {
		return warnings;
	}
	
	
	// TODO case when rankNode is not the head
	private GCQLNode removeRankNode(GCQLNode head) {
		if(head instanceof GCQLFuseNode)
			head = ((GCQLFuseNode) head).subtree;
		return head;
	}

	private String getRankMode(GCQLNode head) {
		if(head instanceof GCQLFuseNode)
		{
			String rankMode = ((GCQLFuseNode) head).getFuseMode().toCQL(); 
			head = ((GCQLFuseNode) head).subtree;
			return rankMode;
		}
		else if(head instanceof GCQLProjectNode)
			return getRankMode(((GCQLProjectNode) head).subtree);
		else if(head instanceof GCQLSortNode)
			return getRankMode(((GCQLSortNode) head).subtree);
		else
			return Constants.DEFAULT;
	}


}
