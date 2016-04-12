package org.gcube.portlets.admin.software_upload_wizard.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.Version;

public class GeneralInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7906811745290513414L;
	public final static String LICENSE_DEFAULT;

	static {
		LICENSE_DEFAULT = "The gCube/gCore software is licensed as Free Open Source software conveying to the EUPL (http://joinup.ec.europa.eu/software/page/eupl/licence-eupl).\n"
				+ "The software and documentation is provided by its authors/distributors \"as is\" and no expressed or\n"
				+ "implied warranty is given for its use, quality or fitness for a particular case.";
	}

	private String applicationName = "";
	private String applicationDescription = "";
	private Version applicationVersion = new Version();
	private Date releaseDate = new Date();
	private ArrayList<Url> urls = new ArrayList<Url>();

	private String installationNotes = "";
	private String configurationNotes = "";
	private String dependenciesNotes = "";
	private String uninstallationNotes = "";

	private ArrayList<Maintainer> maintainers = new ArrayList<Maintainer>();

	private String componentName = "";
	private ArrayList<SoftwareChange> changes = new ArrayList<SoftwareChange>();

	private String license = "";

	public GeneralInfo() {
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getApplicationDescription() {
		return applicationDescription;
	}

	public void setApplicationDescription(String applicationDescription) {
		this.applicationDescription = applicationDescription;
	}

	public Version getApplicationVersion() {
		return applicationVersion;
	}

	public void setApplicationVersion(Version applicationVersion) {
		this.applicationVersion = applicationVersion;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	public ArrayList<Url> getUrls() {
		return urls;
	}

	public void setUrls(ArrayList<Url> urls) {
		this.urls = urls;
	}

	public String getInstallationNotes() {
		return installationNotes;
	}

	public void setInstallationNotes(String installationNotes) {
		this.installationNotes = installationNotes;
	}

	public String getConfigurationNotes() {
		return configurationNotes;
	}

	public void setConfigurationNotes(String configurationNotes) {
		this.configurationNotes = configurationNotes;
	}

	public String getDependenciesNotes() {
		return dependenciesNotes;
	}

	public void setDependenciesNotes(String dependenciesNotes) {
		this.dependenciesNotes = dependenciesNotes;
	}

	public String getUninstallationNotes() {
		return uninstallationNotes;
	}

	public void setUninstallationNotes(String uninstallationNotes) {
		this.uninstallationNotes = uninstallationNotes;
	}

	public ArrayList<Maintainer> getMaintainers() {
		return maintainers;
	}

	public void setMaintainers(ArrayList<Maintainer> maintainers) {
		this.maintainers = maintainers;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public ArrayList<SoftwareChange> getChanges() {
		return changes;
	}

	public void setChanges(ArrayList<SoftwareChange> changes) {
		this.changes = changes;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GeneralInfo [applicationName=");
		builder.append(applicationName);
		builder.append(", applicationDescription=");
		builder.append(applicationDescription);
		builder.append(", applicationVersion=");
		builder.append(applicationVersion);
		builder.append(", releaseDate=");
		builder.append(releaseDate);
		builder.append(", urls=");
		builder.append(urls);
		builder.append(", installationNotes=");
		builder.append(installationNotes);
		builder.append(", configurationNotes=");
		builder.append(configurationNotes);
		builder.append(", dependenciesNotes=");
		builder.append(dependenciesNotes);
		builder.append(", uninstallationNotes=");
		builder.append(uninstallationNotes);
		builder.append(", maintainers=");
		builder.append(maintainers);
		builder.append(", componentName=");
		builder.append(componentName);
		builder.append(", changes=");
		builder.append(changes);
		builder.append(", license=");
		builder.append(license);
		builder.append("]");
		return builder.toString();
	}

}
