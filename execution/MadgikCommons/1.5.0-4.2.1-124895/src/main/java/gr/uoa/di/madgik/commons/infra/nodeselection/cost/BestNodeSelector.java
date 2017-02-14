package gr.uoa.di.madgik.commons.infra.nodeselection.cost;

import java.util.List;

import gr.uoa.di.madgik.commons.infra.HostingNode;
import gr.uoa.di.madgik.commons.infra.nodeselection.HostingNodeInfo;
import gr.uoa.di.madgik.commons.infra.nodeselection.NodeSelector;

public class BestNodeSelector implements NodeSelector
{

	private CostBasedNodeSelector inner = null;
	
	private CostFunction getCostFunction() throws Exception
	{
		CostFunction function = new CostFunction();
		function.addCostFactor(HostingNode.ProcessorCountProperty, 0.09f, true);
		function.addCostFactor(HostingNode.ProcessorTotalClockSpeedProperty, 0.22f, true);
		function.addCostFactor(HostingNode.LoadFifteenMinutesProperty, 0.17f, false);
		function.addCostFactor(HostingNode.PhysicalMemoryAvailableProperty, 0.17f, true);
		function.addCostFactor(HostingNode.VirtualMemoryAvailableProperty, 0.05f, true);
		function.addCostFactor(CostFunction.DistanceToPrevious, 0.3f, false);
		return function;
	}
	public BestNodeSelector() throws Exception
	{
		CostFunction function = getCostFunction();
		inner = new CostBasedNodeSelector(function);
	}
	
	public BestNodeSelector(NodeSelector tieBreaker) throws Exception
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
