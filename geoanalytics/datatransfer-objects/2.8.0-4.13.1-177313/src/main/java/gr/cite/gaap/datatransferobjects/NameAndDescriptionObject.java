package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NameAndDescriptionObject {
	private static Logger logger = LoggerFactory.getLogger(NameAndDescriptionObject.class);
	private String description;
	private String name;
	private String oldName;

	
	
	public NameAndDescriptionObject() {
		super();
		logger.trace("Initialized default contructor for NameAndDescriptionObject");
	}
	public String getOldName() {
		return oldName;
	}
	public void setOldName(String oldName) {
		this.oldName = oldName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
