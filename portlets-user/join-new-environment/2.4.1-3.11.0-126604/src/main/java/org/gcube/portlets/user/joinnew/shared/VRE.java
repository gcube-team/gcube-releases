package org.gcube.portlets.user.joinnew.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class VRE extends ResearchEnvironment implements Serializable {
	private boolean uponRequest;
	private long id;

	public VRE() {
		super();
		this.uponRequest = true;
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
	public VRE(long id, String vreName, String description, String imageURL,
			String vomsGroupName, String friendlyURL,
			UserBelonging userBelonging,  boolean uponRequest) {
		super(vreName, description, imageURL, vomsGroupName, friendlyURL, userBelonging);	
		this.uponRequest = uponRequest;
		this.id = id;
	}
	
	public boolean isUponRequest() {
		return uponRequest;
	}
	public void setUponRequest(boolean uponRequest) {
		this.uponRequest = uponRequest;
	}
	

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "VRE [getName()=" + getName()
				+ ", uponRequest=" + uponRequest+"]";
	}
	
	
}
