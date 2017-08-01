package gr.cite.geoanalytics.dataaccess.entities.workflow;

import gr.cite.geoanalytics.dataaccess.entities.Stampable;
import gr.cite.geoanalytics.dataaccess.entities.document.Document;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Index;

@Entity
@IdClass(WorkflowTaskDocumentPK.class)
@Table(name = "\"WorkflowTaskDocument\"")
public class WorkflowTaskDocument implements gr.cite.geoanalytics.dataaccess.entities.Entity, Stampable {
	
	/**
	 * Documents can belong to more than one workflow task
	 */
	@Id
	@ManyToOne
	@JoinColumn(name = "\"WTD_WorkflowTask\"", nullable = false)
	private WorkflowTask workflowTask = null;

	/**
	 * Each workflow task can have more than one documents
	 */
	@Id
	@ManyToOne
	@JoinColumn(name = "\"WTD_Document\"", nullable = false)
	private Document document = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"WTD_CreationDate\"", nullable = false)
	private Date creationDate = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"WTD_LastUpdate\"", nullable = false)
	private Date lastUpdate = null;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "\"WTD_Creator\"", nullable = false)
	private Principal creator = null;

	public WorkflowTaskDocument() {
	}

	public WorkflowTask getWorkflowTask() {
		return workflowTask;
	}

	public void setWorkflowTask(WorkflowTask workflowTask) {
		this.workflowTask = workflowTask;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creation) {
		this.creationDate = creation;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public Principal getCreator() {
		return creator;
	}

	public void setCreator(Principal creator) {
		this.creator = creator;
	}
	
	@Override
	public String toString()
	{
		return "WorkflowTaskDocument(" + "workflowTask=" + getWorkflowTask().getId() + " document=" + getDocument().getId() + 
				" creation=" + getCreationDate() + " lastUpdate=" + getLastUpdate() +
				" creator=" + (creator != null ? creator.getId() : null);
	}

}