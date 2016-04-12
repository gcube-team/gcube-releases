package org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.gcube.common.resources.gcore.Software;
import org.gcube.common.resources.gcore.Software.Profile.GenericPackage;
import org.gcube.common.resources.gcore.Software.Profile.GenericPackage.Type;
import org.gcube.common.resources.gcore.Software.Profile.SoftwarePackage;
import org.gcube.common.resources.gcore.Software.Profile.SoftwarePackage.Level;
import org.gcube.common.resources.gcore.utils.Group;
import org.gcube.portlets.admin.software_upload_wizard.server.aslmanagers.ASLSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.server.data.SoftwareFile;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.filesmanager.FileManager;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.maven.deploy.IMavenDeployer;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.maven.is.IMavenRepositoryIS;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.registrationmanagers.ISoftwareSubmissionTask;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.scope.ConfigurableScopeAvailable;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.scope.ScopeAvailable;
import org.gcube.portlets.admin.software_upload_wizard.server.softwareprofile.Package;
import org.gcube.portlets.admin.software_upload_wizard.server.softwareprofile.ServiceProfile;
import org.gcube.portlets.admin.software_upload_wizard.server.util.ScopeUtil;
import org.gcube.portlets.admin.software_upload_wizard.server.util.SerializationUtil;
import org.gcube.portlets.admin.software_upload_wizard.shared.IOperationProgress;
import org.gcube.portlets.admin.software_upload_wizard.shared.OperationProgress;
import org.gcube.portlets.admin.software_upload_wizard.shared.OperationState;
import org.gcube.portlets.admin.software_upload_wizard.shared.filetypes.FileType;
import org.gcube.portlets.admin.software_upload_wizard.shared.filetypes.PatchArchiveFileType;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.maven.IMavenRepositoryInfo;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.maven.MavenCoordinates;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.PackageData;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.PackageData.PackageType;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwaretypes.ISoftwareTypeInfo;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwaretypes.SoftwareTypeCode;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwaretypes.SoftwareTypeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class GCubePatchSoftwareManager extends AbstractSoftwareManager {

	// TODO Remove inline variables
	private static final SoftwareTypeCode CODE = SoftwareTypeCode.gCubePatch;

	private static final String NAME = "gCube Patch";

	private final static String DESCRIPTION = "<h1>gCube Patch</h1>"
			+ "<p>One or more bug fixes for a gCube component.</p>"
			+ "<p>A tar.gz archive will be created with the files included in the provided user patch archive and wizard generated files. The newly generated archive will be uploaded to a Maven repository.</p>"
			+ "<h2>Wizard steps</h2>"
			+ "<ul>"

			+ "<li>User enters software data</li>"
			+ "<li>User uploads a patch archive</li>"
			+ "<li>User enters generic software info, documentation and source code/binary URLs</li>"
			+ "<li>User specifies package maintainers and software changes</li>"
			+ "<li>User enters package installation, uninstallation and configuration notes and specifies dependencies</li>"
			+ "<li>User enters license agreement</li>"
			+ "<li>User reviews generated files and submits Software registration</li>"
			+ "</ul>"
			+ "<h2>Requirements</h2>"
			+ "<ul>"
			+ "<li>Provided software must not be third party</li>"
			+ "<li>User provided Patch archive must include in the root directory a file named apply.sh.</li>"
			+ "<li> Files on the root archive directory with the same filenames as the wizard generated deliverables (README, MAINTAINERS, changelog.xml, INSTALL, LICENSE) will be overwritten.</li>"
			+ "</ul>";

	private static final String DEFAULT_ARTIFACT_GROUPID = "org.gcube.patches";

	private static final String DEFAULT_SERVICE_CLASS = "Patches";

	private static final List<String> availableInfras = Lists.newArrayList(ASLSessionManager.D4SCIENCE_INFRASTRUCTURE,
			ASLSessionManager.GCUBE_INFRASTRUCTURE);

	private static final Logger log = LoggerFactory.getLogger(GCubePatchSoftwareManager.class);

	@Inject
	IMavenDeployer mavenDeployer;

	@Inject
	private IMavenRepositoryIS mavenRepositoryIS;

	@Inject
	private FileManager fileManager;

	private ScopeAvailable scopeAvailabilityDelegate = new ConfigurableScopeAvailable(availableInfras);

	@Override
	public ServiceProfile generateInitialSoftwareProfile() {
		ServiceProfile profile = new ServiceProfile();

		ArrayList<FileType> allowedFileTypes = new ArrayList<FileType>();
		allowedFileTypes.add(new PatchArchiveFileType());
		Package pack = new Package(PackageType.Plugin, allowedFileTypes);
		profile.getService().getPackages().add(pack);

		return profile;
	}

	@Override
	public ISoftwareTypeInfo getSoftwareTypeInfo() {
		return new SoftwareTypeInfo(CODE, NAME, DESCRIPTION);
	}

	@Override
	public String getPOM(Package softwarePackage) throws Exception {
		throw new Exception("GCube Patch does not provide package POM.");
	}

	@Override
	protected IMavenRepositoryInfo getTargetRepository() throws Exception {
		if (ScopeUtil.getInfrastructure(getImportSession().getScope()).equals(ASLSessionManager.GCUBE_INFRASTRUCTURE))
			return mavenRepositoryIS.getMavenRepository(IMavenRepositoryIS.SNAPSHOTS_REPO_ID);
		if (ScopeUtil.getInfrastructure(getImportSession().getScope()).equals(
				ASLSessionManager.D4SCIENCE_INFRASTRUCTURE))
			return mavenRepositoryIS.getMavenRepository(IMavenRepositoryIS.RELEASES_REPO_ID);
		throw new Exception("Cannot return a valid Maven target repository for the provided scope infrastructure");
	}

	@Override
	public MavenCoordinates getMavenCoordinates(Package softwarePackage) throws Exception {
		PackageData packageData = softwarePackage.getData();
		String artifactId = packageData.getName().toLowerCase();
		String groupId = DEFAULT_ARTIFACT_GROUPID;
		String version = packageData.getVersion().toString();
		if (ScopeUtil.getInfrastructure(getImportSession().getScope()).equals(ASLSessionManager.GCUBE_INFRASTRUCTURE))
			version += SNAPSHOT_SUFFIX;

		MavenCoordinates result = new MavenCoordinates(groupId, artifactId, version, "tar.gz");
		return result;
	}

	// @Override
	// public String getServiceProfile(boolean withHeader) throws Exception {
	// GCUBEService gcubeService = GHNContext
	// .getImplementation(GCUBEService.class);
	//
	// gcubeService.setServiceName(getImportSession().getServiceProfile()
	// .getService().getData().getName());
	// gcubeService.setDescription(getImportSession().getServiceProfile()
	// .getService().getData().getDescription());
	// gcubeService.setVersion(getImportSession().getServiceProfile()
	// .getService().getData().getVersion().toString());
	// gcubeService.setServiceClass(DEFAULT_SERVICE_CLASS);
	//
	// Software softwarePackage = new Software();
	// Package mainPackage = getImportSession().getServiceProfile()
	// .getService().getPackages().get(0);
	// PackageData mainSoftwarePackageData = mainPackage.getData();
	//
	// softwarePackage.setName(mainSoftwarePackageData.getName());
	// softwarePackage
	// .setDescription(mainSoftwarePackageData.getDescription());
	//
	// if (getImportSession().getServiceProfile().isThirdPartySoftware())
	// softwarePackage.setVersion(mainSoftwarePackageData.getVersion()
	// .toString());
	// else {
	// String infrastructure = getImportSession().getScope()
	// .getInfrastructure().getName();
	// if (infrastructure.equals(ASLSessionManager.GCUBE_INFRASTRUCTURE))
	// softwarePackage.setVersion(mainSoftwarePackageData.getVersion()
	// .toString() + SNAPSHOT_SUFFIX);
	// else if (infrastructure
	// .equals(ASLSessionManager.D4SCIENCE_INFRASTRUCTURE))
	// softwarePackage.setVersion(mainSoftwarePackageData.getVersion()
	// .toString());
	// else
	// throw new Exception("Unmanaged scope infrastructure");
	// }
	//
	// MavenCoordinates mavenCoordinates = getMavenCoordinates(mainPackage);
	// softwarePackage
	// .setMavenCoordinates(mavenCoordinates.getGroupId(),
	// mavenCoordinates.getArtifactId(),
	// mavenCoordinates.getVersion());
	//
	// softwarePackage.setMandatoryLevel(ScopeLevel.NONE);
	//
	// softwarePackage.setType(Type.application);
	//
	// // Files list
	// for (SoftwareFile file : mainPackage.getFilesContainer().getFiles()) {
	// softwarePackage.getFiles().add(file.getFilename());
	// }
	//
	// gcubeService.getPackages().add(softwarePackage);
	//
	// StringWriter xml = new StringWriter();
	// gcubeService.store(xml);
	//
	// String resultXML = XmlFormatter
	// .prettyFormat(xml.toString(), withHeader);
	// Log.trace("XML profile generated:\n\n" + resultXML);
	//
	// return resultXML;
	// }

	public String getServiceProfile(boolean withHeader) throws Exception {
		Software softwareProfile = new Software();
		softwareProfile.newProfile().softwareName(
				getImportSession().getServiceProfile().getService().getData().getName());
		softwareProfile.profile().description(
				getImportSession().getServiceProfile().getService().getData().getDescription());
		softwareProfile.profile().softwareClass(DEFAULT_SERVICE_CLASS);

		Group<SoftwarePackage<?>> packages = softwareProfile.profile().packages();

		GenericPackage genericPackage = packages.add(GenericPackage.class);
		Package mainPackage = getImportSession().getServiceProfile().getService().getPackages().get(0);
		PackageData mainSoftwarePackageData = mainPackage.getData();
		genericPackage.name(mainSoftwarePackageData.getName());
		genericPackage.description(mainSoftwarePackageData.getDescription());

		if (getImportSession().getServiceProfile().isThirdPartySoftware())
			genericPackage.version(mainSoftwarePackageData.getVersion().toString());
		else {
			String infrastructure = ScopeUtil.getInfrastructure(getImportSession().getScope());
			if (infrastructure.equals(ASLSessionManager.GCUBE_INFRASTRUCTURE))
				genericPackage.version(mainSoftwarePackageData.getVersion().toString() + SNAPSHOT_SUFFIX);
			else if (infrastructure.equals(ASLSessionManager.D4SCIENCE_INFRASTRUCTURE))
				genericPackage.version(mainSoftwarePackageData.getVersion().toString());
			else
				throw new RuntimeException("Unmanaged scope infrastructure: " + infrastructure);
		}

		org.gcube.common.resources.gcore.Software.Profile.SoftwarePackage.MavenCoordinates mavenCoordinates = genericPackage
				.newCoordinates();
		mavenCoordinates.artifactId(getMavenCoordinates(mainPackage).getArtifactId());
		mavenCoordinates.groupId(getMavenCoordinates(mainPackage).getGroupId());
		mavenCoordinates.version(getMavenCoordinates(mainPackage).getVersion());

		Level level = genericPackage.mandatory();
		level = Level.NONE;

		genericPackage.type(Type.application);

		//
		Collection<String> files = genericPackage.files();
		for (SoftwareFile file : mainPackage.getFilesContainer().getFiles())
			files.add(file.getFilename());

		// Collection<String> installScripts = genericPackage.installScripts();
		// for (SoftwareFile file :
		// mainPackage.getFilesContainer().getFilesWithFileType(
		// InstallScriptFileType.NAME))
		// installScripts.add(file.getFilename());
		//
		// Collection<String> uninstallScripts =
		// genericPackage.uninstallScripts();
		// for (SoftwareFile file :
		// mainPackage.getFilesContainer().getFilesWithFileType(
		// UninstallScriptFileType.NAME))
		// uninstallScripts.add(file.getFilename());
		//
		// Collection<String> rebootScripts = genericPackage.rebootScripts();
		// for (SoftwareFile file :
		// mainPackage.getFilesContainer().getFilesWithFileType(
		// RebootScriptFileType.NAME))
		// rebootScripts.add(file.getFilename());

		return SerializationUtil.serialize(softwareProfile);
	}

	@Override
	protected ISoftwareSubmissionTask createSofwareSubmissionTask() {
		try {
			ISoftwareSubmissionTask task = new GCubePatchSubmissionTask();
			task.setTargetRepository(getTargetRepository());
			return task;
		} catch (Exception ex) {
			log.error("Error occurred while creating software submission task.", ex);
			return null;
		}
	}

	private class GCubePatchSubmissionTask implements ISoftwareSubmissionTask {

		private IOperationProgress operationProgress = new OperationProgress();
		private IMavenRepositoryInfo targetRepository;

		@Override
		public void run() {
			File serviceArchiveFile = null;
			File serviceArchivePomFile = null;
			try {
				log.debug("Starting software deployment");

				// Create service archive
				operationProgress.setProgress(100, 0);
				operationProgress.setDetails("Creating Service Archive...");

				log.trace("Creating Service Archive...");

				File patchFile = getImportSession().getServiceProfile().getService().getPackages().get(0)
						.getFilesContainer().getFilesWithFileType(PatchArchiveFileType.NAME).get(0).getFile();

				serviceArchiveFile = fileManager.createPatchArchive(getServiceProfile(true), getMiscFiles(), patchFile);

				log.trace("Creating service archive POM file...");
				serviceArchivePomFile = fileManager.createPomFile(getPOM(getImportSession().getServiceProfile()));

				// Deploy service archive
				log.trace("Deploying Service Archive...");
				operationProgress.setProgress(100, 50);
				operationProgress.setDetails("Deploying Service Archive...");

				log.trace("Deploying service archive on maven repository " + targetRepository.getId());
				mavenDeployer.deploy(targetRepository, serviceArchiveFile, serviceArchivePomFile, false,
						SERVICEARCHIVE_CLASSIFIER);

				operationProgress.setProgress(100, 100);
				operationProgress.setState(OperationState.COMPLETED);

				log.debug("Deploy completed succesfully");
			} catch (Exception e) {
				log.error("Error encountered during software submission", e);
				operationProgress.setProgress(100, 0);
				operationProgress.setDetails("Error encountered during software submission. " + e.getMessage());
				operationProgress.setState(OperationState.FAILED);
			} finally {
				// Delete garbage
				// Delete garbage
				try {
					FileUtils.forceDelete(serviceArchiveFile);
					FileUtils.forceDelete(serviceArchivePomFile);
				} catch (Exception ex) {
					log.warn("Unable to delete file." + ex);
				}
				// if (serviceArchiveFile != null)
				// serviceArchiveFile.delete();
				// if (serviceArchivePomFile != null)
				// serviceArchivePomFile.delete();
			}
		}

		@Override
		public IOperationProgress getOperationProgress() {
			return operationProgress;
		}

		@Override
		public void setTargetRepository(IMavenRepositoryInfo targetRepository) {
			this.targetRepository = targetRepository;
		}
	}

	public boolean isAvailableForScope(String scope) {
		return scopeAvailabilityDelegate.isAvailableForScope(scope);
	}

}
