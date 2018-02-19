package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.geoanalytics.dataaccess.entities.workflow.Workflow.WorkflowStatus;

public class WorkflowMessenger {
	private static Logger logger = LoggerFactory.getLogger(WorkflowMessenger.class);

	private String id = null;
	private String name = null;
	private String description = null;
	private Long startDate = null;
	private Long endDate = null;
	private Long reminderDate = null;
	private WorkflowStatus status = null;
	private Long statusDate = null;
	private String template = null;
	private String extraData = null;
	
	

	public WorkflowMessenger() {
		super();
		logger.trace("Initialized default contructor for WorkflowMessenger");

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getStartDate() {
		return startDate;
	}

	public void setStartDate(Long startDate) {
		this.startDate = startDate;
	}

	public Long getEndDate() {
		return endDate;
	}

	public void setEndDate(Long endDate) {
		this.endDate = endDate;
	}

	public Long getReminderDate() {
		return reminderDate;
	}

	public void setReminderDate(Long reminderDate) {
		this.reminderDate = reminderDate;
	}

	public WorkflowStatus getStatus() {
		return status;
	}

	public void setStatus(WorkflowStatus status) {
		this.status = status;
	}

	public Long getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Long statusDate) {
		this.statusDate = statusDate;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getExtraData() {
		return extraData;
	}

	public void setExtraData(String extraData) {
		this.extraData = extraData;
	}

}
