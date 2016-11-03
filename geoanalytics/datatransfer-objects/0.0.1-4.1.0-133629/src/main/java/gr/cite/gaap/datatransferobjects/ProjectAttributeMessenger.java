package gr.cite.gaap.datatransferobjects;

public class ProjectAttributeMessenger {
	private String projectId = null;
	private AttributeInfo attribute = null;
	private String attributeClassType = null;

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
