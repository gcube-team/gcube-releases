package gr.cite.gaap.datatransferobjects;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectAttributes {
	private static Logger logger = LoggerFactory.getLogger(ProjectAttributes.class);

	private String projectId = null;
	private Map<String, AttributeInfo> info = null;

	
	
	public ProjectAttributes() {
		super();
		logger.trace("Initialized default contructor for ProjectAttributes");
	}

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
