package gr.cite.geoanalytics.dataaccess.entities.workflow;

import gr.cite.geoanalytics.dataaccess.entities.Identifiable;
import gr.cite.geoanalytics.dataaccess.entities.Stampable;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;

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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "\"WorkflowTask\"")
public class WorkflowTask implements gr.cite.geoanalytics.dataaccess.entities.Entity, Identifiable, Stampable {

	public enum WorkflowTaskStatus {
		INACTIVE((short)0), ACTIVE((short)1), COMPLETED((short)2), CANCELLED((short)3), TRANSFERRED((short)4);
		
		private final short statusCode;
		
		private static final Map<Short,WorkflowTaskStatus> lookup  = new HashMap<Short,WorkflowTaskStatus>();
		 
		static {
		      for(WorkflowTaskStatus s : EnumSet.allOf(WorkflowTaskStatus.class))
		           lookup.put(s.statusCode(), s);
		 }
		
		WorkflowTaskStatus(short statusCode) {
			this.statusCode = statusCode;
		}
		
		public short statusCode() { return statusCode; }
	
		public static WorkflowTaskStatus fromStatusCode(short statusCode) {
			return lookup.get(statusCode);
		}
	};
	
	public enum Criticality
	{
		/**
		 * 0=The task is not blocking the workflow
		 * 1=the task must be completed to complete the workflow
		 * 2=if the task of the same criticality acquire the same status then the workflow also gets the same status.
		 *   If a critical task fails then the workflow fails
		 */
		NONBLOCKING((short)0), BLOCKING((short)1), CRITICAL((short)2);
		
		private final short criticalityCode;
		
		private static final Map<Short,Criticality> lookup  = new HashMap<Short,Criticality>();
		 
		static {
		      for(Criticality s : EnumSet.allOf(Criticality.class))
		           lookup.put(s.criticalityCode(), s);
		 }
		
		Criticality(short criticalityCode) {
			this.criticalityCode = criticalityCode;
		}
		
		public short criticalityCode() { return criticalityCode; }
	
		public static Criticality fromCriticalityCode(short criticalityCode) {
			return lookup.get(criticalityCode);
		}
	};
	
	public WorkflowTask() {
	}

	@Id
	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
	@Column(name = "\"WFT_ID\"", nullable = false)
	private UUID id = null;

	@ManyToOne
	@JoinColumn(name = "\"WFT_Workflow\"", nullable = false)
	private Workflow workflow = null;

	@Column(name = "\"WFT_Name\"", nullable = false, length = 250)
	private String name = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"WFT_StartDate\"", nullable = true)
	private Date startDate = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"WFT_EndDate\"", nullable = true)
	private Date endDate = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"WFT_ReminderDate\"", nullable = true)
	private Date reminderDate = null;
	
	@Column(name = "\"WFT_Status\"", nullable = false)
	private short status = WorkflowTaskStatus.ACTIVE.statusCode();

	@Type(type="gr.cite.geoanalytics.dataaccess.typedefinition.XMLType") //DEPWARN XML Type: Hibernate dependency, replace when JPA 2.1 annotation is available
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "\"WFT_ExtraData\"", nullable = true, columnDefinition = "xml") //DEPWARN possible db portability issue
	private String extraData = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"WFT_StatusDate\"", nullable = false)
	private Date statusDate = null;

	@Column(name = "\"WFT_Critical\"", nullable = false)
	private short critical = Criticality.NONBLOCKING.criticalityCode();

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"WFT_CreationDate\"", nullable = false)
	private Date creationDate = null;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "\"WFT_LastUpdate\"", nullable = false)
	private Date lastUpdate = null;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "\"WFT_Creator\"", nullable = false)
	private Principal creator = null;

	@ManyToOne
	@JoinColumn(name = "\"WFT_Principal\"", nullable = true)
	private Principal principal = null;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Workflow getWorkflow() {
		return workflow;
	}

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
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

	public WorkflowTaskStatus getStatus() {
		return WorkflowTaskStatus.fromStatusCode(status);
	}

	public void setStatus(WorkflowTaskStatus status) {
		this.status = status.statusCode();
	}

	public String getExtraData() {
		return extraData;
	}

	public void setExtraData(String extraData) {
		this.extraData = extraData;
	}

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public Criticality getCritical() {
		return Criticality.fromCriticalityCode(critical);
	}

	public void setCritical(Criticality critical) {
		this.critical = critical.criticalityCode();
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

	public Principal getPrincipal() {
		return principal;
	}

	public void setPrincipal(Principal user) {
		this.principal = user;
	}
	
	@Override
	public String toString()
	{
		return "WorkflowTask(" + "id=" + getId() + " name=" + getName() +
				" workflow=" + (workflow != null ? workflow.getId() : null) +
				" startDate=" + getStartDate() + " endDate=" + getEndDate() +
				" status=" + getStatus() + " statusDate=" + getStatusDate() + "criticality=" + getCritical() +
				" extraData=" + getExtraData() +
				" creation=" + getCreationDate() + " lastUpdate=" + getLastUpdate() +
				" creator=" + (creator != null ? creator.getId() : null) +
				" user=" + (principal != null ? principal.getId() : null);
	}
}
