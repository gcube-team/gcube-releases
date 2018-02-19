package gr.cite.geoanalytics.dataaccess.entities.workflow;

import gr.cite.geoanalytics.dataaccess.entities.Identifiable;
import gr.cite.geoanalytics.dataaccess.entities.Stampable;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.project.Project;

import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Index;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "\"Workflow\"")
public class Workflow implements gr.cite.geoanalytics.dataaccess.entities.Entity, Identifiable, Stampable {

	public enum WorkflowStatus {
		INACTIVE((short)0), ACTIVE((short)1), COMPLETED((short)2), CANCELLED((short)3), TRANSFERRED((short)4);
		
		private final short statusCode;
		
		private static final Map<Short,WorkflowStatus> lookup  = new HashMap<Short,WorkflowStatus>();
		 
		static {
		      for(WorkflowStatus s : EnumSet.allOf(WorkflowStatus.class))
		           lookup.put(s.statusCode(), s);
		 }
		
		WorkflowStatus(short statusCode) {
			this.statusCode = statusCode;
		}
		
		public short statusCode() { return statusCode; }
	
		public static WorkflowStatus fromStatusCode(short statusCode) {
			return lookup.get(statusCode);
		}
	};
	
	@Id
	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
	@Column(name = "\"WF_ID\"", nullable = false)
	private UUID id = null;

	/**
	 * One project can have many workflows
	 */
	@ManyToOne
	@JoinColumn(name = "\"WF_Project\"", nullable = false)
	private Project project = null;

	/**
	 * Refers to the template that this workflow was created from, so that changes in the template can be propagated to the workflow.
	 * Currently it is ignored.
	 */
	@ManyToOne
	@JoinColumn(name = "\"WF_Template\"", nullable = true) //TODO nullable if used in future?
	private Project template = null;

	@Column(name = "\"WF_Name\"", nullable = false, length = 250)
	private String name = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"WF_StartDate\"", nullable = true)
	
	private Date startDate = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"WF_EndDate\"", nullable = true)
	private Date endDate = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"WF_ReminderDate\"", nullable = true)
	private Date reminderDate = null;

	@Column(name = "\"WF_Status\"", nullable = false)
	private short status = WorkflowStatus.ACTIVE.statusCode();

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"WF_StatusDate\"", nullable = false)
	private Date statusDate = null;

	@Lob
	@Type(type = "org.hibernate.type.TextType") //DEPWARN dependency to Hibernate and PostgreSQL (workaround for text~~bigint hibernate bug)
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "\"WF_Description\"", nullable = true)
	private String description = null;

	@Type(type="gr.cite.geoanalytics.dataaccess.typedefinition.XMLType") //DEPWARN XML Type: Hibernate dependency, replace when JPA 2.1 annotation is available
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "\"WF_ExtraData\"", nullable = true, columnDefinition = "xml") //DEPWARN possible db portability issue
	private String extraData = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"WF_CreationDate\"", nullable = false)
	private Date creationDate = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"WF_LastUpdate\"", nullable = false)
	private Date lastUpdate = null;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "\"WF_Creator\"", nullable = false)
	private Principal creator = null;

	public Workflow() {
		super();
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Project getTemplate() {
		return template;
	}

	public void setTemplate(Project template) {
		this.template = template;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getReminderDate() {
		return reminderDate;
	}

	public void setReminderDate(Date reminderDate) {
		this.reminderDate = reminderDate;
	}

	public WorkflowStatus getStatus() {
		return WorkflowStatus.fromStatusCode(status);
	}

	public void setStatus(WorkflowStatus status) {
		this.status = status.statusCode();
	}

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getExtraData() {
		return extraData;
	}

	public void setExtraData(String extraData) {
		this.extraData = extraData;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
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
		return "Workflow(" + "id=" + getId() + " name=" + getName() + 
				" description=" + getDescription() +
				" project=" + (project != null ? project.getId() : null) +
				" template=" + (template != null ? template.getId() : null) +
				" startDate=" + getStartDate() + " endDate=" + getEndDate() +
				" reminderDate=" + getReminderDate() + " status=" + getStatus() +
				" statusDate=" + getStatusDate() + " extraData=" + getExtraData() +
				" creation=" + getCreationDate() + " lastUpdate=" + getLastUpdate() +
				" creator=" + (creator != null ? creator.getId() : null);
	}
}
