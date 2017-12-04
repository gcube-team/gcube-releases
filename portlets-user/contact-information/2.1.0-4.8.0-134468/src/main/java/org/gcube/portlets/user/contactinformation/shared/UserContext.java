package org.gcube.portlets.user.contactinformation.shared;

import java.io.Serializable;
import java.util.HashMap;

import org.gcube.portal.databook.shared.UserInfo;

/**
 * @author Massimiliano Assante at ISTI-CNR 
 * (massimiliano.assante@isti.cnr.it)
 */
@SuppressWarnings("serial")
public class UserContext implements Serializable {
	private UserInfo userInfo;
	private HashMap<ContactType, String> informations;
	private boolean isInfrastructure;
	private boolean isOwner;
	
	public UserContext() { 
		super();
	}

	public UserContext(UserInfo userInfo,
			HashMap<ContactType, String> informations, boolean isOwner, boolean isInfrastructure) {
		super();
		this.userInfo = userInfo;
		this.informations = informations;
		this.isOwner = isOwner;
		this.isInfrastructure = isInfrastructure;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	
	public HashMap<ContactType, String> getInformations() {
		return informations;
	}

	public void setInformations(HashMap<ContactType, String> informations) {
		this.informations = informations;
	}

	public boolean isOwner() {
		return isOwner;
	}

	public void setOwner(boolean isOwner) {
		this.isOwner = isOwner;
	}

	public boolean isInfrastructure() {
		return isInfrastructure;
	}

	public void setInfrastructure(boolean isInfrastructure) {
		this.isInfrastructure = isInfrastructure;
	}
}
