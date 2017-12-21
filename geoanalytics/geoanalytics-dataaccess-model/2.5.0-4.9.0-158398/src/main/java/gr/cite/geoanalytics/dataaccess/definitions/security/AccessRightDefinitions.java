package gr.cite.geoanalytics.dataaccess.definitions.security;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="accessRights")
@XmlAccessorType(value = XmlAccessType.PUBLIC_MEMBER)
public class AccessRightDefinitions {

	private List<AccessRightDefinition> definitions = new ArrayList<AccessRightDefinition>();
	
	private List<AccessRightStructure> hierarchy = new ArrayList<AccessRightStructure>();

	@XmlElementWrapper(name="accessRightDefinitions", required = false)
	@XmlElement(name="accessRightDefinition")
	public List<AccessRightDefinition> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<AccessRightDefinition> definitions) {
		this.definitions = definitions;
	}

	@XmlElementWrapper(name="accessRightHierarchy", required = false)
	@XmlElement(name="accessRightStructure")
	public List<AccessRightStructure> getHierarchy() {
		return hierarchy;
	}

	public void setHierarchy(List<AccessRightStructure> hierarchy) {
		this.hierarchy = hierarchy;
	}
	
	
}
