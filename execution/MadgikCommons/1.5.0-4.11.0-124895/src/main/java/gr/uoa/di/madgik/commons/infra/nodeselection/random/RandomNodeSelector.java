package gr.uoa.di.madgik.commons.infra.nodeselection.random;

import gr.uoa.di.madgik.commons.infra.HostingNode;
import gr.uoa.di.madgik.commons.infra.nodeselection.HostingNodeInfo;
import gr.uoa.di.madgik.commons.infra.nodeselection.NodeSelector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomNodeSelector implements NodeSelector {
	
	@Override
	public HostingNode selectNode(List<HostingNode> candidates)
	{
		if(candidates.isEmpty()) return null;
		Random rnd = new Random();
		return candidates.get(rnd.nextInt(candidates.size()));
	}

	@Override
	public List<HostingNodeInfo> assessNodes(List<HostingNode> candidates) 
	{
		Collections.shuffle(candidates, new Random());
		List<HostingNodeInfo> hns = new ArrayList<HostingNodeInfo>();
		int i = 0;
		for(HostingNode hn : candidates)
		{
			hns.add(new HostingNodeInfo(hn, 1.0f));
			i++;
		}
		return hns;
	}

	@Override
	public void markSelected(HostingNode node) { }
}
