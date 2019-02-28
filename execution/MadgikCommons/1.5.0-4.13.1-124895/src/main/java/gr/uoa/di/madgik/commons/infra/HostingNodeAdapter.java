package gr.uoa.di.madgik.commons.infra;

import java.util.ArrayList;
import java.util.List;

public abstract class HostingNodeAdapter 
{

	public abstract HostingNode adapt(Object o) throws Exception;
	
	public List<HostingNode> adaptAll(List<? extends Object> l) throws Exception
	{
		List<HostingNode> adapted = new ArrayList<HostingNode>();
		for(Object o : l)
		{
			adapted.add(adapt(o));
		}
		return adapted;
	}
}
