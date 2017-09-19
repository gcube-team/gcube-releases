package gr.uoa.di.madgik.commons.infra.nodeselection.cost;

import gr.uoa.di.madgik.commons.infra.HostingNode;

import java.util.Comparator;
import java.util.Map;

public class CostComparator implements Comparator
{
	private Map<HostingNode, Float> base;
	
	public CostComparator(Map<HostingNode, Float> base)
	{
		this.base = base;
	}

	@Override
	public int compare(Object o1, Object o2) 
	{
		if(base.get(o1) < base.get(o2))
			return 1;
		else if (base.get(o1) == base.get(o2))
			return 0;
		else
			return -1;
	}
}
