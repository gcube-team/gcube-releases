package org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Locale;

import org.gcube.portlets.admin.software_upload_wizard.server.data.ImportSession;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.registrationmanagers.ISoftwareSubmissionTask;
import org.gcube.portlets.admin.software_upload_wizard.server.softwareprofile.Package;
import org.gcube.portlets.admin.software_upload_wizard.server.softwareprofile.ServiceProfile;
import org.gcube.portlets.admin.software_upload_wizard.server.util.ScopeUtil;
import org.gcube.portlets.admin.software_upload_wizard.shared.Deliverable;
import org.gcube.portlets.admin.software_upload_wizard.shared.IOperationProgress;
import org.gcube.portlets.admin.software_upload_wizard.shared.Maintainer;
import org.gcube.portlets.admin.software_upload_wizard.shared.SoftwareChange;
import org.gcube.portlets.admin.software_upload_wizard.shared.Url;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.maven.IMavenRepositoryInfo;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.maven.MavenCoordinates;

public abstract class AbstractSoftwareManager implements ISoftwareTypeManager {
	
	protected static final String SERVICEARCHIVE_CLASSIFIER = "servicearchive";
	protected static final String SERVICEARCHIVE_TYPE = "tar.gz";

	public static final String SNAPSHOT_SUFFIX = "-SNAPSHOT";

	private ImportSession importSession;

	public void setImportSession(ImportSession session) {
		this.importSession = session;
	}

	protected ImportSession getImportSession() {
		return this.importSession;
	}

	/**
	 * Recover target maven repository info
	 * 
	 * @return MavenRepositoryInfo object
	 * @throws Exception
	 */
	protected abstract IMavenRepositoryInfo getTargetRepository() throws Exception;
	
	protected  MavenCoordinates getMavenCoordinates(ServiceProfile serviceProfile) throws Exception{
		return getMavenCoordinates(serviceProfile.getService().getPackages().get(0));
	}

	protected String getReadmeContent() {
		StringBuilder sb = new StringBuilder();
		sb.append("The gCube System - "
				+ importSession.getGeneralInfo().getApplicationName() + "\n");
		sb.append("--------------------------------------------------------------------------------\n\n");
		sb.append("This work has been partially supported by the following European projects:\n");
		sb.append("DILIGENT (FP6-2003-IST-2),\n");
		sb.append("D4Science (FP7-INFRA-2007-1.2.2),\n");
		sb.append("D4Science-II (FP7-INFRA-2008-1.2.2),\n");
		sb.append("Marine (FP7-INFRASTRUCTURES-2011-2), and\n");
		sb.append("EUBrazilOpenBio (FP7-ICT-2011-EU-Brazil).\n\n");
		sb.append("Authors\n");
		sb.append("--------------------------------------------------------------------------------\n\n");
		sb.append(getMaintainersContent() + "\n");
		sb.append("Version and Release Date\n");
		sb.append("--------------------------------------------------------------------------------\n\n");
		sb.append(importSession.getGeneralInfo().getApplicationVersion()
				.toString()
				+ ", "
				+ DateFormat
						.getDateInstance(DateFormat.SHORT, Locale.ITALIAN)
						.format(importSession.getGeneralInfo().getReleaseDate())
				+ "\n\n");
		sb.append("Description\n");
		sb.append("--------------------------------------------------------------------------------\n\n");
		if (importSession.getGeneralInfo().getApplicationDescription() == null
				|| importSession.getGeneralInfo().getApplicationDescription()
						.equals(""))
			sb.append("No description was provided\n\n");
		else
			sb.append(importSession.getGeneralInfo()
					.getApplicationDescription() + "\n\n");
		sb.append("Download information\n");
		sb.append("--------------------------------------------------------------------------------\n\n");
		for (Url url : importSession.getGeneralInfo().getUrls()) {
			if (url.getUrlDescription().equals(Url.SOURCE_CODE)) {
				sb.append("Source code is available from:\n");
				sb.append(url.getUrl() + "\n\n");
			}
			if (url.getUrlDescription().equals(Url.BINARIES)) {
				sb.append("Binaries can be downloaded from:\n");
				sb.append(url.getUrl() + "\n\n");
			}
		}
		sb.append("Documentation\n");
		sb.append("--------------------------------------------------------------------------------\n\n");
		for (Url url : importSession.getGeneralInfo().getUrls()) {
			if (url.getUrlDescription().equals(Url.WIKI)) {
				sb.append("Documentation is available on-line from the Wiki:\n");
				sb.append(url.getUrl() + "\n\n");
			}
			if (url.getUrlDescription().equals(Url.DOCUMENTATION)) {
				sb.append("Documentation is available on-line from:\n");
				sb.append(url.getUrl() + "\n\n");
			}
			if (url.getUrlDescription().equals(Url.TEST_CASE_DOCUMENTATION)) {
				sb.append("Test-suite documentation is available on-line from :\n");
				sb.append(url.getUrl() + "\n\n");
			}
		}
		sb.append("Licensing\n");
		sb.append("--------------------------------------------------------------------------------\n\n");
		sb.append("This software is licensed under the terms you may find in the file named \"LICENSE\" in this directory.\n\n");
		return sb.toString();
	}

	protected String getInstallContent() {
		StringBuilder sb = new StringBuilder();
		sb.append("---------------INSTALLATION---------------\n\n");
		if (importSession.getGeneralInfo().getInstallationNotes() == null
				|| importSession.getGeneralInfo().getInstallationNotes()
						.equals(""))
			sb.append("No installation notes were provided\n\n");
		else
			sb.append(importSession.getGeneralInfo().getInstallationNotes()
					+ "\n\n");
		sb.append("---------------CONFIGURATION---------------\n\n");
		if (importSession.getGeneralInfo().getConfigurationNotes() == null
				|| importSession.getGeneralInfo().getConfigurationNotes()
						.equals(""))
			sb.append("No configuration notes were provided\n\n");
		else
			sb.append(importSession.getGeneralInfo().getConfigurationNotes()
					+ "\n\n");
		sb.append("---------------DEPENDENCIES---------------\n\n");
		if (importSession.getGeneralInfo().getDependenciesNotes() == null
				|| importSession.getGeneralInfo().getDependenciesNotes()
						.equals(""))
			sb.append("No dependencies were defined\n\n");
		else
			sb.append(importSession.getGeneralInfo().getDependenciesNotes()
					+ "\n\n");
		sb.append("---------------UNINSTALLATION-------------\n\n");
		if (importSession.getGeneralInfo().getUninstallationNotes() == null
				|| importSession.getGeneralInfo().getUninstallationNotes()
						.equals(""))
			sb.append("No uninstallation notes were provided\n\n");
		else
			sb.append(importSession.getGeneralInfo().getUninstallationNotes()
					+ "\n\n");
		return sb.toString();
	}

	protected String getMaintainersContent() {
		StringBuilder sb = new StringBuilder();
		for (Maintainer m : importSession.getGeneralInfo().getMaintainers()) {
			sb.append("* " + m.getFirstName() + " " + m.getLastName() + " ("
					+ m.getEmail() + ")" + ", " + m.getOrganization() + "\n");
		}
		return sb.toString();
	}

	protected String getChangelogContent() {
		StringBuilder sb = new StringBuilder();
		sb.append("<ReleaseNotes>\n");
		sb.append("\t<Changeset component=\""
				+ importSession.getGeneralInfo().getComponentName()
				+ "\" date=\""
				+ DateFormat
						.getDateInstance(DateFormat.SHORT, Locale.ITALIAN)
						.format(importSession.getGeneralInfo().getReleaseDate())
				+ "\">\n");
		String prefixString = "";
		for (SoftwareChange sc : importSession.getGeneralInfo().getChanges()) {
			prefixString = "";
			if (sc.getTicketNumber() != null)
				prefixString = "#" + sc.getTicketNumber().toString() + ": ";
			sb.append("\t\t<Change>" + prefixString + sc.getDescription()
					+ "</Change>\n");
		}
		sb.append("\t</Changeset>\n");
		sb.append("</ReleaseNotes>\n\n");
		return sb.toString();
	}

	protected String getLicenseContent() {
		StringBuilder sb = new StringBuilder();
		sb.append("Licensing\n");
		sb.append("--------------------------------------------------------------------------------\n\n");
		sb.append(importSession.getGeneralInfo().getLicense());
		return sb.toString();
	}

	public ArrayList<Deliverable> getMiscFiles() throws Exception {
		ArrayList<Deliverable> files = new ArrayList<Deliverable>();
		files.add(new Deliverable(Deliverable.README, getReadmeContent()));
		files.add(new Deliverable(Deliverable.INSTALL, getInstallContent()));
		files.add(new Deliverable(Deliverable.MAINTAINERS, getMaintainersContent()));
		files.add(new Deliverable(Deliverable.CHANGELOG, getChangelogContent()));
		files.add(new Deliverable(Deliverable.LICENSE, getLicenseContent()));
		return files;
	}

	public abstract String getServiceProfile(boolean withHeader) throws Exception;
	
	@Override
	public String getPOM(Package softwarePackage) throws Exception {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder
				.append("<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n");
		stringBuilder.append("\t<modelVersion>4.0.0</modelVersion>\n");
		stringBuilder.append("\t<groupId>" + getMavenCoordinates(softwarePackage).getGroupId()
				+ "</groupId>\n");
		stringBuilder.append("\t<artifactId>"
				+ getMavenCoordinates(softwarePackage).getArtifactId() + "</artifactId>\n");
		stringBuilder.append("\t<version>" + getMavenCoordinates(softwarePackage).getVersion()
				+ "</version>\n");
		stringBuilder.append("\t<packaging>" + getMavenCoordinates(softwarePackage).getPackaging()
				+ "</packaging>\n");
		if (softwarePackage.getMavenDependencies().size() > 0) {
			stringBuilder.append("\t<dependencies>\n");
			for (MavenCoordinates dep: softwarePackage.getMavenDependencies()){
				stringBuilder.append("\t\t<dependency>\n");
				stringBuilder.append("\t\t\t<groupId>"+dep.getGroupId()+"</groupId>\n");
				stringBuilder.append("\t\t\t<artifactId>"+dep.getArtifactId()+"</artifactId>\n");
				stringBuilder.append("\t\t\t<version>"+dep.getVersion()+"</version>\n");
				stringBuilder.append("\t\t\t<type>jar</type>\n");
				stringBuilder.append("\t\t</dependency>\n");
			}
			stringBuilder.append("\t</dependencies>\n");
		}
		stringBuilder.append("</project>");
		return stringBuilder.toString();
	}
	
	@Override
	public String getPOM(ServiceProfile serviceProfile) throws Exception {
		MavenCoordinates mavenCoordinates = getMavenCoordinates(serviceProfile);
		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder
				.append("<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n");
		stringBuilder.append("\t<modelVersion>4.0.0</modelVersion>\n");
		stringBuilder.append("\t<groupId>" + mavenCoordinates.getGroupId()
				+ "</groupId>\n");
		stringBuilder.append("\t<artifactId>"
				+ mavenCoordinates.getArtifactId() + "</artifactId>\n");
		stringBuilder.append("\t<version>" + mavenCoordinates.getVersion()
				+ "</version>\n");
		//Service archive has its own packaging type
		stringBuilder.append("\t<packaging>tar.gz</packaging>\n");
		//TODO-LF display dependencies of all contained packages?
		stringBuilder.append("</project>");
		return stringBuilder.toString();
	}
	
	@Override
	public IOperationProgress submitSoftware() throws Exception {

		
		ISoftwareSubmissionTask submissionTask = createSofwareSubmissionTask();
		getImportSession().setSubmitProgress(submissionTask.getOperationProgress());		
		
		Thread submitThread = new Thread(submissionTask);
		submitThread.start();
		
		return submissionTask.getOperationProgress();
	}
	
	protected abstract ISoftwareSubmissionTask createSofwareSubmissionTask();
	
	protected String getScopeInfrastructure(){
		return ScopeUtil.getInfrastructure(getImportSession().getScope()); 
	}

}
