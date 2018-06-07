package org.gcube.data.tm.state;

import java.io.Serializable;
import java.util.Calendar;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="SourceProperties")
class SourceInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private Calendar creationTime;
	private boolean isUser;
	

	public Calendar getCreationTime() {
		return creationTime;
	}
	@XmlElement public void setCreationTime(Calendar creationTime) {
		this.creationTime = creationTime;
	}
	
	public boolean isUser() {
		return isUser;
	}

	@XmlElement public void setUser(boolean isUser) {
		this.isUser = isUser;
	}
		
}
