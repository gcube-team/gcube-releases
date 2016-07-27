package gr.uoa.di.madgik.workflow.adaptor.search.nodeassignment;

import gr.uoa.di.madgik.commons.infra.HostingNode;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.PlanNode;

public class NodeAssignmentElement 
{
	public PlanNode processingNode;
	public HostingNode assignedNode;
	
	public String toXML()
	{
		StringBuilder b = new StringBuilder();
		b.append("<NodeAssignment>\n");
		b.append(" <planNode>");
		b.append(" "); b.append(processingNode.getClass().getName());
		b.append(" </planNode>\n");
		b.append(" <assignedNode>");
		b.append(" "); b.append(assignedNode.getId());
		b.append("  </assignedNode>\n");
		b.append("</NodeAssignment>\n");
		return b.toString();
	}
}
