package org.gcube.portlet.user.my_vres.shared;

import java.io.Serializable;
/**
 * @author Massimiliano Assante ISTI-CNR
 */
@SuppressWarnings("serial")
public class ResearchEnvironment implements Serializable{
	private String name;
	
	private String description;
		
	private String imageURL;
	//the infrastructure scope
	private String context;
		
	private String friendlyURL;
	
	private UserBelonging userBelonging;

	public ResearchEnvironment() {
		super();
	}

	public ResearchEnvironment(String name, String description,
			String imageURL, String context, String friendlyURL,
			UserBelonging userBelonging) {
		super();
		this.name = name;
		this.description = description;
		this.imageURL = imageURL;
		this.context = context;
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
	/**
	 * 
	 * @return the infrastructure scope
	 */
	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
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
}
