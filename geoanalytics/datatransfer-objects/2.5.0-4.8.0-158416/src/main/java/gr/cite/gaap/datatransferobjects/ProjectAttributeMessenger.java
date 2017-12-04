package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectAttributeMessenger {
	private static Logger logger = LoggerFactory.getLogger(ProjectAttributeMessenger.class);

	private String projectId = null;
	private AttributeInfo attribute = null;
	private String attributeClassType = null;

	
	
	public ProjectAttributeMessenger() {
		super();
		logger.trace("Initialized default contructor for ProjectAttributeMessenger");
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public AttributeInfo getAttribute() {
		return attribute;
	}

	public void setAttribute(AttributeInfo attribute) {
		this.attribute = attribute;
	}

	public String getAttributeClassType() {
		return attributeClassType;
	}

	public void setAttributeClassType(String attributeClassType) {
		this.attributeClassType = attributeClassType;
	}
}
