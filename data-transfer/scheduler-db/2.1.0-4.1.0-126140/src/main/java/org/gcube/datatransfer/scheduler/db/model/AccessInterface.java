package org.gcube.datatransfer.scheduler.db.model;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;



@PersistenceCapable(table="ACCESSINTERFACE")
public class AccessInterface implements java.io.Serializable {

	private static final long serialVersionUID = 9183231178819521839L;

	@PrimaryKey
	@Persistent(customValueStrategy="uuid")
	private String accessInterface;
	
	private String agentId;


	public String getAccessInterface() {
		return accessInterface;
	}

	public void setAccessInterface(String accessInterface) {
		this.accessInterface = accessInterface;
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}
	

}

