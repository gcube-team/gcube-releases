package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared;

import java.io.Serializable;

/**
 * A bean that contains the tuple :
 * <Organization Name, Organization Url, User's role in it>
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 */
public class BeanUserInOrgRole implements Serializable {
	
	private static final long serialVersionUID = 9022496195659804838L;
	private String orgName;
	private String orgUrl;
	private CkanRole role;

	public BeanUserInOrgRole() {
		super();
	}

	/**
	 * @param orgName
	 * @param orgUrl
	 * @param role
	 */
	public BeanUserInOrgRole(String orgName, String orgUrl, CkanRole role) {
		super();
		this.orgName = orgName;
		this.orgUrl = orgUrl;
		this.role = role;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getOrgUrl() {
		return orgUrl;
	}

	public void setOrgUrl(String orgUrl) {
		this.orgUrl = orgUrl;
	}

	public CkanRole getRole() {
		return role;
	}

	public void setRole(CkanRole role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "BeanUserInOrgRole [orgName=" + orgName + ", orgUrl=" + orgUrl
				+ ", role=" + role + "]";
	}
}
