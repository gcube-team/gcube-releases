package gr.uoa.di.madgik.commons.infra.nodeselection.ru;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import gr.uoa.di.madgik.commons.infra.HostingNode;
import gr.uoa.di.madgik.commons.infra.nodeselection.NodeSelector;

public class LRUNodeSelector extends RUNodeSelector 
{

	public LRUNodeSelector(NodeSelector tieBreaker)
	{
		super(tieBreaker);
	}
	
	public LRUNodeSelector()
	{
		super();
	}
	
	@Override
	public HostingNode getRUNode(List<HostingNode> candidates) 
	{
		HostingNode LRUNode = null;
		Long minTimestamp = Long.MAX_VALUE;
		for(HostingNode candidate : candidates)
		{
			if(timingInfo.get(candidate.getId()) == null)
			{
				LRUNode = candidate;
				break;
			}
			Long candidateTimestamp = timingInfo.get(candidate.getId());
			if(candidateTimestamp < minTimestamp)
			{
				minTimestamp = candidateTimestamp;
				LRUNode = candidate;
			}
		}
		return LRUNode;
	}

	@Override
	public Map<String, Long> sortTimingInfo()
	{
		return new TreeMap<String, Long>(new TimestampComparator(timingInfo, true));
	}

}
