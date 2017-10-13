package gr.cite.gaap.datatransferobjects;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocumentMessenger {
	
	private static Logger logger = LoggerFactory.getLogger(DocumentMessenger.class);
	private String id = null;
	private String name = null;
	private String creator = null;
	private String tenant = null;
	private String description = null;
	private long creationDate = -1;
	private String mimeType = null;
	private String mimeSubType = null;
	private long size = -1;
	private String projectId = null;
	private String projectName = null;
	private List<String> shapeIds = null;
	private List<String> shapeNames = null;
	private Long numOfWorkflowTasks = null;
	
	

	public DocumentMessenger() {
		super();
		logger.trace("Initialized default contructor for DocumentMessenger");
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

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getTenant() {
		return tenant;
	}

	public void setTenant(String customer) {
		this.tenant = customer;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getMimeSubType() {
		return mimeSubType;
	}

	public void setMimeSubType(String mimeSubType) {
		this.mimeSubType = mimeSubType;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public List<String> getShapeIds() {
		return shapeIds;
	}

	public void setShapeIds(List<String> shapeIds) {
		this.shapeIds = shapeIds;
	}

	public List<String> getShapeNames() {
		return shapeNames;
	}

	public void setShapeNames(List<String> shapeNames) {
		this.shapeNames = shapeNames;
	}

	public Long getNumOfWorkflowTasks() {
		return numOfWorkflowTasks;
	}

	public void setNumOfWorkflowTasks(Long numOfWorkflowTasks) {
		this.numOfWorkflowTasks = numOfWorkflowTasks;
	}
}
