package org.gcube.portlets.user.joinvre.shared;

import java.io.Serializable;
/**
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 */
@SuppressWarnings("serial")
public class ResearchEnvironment implements Serializable {
	private String name;
	
	private String description;
		
	private String imageURL;
	
	private String infraScope;
		
	private String friendlyURL;
	
	private UserBelonging userBelonging;

	public ResearchEnvironment() {
		super();
	}

	public ResearchEnvironment(String name, String description,
			String imageURL, String infraScope, String friendlyURL,
			UserBelonging userBelonging) {
		super();
		this.name = name;
		this.description = description;
		this.imageURL = imageURL;
		this.infraScope = infraScope;
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

	public String getinfraScope() {
		return infraScope;
	}

	public void setinfraScope(String infraScope) {
		this.infraScope = infraScope;
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
