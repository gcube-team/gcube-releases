package gr.uoa.di.madgik.workflow.adaptor.search.analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.uoa.di.madgik.workflow.adaptor.search.rewriter.SearchPlanRewriter;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.Constants;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.DataSourceNode;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.OperatorNode;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.PlanNode;

public class SearchPlanAnalyzer 
{
	private int levels = -1;
	private int numElements = -1;
	
	private int numElementsCurr;
	
	private static Logger logger = LoggerFactory.getLogger(SearchPlanAnalyzer.class);
	
	public SearchPlanAnalyzer(int levels, int numElements)
	{
		this.levels = levels;
		this.numElements = numElements;
	}
	
	private boolean doIsComplex(PlanNode node, int level) throws Exception
	{
		if(level > this.levels) return true;
		if(node instanceof DataSourceNode) return false;
		else if(node instanceof OperatorNode)
		{
			OperatorNode opNode = (OperatorNode)node;
			if(opNode.getChildren().size() > this.numElementsCurr) return true;
			this.numElementsCurr -= opNode.getChildren().size();
			for(PlanNode child : opNode.getChildren())
				if(doIsComplex(child, level+1) == true) return true;
			return false;
		}
		else throw new Exception("Unrecognized node type");
	}
	
	public boolean isComplex(PlanNode node) throws Exception
	{
		numElementsCurr = numElements;
		return doIsComplex(node, 1);
	}
	
	
	public int countOperatorNodes(PlanNode node) throws Exception
	{
		if(node instanceof DataSourceNode) return 0;
		else if(node instanceof OperatorNode)
		{
			OperatorNode opNode = (OperatorNode)node;
			int count = 1;
			for(PlanNode child : opNode.getChildren())
				count += countOperatorNodes(child);
			return count;
		}
		throw new Exception("Unrecognized node type");
	}
	
	public int countDatasourceNodes(PlanNode node) throws Exception
	{
		if(node instanceof DataSourceNode) return 1;
		else if(node instanceof OperatorNode)
		{
			OperatorNode opNode = (OperatorNode)node;
			int count = 0;
			for(PlanNode child : opNode.getChildren())
				count += countDatasourceNodes(child);
			return count;
		}
		throw new Exception("Unrecognized node type");
	}
	
	public int countNodes(PlanNode node) throws Exception
	{
		if(node instanceof DataSourceNode) return 1;
		else if(node instanceof OperatorNode)
		{
			OperatorNode opNode = (OperatorNode)node;
			int count = 1;
			for(PlanNode child : opNode.getChildren())
				count += countNodes(child);
			return count;
		}
		throw new Exception("Unrecognized node type");
	}
	
	public float calculateOperatorCost(PlanNode node) throws Exception
	{
		if(node instanceof DataSourceNode) return 0.0f;
		else if(node instanceof OperatorNode)
		{
			OperatorNode opNode = (OperatorNode)node;
			float cost = opNode.calculateCost(false);
			for(PlanNode child : opNode.getChildren())
				cost += calculateOperatorCost(child);
			return cost;
		}
		throw new Exception("Unrecognized node type");
	}
	
	public float calculateDatasourceCost(PlanNode node) throws Exception
	{
		if(node instanceof DataSourceNode) return 1.0f;
		else if(node instanceof OperatorNode)
		{
			OperatorNode opNode = (OperatorNode)node;
			float cost = 0.0f;
			for(PlanNode child : opNode.getChildren())
				cost += calculateDatasourceCost(child);
			return cost;
		}
		throw new Exception("Unrecognized node type");
	}
	
	public float calculateCost(PlanNode node) throws Exception
	{
		if(node instanceof DataSourceNode) return 1.0f;
		else if(node instanceof OperatorNode)
		{
			OperatorNode opNode = (OperatorNode)node;
			float cost = opNode.calculateCost(false);
			for(PlanNode child : opNode.getChildren())
				cost += calculateCost(child);
			return cost;
		}
		throw new Exception("Unrecognized node type");
	}
	
	
	public static void main(String[] args) throws Exception
	{
		int numComplexPlanNodes = 30;
		int complexLevels = 3;
		int complexNumNodes = 20;
		int numSimplePlanNodes = 10;
		
		SearchPlanAnalyzer analyzer = new SearchPlanAnalyzer(complexLevels, complexNumNodes);
		ArrayList<PlanNode> c = new ArrayList<PlanNode>();
		for(int i = 0; i < numComplexPlanNodes; i++)
		{
			Set<String> instanceIds = new HashSet<String>();
			for(int j = 0; j < 3; j++)
				instanceIds.add(i + "_" + j);
			c.add(new DataSourceNode(instanceIds, new HashMap<String, String>(), "cql_"+i, new HashSet<String>()));
		}
		HashMap<String, String> functionalArgs = new HashMap<String, String>();
		functionalArgs.put(Constants.DUPLICATEELIMINATION, "true");
		PlanNode numComplexPlan = new OperatorNode(Constants.MERGE,functionalArgs, c, new HashSet<String>());
		
		System.out.println("numComplexPlan");
		System.out.println(numComplexPlan.toString());
		boolean isComplex = analyzer.isComplex(numComplexPlan);
		System.out.println("Is complex? " + isComplex);
		System.out.println("Operator nodes: " + analyzer.countOperatorNodes(numComplexPlan));
		System.out.println("Data source nodes: " + analyzer.countDatasourceNodes(numComplexPlan));
		System.out.println("Total nodes: " + analyzer.countNodes(numComplexPlan));
		System.out.println("Operator cost: " + analyzer.calculateOperatorCost(numComplexPlan));
		System.out.println("Data source cost: " + analyzer.calculateDatasourceCost(numComplexPlan));
		System.out.println("Total cost: " + analyzer.calculateCost(numComplexPlan));
		assert(isComplex);
		
		SearchPlanRewriter rewriter = new SearchPlanRewriter(3.0f);
		c = new ArrayList<PlanNode>();
		for(int i = 0; i < numSimplePlanNodes; i++)
		{
			Set<String> instanceIds = new HashSet<String>();
			for(int j = 0; j < 3; j++)
				instanceIds.add(i + "_" + j);
			c.add(new DataSourceNode(instanceIds, new HashMap<String, String>(), "cql_"+i, new HashSet<String>()));
		}
		functionalArgs = new HashMap<String, String>();
		functionalArgs.put(Constants.DUPLICATEELIMINATION, "true");
		
		PlanNode smallerPlan = new OperatorNode(Constants.MERGE,functionalArgs, c, new HashSet<String>());
		System.out.println("Smaller plan: Original");
		System.out.println(smallerPlan.toString());
		isComplex = analyzer.isComplex(smallerPlan);
		System.out.println("Original: Is complex? " + isComplex);
		System.out.println("Operator nodes: " + analyzer.countOperatorNodes(smallerPlan));
		System.out.println("Data source nodes: " + analyzer.countDatasourceNodes(smallerPlan));
		System.out.println("Total nodes: " + analyzer.countNodes(smallerPlan));
		System.out.println("Operator cost: " + analyzer.calculateOperatorCost(smallerPlan));
		System.out.println("Data source cost: " + analyzer.calculateDatasourceCost(smallerPlan));
		System.out.println("Total cost: " + analyzer.calculateCost(smallerPlan));
		assert(!isComplex);
		
		PlanNode rewriten = rewriter.rewrite(smallerPlan);
		System.out.println("Smaller plan: Rewriten");
		System.out.println(rewriten.toString());
		
		isComplex = analyzer.isComplex(rewriten);
		System.out.println("Rewriten: Is complex? " + isComplex);
		System.out.println("Operator nodes: " + analyzer.countOperatorNodes(rewriten));
		System.out.println("Data source nodes: " + analyzer.countDatasourceNodes(rewriten));
		System.out.println("Total nodes: " + analyzer.countNodes(rewriten));
		System.out.println("Operator cost: " + analyzer.calculateOperatorCost(rewriten));
		System.out.println("Data source cost: " + analyzer.calculateDatasourceCost(rewriten));
		System.out.println("Total cost: " + analyzer.calculateCost(rewriten));
		assert(isComplex);
	}

}
