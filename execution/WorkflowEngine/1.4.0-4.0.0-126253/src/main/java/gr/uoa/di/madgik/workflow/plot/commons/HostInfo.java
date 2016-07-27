package gr.uoa.di.madgik.workflow.plot.commons;

import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.environment.is.elements.NodeInfo;
import gr.uoa.di.madgik.is.InformationSystem;

public class HostInfo
{
	private String nodeID;
	private NodeInfo nodeInfo=null;
	
	public HostInfo(String nodeID)
	{
		this.nodeID=nodeID;
	}
	
	public HostInfo(String NodeID,NodeInfo nodeInfo)
	{
		this.nodeID=NodeID;
		this.nodeInfo=nodeInfo;
	}
	
	public NodeInfo GetNodeInfo(EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		if(this.nodeInfo==null) this.nodeInfo=InformationSystem.GetNode(nodeID,Hints);
		return this.nodeInfo;
	}
}
