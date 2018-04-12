package gr.uoa.di.madgik.commons.infra.nodeselection;

import gr.uoa.di.madgik.commons.infra.HostingNode;

import java.util.List;

public interface NodeSelector 
{

	/**
	 * Selects the most suitable node and marks the selection internally, if necessary
	 * 
	 * @param candidates The candidate set of nodes
	 * @return The most suitable node
	 */
	public HostingNode selectNode(List<HostingNode> candidates);
	/**
	 * Assesses the suitability of the candidate set of nodes without keeping internal memory of node
	 * selection.
	 * 
	 * @param candidates The candidate set of nodes
	 * @return A sorted list of the candidate set ordered by the most to the least suitable.
	 */
	public List<HostingNodeInfo> assessNodes(List<HostingNode> candidates);
	
	public void markSelected(HostingNode node);
	
}
