package org.gcube.portlets.widgets.ckandatapublisherwidget.shared;

import java.io.Serializable;
import java.util.List;

/**
 * This bean contains the retrieved list of available licenses for CKAN.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
@SuppressWarnings("serial")
public class LicensesBean implements Serializable{

	private List<String> licenseTitles;
	private List<String> licenseUrls;
	
	public LicensesBean() {
		super();
	}

	/**
	 * @param licenses
	 */
	public LicensesBean(List<String> licenseTitles, List<String> licenseUrls) {
		super();
		this.licenseTitles = licenseTitles;
		this.licenseUrls = licenseUrls;
	}

	public List<String> getLicenseTitles() {
		return licenseTitles;
	}

	public void setLicenseTitles(List<String> licenseTitles) {
		this.licenseTitles = licenseTitles;
	}

	public List<String> getLicenseUrls() {
		return licenseUrls;
	}

	public void setLicenseUrls(List<String> licenseUrls) {
		this.licenseUrls = licenseUrls;
	}

	@Override
	public String toString() {
		return "LicensesBean [licenseTitles=" + licenseTitles
				+ ", licenseUrls=" + licenseUrls + "]";
	}
}
