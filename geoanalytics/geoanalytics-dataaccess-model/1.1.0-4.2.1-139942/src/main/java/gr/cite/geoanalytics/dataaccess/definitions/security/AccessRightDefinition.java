package gr.cite.geoanalytics.dataaccess.definitions.security;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "accessRightDefinition")
@XmlAccessorType(value = XmlAccessType.PUBLIC_MEMBER)
public class AccessRightDefinition {

	public enum AccessRightType {
		View,
		Edit
	}
	
	public enum AccessRightClass {
		Normal,
		Encompassing
	}
	
	private UUID id = null;
	private String name = null;
	private UUID parentId = null;
	private AccessRightClass rightClass = AccessRightClass.Normal;
	private AccessRightType type = null;
	private String description = null;
	
	@XmlAttribute(name = "id")
	public UUID getId() {
		return id;
	}
	
	public void setId(UUID id) {
		this.id = id;
	}
	
	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlAttribute(name = "parentId", required = false)
	public UUID getParentId() {
		return parentId;
	}
	
	public void setParentId(UUID parentId) {
		this.parentId = parentId;
	}
	
	@XmlAttribute(name = "class", required = false)
	public AccessRightClass getRightClass() {
		return rightClass;
	}
	public void setRightClass(AccessRightClass rightClass) {
		if(rightClass == null) throw new IllegalArgumentException("Access right class cannot be null");
		this.rightClass = rightClass;
	}
	
	@XmlAttribute(name = "type")
	public AccessRightType getType() {
		return type;
	}
	
	public void setType(AccessRightType type) {
		if(type == null) throw new IllegalArgumentException("Access righttype cannot be null");
		this.type = type;
	}
	
	@XmlAttribute(name = "description", required = false)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
