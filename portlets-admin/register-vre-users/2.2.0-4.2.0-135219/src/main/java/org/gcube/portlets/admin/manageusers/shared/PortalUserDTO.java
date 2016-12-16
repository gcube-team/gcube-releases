package org.gcube.portlets.admin.manageusers.shared;

import com.extjs.gxt.ui.client.data.BaseModel;

@SuppressWarnings("serial")
public class PortalUserDTO extends BaseModel {

	public PortalUserDTO() {
	}
	
	/**
	 * 
	 * @param id
	 * @param initial the first letter of the last name
	 * @param name
	 * @param lastname
	 * @param email
	 * @param role
	 * @param laboratory
	 */
	public PortalUserDTO(String id, String initial, String name, String lastname, String email, String role, String laboratory) {
		set("id", id);
		set("initial", initial);
		set("name", name);
		set("lastname", lastname);
		set("email", email);
		set("role", role);
		set("laboratory", laboratory);
	}
	
	public String getId() { return (String) get("id");	}
	
	public String getInitial() { return (String) get("initial");	}

	public String getName() {return (String) get("name");	}
	
	public String getLastName() {return (String) get("lastname");	}
	
	public String getEmail() {return (String) get("email");	}
	
	public String getRole() {return (String) get("role");	}
	
	public String getLaboratory() {	return (String) get("laboratory");	}

	@Override
	public String toString() {
		return "PortalUserDTO [getId()=" + getId() + ", getInitial()="
				+ getInitial() + ", getName()=" + getName()
				+ ", getLastName()=" + getLastName() + ", getEmail()="
				+ getEmail() + ", getRole()=" + getRole()
				+ ", getLaboratory()=" + getLaboratory() + "]";
	}	
	
	

}
