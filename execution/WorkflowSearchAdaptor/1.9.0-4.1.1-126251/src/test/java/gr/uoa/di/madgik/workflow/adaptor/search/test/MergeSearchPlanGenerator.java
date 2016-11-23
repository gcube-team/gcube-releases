package gr.uoa.di.madgik.workflow.adaptor.search.test;

import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.DataSourceNode;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.OperatorNode;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.PlanNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MergeSearchPlanGenerator implements Generator 
{

	private int inputs = 0;
	private int current = 0;
	private PlanNode[] nodes = null;
	
	public MergeSearchPlanGenerator(int inputs) throws Exception 
	{
		if(inputs <= 0)
			throw new Exception("Non-positive number of inputs");
		
		this.inputs = inputs;
		nodes = new PlanNode[inputs];
	}
	
	public void addDataSource(int id, Set<String> instanceIds, String cql) throws Exception 
	{
		if(id >= current) current = id+1;
		if(id >= inputs) throw new Exception("Invalid input id");
		PlanNode datasourceNode = new DataSourceNode(instanceIds, null, cql, null);
		nodes[id] = datasourceNode;
	}
	
	public void addDataSource(int id, String instanceId, String cql) throws Exception
	{
		Set<String> instanceIds = new HashSet<String>();
		instanceIds.add(instanceId);
		addDataSource(id, instanceIds, cql);
	}
	
	public void addDataSource(Set<String> instanceIds, String cql) throws Exception
	{
		addDataSource(current, instanceIds, cql);
	}
	
	public void addDataSource(String instanceId, String cql) throws Exception 
	{
		addDataSource(current, instanceId, cql);
	}

	public void addDataSourcesWithMultipleInstances(List<Set<String>> instanceIds, List<String> cqlQueries) throws Exception
	{
		if(instanceIds.size() != cqlQueries.size())
			throw new Exception("Instance ids-CQL query size mismatch");
		if(current + instanceIds.size() > inputs)
			throw new Exception("DataSources exceed inputs");
		
		for(int i = 0; i < instanceIds.size(); i++)
		{
			PlanNode datasourceNode = new DataSourceNode(instanceIds.get(i), null, cqlQueries.get(i), null);
			nodes[current++] = datasourceNode;
		}
	}
	
	public void addDataSources(List<String> instanceIds, List<String> cqlQueries) throws Exception
	{
		List<Set<String>> instanceIdSets = new ArrayList<Set<String>>();
		for(String instanceId : instanceIds)
		{
			Set<String> instanceIdSet = new HashSet<String>();
			instanceIdSet.add(instanceId);
			instanceIdSets.add(instanceIdSet);
		}
		addDataSourcesWithMultipleInstances(instanceIdSets, cqlQueries);
	}
	
	public void addPlanNode(int id, PlanNode node) throws Exception 
	{
		if(id > current) current = id;
		if(id >= inputs) throw new Exception("Invalid input id");
		nodes[id] = node;
	}
	
	public void addPlanNode(PlanNode node) throws Exception 
	{
		addPlanNode(this.current, node);
	}

	
	@Override
	public PlanNode generate() 
	{
		ArrayList<PlanNode> mergeChildren = new ArrayList<PlanNode>();
		for(PlanNode node : nodes)
			mergeChildren.add(node);
		
		PlanNode mergeNode = new OperatorNode("merge", new HashMap<String, String>(), mergeChildren, null);
		//String cql = "((((gDocCollectionID == &quot;3572c6f0-2f5e-11df-a838-c20ddc2e724e&quot;) and (gDocCollectionLang == &quot;en&quot;))) and (819777d3-400f-41ee-8e4d-03bcb1e66a1d any map)) project 52b0886f-07e3-43cd-83a9-94cf0d707667 2fa6ce83-2549-4c58-8384-e0b375c9d300";
	
		return mergeNode;
	}
	


}
