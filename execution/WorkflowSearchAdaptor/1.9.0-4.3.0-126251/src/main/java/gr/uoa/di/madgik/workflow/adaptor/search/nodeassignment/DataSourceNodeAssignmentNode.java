package gr.uoa.di.madgik.workflow.adaptor.search.nodeassignment;

public class DataSourceNodeAssignmentNode extends NodeAssignmentNode
{
	public String instanceId;
	
	public String toXML()
	{
		StringBuilder b = new StringBuilder();
		b.append("<DataSourceNodeAssignment>\n");
		b.append(this.element.toXML());
		b.append(" <nodeId>"); b.append(this.nodeId); b.append(" </nodeId>\n");
		b.append(" <instanceId>"); b.append(this.instanceId); b.append(" </instanceId>\n");
		b.append("</DataSourceNodeAssignment>\n");
		return b.toString();
	}
}
