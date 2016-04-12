package org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.resources.gcore.Software;
import org.gcube.common.resources.gcore.Software.Profile.GenericPackage;
import org.gcube.common.resources.gcore.Software.Profile.GenericPackage.Type;
import org.gcube.common.resources.gcore.Software.Profile.SoftwarePackage;
import org.gcube.common.resources.gcore.utils.Group;
import org.gcube.portlets.admin.software_upload_wizard.server.aslmanagers.ASLSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.maven.deploy.IMavenDeployer;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.registrationmanagers.ISoftwareSubmissionTask;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.scope.ConfigurableScopeAvailable;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.scope.ScopeAvailable;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.softwaregateway.ISoftwareGatewayRegistrationManager;
import org.gcube.portlets.admin.software_upload_wizard.server.softwareprofile.Package;
import org.gcube.portlets.admin.software_upload_wizard.server.softwareprofile.ServiceProfile;
import org.gcube.portlets.admin.software_upload_wizard.server.util.SerializationUtil;
import org.gcube.portlets.admin.software_upload_wizard.shared.DataDictionary;
import org.gcube.portlets.admin.software_upload_wizard.shared.Deliverable;
import org.gcube.portlets.admin.software_upload_wizard.shared.IOperationProgress;
import org.gcube.portlets.admin.software_upload_wizard.shared.OperationProgress;
import org.gcube.portlets.admin.software_upload_wizard.shared.OperationState;
import org.gcube.portlets.admin.software_upload_wizard.shared.filetypes.FileType;
import org.gcube.portlets.admin.software_upload_wizard.shared.filetypes.JarFileType;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.maven.IMavenRepositoryInfo;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.maven.MavenCoordinates;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.PackageData.PackageType;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwaretypes.ISoftwareTypeInfo;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwaretypes.SoftwareTypeCode;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwaretypes.SoftwareTypeInfo;
import org.slf4j.Logger;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class SoftwareRegistrationSoftwareManager extends AbstractSoftwareManager {

	private static final List<String> availableInfras = Lists.newArrayList(ASLSessionManager.D4SCIENCE_INFRASTRUCTURE,
			ASLSessionManager.GCUBE_INFRASTRUCTURE);

	private Logger log;

	@Inject
	IMavenDeployer mavenDeployer;

	@Inject
	private ISoftwareGatewayRegistrationManager sgRegistrationManager;

	private ScopeAvailable scopeAvailableDelegate = new ConfigurableScopeAvailable(availableInfras);

	private static final SoftwareTypeCode CODE = SoftwareTypeCode.SoftwareRegistration;
	private static final String NAME = "Software Registration";
	private static final String DESCRIPTION = "<h1>Software Registration</h1>"
			+ "<p>This Wizard allows the user to register on the Software Gateway any software that is currently uploaded on any infrastructure's Maven repository by providing the maven coordinates of the artifact.</p>"
			+ "<p>A Software Profile with a single service package will be created and registered on the Software Gateway.</p>"
			+ "<h2>Wizard steps</h2>"
			+ "<ul>"
			+ "<li>User enters Maven artifact coordinates</li>"
			+ "<li>User reviews XML Software Profile and submits software registration.</li>"
			+ "</ul>"
			+ "<h2>Requirements</h2>"
			+ "<ul>"
			+ "<li>An artifact with the given coordinates must be already registered on infrastructure Maven repositories</li>"
			+ "</ul>";

	public static final String SERVICE_CLASS = "ExternalSoftware";

	// private static final String SERVICE_VERSION = "1.0.0";

	@Override
	public ServiceProfile generateInitialSoftwareProfile() {
		ServiceProfile profile = new ServiceProfile();
		profile.getService().getData().setClazz("External");

		ArrayList<FileType> allowedFileTypes = new ArrayList<FileType>();
		allowedFileTypes.add(new JarFileType());
		Package pack = new Package(PackageType.Software, allowedFileTypes);
		profile.getService().getPackages().add(pack);
		return profile;
	}

	@Override
	public ISoftwareTypeInfo getSoftwareTypeInfo() {
		return new SoftwareTypeInfo(CODE, NAME, DESCRIPTION);
	}

	protected MavenCoordinates getMavenCoordinates() throws Exception {
		String artifactId = getImportSession().getStringData(DataDictionary.ARTIFACT_ID);
		String groupId = getImportSession().getStringData(DataDictionary.ARTIFACT_GROUPID);

		String version = getImportSession().getStringData(DataDictionary.ARTIFACT_VERSION);
		Boolean isSnapshot = Boolean.valueOf(getImportSession().getStringData(DataDictionary.ARTIFACT_ISSNAPSHOT));
		if (isSnapshot)
			version += "-SNAPSHOT";
		return new MavenCoordinates(groupId, artifactId, version);
	}

	@Override
	public String getServiceProfile(boolean withHeader) throws Exception {
		Software softwareResource = new Software();
		softwareResource.newProfile().softwareName(getImportSession().getStringData(DataDictionary.ARTIFACT_ID));
		softwareResource.profile().softwareClass(SERVICE_CLASS);

		Group<SoftwarePackage<?>> packages = softwareResource.profile().packages();
		GenericPackage softwarePackage = packages.add(GenericPackage.class);

		org.gcube.common.resources.gcore.Software.Profile.SoftwarePackage.MavenCoordinates mavenCoordinates = softwarePackage
				.newCoordinates();
		mavenCoordinates.artifactId(getMavenCoordinates().getArtifactId());
		mavenCoordinates.groupId(getMavenCoordinates().getGroupId());
		mavenCoordinates.version(getMavenCoordinates().getVersion());

		softwarePackage.newMandatory(); // Set mandatory to Scope.NONE

		softwarePackage.type(Type.library);

		if (Boolean.valueOf(getImportSession().getStringData(DataDictionary.ARTIFACT_ISSNAPSHOT))) {
			// If it's snapshot software recover user given jar filename
			softwarePackage.files().add(getImportSession().getStringData(DataDictionary.ARTIFACT_FILENAME));
		} else {
			// If it's a release generate filename automatically
			softwarePackage.files().add(
					getImportSession().getStringData(DataDictionary.ARTIFACT_ID) + "-"
							+ getImportSession().getStringData(DataDictionary.ARTIFACT_VERSION) + ".jar");
		}

		return SerializationUtil.serialize(softwareResource);
	}

	@Override
	public ArrayList<Deliverable> getMiscFiles() throws Exception {
		return new ArrayList<Deliverable>();
	}

	@Override
	protected IMavenRepositoryInfo getTargetRepository() throws Exception {
		throw new Exception("SoftwareRegistration does not involve any interaction with Maven repositories");
	}

	@Override
	public MavenCoordinates getMavenCoordinates(Package softwarePackage) throws Exception {
		// TODO Review this method
		throw new Exception("Software Registration Manager does not provide POMs for packages");
	}

	@Override
	protected ISoftwareSubmissionTask createSofwareSubmissionTask() {
		ISoftwareSubmissionTask task = new SoftwareRegistrationSubmissionTask();
		return task;
	}

	private class SoftwareRegistrationSubmissionTask implements ISoftwareSubmissionTask {

		private IOperationProgress operationProgress = new OperationProgress();

		@Override
		public void run() {
			try {
				log.debug("Starting software deployment");

				// Register Profile
				operationProgress.setProgress(100, 0);
				operationProgress.setDetails("Registering profile...");

				log.debug("Registering Service Profile on software gateway...");
				sgRegistrationManager.registerProfile(getServiceProfile(true), getImportSession().getScope());

				operationProgress.setProgress(100, 100);
				operationProgress.setState(OperationState.COMPLETED);
			} catch (Exception e) {
				log.error("Error encountered during software submission", e);
				operationProgress.setProgress(100, 0);
				operationProgress.setDetails("Error encountered during software submission. " + e.getMessage());
				operationProgress.setState(OperationState.FAILED);
			}
		}

		@Override
		public IOperationProgress getOperationProgress() {
			return operationProgress;
		}

		@Override
		public void setTargetRepository(IMavenRepositoryInfo targetRepository) {
			// Do nothing
		}

	}

	public boolean isAvailableForScope(String scope) {
		return scopeAvailableDelegate.isAvailableForScope(scope);
	}

}
