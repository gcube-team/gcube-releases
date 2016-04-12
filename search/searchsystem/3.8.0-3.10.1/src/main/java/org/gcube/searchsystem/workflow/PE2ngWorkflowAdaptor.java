package org.gcube.searchsystem.workflow;

import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.execution.datatype.DataTypeArray;
import gr.uoa.di.madgik.execution.datatype.IDataType;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.exception.ExecutionException;
import gr.uoa.di.madgik.workflow.adaptor.search.WorkflowSearchAdaptor;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.DataSourceNode;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.OperatorNode;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.PlanNode;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.datasource.DataSourceWrapperFactoryConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class PE2ngWorkflowAdaptor implements WorkflowEngineAdaptor {

	private static final Pattern PATTERN = Pattern.compile("(?<= ==)(\\s*[^=-]*?)\\)|(?<= (proximity|<=|fuzzy|adj|>=|within|>|=|<))(\\s*[^=]*?)\\)");
	private static final String RESERVED = "RESERVED)";
	private static Logger logger = LoggerFactory.getLogger(WorkflowEngineAdaptor.class.getName());
	//the workflow engine instance
	private WorkflowSearchAdaptor workflowSearchAdaptor;
	
	
	private EnvHintCollection hints = null;
	private DataSourceWrapperFactoryConfig cfg = null;
	private Set<String> sids = null;
	/**
	 * creates a new WorkflowEngine adaptor for PE2ng
	 * @param scope - the working scope of the adaptor 
	 * @throws Exception 
	 */
	public PE2ngWorkflowAdaptor(EnvHintCollection hints) throws Exception {
		//this.workflowSearchAdaptor = new WorkflowSearchAdaptor(hints);
		this.hints = hints;
	}
	
	/**
	 * creates a new WorkflowEngine adaptor for PE2ng
	 * @param cfg - the configuration for the data source wrapper factory
	 * @param scope - the working scope of the adaptor 
	 * @throws Exception 
	 */
	public PE2ngWorkflowAdaptor(DataSourceWrapperFactoryConfig cfg, EnvHintCollection hints) throws Exception {
		//this.workflowSearchAdaptor = new WorkflowSearchAdaptor(cfg,hints);
		this.hints = hints;
		this.cfg = cfg;
	}
	
	//static Map<PlanNode, WorkflowPlanCacheElement> planCache = new HashMap<PlanNode, WorkflowPlanCacheElement>();
	public static Cache<String, String> planCache = CacheBuilder.newBuilder()
			.maximumSize(1000)
			.expireAfterWrite(30, TimeUnit.MINUTES)
			.build();
	
	
	static Map<String, String> getPlanQueriesMap(PlanNode planNode){
		Map<String, String> queriesMapping = new HashMap<String, String>();
		
		if (planNode instanceof DataSourceNode){
			String cqlQuery = ((DataSourceNode)planNode).getCqlInput().trim();
			String newCqlQuery = transformCQLQuery(cqlQuery.trim());
			
			//detect collisions (rare case)
			if (queriesMapping.containsKey(newCqlQuery) && queriesMapping.get(newCqlQuery).equalsIgnoreCase(cqlQuery) == false){
				logger.error("found the same template for different queries : ");
				logger.error("template : " + newCqlQuery);
				logger.error("old1 : " + cqlQuery);
				logger.error("old2 : " + queriesMapping.get(newCqlQuery));
				newCqlQuery = newCqlQuery.replace(RESERVED, RESERVED + "x");
				logger.error("new query will be : " + newCqlQuery);
			}
			queriesMapping.put(newCqlQuery, cqlQuery);
			((DataSourceNode)planNode).setCqlInput(newCqlQuery);
		} else if (planNode instanceof OperatorNode) {
			for (PlanNode pn : ((OperatorNode)planNode).getChildren()){
				queriesMapping.putAll(getPlanQueriesMap(pn));
			}
			
		}
		
		return queriesMapping;
	}
	
	void replacePlanNode(PlanNode planNode, Map<String, String> queriesMapping){
		
		if (planNode instanceof DataSourceNode){
			String cqlQuery = ((DataSourceNode)planNode).getCqlInput().trim();
			
			if (queriesMapping.containsKey(cqlQuery)){
				((DataSourceNode)planNode).setCqlInput(queriesMapping.get(cqlQuery));

			}
		} else if (planNode instanceof OperatorNode) {
			for (PlanNode pn : ((OperatorNode)planNode).getChildren()){
				replacePlanNode(pn, queriesMapping);
			}
		}
	}
	

	/**
     * Replaces terms of a cql query with a keyword in order to be able to match new plans with
     * similar previous ones
     * */
	/*private static String transformCQLQuery(String cqlQuery)
    {
		String transformed = PATTERN.matcher(cqlQuery).replaceAll(RESERVED);
    	logger.info("transformed query from: "+cqlQuery+'\n'+"to: "+transformed);
    	return transformed;
    }*/
    private static String transformCQLQuery(String cqlQuery)
    {
    	MatchReplacer replacer = new MatchReplacer(PATTERN) {
        int i = 1;
        @Override public String replacement(MatchResult m) { 
            return " RESERVED" + i+++")";
        }
    	};
    	String newCqlQuery = replacer.replace(cqlQuery);
    	logger.info("transformed query from: "+cqlQuery+'\n'+"to: "+newCqlQuery);
    	return newCqlQuery;
    }
    
	private static String restoreCQLQuery(String cqlQuery,
			String transformedCQLQuery) {
		List<String> terms = new ArrayList<String>();
		Pattern pattern = Pattern.compile("(?<= =)(\\s*[^=-]*?)\\)");
		Matcher m = pattern.matcher(cqlQuery);
		while (m.find()) {
			for (int i = 1; i <= m.groupCount(); i++) {
				terms.add(m.group(1));
			}
		}
		MatchReplacer replacer = new MatchReplacer(
				Pattern.compile("(?<= =)(\\s*[^=-]*?)\\)")) {
			int i = 0;
			List<String> terms;

			@Override
			public String replacement(MatchResult m) {
				return terms.get(i++) + ')';
			}

			public MatchReplacer init(List<String> terms) {
				this.terms = terms;
				return this;
			}
		}.init(terms);
		return replacer.replace(transformedCQLQuery);
	}
	
	
	private String createWorkflow(PlanNode plan, boolean addInCache) throws Exception{
		long before = System.currentTimeMillis();
		if (this.cfg != null)
			this.workflowSearchAdaptor = new WorkflowSearchAdaptor(this.cfg, this.hints);
		else
			this.workflowSearchAdaptor = new WorkflowSearchAdaptor(this.hints);
		long  after = System.currentTimeMillis();
		
		
		logger.info("profiling: workflowsearchadaptor initialization time: " + (after - before) + " millis");
		
		before = System.currentTimeMillis();
		this.workflowSearchAdaptor.SetInputPlan(plan);
		this.workflowSearchAdaptor.CreatePlan();
		after = System.currentTimeMillis();
		logger.info("profiling: create plan time: " + (after - before) + " millis");
		
		logger.trace("-----------------------");
		logger.trace("template plan");
		logger.trace(this.workflowSearchAdaptor.GetCreatedPlan().Serialize());
		logger.trace("-----------------------");
		
		//cachedWorkflow = this.workflowSearchAdaptor.getWorkflowPlanElements();
		if (addInCache){
			String cachedWorkflow = null;
			try {
				cachedWorkflow = WorkflowSearchAdaptor.serializePlan(this.workflowSearchAdaptor);
			} catch (Exception e) {
				logger.error("Could not serialize workflow for cache", e);
				return null;
			}
			logger.info("cache element size : " + cachedWorkflow.length());
			planCache.put(plan.toString(), cachedWorkflow);
			logger.info("cache has : " + planCache.asMap().keySet());
			
			return cachedWorkflow;
		}
		
		return null;
	}
	
	
	public String getExecutionResultSimple(PlanNode plan) throws Exception{
		
		//set the plan to the workflow adaptor instance
		if (this.cfg != null)
			this.workflowSearchAdaptor = new WorkflowSearchAdaptor(this.cfg, this.hints);
		else
			this.workflowSearchAdaptor = new WorkflowSearchAdaptor(this.hints);
		
		this.workflowSearchAdaptor.SetInputPlan(plan);
		
		
		//create the plan
		this.workflowSearchAdaptor.CreatePlan();
		
//		this.replaceTree(this.workflowSearchAdaptor.getWrapperTree());
		//execute the plan 
		for (NamedDataType ndt  : this.workflowSearchAdaptor.getVariableCollection()){
			logger.trace("checking : " + ndt.Name + " , " + ndt.Value.GetStringValue());
			if (ndt.Value.GetStringValue().equalsIgnoreCase("sids_placeholder")){
				DataTypeArray sidsDT = new DataTypeArray();
				sidsDT.SetArrayClassCode("["+IDataType.DataTypes.String);
				sidsDT.SetValue(sids == null? new String[0] : sids.toArray(new String[sids.size()]));
				ndt.Value = sidsDT;
			}
		}
		
		String resultsetEpr = this.workflowSearchAdaptor.ExecutePlan();
		
		//get the exception if any
		ExecutionException exception = this.workflowSearchAdaptor.GetCompletionError();
		
		if(exception != null) {
			try {
				planCache.invalidate(plan.toString());
			}catch (Exception ex) {
				logger.error("error while invalidating the cache for copiedPlan " + plan.toString(), ex);
			}
			throw exception;
		} else {
			
			// add simple plan in cache
			String cachedWorkflow = null;
			try {
				cachedWorkflow = WorkflowSearchAdaptor.serializePlan(this.workflowSearchAdaptor);
				logger.info("cache element size : " + cachedWorkflow.length());
				planCache.put(plan.toString(), cachedWorkflow);
				logger.info("cache has : " + planCache.asMap().keySet());
			} catch (Exception e) {
				logger.error("Could not serialize workflow for cache", e);
			}
			
			return resultsetEpr;
		}
			
	}
	
	
	@Override
	public String getExecutionResult(PlanNode plan) throws Exception{
		PlanNode copiedPlan = (PlanNode) plan.clone();
		
		Map<String, String> planQueriesMap = null;
		try {
			logger.info("called getExecutionResult with plan : " + copiedPlan.myToString());
			
			//set the plan to the workflow adaptor instance
			boolean workflowIsCached = false; 
			long beforeTotalCreate = System.currentTimeMillis();
			
			long before = System.currentTimeMillis();
	//		this.workflowSearchAdaptor.SetInputPlan(plan);
			long after = System.currentTimeMillis();
	//		logger.info("profiling: set input plan time: " + (after - before) + " millis");
	//		this.workflowSearchAdaptor.CreatePlan();
	//		
	//		logger.info("cache has : " + planCache.asMap().keySet());
			
			
	//		logger.info("-----------------------");
	//		logger.info("Initial plan");
	//		String oldVars = this.workflowSearchAdaptor.GetCreatedPlan().Variables.ToXML(); 
	//		this.workflowSearchAdaptor.CreatePlan();
	//		logger.info(this.workflowSearchAdaptor.GetCreatedPlan().Serialize());
	//		this.workflowSearchAdaptor.GetCreatedPlan().Variables.FromXML(oldVars);
	//		logger.info("-----------------------");
			
			planQueriesMap = getPlanQueriesMap(copiedPlan);
			logger.trace("executing plan after mapping: " + copiedPlan.myToString());
			logger.trace("executing plan after mapping: " + copiedPlan);
			logger.trace("planQueriesMap: " + planQueriesMap);
			
			String cachedWorkflow = planCache.getIfPresent(copiedPlan.toString());
			boolean serializableWorkflow = true;
			
			if (cachedWorkflow == null){
				//create the plan
				cachedWorkflow = this.createWorkflow(copiedPlan, true);
				
				if (cachedWorkflow == null){
					logger.error("error serializing the workflow");
					serializableWorkflow = false;
				}
				
				
	//			before = System.currentTimeMillis();
	//			this.workflowSearchAdaptor.CreatePlan();
	//			after = System.currentTimeMillis();
	//			logger.info("profiling: create plan time: " + (after - before) + " millis");
	//			
	//			logger.trace("-----------------------");
	//			logger.trace("template plan");
	//			logger.trace(this.workflowSearchAdaptor.GetCreatedPlan().Serialize());
	//			logger.trace("-----------------------");
	//			
	//			//cachedWorkflow = this.workflowSearchAdaptor.getWorkflowPlanElements();
	//			cachedWorkflow = WorkflowSearchAdaptor.serializePlan(this.workflowSearchAdaptor);
	//			logger.info("cache element size : " + cachedWorkflow.length());
	//			planCache.put(plan.toString(), cachedWorkflow);
	//			logger.info("cache has : " + planCache.asMap().keySet());
			} else{
				workflowIsCached = true;
			}
			before = System.currentTimeMillis();
			
			//we chose a read through policy for the cache for all the serializable workflows
			try {
				if (serializableWorkflow) // in case that is not serializable we have a created a new one in createWorkflow
					this.workflowSearchAdaptor = (WorkflowSearchAdaptor) WorkflowSearchAdaptor.deserializePlan(cachedWorkflow);
			} catch (Exception e) {
				logger.error("Error deserializing plan from cache. Will create it again");
				planCache.invalidateAll();
				this.createWorkflow(copiedPlan, false);
			}
			
			after = System.currentTimeMillis();
			logger.info("profiling: workflowSearchAdaptor deserialization time: " + (after - before) + " millis");
	//		logger.info("plan xml : " + cachedPlan.executionPlan);
	//		
	//		String serializedPlan = cachedPlan.executionPlan;
			
	//		ExecutionPlan fromSerializationPlan = null;
	//		try {
	//			fromSerializationPlan = (ExecutionPlan) WorkflowSearchAdaptor.deserializePlan(serializedPlan);
	//		} catch (Exception e) {
	//			logger.error("error deserializing the plan", e);
	//			throw e;
	//		}
	//		logger.info("plan de-se equal  : " + serializedPlan.equals(fromSerializationPlan.Serialize()));
	//		logger.info("serialized plan   : " + serializedPlan);
	//		logger.info("deserialized plan : " + fromSerializationPlan.Serialize());
	//		
	//		logger.info("plan is equal to cache : " + this.workflowSearchAdaptor.GetCreatedPlan().Serialize().equals(cachedPlan.executionPlan));
	//		logger.info("plan hash code         : " + this.workflowSearchAdaptor.GetCreatedPlan().Serialize().hashCode());
	//		logger.info("plan cache hash cache  : " + cachedPlan.executionPlan.hashCode());
			
			
	//		this.workflowSearchAdaptor.GetCreatedPlan().Deserialize(cachedPlan.executionPlan);
	//		logger.info("plan from cache de-se  : " + this.workflowSearchAdaptor.GetCreatedPlan().Serialize());
			
			
			
			//this.workflowSearchAdaptor.setCreatedPlan(fromSerializationPlan);
			
	//		String newVarialbes = this.workflowSearchAdaptor.getVariableCollection().ToXML();
	//		for (Entry<String, String> queryMap : planQueriesMap.entrySet()){
	//			logger.info("\t replacing : " + queryMap.getKey() + " with " + queryMap.getValue());
	//			newVarialbes = newVarialbes.replace(queryMap.getKey(), queryMap.getValue());
	//		}
	//		this.workflowSearchAdaptor.getVariableCollection().FromXML(newVarialbes);
			
			before = System.currentTimeMillis();
			//logger.trace("profiling: before restore : " + copiedPlan.myToString());
			//replacePlanNode(copiedPlan, planQueriesMap);
			//logger.trace("profiling: after restore : " + copiedPlan.myToString());
			logger.trace("before replacement : " + this.workflowSearchAdaptor.getVariableCollection().ToXML());
			for (NamedDataType ndt  : this.workflowSearchAdaptor.getVariableCollection()){
				logger.trace("checking : " + ndt.Name + " , " + ndt.Value.GetStringValue());
				if (planQueriesMap.containsKey(ndt.Value.GetStringValue())){
					logger.trace("replacing : " + ndt.Value.GetValue() + " with " + planQueriesMap.get(ndt.Value.GetStringValue()));
					ndt.Value.SetValue(planQueriesMap.get(ndt.Value.GetStringValue()));
				}
			}
			
			
			for (NamedDataType ndt  : this.workflowSearchAdaptor.getVariableCollection()){
				logger.trace("checking : " + ndt.Name + " , " + ndt.Value.GetStringValue());
				if (ndt.Value.GetStringValue().equalsIgnoreCase("sids_placeholder")){
					DataTypeArray sidsDT = new DataTypeArray();
					sidsDT.SetArrayClassCode("["+IDataType.DataTypes.String);
					sidsDT.SetValue(sids == null? new String[0] : sids.toArray(new String[sids.size()]));
					ndt.Value = sidsDT;
				}
			}
			
			logger.trace("after replacement : " + this.workflowSearchAdaptor.getVariableCollection().ToXML());
			
			after = System.currentTimeMillis();
			logger.info("profiling: variable replacement time: " + (after - before) + " millis");
			//this.workflowSearchAdaptor.GetCreatedPlan().Deserialize(newPlan);
			
			//logger.info("plan after replacement: " + this.workflowSearchAdaptor.GetCreatedPlan().Serialize());
			
			long afterTotalCreate = System.currentTimeMillis();
			logger.info("profiling: create or get from cache execution plan time: " + (afterTotalCreate - beforeTotalCreate) + " millis");
	
			
			
//			this.replaceTree(this.workflowSearchAdaptor.getWrapperTree());
			
			//execute the plan 
			before = System.currentTimeMillis();
			String resultsetEpr = this.workflowSearchAdaptor.ExecutePlan();
			after = System.currentTimeMillis();
			logger.info("profiling: execute plan time: " + (after - before) + " millis");
			
			//get the exception if any
			ExecutionException exception = this.workflowSearchAdaptor.GetCompletionError();
			
			// in case something went wrong and it was cached we will retry that
			if (workflowIsCached && (resultsetEpr == null || resultsetEpr.trim().length() == 0 || exception != null)){
				logger.info("execution failed and workflow was cached. we are going to retry after clearing the cache first");
				planCache.invalidate(copiedPlan.toString());
				return getExecutionResult(plan);
			}
			
			if(exception != null) {
				planCache.invalidate(copiedPlan.toString());
				throw exception;
			} else {
				return resultsetEpr;
			}
		} catch (Exception e) {
			try {
				planCache.invalidate(copiedPlan.toString());
			}catch (Exception ex) {
				logger.error("error while invalidating the cache for copiedPlan " + copiedPlan.toString(), ex);
			}
			logger.error("an exception happened. trying to run a new (simple) execution", e);
			//if (planQueriesMap != null)
			//	replacePlanNode(copiedPlan, planQueriesMap);
			
			return getExecutionResultSimple(plan);
		}
			
	}
	
	
//	void replaceTree(WrapperNode wrNode) {
//		//System.out.println("In replace tree");
//		if (wrNode == null){
//			//System.out.println("wrNode is null");
//			return;
//		}
//		if (wrNode.wrapper != null){
//			if (wrNode.wrapper instanceof FullTextIndexNodeWrapper) {
//				//System.out.println("Will replace..");
//				String soap = ((FullTextIndexNodeWrapper) wrNode.wrapper).getSOAPTemplate();
//				
//				StringBuffer sidsSOAP = new StringBuffer();
//				
//				if (sids != null && sids.size() > 0){
//					sidsSOAP.append("<pref:sids>");
//					sidsSOAP.append("<StringArray>");
//					sidsSOAP.append("<array>");
//					
//					for (String sid : sids){
//						sidsSOAP.append("<value>");
//						sidsSOAP.append(sid);
//						sidsSOAP.append("</value>");
//					}
//					sidsSOAP.append("</array>");
//					sidsSOAP.append("</StringArray>");
//					sidsSOAP.append("</pref:sids>");
//				} else {
//					
//				}
//				
//				System.out.println("sidsSOAP : " + sidsSOAP);
//				soap = soap.replace("</pref:sids>", sidsSOAP.toString());
//				((FullTextIndexNodeWrapper) wrNode.wrapper).setSOAPTemplate(soap);
//				
//				//System.out.println("After Replacement : ");
//				
//				//System.out.println(((FullTextIndexNodeWrapper) wrNode.wrapper).getSOAPTemplate());;
//				
//			}
//		}
//		
//		if (wrNode.children != null){
//			for (WrapperNode child : wrNode.children) {
//				replaceTree(child);
//			}
//		}
//		
//	}
	
	@Override
	public Object getExecutionPlan(PlanNode plan) throws Exception{
		if (this.cfg != null)
			this.workflowSearchAdaptor = new WorkflowSearchAdaptor(this.cfg, this.hints);
		else
			this.workflowSearchAdaptor = new WorkflowSearchAdaptor(this.hints);
		
		//set the plan to the workflow adaptor instance
		this.workflowSearchAdaptor.SetInputPlan(plan);
		
		//create the workflow plan
		this.workflowSearchAdaptor.CreatePlan();
		
		//return the workflow plan for the execution engine
		return this.workflowSearchAdaptor.GetCreatedPlan();
		
	}
	
	
	public void setSids(Set<String> sids){
		this.sids = sids;
	}
}
