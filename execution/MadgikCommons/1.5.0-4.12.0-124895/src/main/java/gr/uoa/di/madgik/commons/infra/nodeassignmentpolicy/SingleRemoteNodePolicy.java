package gr.uoa.di.madgik.commons.infra.nodeassignmentpolicy;

import java.util.List;
import java.util.logging.Logger;

import gr.uoa.di.madgik.commons.infra.HostingNode;
import gr.uoa.di.madgik.commons.infra.nodeselection.HostingNodeInfo;
import gr.uoa.di.madgik.commons.infra.nodeselection.NodeSelector;

/**
 * 
 * @author gerasimos.farantatos - DI NKUA
 *
 */
public class SingleRemoteNodePolicy implements NodeAssignmentPolicy 
{
	
	private static Logger logger = Logger.getLogger(SingleRemoteNodePolicy.class.getName());
	
	private NodeSelector selector = null;
	private CollocationRegistry registry = new CollocationRegistry();
	
	public SingleRemoteNodePolicy(NodeSelector selector) 
	{
		this.selector = selector;
	}
	
	public SingleRemoteNodePolicy(NodeSelector selector, float threshold)
	{
		this.selector = selector;
	}

	@Override
	public Type getType() 
	{
		return Type.SingleNode;
	}

	@Override
	public void setPenalty(float collocationPenalty) throws Exception { }

	@Override
	public HostingNode selectNode(List<HostingNode> candidates) throws Exception 
	{
		for(HostingNode hn : candidates)
		{
			if(registry.isSelected(hn)) return hn;
		}
		List<HostingNodeInfo> hns = selector.assessNodes(candidates);
		for(HostingNodeInfo hn : hns)
		{
			if(!hn.node.isLocal())
			{
				selector.markSelected(hn.node);
				registry.markSelected(hn.node);
				return hn.node;
			}
		}
		logger.warning("None of the nodes found are marked as remote or only the local node was found. Candidate size=" + candidates.size() +". Relying on node selector.");
		return selector.selectNode(candidates);
	}
	
	@Override
	public void reset()
	{
		this.registry = new CollocationRegistry();
	}
}
