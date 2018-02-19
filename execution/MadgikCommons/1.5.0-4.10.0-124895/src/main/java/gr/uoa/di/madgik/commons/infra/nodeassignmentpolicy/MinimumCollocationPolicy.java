package gr.uoa.di.madgik.commons.infra.nodeassignmentpolicy;

import java.util.List;
import java.util.Random;

import gr.uoa.di.madgik.commons.infra.HostingNode;
import gr.uoa.di.madgik.commons.infra.nodeselection.HostingNodeInfo;
import gr.uoa.di.madgik.commons.infra.nodeselection.NodeSelector;

/**
 * This policy tries to assign as few functional plan elements of the same plan as possible to the same boundary.
 * 
 * @author gerasimos.farantatos - DI NKUA
 *
 */
public class MinimumCollocationPolicy implements NodeAssignmentPolicy
{

	public static float DefaultPenalty=0.0f;
	private NodeSelector selector = null;
	private CollocationRegistry registry = new CollocationRegistry();
	private float threshold = 0.0f;
	private float collocationPenalty;
	
	public MinimumCollocationPolicy(NodeSelector selector)
	{
		this.selector = selector;
	}
	
	public MinimumCollocationPolicy(NodeSelector selector, float threshold) 
	{
		this.selector = selector;
		this.threshold = threshold;
	}

	@Override
	public Type getType() 
	{
		return Type.MinimumCollocation;
	}
	
	@Override
	public void setPenalty(float collocationPenalty)
	{
		this.collocationPenalty = collocationPenalty;
	}

	@Override
	public HostingNode selectNode(List<HostingNode> candidates) throws Exception 
	{
		List<HostingNodeInfo> assessed = selector.assessNodes(candidates);
		for(HostingNodeInfo hn : assessed)
		{
			if(!registry.isSelected(hn.node))
			{
				if(hn.score + registry.getCollocationScore(hn.node) > threshold)
				{
					if(collocationPenalty > 0.0f + 1e-10 || collocationPenalty < 0.0 - 1e-10)
						registry.addToCollocationScore(hn.node, collocationPenalty);
					selector.markSelected(hn.node);
					return hn.node;
				}
			}
		}
		
		for(HostingNodeInfo hn : assessed)
		{
			if(!registry.isSelected(hn.node))
			{
				registry.markSelected(hn.node);
				if(collocationPenalty > 0.0f + 1e-10 || collocationPenalty < 0.0 - 1e-10)
					registry.addToCollocationScore(hn.node, collocationPenalty);
					selector.markSelected(hn.node);
					return hn.node;
			}
		}
		
		HostingNode selected = null;
		Random rnd = new Random();
		do
		{
			selected = assessed.get(rnd.nextInt()).node;
		}while(!registry.isSelected(selected));
		
		if(collocationPenalty > 0.0f + 1e-10 || collocationPenalty < 0.0 - 1e-10)
			registry.addToCollocationScore(selected, collocationPenalty);
		return selected;
	}
	
	@Override
	public void reset()
	{
		this.registry = new CollocationRegistry();
	}
}
