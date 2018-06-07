package org.gcube.data.access.bean;

import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "userGroup")
public class UserGroup {
	
	private boolean enabled;
	private String extId;
	private String name;
	private Date dateCreation;
	
	public UserGroup(){
	}
	
	public UserGroup(boolean enabled){
		this.enabled = enabled;
	}
	
	@XmlAttribute(name="enabled")
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getExtId() {
		return extId;
	}

	public void setExtId(String extId) {
		this.extId = extId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}
}
