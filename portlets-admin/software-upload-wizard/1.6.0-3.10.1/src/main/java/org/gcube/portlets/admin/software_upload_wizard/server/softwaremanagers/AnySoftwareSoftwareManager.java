package org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.gcube.common.resources.gcore.Software;
import org.gcube.common.resources.gcore.Software.Profile.GenericPackage;
import org.gcube.common.resources.gcore.Software.Profile.GenericPackage.Type;
import org.gcube.common.resources.gcore.Software.Profile.SoftwarePackage;
import org.gcube.common.resources.gcore.utils.Group;
import org.gcube.portlets.admin.software_upload_wizard.server.aslmanagers.ASLSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.server.data.SoftwareFile;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.filesmanager.FileManager;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.maven.deploy.IMavenDeployer;
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
import org.gcube.portlets.admin.software_upload_wizard.shared.filetypes.RebootScriptFileType;
import org.gcube.portlets.admin.software_upload_wizard.shared.filetypes.SoftwareTarballFileType;
import org.gcube.portlets.admin.software_upload_wizard.shared.filetypes.UninstallScriptFileType;
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

public class AnySoftwareSoftwareManager extends AbstractSoftwareManager {

	private static final Logger log = LoggerFactory.getLogger(AnySoftwareSoftwareManager.class);

	@Inject
	IMavenDeployer mavenDeployer;

	private static final SoftwareTypeCode CODE = SoftwareTypeCode.AnySoftware;

	private static final String NAME = "Any Software";

	private static final List<String> availableScopeInfras = Lists.newArrayList("gcube");

	// TODO Remove inline of variables
	private static String DESCRIPTION = "<h1>Any Software</h1>"
			+ "<p>Generic software that can run on the infrastructure.<br/>"
			+ "A Service Profile with a single package will be created and registered on the Software Gateway, making the software available on the infrastructure. All User provided files will be made available on gcube maven repositories as a Software Archive.</p>"
			+ "<h2>Wizard steps</h2>"
			+ "<ul>"
			+ "<li>User enters Software Profile specific data</li>"
			+ "<li>User uploads several package related files</li>"
			+ "<li>User enters generic software info, documentation and source code/binary URLs</li>"
			+ "<li>User specifies package maintainers and software changes</li><li>User enters package installation, uninstallation and configuration notes and specifies dependencies</li>"
			+ "<li>User enters license agreement</li>"
			+ "<li>User reviews XML Software Profile and generated files and submits Software Archive to the platform.</li>"
			+ "</ul>" + "<h2>Requirements</h2>" + "<ul>"
			+ "<li>User must be working on an scope infrastructure different from 'gcube'</li>" + "</ul>";

	private static final String DEFAULT_SERVICE_CLASS = "External";

	private static final String DEFAULT_ARTIFACT_GROUPID = "org.gcube.External";

	private static final List<String> availableInfras = Lists.newArrayList(ASLSessionManager.D4SCIENCE_INFRASTRUCTURE);

	@Inject
	private IMavenRepositoryIS mavenRepositoryIS;

	@Inject
	private ISoftwareGatewayRegistrationManager sgRegistrationManager;

	@Inject
	private FileManager fileManager;

	private ScopeAvailable scopeAvailabilityDelegate = new ConfigurableScopeAvailable(availableInfras);

	public boolean isAvailableForScope(String scope) {
		return scopeAvailabilityDelegate.isAvailableForScope(scope);
	}

	@Override
	public ServiceProfile generateInitialSoftwareProfile() {
		ServiceProfile profile = new ServiceProfile();

		ArrayList<FileType> allowedFileTypes = new ArrayList<FileType>();
		allowedFileTypes.add(new SoftwareTarballFileType());
		allowedFileTypes.add(new InstallScriptFileType(true));
		allowedFileTypes.add(new UninstallScriptFileType(true));
		allowedFileTypes.add(new RebootScriptFileType(false));
		Package pack = new Package(PackageType.Software, allowedFileTypes);
		profile.getService().getPackages().add(pack);

		return profile;
	}

	@Override
	public ISoftwareTypeInfo getSoftwareTypeInfo() {
		return new SoftwareTypeInfo(CODE, NAME, DESCRIPTION);
	}

	@Override
	public MavenCoordinates getMavenCoordinates(Package softwarePackage) throws Exception {
		String artifactId = getServiceName();
		String groupId = DEFAULT_ARTIFACT_GROUPID;
		String version = getImportSession().getServiceProfile().getService().getPackages().get(0).getData()
				.getVersion().toString();
		return new MavenCoordinates(groupId, artifactId, version, "tar.gz");
	}

	@Override
	protected IMavenRepositoryInfo getTargetRepository() throws Exception {
		return mavenRepositoryIS.getMavenRepository(IMavenRepositoryIS.EXTERNALS_REPO_ID);
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
	// softwarePackage.setVersion(mainSoftwarePackageData.getVersion()
	// .toString());
	//
	// softwarePackage.setMavenCoordinates(getMavenCoordinates(mainPackage)
	// .getGroupId(),
	// getMavenCoordinates(mainPackage).getArtifactId(),
	// getMavenCoordinates(mainPackage).getVersion());
	//
	// softwarePackage.setMandatoryLevel(ScopeLevel.NONE);
	//
	// softwarePackage.setType(Type.application);
	//
	// for (SoftwareFile file : mainPackage.getFilesContainer().getFiles()) {
	// softwarePackage.getFiles().add(file.getFilename());
	// }
	//
	// /** Set scripts **/
	// List<SoftwareFile> scripts;
	//
	// // Install
	// scripts = mainPackage.getFilesContainer().getFilesWithFileType(
	// InstallScriptFileType.NAME);
	// softwarePackage.setInstallScripts(new ArrayList<String>(Collections2
	// .transform(scripts, new ScriptTransformFunction())));
	//
	// // Uninstall
	// scripts = mainPackage.getFilesContainer().getFilesWithFileType(
	// UninstallScriptFileType.NAME);
	// softwarePackage.setUninstallScripts(new ArrayList<String>(Collections2
	// .transform(scripts, new ScriptTransformFunction())));
	//
	// // Reboot
	// scripts = mainPackage.getFilesContainer().getFilesWithFileType(
	// RebootScriptFileType.NAME);
	// softwarePackage.setRebootScripts(new ArrayList<String>(Collections2
	// .transform(scripts, new ScriptTransformFunction())));
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

	@Override
	public String getServiceProfile(boolean withHeader) throws Exception {
		Software softwareProfile = new Software();
		softwareProfile.newProfile().softwareName(getServiceName());
		softwareProfile.profile().description(getServiceDescription());
		softwareProfile.profile().softwareClass(DEFAULT_SERVICE_CLASS);

		Group<SoftwarePackage<?>> packages = softwareProfile.profile().packages();

		GenericPackage genericPackage = packages.add(GenericPackage.class);
		Package mainPackage = getImportSession().getServiceProfile().getService().getPackages().get(0);
		PackageData mainSoftwarePackageData = mainPackage.getData();
		genericPackage.name(mainSoftwarePackageData.getName());
		genericPackage.description(mainSoftwarePackageData.getDescription());
		genericPackage.version(mainSoftwarePackageData.getVersion().toString());

		org.gcube.common.resources.gcore.Software.Profile.SoftwarePackage.MavenCoordinates mavenCoordinates = genericPackage
				.newCoordinates();
		mavenCoordinates.artifactId(getMavenCoordinates(mainPackage).getArtifactId());
		mavenCoordinates.groupId(getMavenCoordinates(mainPackage).getGroupId());
		mavenCoordinates.version(getMavenCoordinates(mainPackage).getVersion());

		genericPackage.newMandatory(); // Set to Scope.NONE

		genericPackage.type(Type.application);

		Collection<String> files = genericPackage.files();
		for (SoftwareFile file : mainPackage.getFilesContainer().getFiles())
			files.add(file.getFilename());

		Collection<String> installScripts = genericPackage.installScripts();
		for (SoftwareFile file : mainPackage.getFilesContainer().getFilesWithFileType(InstallScriptFileType.NAME))
			installScripts.add(file.getFilename());

		Collection<String> uninstallScripts = genericPackage.uninstallScripts();
		for (SoftwareFile file : mainPackage.getFilesContainer().getFilesWithFileType(UninstallScriptFileType.NAME))
			uninstallScripts.add(file.getFilename());

		Collection<String> rebootScripts = genericPackage.rebootScripts();
		for (SoftwareFile file : mainPackage.getFilesContainer().getFilesWithFileType(RebootScriptFileType.NAME))
			rebootScripts.add(file.getFilename());

		return SerializationUtil.serialize(softwareProfile);
	}

	public String getServiceDescription() {
		return getImportSession().getServiceProfile().getService().getData().getDescription();
	}

	public String getServiceName() {
		return getImportSession().getServiceProfile().getService().getData().getName();
	}

	@Override
	protected ISoftwareSubmissionTask createSofwareSubmissionTask() {
		try {
			ISoftwareSubmissionTask task = new AnySoftwareSubmissionTask();
			task.setTargetRepository(getTargetRepository());
			return task;
		} catch (Exception ex) {
			log.error("Error occurred while creating software submission task.", ex);
			return null;
		}
	}

	private class AnySoftwareSubmissionTask implements ISoftwareSubmissionTask {

		private IOperationProgress operationProgress = new OperationProgress();
		private IMavenRepositoryInfo targetRepository;

		@Override
		public void run() {
			File serviceArchiveFile = null;
			File serviceArchivePomFile = null;
			try {
				log.debug("Starting software deployment");

				// Create service archive
				operationProgress.setProgress(100, 25);
				operationProgress.setDetails("Creating Service Archive...");

				log.trace("Creating Service Archive...");
				serviceArchiveFile = fileManager.createServiveArchive(getServiceProfile(true), getMiscFiles(),
						getImportSession().getServiceProfile());

				log.trace("Creating service archive POM file...");
				serviceArchivePomFile = fileManager.createPomFile(getPOM(getImportSession().getServiceProfile()));

				// Deploy service archive
				log.trace("Deploying Service Archive...");
				operationProgress.setProgress(100, 50);
				operationProgress.setDetails("Deploying Service Archive...");

				log.trace("Deploying service archive on maven repository " + targetRepository.getId());
				mavenDeployer.deploy(targetRepository, serviceArchiveFile, serviceArchivePomFile, false,
						SERVICEARCHIVE_CLASSIFIER);

				// Register Profile

				operationProgress.setProgress(100, 75);
				operationProgress.setDetails("Registering Service Profile...");

				log.trace("Registering Service Profile on software gateway...");
				sgRegistrationManager.registerProfile(getServiceProfile(true), getImportSession().getScope());

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
				if (serviceArchiveFile != null)
					serviceArchiveFile.delete();
				if (serviceArchivePomFile != null)
					serviceArchivePomFile.delete();
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

}
