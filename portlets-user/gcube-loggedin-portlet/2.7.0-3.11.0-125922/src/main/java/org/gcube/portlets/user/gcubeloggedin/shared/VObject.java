package org.gcube.portlets.user.gcubeloggedin.shared;

import java.io.Serializable;


@SuppressWarnings("serial")
public class VObject implements Serializable  {
	
	public enum UserBelongingClient { 
		BELONGING, NOT_BELONGING, PENDING
	}
	
	private String name;
	private String groupName;
	private String description;
	private String imageURL;
	private String friendlyURL;
	
	private boolean mandatory;
	private boolean uponRequest;

	
	private UserBelongingClient userBelonging;
	public VObject() {
	}
	
	public VObject(String name, String groupName, String description,
			String imageURL, String friendlyURL, UserBelongingClient userBelonging, boolean mandatory,
			boolean uponRequest) {
		this.name = name;
		this.groupName = groupName;
		this.description = description;
		this.imageURL = imageURL;
		this.friendlyURL = friendlyURL;
		this.mandatory = mandatory;
		this.uponRequest = uponRequest;
		this.userBelonging = userBelonging;
	}


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImageURL() {
		return imageURL;
	}
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
	public UserBelongingClient getUserBelonging() {
		return userBelonging;
	}
	public void setUserBelonging(UserBelongingClient userBelonging) {
		this.userBelonging = userBelonging;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getFriendlyURL() {
		return friendlyURL;
	}

	public void setFriendlyURL(String friendlyURL) {
		this.friendlyURL = friendlyURL;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public boolean isUponRequest() {
		return uponRequest;
	}

	public void setUponRequest(boolean uponRequest) {
		this.uponRequest = uponRequest;
	}
	
}