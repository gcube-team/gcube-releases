package org.gcube.portlet.user.my_vres.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class VRE extends ResearchEnvironment implements Serializable, Comparable<VRE> {
	
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

	public int compareTo(VRE arg0) {
		return this.getName().compareTo(arg0.getName());
	}
}
