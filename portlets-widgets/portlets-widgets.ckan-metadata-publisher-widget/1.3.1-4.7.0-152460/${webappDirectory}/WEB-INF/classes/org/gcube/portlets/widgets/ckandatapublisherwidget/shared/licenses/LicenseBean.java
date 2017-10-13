package org.gcube.portlets.widgets.ckandatapublisherwidget.shared.licenses;

import java.io.Serializable;

/**
 * A license bean like the ckan's one.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class LicenseBean implements Serializable{

	private static final long serialVersionUID = -2079275598877326206L;
	private String title;
	private String url;
	
	public LicenseBean() {
		super();
	}

	public LicenseBean(String title, String url) {
		super();
		this.title = title;
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj.getClass().equals(this.getClass()) && ((LicenseBean)obj).getTitle().equals(this.title);
	}

	@Override
	public String toString() {
		return "LicenseBean [title=" + title + ", url=" + url + "]";
	}
}
