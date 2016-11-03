package org.gcube.portlets.widgets.ckandatapublisherwidget.shared;

import java.io.Serializable;

/**
 * A ckan organization like bean with organization name and title
 * @author Costantino Perciante (costantino.perciante@isti.cnr.it)
 */
public class OrganizationBean implements Serializable{

	private static final long serialVersionUID = -6566519399945530602L;
	private String title;
	private String name;
	
	public OrganizationBean(){
		super();
	}

	public OrganizationBean(String title, String name) {
		super();
		this.title = title;
		this.name = name;
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

	@Override
	public String toString() {
		return "OrganizationBean [title=" + title + ", name=" + name + "]";
	}
}
