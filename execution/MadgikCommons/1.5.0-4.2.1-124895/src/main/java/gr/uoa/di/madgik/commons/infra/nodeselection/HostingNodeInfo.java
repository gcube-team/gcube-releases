package gr.uoa.di.madgik.commons.infra.nodeselection;

import gr.uoa.di.madgik.commons.infra.HostingNode;

public class HostingNodeInfo 
{
	public HostingNode node;
	public Float score = 0.0f;
	
	public HostingNodeInfo(HostingNode node, Float score)
	{
		this.node = node;
		this.score = score;
	}
}
