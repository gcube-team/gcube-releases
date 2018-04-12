package gr.uoa.di.madgik.commons.infra.nodeselection.cost;

import java.util.List;

import gr.uoa.di.madgik.commons.infra.HostingNode;
import gr.uoa.di.madgik.commons.infra.nodeselection.HostingNodeInfo;
import gr.uoa.di.madgik.commons.infra.nodeselection.NodeSelector;

public class DistanceNodeSelector implements NodeSelector 
{
	
	private CostBasedNodeSelector inner = null;
	
	private CostFunction getCostFunction() throws Exception
	{
		CostFunction function = new CostFunction();
		function.addCostFactor(CostFunction.DistanceToPrevious, 1.0f, false);
		return function;
	}
	public DistanceNodeSelector() throws Exception
	{
		CostFunction function = getCostFunction();
		inner = new CostBasedNodeSelector(function);
	}
	
	public DistanceNodeSelector(NodeSelector tieBreaker) throws Exception
	{
		CostFunction function = getCostFunction();
		inner = new CostBasedNodeSelector(function, tieBreaker);
	}
	
	@Override
	public HostingNode selectNode(List<HostingNode> candidates) 
	{
		return inner.selectNode(candidates);
	}
	
	@Override
	public List<HostingNodeInfo> assessNodes(List<HostingNode> candidates) 
	{
		return inner.assessNodes(candidates);
	}

	@Override
	public void markSelected(HostingNode node) 
	{
		inner.markSelected(node);
	}
}
