package org.gcube.datacatalogue.ckanutillibrary.shared;

import java.io.Serializable;

/**
 * This bean offers the following information:
 * <ul>
 * <li> landing page url to the items page (within the portlet)
 * <li> landing page url to the orgs page (within the portlet)
 * <li> landing page url to the groups page (within the portlet)
 * <li> landing page url to the types page (within the portlet)
 * </ul>
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class LandingPages implements Serializable {
	
	private static final long serialVersionUID = -5617896049674346109L;
	private String urlTypes;
	private String urlOrganizations;
	private String urlGroups;
	private String urlItems;
	
	public LandingPages() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getUrlTypes() {
		return urlTypes;
	}
	public void setUrlTypes(String urlTypes) {
		this.urlTypes = urlTypes;
	}
	public String getUrlOrganizations() {
		return urlOrganizations;
	}
	public void setUrlOrganizations(String urlOrganizations) {
		this.urlOrganizations = urlOrganizations;
	}
	public String getUrlGroups() {
		return urlGroups;
	}
	public void setUrlGroups(String urlGroups) {
		this.urlGroups = urlGroups;
	}
	public String getUrlItems() {
		return urlItems;
	}
	public void setUrlItems(String urlItems) {
		this.urlItems = urlItems;
	}
	@Override
	public String toString() {
		return "LandingPages [urlTypes=" + urlTypes + ", urlOrganizations="
				+ urlOrganizations + ", urlGroups=" + urlGroups + ", urlItems="
				+ urlItems + "]";
	}

}
