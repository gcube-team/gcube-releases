package org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.gcube.common.resources.gcore.Software;
import org.gcube.common.resources.gcore.Software.Profile.PluginPackage;
import org.gcube.common.resources.gcore.Software.Profile.PluginPackage.TargetService;
import org.gcube.common.resources.gcore.Software.Profile.SoftwarePackage;
import org.gcube.common.resources.gcore.utils.Group;
import org.gcube.portlets.admin.software_upload_wizard.server.aslmanagers.ASLSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.server.data.SoftwareFile;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.filesmanager.FileManager;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.maven.deploy.IMavenDeployer;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.maven.deploy.PrimaryArtifactAttachment;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.maven.is.IMavenRepositoryIS;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.registrationmanagers.ISoftwareSubmissionTask;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.scope.ConfigurableScopeAvailable;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.scope.ScopeAvailable;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.softwaregateway.ISoftwareGatewayRegistrationManager;
import org.gcube.portlets.admin.software_upload_wizard.server.softwareprofile.Package;
import org.gcube.portlets.admin.software_upload_wizard.server.softwareprofile.ServiceProfile;
import org.gcube.portlets.admin.software_upload_wizard.server.util.SerializationUtil;
import org.gcube.portlets.admin.software_upload_wizard.shared.IOperationProgress;
import org.gcube.portlets.admin.software_upload_wizard.shared.OperationProgress;
import org.gcube.portlets.admin.software_upload_wizard.shared.OperationState;
import org.gcube.portlets.admin.software_upload_wizard.shared.filetypes.FileType;
import org.gcube.portlets.admin.software_upload_wizard.shared.filetypes.InstallScriptFileType;
import org.gcube.portlets.admin.software_upload_wizard.shared.filetypes.JarFileType;
import org.gcube.portlets.admin.software_upload_wizard.shared.filetypes.MiscFileType;
import org.gcube.portlets.admin.software_upload_wizard.shared.filetypes.RebootScriptFileType;
import org.gcube.portlets.admin.software_upload_wizard.shared.filetypes.UninstallScriptFileType;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.maven.IMavenRepositoryInfo;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.maven.MavenCoordinates;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.PackageData;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.PackageData.PackageType;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.ServiceData;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwaretypes.ISoftwareTypeInfo;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwaretypes.SoftwareTypeCode;
import org.gcube.portlets.admin.software_upload_wizard.shared.softwaretypes.SoftwareTypeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class GCubePluginSoftwareManager extends AbstractSoftwareManager {

	private static final List<String> availableInfras = Lists.newArrayList(ASLSessionManager.GCUBE_INFRASTRUCTURE,
			ASLSessionManager.D4SCIENCE_INFRASTRUCTURE);

	private static final Logger log = LoggerFactory.getLogger(GCubePluginSoftwareManager.class);

	@Inject
	IMavenDeployer mavenDeployer;

	@Inject
	private IMavenRepositoryIS mavenRepositoryIS;

	@Inject
	private FileManager fileManager;

	@Inject
	private ISoftwareGatewayRegistrationManager sgRegistrationManager;

	private ScopeAvailable scopeAvailableDelegate = new ConfigurableScopeAvailable(availableInfras);

	public boolean isAvailableForScope(String scope) {
		return scopeAvailableDelegate.isAvailableForScope(scope);
	}

	private static final SoftwareTypeCode CODE = SoftwareTypeCode.gCubePlugin;
	private static final String NAME = "gCube Plugin";
	private static final String ARTIFACT_GROUPID_PREFIX = "org.gcube.";
	// TODO Remove inline of variables
	private static String DESCRIPTION = "<h1>gCube Plugin</h1>"
			+ "<p>A gCube Plugin is a a set of software components that extends the functionalities of a gCube component.</p>"
			+ "<h2>Wizard steps</h2>"
			+ "<ul>"
			+ "<li>User enters Software Profile specific data</li>"
			+ "<li>User uploads several package related files</li>"
			+ "<li>User enters generic software info, documentation and source code/binary URLs</li>"
			+ "<li>User specifies package maintainers and software changes</li>"
			+ "<li>User enters package installation, uninstallation and configuration notes and specifies dependencies</li>"
			+ "<li>User enters license agreement</li>"
			+ "<li>User reviews XML Software Profile and generated files and submits Software Archive registration</li>"
			+ "</ul>" + "<h2>Requirements</h2>" + "<ul>" + "<li>Provided software must not be third party</li>"
			+ "</ul>";

	@Override
	public ServiceProfile generateInitialSoftwareProfile() {
		ServiceProfile profile = new ServiceProfile();

		ArrayList<FileType> allowedFileTypes = new ArrayList<FileType>();
		allowedFileTypes.add(new JarFileType());
		allowedFileTypes.add(new InstallScriptFileType());

		allowedFileTypes.add(new UninstallScriptFileType());
		allowedFileTypes.add(new RebootScriptFileType());
		allowedFileTypes.add(new MiscFileType());
		Package pack = new Package(PackageType.Plugin, allowedFileTypes);
		profile.getService().getPackages().add(pack);

		return profile;
	}

	@Override
	public ISoftwareTypeInfo getSoftwareTypeInfo() {
		return new SoftwareTypeInfo(CODE, NAME, DESCRIPTION);
	}

	@Override
	protected IMavenRepositoryInfo getTargetRepository() throws Exception {
		if (getScopeInfrastructure().equals(ASLSessionManager.GCUBE_INFRASTRUCTURE))
			return mavenRepositoryIS.getMavenRepository(IMavenRepositoryIS.SNAPSHOTS_REPO_ID);
		if (getScopeInfrastructure().equals(ASLSessionManager.D4SCIENCE_INFRASTRUCTURE))
			return mavenRepositoryIS.getMavenRepository(IMavenRepositoryIS.RELEASES_REPO_ID);
		throw new Exception("Cannot return a valid Maven target repository for the provided scope infrastructure");
	}

	@Override
	public MavenCoordinates getMavenCoordinates(Package softwarePackage) throws Exception {
		PackageData packageData = softwarePackage.getData();
		String artifactId = packageData.getName().toLowerCase();

		String normalizedServiceClass = getImportSession().getServiceProfile().getService().getData().getClazz()
				.toLowerCase().replaceAll("-", "").replaceAll("\\.", "");

		String groupId = ARTIFACT_GROUPID_PREFIX + normalizedServiceClass;
		String version = packageData.getVersion().toString();
		if (getScopeInfrastructure().equals(ASLSessionManager.GCUBE_INFRASTRUCTURE))
			version += SNAPSHOT_SUFFIX;

		MavenCoordinates result = new MavenCoordinates(groupId, artifactId, version);
		return result;
	}

	// @Override
	// public String getServiceProfile(boolean withHeader) throws Exception {
	// GCUBEService gcubeService = GHNContext
	// .getImplementation(GCUBEService.class);
	//
	// ServiceData serviceData = getImportSession().getServiceProfile()
	// .getService().getData();
	// Package mainPackage = getImportSession().getServiceProfile()
	// .getService().getPackages().get(0);
	// PackageData mainSoftwarePackageData = mainPackage.getData();
	//
	// gcubeService.setServiceName(serviceData.getName());
	// gcubeService.setDescription(serviceData.getDescription());
	// gcubeService.setVersion(serviceData.getVersion().toString());
	// gcubeService.setServiceClass(serviceData.getClazz());
	//
	// Plugin pluginPackage = new Plugin();
	//
	// pluginPackage.setName(mainSoftwarePackageData.getName());
	// pluginPackage.setDescription(mainSoftwarePackageData.getDescription());
	//
	// String infrastructure = getImportSession().getScope()
	// .getInfrastructure().getName();
	// if (infrastructure.equals(ASLSessionManager.GCUBE_INFRASTRUCTURE))
	// pluginPackage.setVersion(mainSoftwarePackageData.getVersion()
	// .toString() + SNAPSHOT_SUFFIX);
	// else if (infrastructure
	// .equals(ASLSessionManager.D4SCIENCE_INFRASTRUCTURE))
	// pluginPackage.setVersion(mainSoftwarePackageData.getVersion()
	// .toString());
	// else
	// throw new Exception(
	// "Cannot generate valid package version with the given scope infrastructure");
	//
	// MavenCoordinates mavenCoordinates = getMavenCoordinates(mainPackage);
	// pluginPackage
	// .setMavenCoordinates(mavenCoordinates.getGroupId(),
	// mavenCoordinates.getArtifactId(),
	// mavenCoordinates.getVersion());
	//
	// pluginPackage.setMandatoryLevel(ScopeLevel.NONE);
	//
	// TargetService gCubeTargetService = new TargetService();
	// gCubeTargetService.setClazz(mainSoftwarePackageData.getTargetService()
	// .getServiceClass());
	// gCubeTargetService.setName(mainSoftwarePackageData.getTargetService()
	// .getServiceName());
	// gCubeTargetService.setVersion(mainSoftwarePackageData
	// .getTargetService().getServiceVersion().toString());
	// gCubeTargetService.setTargetPackage(mainSoftwarePackageData
	// .getTargetService().getPackageName());
	// gCubeTargetService.setTargetVersion(mainSoftwarePackageData
	// .getTargetService().getPackageVersion());
	//
	// pluginPackage.setTargetService(gCubeTargetService);
	//
	// for (SoftwareFile file : mainPackage.getFilesContainer().getFiles()) {
	// pluginPackage.getFiles().add(file.getFilename());
	// }
	//
	// /** Set scripts **/
	// List<SoftwareFile> scripts;
	//
	// // Install
	// scripts = mainPackage.getFilesContainer().getFilesWithFileType(
	// InstallScriptFileType.NAME);
	// pluginPackage.setInstallScripts(new ArrayList<String>(Collections2
	// .transform(scripts, new ScriptTransformFunction())));
	//
	// // Uninstall
	// scripts = mainPackage.getFilesContainer().getFilesWithFileType(
	// UninstallScriptFileType.NAME);
	// pluginPackage.setUninstallScripts(new ArrayList<String>(Collections2
	// .transform(scripts, new ScriptTransformFunction())));
	//
	// // Reboot
	// scripts = mainPackage.getFilesContainer().getFilesWithFileType(
	// RebootScriptFileType.NAME);
	// pluginPackage.setRebootScripts(new ArrayList<String>(Collections2
	// .transform(scripts, new ScriptTransformFunction())));
	//
	// gcubeService.getPackages().add(pluginPackage);
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

	@Override
	protected ISoftwareSubmissionTask createSofwareSubmissionTask() {
		try {
			ISoftwareSubmissionTask task = new GCubePluginSubmissionTask();
			task.setTargetRepository(getTargetRepository());
			return task;
		} catch (Exception ex) {
			log.error("Error occurred while creating software submission task.", ex);
			return null;
		}
	}

	private class GCubePluginSubmissionTask implements ISoftwareSubmissionTask {

		private IOperationProgress operationProgress = new OperationProgress();

		@Override
		public void run() {
			File primaryArtifactFile = null;
			File primaryArtifactPomFile = null;
			File serviceArchiveFile = null;
			File serviceArchivePomFile = null;

			try {
				log.debug("Starting software deployment");

				Package mainPackage = getImportSession().getServiceProfile().getService().getPackages().get(0);
				List<SoftwareFile> jarSoftwareFiles = mainPackage.getFilesContainer().getFilesWithFileType(
						JarFileType.NAME);

				operationProgress.setProgress(100, 0);
				operationProgress.setDetails("Creating primary artifact POM...");

				primaryArtifactPomFile = fileManager.createPomFile(getPOM(mainPackage));
				log.debug("Primary artifact POM created");

				primaryArtifactFile = jarSoftwareFiles.get(0).getFile();

				// Create service archive
				operationProgress.setProgress(100, 25);
				operationProgress.setDetails("Creating Service Archive...");

				serviceArchiveFile = fileManager.createServiveArchive(getServiceProfile(true), getMiscFiles(),
						getImportSession().getServiceProfile());
				log.debug("Service Archive created");

				serviceArchivePomFile = fileManager.createPomFile(getPOM(getImportSession().getServiceProfile()));
				log.debug("Sservice archive POM created");

				// Deploy artifacts on maven repo
				operationProgress.setProgress(100, 50);
				operationProgress.setDetails("Deploying artifacts...");

				log.trace("Deploying artifacts on maven repository " + targetRepository.getId());
				PrimaryArtifactAttachment serviceArchiveAttachment = new PrimaryArtifactAttachment(serviceArchiveFile,
						SERVICEARCHIVE_CLASSIFIER, SERVICEARCHIVE_TYPE);
				mavenDeployer.deploy(targetRepository, primaryArtifactFile, primaryArtifactPomFile,
						serviceArchiveAttachment);

				// Register Profile
				operationProgress.setProgress(100, 75);
				operationProgress.setDetails("Registering Service Profile...");

				log.trace("Registering Service Profile on software gateway...");
				sgRegistrationManager.registerProfile(getServiceProfile(true), getImportSession().getScope());

				operationProgress.setProgress(100, 100);
				operationProgress.setState(OperationState.COMPLETED);

			} catch (Exception e) {
				log.error("Error encountered during software submission", e);
				operationProgress.setProgress(100, 0);
				operationProgress.setDetails("Error encountered during software submission. " + e.getMessage());
				operationProgress.setState(OperationState.FAILED);
			} finally {
				// Delete garbage
				if (serviceArchiveFile != null)
					serviceArchiveFile.delete();
				if (serviceArchivePomFile != null)
					serviceArchivePomFile.delete();
				if (primaryArtifactPomFile != null)
					primaryArtifactPomFile.delete();
				if (primaryArtifactFile != null)
					primaryArtifactFile.delete();
			}
		}

		@Override
		public IOperationProgress getOperationProgress() {
			return operationProgress;
		}

		private IMavenRepositoryInfo targetRepository;

		@Override
		public void setTargetRepository(IMavenRepositoryInfo targetRepository) {
			this.targetRepository = targetRepository;
		}

	}

	@Override
	public String getServiceProfile(boolean withHeader) throws Exception {

		ServiceData serviceData = getImportSession().getServiceProfile().getService().getData();
		Package mainPackage = getImportSession().getServiceProfile().getService().getPackages().get(0);
		PackageData mainSoftwarePackageData = mainPackage.getData();

		Software softwareProfile = new Software();
		softwareProfile.newProfile().softwareName(serviceData.getName());
		softwareProfile.profile().description(serviceData.getDescription());
		// gcubeService.setVersion(serviceData.getVersion().toString());
		softwareProfile.profile().softwareClass(serviceData.getClazz());

		Group<SoftwarePackage<?>> packages = softwareProfile.profile().packages();
		PluginPackage pluginPackage = packages.add(PluginPackage.class);
		pluginPackage.name(mainSoftwarePackageData.getName());
		pluginPackage.description(mainSoftwarePackageData.getDescription());

		if (getScopeInfrastructure().equals(ASLSessionManager.GCUBE_INFRASTRUCTURE))
			pluginPackage.version(mainSoftwarePackageData.getVersion().toString() + SNAPSHOT_SUFFIX);
		else if (getScopeInfrastructure().equals(ASLSessionManager.D4SCIENCE_INFRASTRUCTURE))
			pluginPackage.version(mainSoftwarePackageData.getVersion().toString());
		else
			throw new RuntimeException("Cannot generate valid package version with the given scope infrastructure");

		org.gcube.common.resources.gcore.Software.Profile.SoftwarePackage.MavenCoordinates mavenCoordinates = pluginPackage
				.newCoordinates();
		MavenCoordinates mavenCoords = getMavenCoordinates(mainPackage);
		mavenCoordinates.artifactId(mavenCoords.getArtifactId());
		mavenCoordinates.groupId(mavenCoords.getGroupId());
		mavenCoordinates.version(mavenCoords.getVersion());

		pluginPackage.newMandatory();

		TargetService targetService = pluginPackage.newTargetService();
		targetService.newService().serviceClass(mainSoftwarePackageData.getTargetService().getServiceClass());
		targetService.service().serviceName(mainSoftwarePackageData.getTargetService().getServiceName());
		targetService.version(mainSoftwarePackageData.getTargetService().getServiceVersion().toString());
		targetService.servicePackage(mainSoftwarePackageData.getTargetService().getPackageName());
		// gCubeTargetService.setTargetVersion(mainSoftwarePackageData
		// .getTargetService().getPackageVersion());

		Collection<String> files = pluginPackage.files();
		for (SoftwareFile file : mainPackage.getFilesContainer().getFiles())
			files.add(file.getFilename());

		Collection<String> installScripts = pluginPackage.installScripts();
		for (SoftwareFile file : mainPackage.getFilesContainer().getFilesWithFileType(InstallScriptFileType.NAME))
			installScripts.add(file.getFilename());

		Collection<String> uninstallScripts = pluginPackage.uninstallScripts();
		for (SoftwareFile file : mainPackage.getFilesContainer().getFilesWithFileType(UninstallScriptFileType.NAME))
			uninstallScripts.add(file.getFilename());

		Collection<String> rebootScripts = pluginPackage.rebootScripts();
		for (SoftwareFile file : mainPackage.getFilesContainer().getFilesWithFileType(RebootScriptFileType.NAME))
			rebootScripts.add(file.getFilename());

		return SerializationUtil.serialize(softwareProfile);

	}

}
