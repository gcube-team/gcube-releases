package org.gcube.portlets.user.joinvre.shared;

import java.io.Serializable;

/**
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
@SuppressWarnings("serial")
public class VRE extends ResearchEnvironment implements Serializable, Comparable<VRE> {
	
	protected boolean uponRequest;
	protected long id;
	protected boolean isExternal;
	protected String url;

	public VRE() {
		super();
	}

	/**
	 * @param id
	 * @param vreName
	 * @param description
	 * @param imageURL
	 * @param groupName
	 * @param friendlyURL
	 * @param categories
	 * @param userBelonging
	 * @param uponRequest
	 */
	public VRE(long id, String vreName, String description, String imageURL,
			String infraScope, String friendlyURL, UserBelonging userBelonging,  boolean uponRequest) {
		super(vreName, description, imageURL, infraScope, friendlyURL, userBelonging);	
		this.uponRequest = uponRequest;
		this.id = id;
		isExternal = false;
		url = "";
	}
	
	public VRE(long id, String vreName, String description, String imageURL,
			String infraScope, String friendlyURL, UserBelonging userBelonging, boolean uponRequest, boolean isExternal, String url) {
		super(vreName, description, imageURL, infraScope, friendlyURL, userBelonging);	
		this.uponRequest = uponRequest;
		this.id = id;
		this.isExternal = isExternal;
		this.url = url;
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
	
	public boolean isExternal() {
		return isExternal;
	}

	public void setExternal(boolean isExternal) {
		this.isExternal = isExternal;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "VRE {" + getName() + ", "+ getFriendlyURL() + ", uponRequest=" + uponRequest+"}";
	}

	@Override
	public int compareTo(VRE vre) {
		return this.getName().compareTo(vre.getName());
	}
	
}
