package gr.uoa.di.madgik.commons.infra.nodeselection.ru;

import gr.uoa.di.madgik.commons.infra.HostingNode;
import gr.uoa.di.madgik.commons.infra.nodeselection.NodeSelector;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MRUNodeSelector extends RUNodeSelector
{
	private static Long seasonChange = Long.MIN_VALUE;
	
	public MRUNodeSelector(NodeSelector tieBreaker)
	{
		super(tieBreaker);
	}
	
	public MRUNodeSelector()
	{
		super();
	}
	
	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.infra.nodeselection.ru.RUNodeSelector#getRUNode(java.util.List)
	 */
	@Override
	public HostingNode getRUNode(List<HostingNode> candidates) 
	{
		HostingNode MRUNode = null;
		HostingNode newSeasonNode = null;
		Long minInterval = Long.MAX_VALUE;
		Long newSeasonTimeStamp = Long.MIN_VALUE;
		for(HostingNode candidate : candidates)
		{
			if(timingInfo.get(candidate.getId()) == null)
			{
				MRUNode = candidate;
				break;
			}
			Long candidateTimestamp = timingInfo.get(candidate.getId());
			
			if (candidateTimestamp < seasonChange && minInterval > java.lang.Math.abs(candidateTimestamp - seasonChange)){
				minInterval = java.lang.Math.abs(candidateTimestamp - seasonChange);
				MRUNode = candidate;
			}
			if (newSeasonTimeStamp < candidateTimestamp){
				newSeasonTimeStamp = candidateTimestamp;
				newSeasonNode = candidate;
			}
		}

		MRUNode = MRUNode != null? MRUNode : newSeasonNode;
		Long timestamp = timingInfo.get(MRUNode.getId());
		if (timestamp!=null && timestamp >= seasonChange) {
			seasonChange = timestamp;
		}

		return MRUNode;
	}

	@Override
	public Map<String, Long> sortTimingInfo()
	{
		return new TreeMap<String, Long>(new TimestampComparator(timingInfo, false));
	}
}
