package gr.uoa.di.madgik.workflow.adaptor.search.nodeassignment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import gr.uoa.di.madgik.commons.infra.HostingNode;
import gr.uoa.di.madgik.commons.infra.HostingNodeAdapter;
import gr.uoa.di.madgik.commons.infra.nodeassignmentpolicy.NodeAssignmentPolicy;
import gr.uoa.di.madgik.commons.infra.nodeselection.NodeSelector;
import gr.uoa.di.madgik.rr.element.infra.RRHostingNode2HnAdapter;
import gr.uoa.di.madgik.rr.element.query.QueryHelper;
import gr.uoa.di.madgik.rr.element.search.index.DataSource;
import gr.uoa.di.madgik.rr.element.search.index.DataSourceService;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.DataSourceNode;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.OperatorNode;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.PlanNode;

public class NodeAssignmentTree 
{
	private NodeAssignmentNode root = null;
	private NodeAssignmentPolicy policy = null;
	private NodeSelector dsSelector = null;
	private float maxCollocationCost;
	private List<HostingNode> candidates = null;
	private Map<String, Float> collocationScores = null;
	private Map<String, Float> utilizationFactors = null;
	
	public NodeAssignmentTree(List<HostingNode> candidates, NodeAssignmentPolicy policy, NodeSelector dsSelector, float maxCollocationCost)
	{
		this.candidates = candidates;
		this.policy = policy;
		this.dsSelector = dsSelector;
		this.maxCollocationCost = maxCollocationCost;
	}
	
	private NodeAssignmentNode doBuild(PlanNode node) throws Exception
	{
		if(node instanceof OperatorNode)
		{
			OperatorNode opNode = (OperatorNode)node;
			List<NodeAssignmentNode> children = new ArrayList<NodeAssignmentNode>();
			
			for(PlanNode pc : opNode.getChildren())
			{
				NodeAssignmentNode c = doBuild(pc);
				if(c != null) children.add(c);
			}
			
			float cost = opNode.calculateCost(false);
			policy.setPenalty(cost/maxCollocationCost);
			HostingNode selected = policy.selectNode(candidates);
			float cScore = 0.0f;
			for(NodeAssignmentNode c : children)
			{
				if(!(c instanceof OperatorNodeAssignmentNode)) continue;
				if(c.element.assignedNode.getId().equals(selected.getId()))
					cScore += ((OperatorNodeAssignmentNode)c).collocationScore;
			}
			
			OperatorNodeAssignmentNode nan = new OperatorNodeAssignmentNode();
			cScore += cost;
			nan.collocationScore = cScore;
			nan.nodeId = selected.getId();
			nan.setChildren(children);
			
			NodeAssignmentElement nae = new NodeAssignmentElement();
			nae.assignedNode = selected;
			nae.processingNode = (OperatorNode)node;
			
			nan.element = nae;
			
			if(!collocationScores.containsKey(nan.nodeId)) collocationScores.put(nan.nodeId, cScore);
			else collocationScores.put(nan.nodeId, collocationScores.get(nan.nodeId) + cScore);
			return nan;
		}
		else if(node instanceof DataSourceNode)
		{
			return selectDataSource((DataSourceNode)node);	
		}
		throw new Exception("Unrecognized node type: " + node.getClass().getName());
		
	}
	
	private DataSourceNodeAssignmentNode selectDataSource(DataSourceNode planNode) throws Exception
	{
		Random rnd = new Random();
		HostingNodeAdapter adapter = new RRHostingNode2HnAdapter();
		List<gr.uoa.di.madgik.commons.infra.HostingNode> hns = new ArrayList<gr.uoa.di.madgik.commons.infra.HostingNode>();
		Map<String, List<String>> hnToInstance = new HashMap<String, List<String>>();
		for(String instanceId : planNode.getInstanceIds())
		{
			DataSource ds = QueryHelper.GetSourceById(instanceId);
			for(DataSourceService dss : ds.getDataSourceServices())
			{
				if(hnToInstance.containsKey(dss.getHostingNode())) continue;
				
				gr.uoa.di.madgik.rr.element.infra.HostingNode hn = new gr.uoa.di.madgik.rr.element.infra.HostingNode();
				hn.setID(dss.getHostingNode());
				hn.load(true);
				hns.add(adapter.adapt(hn));
				if(!hnToInstance.containsKey(hn.getID())) hnToInstance.put(hn.getID(), new ArrayList<String>());
				hnToInstance.get(hn.getID()).add(dss.getID());
			}
		}
		HostingNode selected = dsSelector.selectNode(hns);
		List<String> instances = hnToInstance.get(selected.getId());
		
		NodeAssignmentElement nae = new NodeAssignmentElement();
		nae.assignedNode = selected;
		nae.processingNode = planNode;
		
		DataSourceNodeAssignmentNode nan = new DataSourceNodeAssignmentNode();
		nan.element = nae;
		nan.nodeId = selected.getId();
		nan.instanceId = instances.get(rnd.nextInt(instances.size()));
		return nan;
	}
	
	public NodeAssignmentNode build(PlanNode plan) throws Exception
	{
		this.utilizationFactors = null;
		this.collocationScores = new HashMap<String, Float>();
		this.root = doBuild(plan);
		return this.root;
	}
	
	private NodeAssignmentNode getNode(String nodeId, NodeAssignmentNode node)
	{
		if(node.nodeId.equals(nodeId)) return node;
		if(node instanceof OperatorNodeAssignmentNode)
		{
			OperatorNodeAssignmentNode opNode = (OperatorNodeAssignmentNode)node;
			for(NodeAssignmentNode child : opNode.getChildren())
			{
				NodeAssignmentNode n = getNode(nodeId, child);
				if(n != null) return n;
			}
			return null;
		}
		return null;
	}
	
	public NodeAssignmentNode getNode(String nodeId)
	{
		return getNode(nodeId, root);
	}
	
//	private Map<String, Float> doGetUtilizationtFactors(NodeAssignmentNode node)
//	{
//		if(!(node instanceof OperatorNodeAssignmentNode)) return utilizationFactors;
//		OperatorNodeAssignmentNode opNode = (OperatorNodeAssignmentNode)node;
//		if(!utilizationFactors.containsKey(opNode.nodeId))
//			utilizationFactors.put(opNode.nodeId, opNode.collocationScore/collocationThreshold);
//		for(NodeAssignmentNode c : opNode.getChildren())
//			doGetUtilizationFactors(c);
//		return utilizationFactors;
//	}
	
	public Map<String, Float> getUtilizationFactors() throws Exception
	{
		if(root == null) throw new Exception("No tree has been constructed");
		if(utilizationFactors != null) return utilizationFactors;
		utilizationFactors = new HashMap<String, Float>();
		for(Map.Entry<String, Float> score : collocationScores.entrySet())
			utilizationFactors.put(score.getKey(), score.getValue()/maxCollocationCost);
		return utilizationFactors;
	//	return doGetUtilizationFactors(this.root);
	}
}
