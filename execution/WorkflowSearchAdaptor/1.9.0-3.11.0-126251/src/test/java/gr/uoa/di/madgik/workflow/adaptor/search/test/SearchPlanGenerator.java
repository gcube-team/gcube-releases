package gr.uoa.di.madgik.workflow.adaptor.search.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.Constants;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.DataSourceNode;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.OperatorNode;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.PlanNode;

public class SearchPlanGenerator implements Generator 
{
	
	@Override
	public PlanNode generate() 
	{
		
//	List<FTIndexService> ftIndexes = new ArrayList<FTIndexService>();
		Set<String> datasourceInstanceIds = new HashSet<String>();
//		for(FTIndexService index : ftIndexes)
//			datasourceInstanceIds.add(index.getID());
		datasourceInstanceIds.add("123-456");
		datasourceInstanceIds.add("899-909");
		
		HashMap<String, String> topMergeArgs = new HashMap<String, String>();
		topMergeArgs.put(Constants.DUPLICATEELIMINATION, "true");
		
			HashMap<String, String> lvl1LeftJoinArgs = new HashMap<String, String>();
			lvl1LeftJoinArgs.put(Constants.PAYLOADSIDE, Constants.PAYLOADBOTH);
			
				PlanNode lvl2LeftFTIndexNode = new DataSourceNode(datasourceInstanceIds, null, "third Lvl Left FT Index query", null);
				PlanNode lvl2RightFTIndexNode = new DataSourceNode(datasourceInstanceIds, null, "third lvl Right FT Index query", null);
			
			ArrayList<PlanNode> lvl1JoinNodeChildren = new ArrayList<PlanNode>();
			lvl1JoinNodeChildren.add(lvl2LeftFTIndexNode);
			lvl1JoinNodeChildren.add(lvl2RightFTIndexNode);
			PlanNode lvl1JoinNode = new OperatorNode(Constants.JOIN, lvl1LeftJoinArgs, lvl1JoinNodeChildren, null);
			
			PlanNode lvl1FTIndexOneNode = new DataSourceNode(datasourceInstanceIds, null, "second Lvl FT Index One query", null);
			PlanNode lvl1FTIndexTwoNode = new DataSourceNode(datasourceInstanceIds, null, "second Lvl FT Index Two query", null);
			PlanNode lvl1FTIndexThreeNode = new DataSourceNode(datasourceInstanceIds, null, "second Lvl FT Index Three query", null);
			
		ArrayList<PlanNode> topMergeChildren = new ArrayList<PlanNode>();
		topMergeChildren.add(lvl1JoinNode);
		topMergeChildren.add(lvl1FTIndexOneNode);
		topMergeChildren.add(lvl1FTIndexTwoNode);
		topMergeChildren.add(lvl1FTIndexThreeNode);
		
		PlanNode top = new OperatorNode(Constants.MERGE, topMergeArgs, topMergeChildren, null);
		
		return top;
	}
}
