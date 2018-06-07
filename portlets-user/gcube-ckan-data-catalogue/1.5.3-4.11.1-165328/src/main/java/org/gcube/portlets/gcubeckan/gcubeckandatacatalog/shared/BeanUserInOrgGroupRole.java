package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared;

import java.io.Serializable;

import org.gcube.datacatalogue.ckanutillibrary.shared.RolesCkanGroupOrOrg;

/**
 * A bean that contains the tuple:
 * <ul>
 * <li>Organization/Group Name
 * <li>Organization/Group Url
 * <li>User's role in it
 * </ul>
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 */
public class BeanUserInOrgGroupRole implements Serializable {
	
	private static final long serialVersionUID = 9022496195659804838L;
	private String name;
	private String url;
	private RolesCkanGroupOrOrg role;

	public BeanUserInOrgGroupRole() {
		super();
	}

	/**
	 * @param orgName
	 * @param orgUrl
	 * @param role
	 */
	public BeanUserInOrgGroupRole(String name, String url, RolesCkanGroupOrOrg role) {
		super();
		this.name = name;
		this.url = url;
		this.role = role;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public RolesCkanGroupOrOrg getRole() {
		return role;
	}

	public void setRole(RolesCkanGroupOrOrg role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "BeanUserInOrgGroupRole [name=" + name + ", url=" + url
				+ ", role=" + role + "]";
	}
}
