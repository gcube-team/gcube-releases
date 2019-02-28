package org.gcube.portlets.user.performfish.bean;

import java.util.ArrayList;
import java.util.List;

import org.gcube.vomanagement.usermanagement.model.GCubeUser;

public class CompanyMember {
	private GCubeUser user;
	private List<Farm> associatedFarms = new ArrayList<>();
	private boolean isAdmin;
	public CompanyMember(GCubeUser user) {
		super();
		this.user = user;
	}
	
	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public GCubeUser getUser() {
		return user;
	}
	public void setUser(GCubeUser user) {
		this.user = user;
	}
	
	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CompanyMember [user=");
		builder.append(user);
		builder.append(", associatedFarms=");
		builder.append(associatedFarms);
		builder.append(", isAdmin=");
		builder.append(isAdmin);
		builder.append("]");
		return builder.toString();
	}

	public List<Farm> getAssociatedFarms() {
		return associatedFarms;
	}
	
	public String getAssociatedFarmNames() {
		String toReturn = "";
		for (Farm farm : associatedFarms) {
			toReturn += " " + farm.getName();
		}
		return toReturn;
	}

	public void setAssociatedFarms(List<Farm> associatedFarms) {
		this.associatedFarms = associatedFarms;
	}

	public String getUserAvatarURL() {
		return this.user.getUserAvatarURL();
	}
	
	public String getjobTitle() {
		return this.user.getJobTitle();
	}
	
	public String getFullname() {
		return this.user.getFullname();
	}
	public long getUserId() {
		return this.user.getUserId();
	}
	
	public String getUsername() {
		return this.user.getUsername();
	}
	
	public String getEmail() {
		return this.user.getEmail();
	}
	
	public void setEmail(String email) {
		this.user.setEmail(email);
	}

}
