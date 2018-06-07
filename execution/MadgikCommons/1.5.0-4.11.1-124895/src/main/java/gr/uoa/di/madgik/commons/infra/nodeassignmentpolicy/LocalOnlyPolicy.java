package gr.uoa.di.madgik.commons.infra.nodeassignmentpolicy;

import gr.uoa.di.madgik.commons.infra.HostingNode;
import gr.uoa.di.madgik.commons.infra.nodeselection.NodeSelector;

import java.util.List;

/**
 * 
 * @author gerasimos.farantatos - DI NKUA
 *
 */
public class LocalOnlyPolicy implements NodeAssignmentPolicy 
{

	public LocalOnlyPolicy() { }

	public LocalOnlyPolicy(NodeSelector selector) { }
	
	public LocalOnlyPolicy(NodeSelector selector, float threshold) { }
	
	@Override
	public Type getType() 
	{
		return Type.LocalOnly;
	}

	@Override
	public void setPenalty(float collocationPenalty) throws Exception { }

	@Override
	public HostingNode selectNode(List<HostingNode> candidates) throws Exception {
		for(HostingNode candidate: candidates)
		{
			if(candidate.isLocal()) return candidate;
		}
		throw new Exception("Local node was not found among candidates");
	}
	
	@Override
	public void reset() { }
}
