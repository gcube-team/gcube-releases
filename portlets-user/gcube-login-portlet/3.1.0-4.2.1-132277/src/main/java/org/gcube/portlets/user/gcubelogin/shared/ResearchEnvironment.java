package org.gcube.portlets.user.gcubelogin.shared;

import java.io.Serializable;
/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 * 
 * @version 2.0 Jan 10th 2012
 */
@SuppressWarnings("serial")
public class ResearchEnvironment implements Serializable{
	private String name;
	
	private String description;
		
	private String imageURL;
	
	private String groupName;
		
	private String friendlyURL;
	
	private UserBelonging userBelonging;

	public ResearchEnvironment() {
		super();
	}

	public ResearchEnvironment(String name, String description,
			String imageURL, String groupName, String friendlyURL,
			UserBelonging userBelonging) {
		super();
		this.name = name;
		this.description = description;
		this.imageURL = imageURL;
		this.groupName = groupName;
		this.friendlyURL = friendlyURL;
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

	public UserBelonging getUserBelonging() {
		return userBelonging;
	}

	public void setUserBelonging(UserBelonging userBelonging) {
		this.userBelonging = userBelonging;
	}

	@Override
	public String toString() {
		return "ResearchEnvironment [name=" + name + ", description="
				+ description + ", imageURL=" + imageURL + ", groupName="
				+ groupName + ", friendlyURL=" + friendlyURL
				+ ", userBelonging=" + userBelonging + "]";
	}	
	
}
