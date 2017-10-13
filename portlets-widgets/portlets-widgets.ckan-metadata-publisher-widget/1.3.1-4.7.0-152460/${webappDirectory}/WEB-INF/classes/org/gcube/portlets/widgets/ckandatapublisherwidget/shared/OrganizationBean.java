package org.gcube.portlets.widgets.ckandatapublisherwidget.shared;

import java.io.Serializable;

/**
 * A ckan organization/group (you can check its nature by looking at getIsOrganization();) like bean with name and title
 * @author Costantino Perciante (costantino.perciante@isti.cnr.it)
 */
public class OrganizationBean implements Serializable{

	private static final long serialVersionUID = -6566519399945530602L;
	private String title;
	private String name;
	private boolean isOrganization;
	private boolean propagateUp; // an item linked to this group has to be added on the whole hierarchy chain
	
	public OrganizationBean(){
		super();
	}

	public OrganizationBean(String title, String name, boolean isOrganization) {
		super();
		this.title = title;
		this.name = name;
		this.isOrganization = isOrganization;
	}
	
	public OrganizationBean(String title, String name, boolean isOrganization, boolean propagateUp) {
		super();
		this.title = title;
		this.name = name;
		this.isOrganization = isOrganization;
		this.propagateUp = propagateUp;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isOrganization() {
		return isOrganization;
	}

	public void setOrganization(boolean isOrganization) {
		this.isOrganization = isOrganization;
	}

	public boolean isPropagateUp() {
		return propagateUp;
	}

	public void setPropagateUp(boolean propagateUp) {
		this.propagateUp = propagateUp;
	}

	@Override
	public String toString() {
		return "OrganizationBean [title=" + title + ", name=" + name
				+ ", isOrganization=" + isOrganization + ", propagateUp="
				+ propagateUp + "]";
	}

}
