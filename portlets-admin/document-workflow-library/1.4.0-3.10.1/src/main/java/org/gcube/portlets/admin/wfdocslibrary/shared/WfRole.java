package org.gcube.portlets.admin.wfdocslibrary.shared;
import java.io.Serializable;


@SuppressWarnings("serial")
public class WfRole implements Serializable {
	private String roleid;
	private String rolename;
	private String roledescription;

	public WfRole(){}
	/**
	 * 
	 * @param roleid . 
	 * @param rolename .
	 * @param roledescription .
	 */
	public WfRole(String roleid, String rolename, String roledescription) {
		super();
		this.roleid = roleid;
		this.rolename = rolename;
		this.roledescription = roledescription;
	}
	public String getRoleid() {
		return roleid;
	}

	public void setRoleid(String roleid) {	this.roleid = roleid;	}
	public String getRolename() {	return rolename;	}
	public void setRolename(String rolename) {	this.rolename = rolename;	}
	public String getRoledescription() { return roledescription;	}
	public void setRoledescription(String roledescription) { this.roledescription = roledescription;	}	
}
