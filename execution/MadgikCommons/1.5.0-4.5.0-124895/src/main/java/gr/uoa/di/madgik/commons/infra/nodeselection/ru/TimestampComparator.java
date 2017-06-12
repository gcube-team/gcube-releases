package gr.uoa.di.madgik.commons.infra.nodeselection.ru;

import java.util.Comparator;
import java.util.Map;

public class TimestampComparator implements Comparator
{
	private Map<String, Long> base;
	private boolean ascending;
	
	public TimestampComparator(Map<String, Long> base, boolean ascending)
	{
		this.base = base;
		this.ascending = ascending;
	}

	@Override
	public int compare(Object o1, Object o2) 
	{
		if(base.get(o1) < base.get(o2))
			return ascending ? -1 : 1;
		else if (base.get(o1) == base.get(o2))
			return 0;
		else
			return ascending ? 1 : -1;
	}
}
