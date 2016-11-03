package gr.uoa.di.madgik.environment.infra;

import java.util.HashMap;
import java.util.Map;

import gr.uoa.di.madgik.commons.infra.HostingNode;
import gr.uoa.di.madgik.commons.infra.HostingNodeAdapter;
import gr.uoa.di.madgik.environment.is.elements.ExtensionPair;
import gr.uoa.di.madgik.environment.is.elements.NodeInfo;

public class NodeInfo2HostingNodeAdapter extends HostingNodeAdapter
{
	

	@Override
	public HostingNode adapt(Object o) throws Exception 
	{	
		if(o == null) return null;
		if(!(o instanceof NodeInfo)) throw new Exception("Cannot adapt object of type " + o.getClass().getName());
		NodeInfo nodeInfo = (NodeInfo)o;
		Map<String, String> pairs = new HashMap<String, String>();
		for(Map.Entry<String, ExtensionPair> se : nodeInfo.StaticExtensions.entrySet())
			pairs.put(se.getKey(), se.getValue().Value);
		for(Map.Entry<String, ExtensionPair> se : nodeInfo.DynamicExtensions.entrySet())
			pairs.put(se.getKey(), se.getValue().Value);
		HostingNode hn = new HostingNode(nodeInfo.ID, pairs);
		if(nodeInfo.isLocal()) hn.markLocal();
		return hn;
	}

}
