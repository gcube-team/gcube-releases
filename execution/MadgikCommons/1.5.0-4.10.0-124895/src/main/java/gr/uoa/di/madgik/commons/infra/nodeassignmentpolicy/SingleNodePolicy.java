package gr.uoa.di.madgik.commons.infra.nodeassignmentpolicy;

import java.util.List;

import gr.uoa.di.madgik.commons.infra.HostingNode;
import gr.uoa.di.madgik.commons.infra.nodeselection.NodeSelector;

/**
 * 
 * @author gerasimos.farantatos - DI NKUA
 *
 */
public class SingleNodePolicy implements NodeAssignmentPolicy 
{
	
	private NodeSelector selector = null;
	private CollocationRegistry registry = new CollocationRegistry();
	
	public SingleNodePolicy(NodeSelector selector) 
	{
		this.selector = selector;
	}
	
	public SingleNodePolicy(NodeSelector selector, float threshold)
	{
		this.selector = selector;
	}
	

	@Override
	public Type getType() 
	{
		return Type.SingleNode;
	}

	@Override
	public void setPenalty(float collocationPenalty) throws Exception { }

	@Override
	public HostingNode selectNode(List<HostingNode> candidates) throws Exception 
	{
		for(HostingNode hn : candidates)
		{
			if(registry.isSelected(hn)) return hn;
		}
		return selector.selectNode(candidates);
	}
	
	@Override
	public void reset()
	{
		this.registry = new CollocationRegistry();
	}
}
