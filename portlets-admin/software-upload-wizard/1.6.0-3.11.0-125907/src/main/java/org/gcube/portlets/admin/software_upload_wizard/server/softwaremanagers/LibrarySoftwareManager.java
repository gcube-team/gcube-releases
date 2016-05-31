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
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.filesmanager.JarArchiveManager;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.maven.deploy.IMavenDeployer;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.maven.deploy.PrimaryArtifactAttachment;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.maven.is.IMavenRepositoryIS;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.registrationmanagers.ISoftwareSubmissionTask;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.scope.ConfigurableScopeAvailable;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.scope.ScopeAvailable;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.softwaregateway.ISoftwareGatewayRegistrationManager;
import org.gcube.portlets.admin.software_upload_wizard.server.softwareprofile.Package;
import org.gcube.portlets.admin.software_upload_wizard.server.softwareprofile.Service;
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

public class LibrarySoftwareManager extends AbstractSoftwareManager {

	private static final Logger log = LoggerFactory.getLogger(LibrarySoftwareManager.class);

	@Inject
	IMavenDeployer mavenDeployer;

	@Inject
	private IMavenRepositoryIS mavenRepositoryIS;

	@Inject
	private ISoftwareGatewayRegistrationManager sgRegistrationManager;

	@Inject
	private FileManager fileManager;

	private static final String THIRD_PARTY_SOFTWARE_CLASS = "externals";

	private static final SoftwareTypeCode CODE = SoftwareTypeCode.Library;

	private static final List<String> availableInfraScopes = Lists.newArrayList(
			ASLSessionManager.D4SCIENCE_INFRASTRUCTURE, ASLSessionManager.GCUBE_INFRASTRUCTURE);

	private ScopeAvailable scopeAvailableDelegate = new ConfigurableScopeAvailable(availableInfraScopes);

	private static final String NAME = "Library";
	// TODO Remove inline of variables
	private static String DESCRIPTION = "<h1>Library</h1>"
			+ "<p>A collection of reusable java software packages. Third party libraries are supported.</p>"
			+ "<p>A Software Archive will be created and registered on a gCube Maven repository along with the provided library jars. A Service Profile with a single service package will be created and registered on the Software Gateway. </p>"
			+ "<h2>Wizard steps</h2>"
			+ "<ul>"
			+ "<li>User enters Service Profile data</li>"
			+ "<li>User uploads several package related files</li>"
			+ "<li>User enters generic software info, documentation and source code/binary URLs</li>"
			+ "<li>User specifies package maintainers and software changes</li>"
			+ "<li>User enters package installation, uninstallation and configuration notes and specifies dependencies</li>"
			+ "<li>User enters license agreement</li>"
			+ "<li>User reviews XML Service Profile and generated deliverables and submits the software to the platform.</li>"
			+ "</ul>"
			+ "<h2>Requirements</h2>"
			+ "<ul>"
			+ "<li>The library must be compatible with JRE 1.6</li>"
			+ "</ul>"
			+ "<h2>Disclaimer</h2>"
			+ "<p>If multiple jar library files are provided, those archives will be repackaged into a single jar, thereby losing any signature on the original jar files.</p> ";

	@Override
	public ServiceProfile generateInitialSoftwareProfile() {
		ServiceProfile profile = new ServiceProfile();

		ArrayList<FileType> allowedFileTypes = new ArrayList<FileType>();
		allowedFileTypes.add(new JarFileType(true, true));
		allowedFileTypes.add(new InstallScriptFileType());
		allowedFileTypes.add(new UninstallScriptFileType());
		allowedFileTypes.add(new MiscFileType(false));
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
		Service service = getImportSession().getServiceProfile().getService();
		ServiceData serviceData = service.getData();
		PackageData mainPackageData = softwarePackage.getData();

		String artifactId = mainPackageData.getName().toLowerCase();
		String version = mainPackageData.getVersion().toString();

		// third party software
		if (getImportSession().getServiceProfile().isThirdPartySoftware())
			return new MavenCoordinates("org.gcube." + THIRD_PARTY_SOFTWARE_CLASS, artifactId, version);

		String normalizedServiceClass = serviceData.getClazz().toLowerCase().replaceAll("-", "").replaceAll("\\.", "");
		// gcube infrastructure
		if (getScopeInfrastructure().equals(ASLSessionManager.GCUBE_INFRASTRUCTURE))
			return new MavenCoordinates("org.gcube." + normalizedServiceClass, artifactId, version + SNAPSHOT_SUFFIX);

		// d4science infrastructure
		if (getScopeInfrastructure().equals(ASLSessionManager.D4SCIENCE_INFRASTRUCTURE))
			return new MavenCoordinates("org.gcube." + normalizedServiceClass, artifactId, version);

		throw new Exception("Unmanaged scope infrastructure");
	}

	@Override
	public String getServiceProfile(boolean withHeader) throws Exception {
		Service service = getImportSession().getServiceProfile().getService();
		ServiceData serviceData = service.getData();
		Package mainPackage = service.getPackages().get(0);
		PackageData mainSoftwarePackageData = mainPackage.getData();

		Software softwareProfile = new Software();
		softwareProfile.newProfile().softwareName(serviceData.getName());
		softwareProfile.profile().description(serviceData.getDescription());
		softwareProfile.profile().softwareClass(serviceData.getClazz());

		Group<SoftwarePackage<?>> packages = softwareProfile.profile().packages();
		GenericPackage softwarePackage = packages.add(GenericPackage.class);

		softwarePackage.name(mainSoftwarePackageData.getName());
		softwarePackage.description(mainSoftwarePackageData.getDescription());
		if (getImportSession().getServiceProfile().isThirdPartySoftware())
			softwarePackage.version(mainSoftwarePackageData.getVersion().toString());
		else if (getScopeInfrastructure().equals(ASLSessionManager.GCUBE_INFRASTRUCTURE))
			softwarePackage.version(mainSoftwarePackageData.getVersion().toString() + SNAPSHOT_SUFFIX);
		else if (getScopeInfrastructure().equals(ASLSessionManager.D4SCIENCE_INFRASTRUCTURE))
			softwarePackage.version(mainSoftwarePackageData.getVersion().toString());
		else
			throw new Exception("Unmanaged scope infrastructure");

		org.gcube.common.resources.gcore.Software.Profile.SoftwarePackage.MavenCoordinates mavenCoordinates = softwarePackage
				.newCoordinates();
		mavenCoordinates.artifactId(getMavenCoordinates(mainPackage).getArtifactId());
		mavenCoordinates.groupId(getMavenCoordinates(mainPackage).getGroupId());
		mavenCoordinates.version(getMavenCoordinates(mainPackage).getVersion());

		softwarePackage.newMandatory(); // Set to Scope.NONE

		softwarePackage.type(Type.library);

		Collection<String> files = softwarePackage.files();
		for (SoftwareFile file : mainPackage.getFilesContainer().getFiles())
			files.add(file.getFilename());

		Collection<String> installScripts = softwarePackage.installScripts();
		for (SoftwareFile file : mainPackage.getFilesContainer().getFilesWithFileType(InstallScriptFileType.NAME))
			installScripts.add(file.getFilename());

		Collection<String> uninstallScripts = softwarePackage.uninstallScripts();
		for (SoftwareFile file : mainPackage.getFilesContainer().getFilesWithFileType(UninstallScriptFileType.NAME))
			uninstallScripts.add(file.getFilename());

		Collection<String> rebootScripts = softwarePackage.rebootScripts();
		for (SoftwareFile file : mainPackage.getFilesContainer().getFilesWithFileType(RebootScriptFileType.NAME))
			rebootScripts.add(file.getFilename());

		return SerializationUtil.serialize(softwareProfile);
	}

	@Override
	protected IMavenRepositoryInfo getTargetRepository() throws Exception {
		if (getImportSession().getServiceProfile().isThirdPartySoftware())
			return mavenRepositoryIS.getMavenRepository(IMavenRepositoryIS.EXTERNALS_REPO_ID);
		else {
			if (getScopeInfrastructure().equals(ASLSessionManager.D4SCIENCE_INFRASTRUCTURE))
				return mavenRepositoryIS.getMavenRepository(IMavenRepositoryIS.RELEASES_REPO_ID);
			if (getScopeInfrastructure().equals(ASLSessionManager.GCUBE_INFRASTRUCTURE))
				return mavenRepositoryIS.getMavenRepository(IMavenRepositoryIS.SNAPSHOTS_REPO_ID);
		}
		throw new Exception("Unmanaged scope infrastructure");
	}

	@Override
	protected ISoftwareSubmissionTask createSofwareSubmissionTask() {
		try {
			ISoftwareSubmissionTask task = new LibrarySubmissionTask();
			task.setTargetRepository(getTargetRepository());
			return task;
		} catch (Exception ex) {
			log.error("Error occurred while creating software submission task.", ex);
			return null;
		}
	}

	public boolean isAvailableForScope(String scope) {
		return scopeAvailableDelegate.isAvailableForScope(scope);
	}

	private class LibrarySubmissionTask implements ISoftwareSubmissionTask {

		private IOperationProgress operationProgress = new OperationProgress();

		@Override
		public void run() {
			File serviceArchiveFile = null;
			File primaryArtifactPomFile = null;
			File primaryArtifactFile = null;
			File serviceArchivePomFile = null;
			File repackagedJarFile = null;
			try {
				log.debug("Starting software deployment");

				Package mainPackage = getImportSession().getServiceProfile().getService().getPackages().get(0);
				List<SoftwareFile> jarSoftwareFiles = mainPackage.getFilesContainer().getFilesWithFileType(
						JarFileType.NAME);

				operationProgress.setProgress(100, 0);
				operationProgress.setDetails("Creating primary artifact and POM...");

				if (jarSoftwareFiles.size() == 1) {
					// If a single jar is provided

					primaryArtifactPomFile = fileManager.createPomFile(getPOM(mainPackage));
					log.debug("Primary artifact POM created");

					primaryArtifactFile = jarSoftwareFiles.get(0).getFile();
					log.debug("Got primary artifact file");

				} else if (jarSoftwareFiles.size() > 1) {
					// If multiple jars are provided
					// Repackage jars

					log.debug("Found multiple jars in package, repackaging jars...");
					ArrayList<File> jarFiles = new ArrayList<File>();
					for (SoftwareFile sf : jarSoftwareFiles) {
						jarFiles.add(sf.getFile());
					}
					primaryArtifactFile = JarArchiveManager.mergeJars(jarFiles);
					log.debug("Jars have been repackaged");

					primaryArtifactPomFile = fileManager.createPomFile(getPOM(mainPackage));
					log.debug("Primary artifact POM created");

				} else
					throw new Exception("Number of jar files in package < 0");

				// Create service archive
				operationProgress.setProgress(100, 25);
				operationProgress.setDetails("Creating Service Archive...");

				log.debug("Creating Service Archive...");
				serviceArchiveFile = fileManager.createServiveArchive(getServiceProfile(true), getMiscFiles(),
						getImportSession().getServiceProfile());
				log.debug("Service Archive created");

				// Deploy service archive
				operationProgress.setProgress(100, 50);
				operationProgress.setDetails("Deploying artifacts...");

				log.debug("Deploying artifacts on maven repository " + targetRepository.getId());
				PrimaryArtifactAttachment serviceArchiveAttachment = new PrimaryArtifactAttachment(serviceArchiveFile,
						SERVICEARCHIVE_CLASSIFIER, SERVICEARCHIVE_TYPE);
				mavenDeployer.deploy(targetRepository, primaryArtifactFile, primaryArtifactPomFile,
						serviceArchiveAttachment);
				log.debug("Artifacts deployed on maven repository");

				// Register Profile
				operationProgress.setProgress(100, 75);
				operationProgress.setDetails("Registering Service Profile...");

				log.debug("Registering Service Profile on software gateway...");
				sgRegistrationManager.registerProfile(getServiceProfile(true), getImportSession().getScope());
				log.debug("Service Profile registered on IS");

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
				if (primaryArtifactPomFile != null)
					primaryArtifactPomFile.delete();
				if (repackagedJarFile != null)
					repackagedJarFile.delete();
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

}
