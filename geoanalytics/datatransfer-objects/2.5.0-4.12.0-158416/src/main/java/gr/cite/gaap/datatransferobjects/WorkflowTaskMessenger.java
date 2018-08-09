package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.geoanalytics.dataaccess.entities.workflow.WorkflowTask.Criticality;
import gr.cite.geoanalytics.dataaccess.entities.workflow.WorkflowTask.WorkflowTaskStatus;

public class WorkflowTaskMessenger {
	private static Logger logger = LoggerFactory.getLogger(WorkflowTaskMessenger.class);

	private String id = null;
	private String project = null;
	private String workflow = null;
	private String user = null;
	private String name = null;
	private Long startDate = null;
	private Long endDate = null;
	private Long reminderDate = null;
	private long statusDate = -1;
	private String extraData = null;
	private WorkflowTaskStatus status;
	private Criticality critical;
	private long numDocuments = -1;
	
	

	public WorkflowTaskMessenger() {
		super();
		logger.trace("Initialized default contructor for WorkflowTaskMessenger");

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getWorkflow() {
		return workflow;
	}

	public void setWorkflow(String workflow) {
		this.workflow = workflow;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getStartDate() {
		return startDate;
	}

	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}

	public Long getEndDate() {
		return endDate;
	}

	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}

	public Long getReminderDate() {
		return reminderDate;
	}

	public void setReminderDate(Long reminderDate) {
		this.reminderDate = reminderDate;
	}

	public long getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(long statusDate) {
		this.statusDate = statusDate;
	}

	public String getExtraData() {
		return extraData;
	}

	public void setExtraData(String extraData) {
		this.extraData = extraData;
	}

	public WorkflowTaskStatus getStatus() {
		return status;
	}

	public void setStatus(WorkflowTaskStatus status) {
		this.status = status;
	}

	public Criticality getCritical() {
		return critical;
	}

	public void setCritical(Criticality critical) {
		this.critical = critical;
	}

	public long getNumDocuments() {
		return numDocuments;
	}

	public void setNumDocuments(long numDocuments) {
		this.numDocuments = numDocuments;
	}
}
