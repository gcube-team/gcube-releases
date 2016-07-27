package org.gcube.portlets.user.gcubelogin.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class VRE extends ResearchEnvironment implements Serializable {
	
	/**
	 * 
	 */
	public VRE() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param vreName
	 * @param description
	 * @param imageURL
	 * @param vomsGroupName
	 * @param friendlyURL
	 * @param userBelonging
	 */
	public VRE(String vreName, String description, String imageURL,
			String vomsGroupName, String friendlyURL,
			UserBelonging userBelonging) {
		super(vreName, description, imageURL, vomsGroupName, friendlyURL, userBelonging);		
	}
}
