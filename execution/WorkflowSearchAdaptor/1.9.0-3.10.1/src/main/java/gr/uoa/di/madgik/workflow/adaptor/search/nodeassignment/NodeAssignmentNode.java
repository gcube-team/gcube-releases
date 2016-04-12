package gr.uoa.di.madgik.workflow.adaptor.search.nodeassignment;

public abstract class NodeAssignmentNode 
{
	public NodeAssignmentElement element;
	public String nodeId;
	
	public abstract String toXML();
}
