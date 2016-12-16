package gr.uoa.di.madgik.workflow.adaptor.search.nodeassignment;

import java.util.ArrayList;
import java.util.List;


public class OperatorNodeAssignmentNode extends NodeAssignmentNode
{
	private List<NodeAssignmentNode> children =  new ArrayList<NodeAssignmentNode>();
	public float collocationScore = 0.0f;
	
	public List<NodeAssignmentNode> getChildren()
	{
		return children;
	}
	
	public void setChildren(List<NodeAssignmentNode> children)
	{
		this.children = children;
	}
	
	public String toXML()
	{
		StringBuilder b = new StringBuilder();
		b.append("<OperatorNodeAssignment>\n");
		b.append(this.element.toXML());
		b.append(" <nodeId>"); b.append(this.nodeId); b.append(" </nodeId>\n");
		b.append(" <children>\n");
		for(NodeAssignmentNode c : this.children)
			b.append(c.toXML());
		b.append(" </children>\n");
		b.append("</OperatorNodeAssignment>\n");
		return b.toString();
	}
}
