package gr.uoa.di.madgik.workflow.adaptor.search.rewriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.Constants;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.DataSourceNode;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.OperatorNode;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.PlanNode;

public class SearchPlanRewriter 
{
	private float costThreshold = 0.0f;
	
	private static Logger logger = LoggerFactory.getLogger(SearchPlanRewriter.class);
	
	public SearchPlanRewriter(float costThreshold)
	{
		this.costThreshold = costThreshold;
	}
	
	public PlanNode rewrite(PlanNode node) throws Exception
	{
		if(node instanceof DataSourceNode) return node;
		else if(node instanceof OperatorNode)
		{
			OperatorNode opNode = (OperatorNode)node;
			if(opNode.getFunctionality().equals(Constants.MERGE) || opNode.getFunctionality().equals(Constants.MERGESORT))
			{
				if(opNode.calculateCost(false) > costThreshold)
				{
					int numChildren = opNode.maxChildrenForCost(costThreshold);
					if(numChildren < 2)
					{
						logger.warn("Cost threshold " + this.costThreshold + " results in less a maximum number of children less than 2. Setting children=2");
						numChildren = 2;
					}
					
					if(opNode.getChildren().size() > numChildren + 1)
					{
						ArrayList<PlanNode> nc = new ArrayList<PlanNode>();
						int i = 0;
						int remaining = opNode.getChildren().size();
						for(int c = 0; c < numChildren; c++)
						{
							ArrayList<PlanNode> bottomC = new ArrayList<PlanNode>();
							int numBottomC = (int)Math.ceil(opNode.getChildren().size()/numChildren);
							remaining -= numBottomC;
							if(remaining == 1)
							{
								numBottomC--;
								if(numBottomC == 1)
								{
									numBottomC = remaining + 2;
									remaining = 0;
								}else remaining++;
							}
							for(int j = 0; j < numBottomC; j++)
							{
								bottomC.add(opNode.getChildren().get(i++));
							}
							OperatorNode child = new OperatorNode(opNode.getFunctionality(), new HashMap<String, String>(opNode.getFunctionalArgs()), bottomC, new HashSet<String>(opNode.getProjections()));
							nc.add(child);
						}
						opNode.setChildren(nc);
					}
				}
				
			}
			
			ArrayList<PlanNode> nc = new ArrayList<PlanNode>();
			for(PlanNode c : opNode.getChildren())
				nc.add(rewrite(c));
			opNode.setChildren(nc);
			return opNode;
		}
		else throw new Exception("Unrecognized node type");
	}
	
	public static void main(String[] args) throws Exception
	{
		SearchPlanRewriter rewriter = new SearchPlanRewriter(4.0f);
		ArrayList<PlanNode> c = new ArrayList<PlanNode>();
		for(int i = 0; i < 20; i++)
		{
			Set<String> instanceIds = new HashSet<String>();
			for(int j = 0; j < 3; j++)
				instanceIds.add(i + "_" + j);
			c.add(new DataSourceNode(instanceIds, new HashMap<String, String>(), "cql_"+i, new HashSet<String>()));
		}
		HashMap<String, String> functionalArgs = new HashMap<String, String>();
		functionalArgs.put(Constants.DUPLICATEELIMINATION, "true");
		PlanNode p = new OperatorNode(Constants.MERGE,functionalArgs, c, new HashSet<String>());
		
		System.out.println(p.toString());
		PlanNode newP = rewriter.rewrite(p);
		System.out.println("Rewritten");
		System.out.println(newP.toString());
	}

}
