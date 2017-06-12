package gr.uoa.di.madgik.commons.configuration.parameter.elements.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a node in the depencdency graph constructed by the configuration file and maintained
 * throug the {@link DependencyGraph}. As a convention. incoming nodes are considered the ones that
 * need to be instnatiated and evaluated before the current nopde can be evaluated. Respectivly,
 * outgoing nodes are considered the ones that depend on this node
 *
 * @author gpapanikos
 */
public class GraphElement
{

	/**
	 * The name of the node
	 */
	public String Name = null;
	/**
	 * The names of the nodes that are connected with the current node with a directed edge, incoming
	 * to the current node
	 */
	public List<String> Incoming = null;
	/**
	 * The names of the nodes that are connected with the current node with a directed edge, outgoing
	 * from the current node
	 */
	public List<String> Outgoing = null;

	/**
	 * Creates a new instance
	 *
	 * @param Name The name of the node
	 */
	public GraphElement(String Name)
	{
		this.Name = Name;
		this.Incoming = new ArrayList<String>();
		this.Outgoing = new ArrayList<String>();
	}
}
