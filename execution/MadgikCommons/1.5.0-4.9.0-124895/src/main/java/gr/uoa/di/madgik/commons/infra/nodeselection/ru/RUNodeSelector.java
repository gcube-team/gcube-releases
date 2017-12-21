package gr.uoa.di.madgik.commons.infra.nodeselection.ru;

import gr.uoa.di.madgik.commons.infra.HostingNode;
import gr.uoa.di.madgik.commons.infra.nodeselection.HostingNodeInfo;
import gr.uoa.di.madgik.commons.infra.nodeselection.NodeSelector;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public abstract class RUNodeSelector implements NodeSelector {

	protected static Map<String, Long> timingInfo = new ConcurrentHashMap<String, Long>();
	
	protected NodeSelector tieBreakerSelector = null;
	
	public RUNodeSelector() { }
	
	public RUNodeSelector(NodeSelector tieBreaker)
	{
		this.tieBreakerSelector = tieBreaker;
	}
	
	@Override
	public HostingNode selectNode(List<HostingNode> candidates)
	{
		List<HostingNode> leftOver = new ArrayList<HostingNode>();
		boolean firstRound = false;
		if(tieBreakerSelector != null)
		{
			for(HostingNode c : candidates)
			{
				if(!timingInfo.containsKey(c.getId())) {
					firstRound = true;
					leftOver.add(c);
				}
			}
		}
		HostingNode RUNode = null;
		if(candidates.isEmpty()) return null;
		if(firstRound)
			RUNode = tieBreakerSelector.selectNode(leftOver);
		else
			RUNode = getRUNode(candidates);
		
		timingInfo.put(RUNode.getId(), System.nanoTime());
		return RUNode;
	}
	
	public abstract HostingNode getRUNode(List<HostingNode> candidates);
	public abstract Map<String, Long> sortTimingInfo();
	
	@Override
	public List<HostingNodeInfo> assessNodes(List<HostingNode> candidates) {
		Map<String, Long> sortedTimingInfo = sortTimingInfo();
		sortedTimingInfo.putAll(timingInfo);
		Float firstRoundMinScore = Float.MAX_VALUE;
		List<HostingNodeInfo> assessedNodes = new ArrayList<HostingNodeInfo>();
		List<HostingNode> unselectedNodes = new ArrayList<HostingNode>();
		List<HostingNodeInfo> firstRoundAssessed = null;
		
		//scores are fused with those of first round selector
		int foundUnselected = 0;
		for(HostingNode hn : candidates)
		{
			if(!timingInfo.containsKey(hn.getId()))
			{
				if(firstRoundAssessed == null && tieBreakerSelector != null)
				{
					firstRoundAssessed = tieBreakerSelector.assessNodes(candidates);
					firstRoundMinScore = firstRoundAssessed.get(firstRoundAssessed.size()-1).score;
				}
				foundUnselected++;
				unselectedNodes.add(hn);
			}
		}
		
		Map<String, HostingNode> hnIndex = new HashMap<String, HostingNode>();
		for(HostingNode hn : candidates) hnIndex.put(hn.getId(), hn);
		
		if(foundUnselected > 0)
		{
			if(tieBreakerSelector != null)
			{
				for(HostingNodeInfo fra : firstRoundAssessed)
				{
					if(!timingInfo.containsKey(fra.node.getId()))
						assessedNodes.add(new HostingNodeInfo(fra.node, fra.score));
				}
				if(Math.abs(assessedNodes.get(0).score - 1.0f) > 1e-10)
				{
					//normalize
					float factor = 1.0f/assessedNodes.get(0).score;
					for(HostingNodeInfo hn : assessedNodes)
						hn.score *= factor;
					firstRoundMinScore *= factor;
				}
				if(foundUnselected < candidates.size())
				{
					int j = 0;
					float step = firstRoundMinScore / (candidates.size()-foundUnselected+1);
					for(String hnId : sortedTimingInfo.keySet())
					{
						HostingNode hn = hnIndex.get(hnId);
						if(hn != null) assessedNodes.add(new HostingNodeInfo(hn, firstRoundMinScore - (++j)*step));
					}
				}
			}else
			{
				for(HostingNode hn : unselectedNodes)
					assessedNodes.add(new HostingNodeInfo(hn, 1.0f));
				int i = 1;
				for(String hnId : sortedTimingInfo.keySet())
				{
					HostingNode hn = hnIndex.get(hnId);
					if(hn != null) assessedNodes.add(new HostingNodeInfo(hn, 1.0f - (i++)*(1.0f/(float)candidates.size())));
				}
			}
		}else
		{
			
			int i = 0;
			for(String hnId : sortedTimingInfo.keySet())
			{
				HostingNode hn = hnIndex.get(hnId);
				if(hn != null) assessedNodes.add(new HostingNodeInfo(hn, 1.0f - (i++)*(1.0f/(float)candidates.size())));
			}
		}
		return assessedNodes;
	}
	
	@Override
	public void markSelected(HostingNode node)
	{
		timingInfo.put(node.getId(), System.nanoTime());
	}
	
	public static void main(String[] args)
	{
//		NodeSelector ns = new LRUNodeSelector();
//		List<NodeInfo> ni = new ArrayList<NodeInfo>();
//		for(int i = 0; i < 10; i++)
//			ni.add(new NodeInfo());
//		int[] selections = new int[ni.size()];
//		
//		while(true)
//		{
//			NodeInfo selected = ns.selectNode(ni);
//			for(int i = 0; i < 10; i++)
//			{
//				if(ni.get(i) == selected)
//				{
//					selections[i]++;
//					break;
//				}
//			}
//			System.out.println(Arrays.toString(selections));
//			try {TimeUnit.SECONDS.sleep(10);}catch(InterruptedException e) { }
//		}
		
	}
}
