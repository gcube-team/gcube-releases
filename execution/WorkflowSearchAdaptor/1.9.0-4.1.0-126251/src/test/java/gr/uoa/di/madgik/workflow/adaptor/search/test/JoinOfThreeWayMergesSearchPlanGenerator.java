package gr.uoa.di.madgik.workflow.adaptor.search.test;

import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.DataSourceNode;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.OperatorNode;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.PlanNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class JoinOfThreeWayMergesSearchPlanGenerator implements Generator {

	@Override
	public PlanNode generate() 
	{
		 //1
		//	
		//   
		
		Set<String> dataSource1InstanceIds = new HashSet<String>();
		dataSource1InstanceIds = new HashSet<String>();
		dataSource1InstanceIds.add("001c2f00-abcc-11e0-afbe-c703ad50473c"); //1
		String dataSource1Cql = "((((gDocCollectionID == \"5b268db0-9d63-11de-8d8f-a04a2d1ca936\") and (gDocCollectionLang == \"en\"))) and (79697524-e3bf-457b-891a-faa3a9b0385f contains an*))";
		PlanNode dataSource1Node = new DataSourceNode(dataSource1InstanceIds, null, dataSource1Cql, null);
		
		Set<String> dataSource2InstanceIds = new HashSet<String>();
		dataSource2InstanceIds.add("7c23f090-a331-11e0-855d-cb4345c5ce23"); //119
		String dataSource2Cql = "((((gDocCollectionID == \"f7e075a0-9d84-11de-a010-bf56b4e46eb8\") and (gDocCollectionLang == \"en\"))) and (79697524-e3bf-457b-891a-faa3a9b0385f contains an*))";
		PlanNode dataSource2Node = new DataSourceNode(dataSource2InstanceIds, null, dataSource2Cql, null);
		
		Set<String> dataSource3InstanceIds = new HashSet<String>();
		dataSource3InstanceIds = new HashSet<String>();
		dataSource3InstanceIds.add("342dddd0-a63a-11e0-98cf-e219cd24d91b"); //4
		String dataSource3Cql = "((((gDocCollectionID == \"c5b83790-f35f-11dd-9a37-9b05ac676cca\") and (gDocCollectionLang == \"en\"))) and (79697524-e3bf-457b-891a-faa3a9b0385f contains an*))";
		PlanNode dataSource3Node = new DataSourceNode(dataSource3InstanceIds, null, dataSource3Cql, null);
	
		ArrayList<PlanNode> merge1Children = new ArrayList<PlanNode>();
		merge1Children.add(dataSource1Node);
		merge1Children.add(dataSource2Node);
		merge1Children.add(dataSource3Node);
		PlanNode merge1Node = new OperatorNode("merge", new HashMap<String, String>(), merge1Children, null);
		//String cql = "((((gDocCollectionID == &quot;3572c6f0-2f5e-11df-a838-c20ddc2e724e&quot;) and (gDocCollectionLang == &quot;en&quot;))) and (819777d3-400f-41ee-8e4d-03bcb1e66a1d any map)) project 52b0886f-07e3-43cd-83a9-94cf0d707667 2fa6ce83-2549-4c58-8384-e0b375c9d300";
	
		Set<String> dataSource4InstanceIds = new HashSet<String>();
		dataSource4InstanceIds = new HashSet<String>();
		dataSource4InstanceIds.add("63cf7850-a63a-11e0-98d0-e219cd24d91b");
		String dataSource4Cql = "((((gDocCollectionID == \"c5b83790-f35f-11dd-9a37-9b05ac676cca\") and (gDocCollectionLang == \"en\"))) and (79697524-e3bf-457b-891a-faa3a9b0385f exact \"*\")) project b7f6b092-eed9-4a1f-b869-3f248034683c 150fae0f-e0d7-4a01-bb9e-cacb4e5da9e0 49daf947-94fe-488f-84be-0308a3c46687";
		PlanNode dataSource4Node = new DataSourceNode(dataSource4InstanceIds, null, dataSource4Cql, null);
		
		Set<String> dataSource5InstanceIds = new HashSet<String>();
		dataSource5InstanceIds.add("b3e6e600-abe1-11e0-abe8-a7a4579046dd");
		String dataSource5Cql = "((((gDocCollectionID == \"5b268db0-9d63-11de-8d8f-a04a2d1ca936\") and (gDocCollectionLang == \"en\"))) and (79697524-e3bf-457b-891a-faa3a9b0385f exact \"*\")) project b7f6b092-eed9-4a1f-b869-3f248034683c 150fae0f-e0d7-4a01-bb9e-cacb4e5da9e0 49daf947-94fe-488f-84be-0308a3c46687";
		PlanNode dataSource5Node = new DataSourceNode(dataSource5InstanceIds, null, dataSource5Cql, null);
		
		Set<String> dataSource6InstanceIds = new HashSet<String>();
		dataSource6InstanceIds.add("d48100b0-a2fb-11e0-b7e9-abd5e3cba12b"); //4
		String dataSource6Cql = "((((gDocCollectionID == \"f7e075a0-9d84-11de-a010-bf56b4e46eb8\") and (gDocCollectionLang == \"en\"))) and (79697524-e3bf-457b-891a-faa3a9b0385f exact \"*\")) project b7f6b092-eed9-4a1f-b869-3f248034683c 150fae0f-e0d7-4a01-bb9e-cacb4e5da9e0 49daf947-94fe-488f-84be-0308a3c46687";
		PlanNode dataSource6Node = new DataSourceNode(dataSource6InstanceIds, null, dataSource6Cql, null);
	
		
		ArrayList<PlanNode> merge2Children = new ArrayList<PlanNode>();
		merge2Children.add(dataSource4Node);
		merge2Children.add(dataSource5Node);
		merge2Children.add(dataSource6Node);
		PlanNode merge2Node = new OperatorNode("merge", new HashMap<String, String>(), merge2Children, null);
		
		ArrayList<PlanNode> joinChildren = new ArrayList<PlanNode>();
		HashMap<String, String> joinFunctionalArgs = new HashMap<String, String>();
		joinFunctionalArgs.put("payloadSide", "right");
		joinChildren.add(merge1Node);
		joinChildren.add(merge2Node);
		PlanNode joinNode = new OperatorNode("join", joinFunctionalArgs, joinChildren, null);
		
		return joinNode;
	}
	


}
