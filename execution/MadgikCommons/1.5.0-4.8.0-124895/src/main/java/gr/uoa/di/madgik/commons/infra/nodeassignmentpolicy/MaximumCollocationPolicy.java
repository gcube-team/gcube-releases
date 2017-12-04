package gr.uoa.di.madgik.commons.infra.nodeassignmentpolicy;

import java.util.Collections;
import java.util.List;

import gr.uoa.di.madgik.commons.infra.HostingNode;
import gr.uoa.di.madgik.commons.infra.nodeselection.HostingNodeInfo;
import gr.uoa.di.madgik.commons.infra.nodeselection.NodeSelector;

/**
 * This policy tries to assign as many functional plan elements of the same plan as possible to the same boundary.
 * Conversely, the policy will attempt to spread functional plan elements of different plans to different boundaries.
 * 
 * @author gerasimos.farantatos - DI NKUA
 *
 */
public class MaximumCollocationPolicy implements NodeAssignmentPolicy 
{

	public static float DefaultPenalty=0.0f;
	private NodeSelector selector = null;
	private CollocationRegistry registry = new CollocationRegistry();
	private float threshold = 0.0f;
	private float collocationPenalty;
	
	public MaximumCollocationPolicy(NodeSelector selector)
	{
		this.selector = selector;
	}
	
	public MaximumCollocationPolicy(NodeSelector selector, Float threshold){
		this.selector = selector;
		this.threshold = threshold.floatValue();
	}
	
	public MaximumCollocationPolicy(NodeSelector selector, float threshold) {
		this.selector = selector;
		this.threshold = threshold;
	}

	@Override
	public Type getType() 
	{
		return Type.MaximumCollocation;
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
		int minRound = Integer.MAX_VALUE;
		for(HostingNodeInfo hn : assessed)
		{
			int currRound = registry.currentRound(hn.node);
			if(currRound < minRound)
				minRound = currRound; 
		}
		
		for(HostingNodeInfo hn : assessed)
		{
			if(registry.isSelected(hn.node) && registry.currentRound(hn.node) == minRound)
			{
				if(hn.score + registry.getCollocationScore(hn.node) > threshold)
				{
					if(collocationPenalty > 0.0f + 1e-10 || collocationPenalty < 0.0 - 1e-10)
						registry.addToCollocationScore(hn.node, -collocationPenalty);
					selector.markSelected(hn.node);
					if(hn.score + registry.getCollocationScore(hn.node) <= threshold)
						registry.newRound(hn.node);
					return hn.node;
				}
			}
		}
		
		minRound = Integer.MAX_VALUE;
		HostingNode selected = null;
		for(HostingNodeInfo hn : assessed)
		{
			int currRound = registry.currentRound(hn.node);
			if(currRound < minRound && hn.score > threshold)
			{
				minRound = currRound;
				selected = hn.node;
			}
		}
			
		if(selected == null) throw new Exception("No nodes satisfying conditions were found during first round...check threshold (" + 
								threshold + ")" + " vs penalty (" + collocationPenalty + ")");
		registry.markSelected(selected);
		if(collocationPenalty > 0.0f + 1e-10 || collocationPenalty < 0.0 - 1e-10)
			registry.addToCollocationScore(selected, -collocationPenalty);
		selector.markSelected(selected);
		return selected;

		
//		Random rnd = new Random();
//		do
//		{
//			selected = assessed.get(rnd.nextInt()).node;
//		}while(!registry.isSelected(selected));
		
	}
	
	public float getCommitFactor(HostingNode hn) throws Exception
	{
		List<HostingNodeInfo> assessed = selector.assessNodes(Collections.singletonList(hn));
		return registry.getTotalCollocationScore(hn) / (assessed.get(0).score - threshold);
	}
	
	@Override
	public void reset()
	{
		this.registry = new CollocationRegistry();
	}
}
