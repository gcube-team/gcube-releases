package gr.cite.gaap.datatransferobjects;

import java.util.List;

import gr.cite.geoanalytics.dataaccess.entities.workflow.Workflow.WorkflowStatus;

public class ProjectSummary {
	private String id;
	private String name;
	private long startDate;
	private String client;
	private WorkflowStatus status;
	private String shape;
	private String description = null;
	private String creator = null;
	private int numberOfUsers = 0;
	private int numberOfLayers = 0;
	private List<String> usersNames;
	private List<String> layerNames;

	public List<String> getLayerNames() {
		return layerNames;
	}

	public void setLayerNames(List<String> layerNames) {
		this.layerNames = layerNames;
	}

	public List<String> getUsersNames() {
		return usersNames;
	}

	public void setUsersNames(List<String> usersNames) {
		this.usersNames = usersNames;
	}

	public int getNumberOfUsers() {
		return numberOfUsers;
	}

	public void setNumberOfUsers(int numberOfUsers) {
		this.numberOfUsers = numberOfUsers;
	}

	public int getNumberOfLayers() {
		return numberOfLayers;
	}

	public void setNumberOfLayers(int numberOfLayers) {
		this.numberOfLayers = numberOfLayers;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public long getStartDate() {
		return startDate;
	}

	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public WorkflowStatus getStatus() {
		return status;
	}

	public void setStatus(WorkflowStatus status) {
		this.status = status;
	}

	public String getShape() {
		return shape;
	}

	public void setShape(String shape) {
		this.shape = shape;
	}

	@Override
	public String toString() {
		return "ProjectSummary [id=" + id + ", name=" + name + ", startDate=" + startDate + ", client=" + client
				+ ", status=" + status + ", shape=" + shape + ", description=" + description + ", creator=" + creator
				+ ", numberOfUsers=" + numberOfUsers + ", numberOfLayers=" + numberOfLayers + ", usersNames="
				+ usersNames + ", layerNames=" + layerNames + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProjectSummary other = (ProjectSummary) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
