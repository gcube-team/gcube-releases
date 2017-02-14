package gr.cite.gaap.datatransferobjects;

import java.util.Map;

public class ProjectAttributes {
	private String projectId = null;
	private Map<String, AttributeInfo> info = null;

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public Map<String, AttributeInfo> getInfo() {
		return info;
	}

	public void setInfo(Map<String, AttributeInfo> attribute) {
		this.info = attribute;
	}
}
