package gr.cite.geoanalytics.dataaccess.definitions.security;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAttribute;

public class AccessRightStructure {
	private UUID id = null;
	private String name = null;
	private UUID parentId = null;
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
	
	@XmlAttribute(name = "description", required = false)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
