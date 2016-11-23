package gr.uoa.di.madgik.commons.infra.nodeassignmentpolicy;

import gr.uoa.di.madgik.commons.infra.HostingNode;

import java.util.List;

/**
 * 
 * @author gerasimos.farantatos - DI NKUA
 *
 */
public interface NodeAssignmentPolicy
{
	
	public enum Type 
	{
		LocalOnly,
		SingleNode,
		SingleRemoteNode,
		MinimumCollocation,
		MaximumCollocation
	}
	
	public Type getType();
	
	public void setPenalty(float collocationPenalty) throws Exception;
	public HostingNode selectNode(List<HostingNode> candidates) throws Exception;
	public void reset();
}
