package gr.cite.geoanalytics.dataaccess.entities.workflow;

import java.io.Serializable;
import java.util.UUID;

import org.hibernate.annotations.Type;

public class WorkflowTaskDocumentPK implements Serializable
{
	
	private static final long serialVersionUID = 9033586910534392488L;
	
	@Type(type="org.hibernate.type.PostgresUUIDType")
	private UUID workflowTask;
	@Type(type="org.hibernate.type.PostgresUUIDType")
	private UUID document;
	
	public WorkflowTaskDocumentPK() { }
	
	public WorkflowTaskDocumentPK(UUID workflowTask, UUID document)
	{
		this.workflowTask = workflowTask;
		this.document = document;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (this == other)
	        return true;
	    if (!(other instanceof WorkflowTaskDocumentPK))
	        return false;
	    WorkflowTaskDocumentPK castOther = (WorkflowTaskDocumentPK) other;
	    return workflowTask.equals(castOther.workflowTask) && document.equals(castOther.document);
	}
	
	@Override
	public int hashCode() 
	{
	    final int prime = 31;
	    int hash = 17;
	    hash = hash * prime + this.workflowTask.hashCode();
	    hash = hash * prime + this.document.hashCode();
	    return hash;
	}
}
