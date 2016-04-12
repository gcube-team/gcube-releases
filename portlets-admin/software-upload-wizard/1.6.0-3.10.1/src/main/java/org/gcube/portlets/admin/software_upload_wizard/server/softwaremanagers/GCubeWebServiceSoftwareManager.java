package org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.gcube.common.resources.gcore.Software;
import org.gcube.common.resources.gcore.Software.Profile.GenericPackage;
import org.gcube.common.resources.gcore.Software.Profile.GenericPackage.Type;
import org.gcube.common.resources.gcore.Software.Profile.ServicePackage;
import org.gcube.common.resources.gcore.Software.Profile.ServicePackage.PortType;
import org.gcube.common.resources.gcore.Software.Profile.SoftwarePackage;
import org.gcube.common.resources.gcore.Software.Profile.SoftwarePackage.Requirement;
import org.gcube.common.resources.gcore.Software.Profile.SoftwarePackage.Requirement.OpType;
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
import org.gcube.portlets.admin.software_upload_wizard.server.util.ScopeUtil;
import org.gcube.portlets.admin.software_upload_wizard.server.util.SerializationUtil;
import org.gcube.portlets.admin.software_upload_wizard.shared.IOperationProgress;
import org.gcube.portlets.admin.software_upload_wizard.shared.OperationProgress;
import org.gcube.portlets.admin.software_upload_wizard.shared.OperationState;
import org.gcube.portlets.admin.software_upload_wizard.shared.filetypes.FileType;
import org.gcube.portlets.admin.software_upload_wizard.shared.filetypes.GarFileType;
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

public class GCubeWebServiceSoftwareManager extends AbstractSoftwareManager {

	private static final Logger log = LoggerFactory.getLogger(GCubeWebServiceSoftwareManager.class);

	@Inject
	IMavenDeployer mavenDeployer;

	private static final SoftwareTypeCode CODE = SoftwareTypeCode.gCubeWebService;

	private static final String NAME = "gCube Web Service";

	private static final String DESCRIPTION = "<h1>gCube Web Service</h1>"
			+ "<p>A Web Service of the GCube Infrastructure.</p>"
			+ "<p>Java archives provided for the Main and Stubs packages will be uploaded on a maven repository with a generated Service Archive. A Service Profile with will be created and registered on the Software Gateway.</p>"
			+ "<h2>Wizard steps</h2>"
			+ "<ul>"
			+ "<li>User enters Service Profile data related to the Service</li>"
			+ "<li>User enters Service Profile data related to the Main Package</li>"
			+ "<li>User uploads a GAR and other optional files related to the Main Package</li>"
			+ "<li>User edits maven coordinates related to the Main Package artifact</li>"
			+ "<li>User edits maven dependencies related to the Main Package artifact</li>"
			+ "<li>User enters Service Profile data related to the Stubs Package</li>"
			+ "<li>User uploads a Jar archive related to the Stubs Package</li>"
			+ "<li>User edits maven coordinates related to the Stubs Package artifact</li>"
			+ "<li>User edits maven dependencies related to the Stubs Package artifact</li>"
			+ "<li>User enters generic software info, documentation and source code/binary URLs</li>"
			+ "<li>User specifies package maintainers and software changes</li>"
			+ "<li>User enters package installation, uninstallation and configuration notes and specifies dependencies</li>"
			+ "<li>User enters license agreement</li>"
			+ "<li>User reviews XML Service Profile and generated deliverables and submits the software to the platform.</li>"
			+ "</ul>";

	private static final String GROUPID_PREFIX = "org.gcube.";

	private static final List<String> availableInfras = Lists.newArrayList(ASLSessionManager.GCUBE_INFRASTRUCTURE,
			ASLSessionManager.GCUBE_INFRASTRUCTURE);

	@Inject
	private IMavenRepositoryIS mavenRepositoryIS;

	@Inject
	private FileManager fileManager;

	@Inject
	private ISoftwareGatewayRegistrationManager sgRegistrationManager;

	private ScopeAvailable scopeAvailabilityDelegate = new ConfigurableScopeAvailable(availableInfras);

	public ScopeAvailable getScopeAvailabilityDelegate() {
		return scopeAvailabilityDelegate;
	}

	public void setScopeAvailabilityDelegate(ScopeAvailable scopeAvailabilityDelegate) {
		this.scopeAvailabilityDelegate = scopeAvailabilityDelegate;
	}

	@Override
	public ServiceProfile generateInitialSoftwareProfile() {
		ServiceProfile profile = new ServiceProfile();

		ArrayList<FileType> mainPackageAllowedFileTypes = new ArrayList<FileType>();
		mainPackageAllowedFileTypes.add(new GarFileType());
		mainPackageAllowedFileTypes.add(new InstallScriptFileType(false, false));
		mainPackageAllowedFileTypes.add(new UninstallScriptFileType(false, false));
		mainPackageAllowedFileTypes.add(new RebootScriptFileType(false, false));
		mainPackageAllowedFileTypes.add(new MiscFileType());
		Package mainPackage = new Package(PackageType.Main, mainPackageAllowedFileTypes);
		profile.getService().getPackages().add(mainPackage);
		ArrayList<FileType> stubsPackageAllowedFileTypes = new ArrayList<FileType>();
		stubsPackageAllowedFileTypes.add(new JarFileType());
		Package stubsPackage = new Package(PackageType.Software, stubsPackageAllowedFileTypes);
		profile.getService().getPackages().add(stubsPackage);
		return profile;
	}

	@Override
	public ISoftwareTypeInfo getSoftwareTypeInfo() {
		return new SoftwareTypeInfo(CODE, NAME, DESCRIPTION);
	}

	@Override
	public String getPOM(Package softwarePackage) throws Exception {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder
				.append("<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n");
		stringBuilder.append("\t<modelVersion>4.0.0</modelVersion>\n");
		stringBuilder.append("\t<groupId>" + getMavenCoordinates(softwarePackage).getGroupId() + "</groupId>\n");
		stringBuilder.append("\t<artifactId>" + getMavenCoordinates(softwarePackage).getArtifactId()
				+ "</artifactId>\n");
		stringBuilder.append("\t<version>" + getMavenCoordinates(softwarePackage).getVersion() + "</version>\n");
		stringBuilder.append("\t<packaging>" + getMavenCoordinates(softwarePackage).getPackaging() + "</packaging>\n");
		if (softwarePackage == getImportSession().getServiceProfile().getService().getPackages().get(0)) {
			// Add stubs package coords
			stringBuilder.append("\t<dependencies>\n");
			MavenCoordinates stubsPackageCoordinates = getImportSession().getServiceProfile().getService()
					.getPackages().get(1).getArtifactCoordinates();
			stringBuilder.append("\t\t<dependency>\n");
			stringBuilder.append("\t\t\t<groupId>" + stubsPackageCoordinates.getGroupId() + "</groupId>\n");
			stringBuilder.append("\t\t\t<artifactId>" + stubsPackageCoordinates.getArtifactId() + "</artifactId>\n");
			stringBuilder.append("\t\t\t<version>" + stubsPackageCoordinates.getVersion() + "</version>\n");
			stringBuilder.append("\t\t\t<type>" + stubsPackageCoordinates.getPackaging() + "</type>\n");
			stringBuilder.append("\t\t</dependency>\n");

			// Add other user defined deps
			if (softwarePackage.getMavenDependencies().size() > 0) {
				for (MavenCoordinates dep : softwarePackage.getMavenDependencies()) {
					stringBuilder.append("\t\t<dependency>\n");
					stringBuilder.append("\t\t\t<groupId>" + dep.getGroupId() + "</groupId>\n");
					stringBuilder.append("\t\t\t<artifactId>" + dep.getArtifactId() + "</artifactId>\n");
					stringBuilder.append("\t\t\t<version>" + dep.getVersion() + "</version>\n");
					stringBuilder.append("\t\t\t<type>jar</type>\n");
					stringBuilder.append("\t\t</dependency>\n");
				}

			}
			stringBuilder.append("\t</dependencies>\n");
		} else {
			if (softwarePackage.getMavenDependencies().size() > 0) {
				stringBuilder.append("\t<dependencies>\n");
				for (MavenCoordinates dep : softwarePackage.getMavenDependencies()) {
					stringBuilder.append("\t\t<dependency>\n");
					stringBuilder.append("\t\t\t<groupId>" + dep.getGroupId() + "</groupId>\n");
					stringBuilder.append("\t\t\t<artifactId>" + dep.getArtifactId() + "</artifactId>\n");
					stringBuilder.append("\t\t\t<version>" + dep.getVersion() + "</version>\n");
					stringBuilder.append("\t\t\t<type>jar</type>\n");
					stringBuilder.append("\t\t</dependency>\n");
				}
				stringBuilder.append("\t</dependencies>\n");
			}
		}
		stringBuilder.append("</project>");
		return stringBuilder.toString();
	}

	@Override
	public MavenCoordinates getMavenCoordinates(Package softwarePackage) throws Exception {
		if (softwarePackage.getArtifactCoordinates() != null)
			return softwarePackage.getArtifactCoordinates();

		PackageData packageData = softwarePackage.getData();

		String artifactId = packageData.getName().toLowerCase();
		String groupId = GROUPID_PREFIX + getImportSession().getServiceProfile().getService().getData().getClazz();
		String version = packageData.getVersion().toString();
		String packaging = "jar";
		if (packageData.getPackageType() == PackageType.Main) {
			packaging = "gar";
		}

		// gcube infrastructure
		if (ScopeUtil.getInfrastructure(getImportSession().getScope()).equals(ASLSessionManager.GCUBE_INFRASTRUCTURE))
			return new MavenCoordinates(groupId, artifactId, version + SNAPSHOT_SUFFIX, packaging);

		// d4science infrastructure
		if (ScopeUtil.getInfrastructure(getImportSession().getScope()).equals(
				ASLSessionManager.D4SCIENCE_INFRASTRUCTURE))
			return new MavenCoordinates(groupId, artifactId, version, packaging);

		throw new Exception("Unmanaged scope infrastructure");
	}

	// @Override
	// public String getServiceProfile(boolean withHeader) throws Exception {
	// GCUBEService gcubeService = GHNContext
	// .getImplementation(GCUBEService.class);
	//
	// ServiceData serviceData = getImportSession().getServiceProfile()
	// .getService().getData();
	//
	// // Service attributes
	// gcubeService.setServiceName(serviceData.getName());
	// gcubeService.setDescription(serviceData.getDescription());
	// gcubeService.setServiceClass(serviceData.getClazz());
	// gcubeService.setVersion(serviceData.getVersion().toString());
	//
	// /** Start of Main Package section **/
	//
	// // Main Package - Attributes
	// MainPackage gCubeMainSoftwarePackage = new MainPackage();
	// Package mainSoftwarePackage = getImportSession().getServiceProfile()
	// .getService().getPackages().get(0);
	// PackageData mainSoftwarePackageData = mainSoftwarePackage.getData();
	//
	// gCubeMainSoftwarePackage.setName(mainSoftwarePackageData.getName());
	// gCubeMainSoftwarePackage.setDescription(mainSoftwarePackageData
	// .getDescription());
	//
	// String infrastructureName = getImportSession().getScope()
	// .getInfrastructure().getName();
	// if (infrastructureName.equals(ASLSessionManager.GCUBE_INFRASTRUCTURE))
	// gCubeMainSoftwarePackage.setVersion(mainSoftwarePackageData
	// .getVersion().toString() + SNAPSHOT_SUFFIX);
	// else if (infrastructureName
	// .equals(ASLSessionManager.D4SCIENCE_INFRASTRUCTURE))
	// gCubeMainSoftwarePackage.setVersion(mainSoftwarePackageData
	// .getVersion().toString());
	// else
	// throw new Exception(
	// "Unmanaged scope infrastructure, unable to evaluate main package version");
	//
	// // Main Package - Maven coordinates
	// MavenCoordinates mavenCoordinates =
	// getMavenCoordinates(mainSoftwarePackage);
	// gCubeMainSoftwarePackage
	// .setMavenCoordinates(mavenCoordinates.getGroupId(),
	// mavenCoordinates.getArtifactId(),
	// mavenCoordinates.getVersion());
	//
	// // Main Package - Target Platform
	// PlatformDescription targetPlatform = new PlatformDescription();
	// targetPlatform.setName("Tomcat");
	// targetPlatform.setVersion((short) 6);
	// targetPlatform.setMinorVersion((short) 0);
	// gCubeMainSoftwarePackage.setTargetPlatform(targetPlatform);
	//
	// gCubeMainSoftwarePackage.setMandatoryLevel(ScopeLevel.NONE);
	//
	// // Main Package - Platform requirement
	// GHNRequirement requirement = new GHNRequirement();
	// requirement.setCategory(Category.SITE_LOCATION);
	// requirement.setOperator(OpType.GE);
	// requirement.setRequirement("string");
	// requirement.setValue("java1.6");
	// gCubeMainSoftwarePackage.setGHNRequirements(Collections
	// .singletonList(requirement));
	//
	// // Main Package - GAR Archive
	//
	// List<SoftwareFile> garFiles = mainSoftwarePackage.getFilesContainer()
	// .getFilesWithFileType(GarFileType.NAME);
	// if (garFiles.size() != 1)
	// throw new Exception("Number of GAR files is " + garFiles.size());
	// gCubeMainSoftwarePackage.setGarArchive(garFiles.get(0).getFilename());
	//
	// // Main Package - Port types
	// for
	// (org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.PortType
	// pt : mainSoftwarePackageData
	// .getPortTypes()) {
	// PortType tmp = new PortType();
	// tmp.setName(pt.getName());
	// gCubeMainSoftwarePackage.getPorttypes().add(tmp);
	// }
	//
	// // Main Package - Scripts
	// List<SoftwareFile> scripts;
	//
	// // Install
	// scripts = mainSoftwarePackage.getFilesContainer().getFilesWithFileType(
	// InstallScriptFileType.NAME);
	// gCubeMainSoftwarePackage
	// .setInstallScripts(new ArrayList<String>(Collections2
	// .transform(scripts, new ScriptTransformFunction())));
	//
	// // Uninstall
	// scripts = mainSoftwarePackage.getFilesContainer().getFilesWithFileType(
	// UninstallScriptFileType.NAME);
	// gCubeMainSoftwarePackage
	// .setUninstallScripts(new ArrayList<String>(Collections2
	// .transform(scripts, new ScriptTransformFunction())));
	//
	// // Reboot
	// scripts = mainSoftwarePackage.getFilesContainer().getFilesWithFileType(
	// RebootScriptFileType.NAME);
	// gCubeMainSoftwarePackage
	// .setRebootScripts(new ArrayList<String>(Collections2.transform(
	// scripts, new ScriptTransformFunction())));
	//
	// gcubeService.getPackages().add(gCubeMainSoftwarePackage);
	// /** End of Main Package section **/
	//
	// /** Start of Stubs Package section **/
	//
	// // Stubs Package
	// Software gCubeStubsSoftwarePackage = new Software();
	// Package stubsSoftwarePackage = getImportSession().getServiceProfile()
	// .getService().getPackages().get(1);
	// PackageData stubsSoftwarePackageData = stubsSoftwarePackage.getData();
	//
	// gCubeStubsSoftwarePackage.setName(stubsSoftwarePackageData.getName());
	// gCubeStubsSoftwarePackage.setDescription(stubsSoftwarePackageData
	// .getDescription());
	//
	// if (infrastructureName.equals(ASLSessionManager.GCUBE_INFRASTRUCTURE))
	// gCubeStubsSoftwarePackage.setVersion(stubsSoftwarePackageData
	// .getVersion().toString() + SNAPSHOT_SUFFIX);
	// else if (infrastructureName
	// .equals(ASLSessionManager.D4SCIENCE_INFRASTRUCTURE))
	// gCubeStubsSoftwarePackage.setVersion(stubsSoftwarePackageData
	// .getVersion().toString());
	// else
	// throw new Exception(
	// "Unmanaged scope infrastructure, unable to evaluate main package version");
	//
	// gCubeStubsSoftwarePackage.setType(Type.library);
	//
	// // Stubs Package - Maven coordinates
	// mavenCoordinates = getMavenCoordinates(stubsSoftwarePackage);
	// gCubeStubsSoftwarePackage
	// .setMavenCoordinates(mavenCoordinates.getGroupId(),
	// mavenCoordinates.getArtifactId(),
	// mavenCoordinates.getVersion());
	//
	// // Stubs Package - Files
	//
	// for (SoftwareFile file : stubsSoftwarePackage.getFilesContainer()
	// .getFiles()) {
	// gCubeStubsSoftwarePackage.getFiles().add(file.getFilename());
	// }
	//
	// gcubeService.getPackages().add(gCubeStubsSoftwarePackage);
	//
	// /** Additional packages section **/
	//
	// if (getImportSession().getServiceProfile().getService().getPackages()
	// .size() > 2) {
	// // For each additional package
	// for (int i = 2; i < getImportSession().getServiceProfile()
	// .getService().getPackages().size(); i++) {
	//
	// // Stubs Package
	// Software additionalStubsSoftwarePackage = new Software();
	// Package additionalSoftwarePackage = getImportSession()
	// .getServiceProfile().getService().getPackages().get(i);
	// PackageData additionalSoftwarePackageData = additionalSoftwarePackage
	// .getData();
	//
	// additionalStubsSoftwarePackage
	// .setName(additionalSoftwarePackageData.getName());
	// additionalStubsSoftwarePackage
	// .setDescription(additionalSoftwarePackageData
	// .getDescription());
	//
	// if (infrastructureName
	// .equals(ASLSessionManager.GCUBE_INFRASTRUCTURE))
	// additionalStubsSoftwarePackage
	// .setVersion(additionalSoftwarePackageData
	// .getVersion().toString() + SNAPSHOT_SUFFIX);
	// else if (infrastructureName
	// .equals(ASLSessionManager.D4SCIENCE_INFRASTRUCTURE))
	// additionalStubsSoftwarePackage
	// .setVersion(additionalSoftwarePackageData
	// .getVersion().toString());
	// else
	// throw new Exception(
	// "Unmanaged scope infrastructure, unable to evaluate main package version");
	//
	// additionalStubsSoftwarePackage.setType(Type.library);
	//
	// // Stubs Package - Maven coordinates
	// mavenCoordinates = getMavenCoordinates(additionalSoftwarePackage);
	// additionalStubsSoftwarePackage.setMavenCoordinates(
	// mavenCoordinates.getGroupId(),
	// mavenCoordinates.getArtifactId(),
	// mavenCoordinates.getVersion());
	//
	// // Stubs Package - Files
	//
	// for (SoftwareFile file : additionalSoftwarePackage
	// .getFilesContainer().getFiles()) {
	// additionalStubsSoftwarePackage.getFiles().add(
	// file.getFilename());
	// }
	//
	// gcubeService.getPackages().add(additionalStubsSoftwarePackage);
	//
	// }
	// }
	//
	// /** End of additional packages section **/
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
	protected IMavenRepositoryInfo getTargetRepository() throws Exception {
		if (getScopeInfrastructure().equals(ASLSessionManager.D4SCIENCE_INFRASTRUCTURE))
			return mavenRepositoryIS.getMavenRepository(IMavenRepositoryIS.RELEASES_REPO_ID);
		if (getScopeInfrastructure().equals(ASLSessionManager.GCUBE_INFRASTRUCTURE))
			return mavenRepositoryIS.getMavenRepository(IMavenRepositoryIS.SNAPSHOTS_REPO_ID);

		throw new Exception("Unmanaged scope infrastructure");
	}

	@Override
	protected ISoftwareSubmissionTask createSofwareSubmissionTask() {
		try {
			ISoftwareSubmissionTask task = new GCubeWebServiceSubmissionTask();
			task.setTargetRepository(getTargetRepository());
			return task;
		} catch (Exception ex) {
			log.error("Error occurred while creating software submission task.", ex);
			return null;
		}
	}

	private class GCubeWebServiceSubmissionTask implements ISoftwareSubmissionTask {

		private IOperationProgress operationProgress = new OperationProgress();
		private IMavenRepositoryInfo targetRepository;

		@Override
		public void run() {
			File primaryArtifactFile = null;
			File primaryArtifactPomFile = null;
			File serviceArchiveFile = null;
			File serviceArchivePomFile = null;
			ArrayList<File> artifactsPomFiles = new ArrayList<File>();

			try {
				log.debug("Starting software deployment");
				// Deploy primary artifact
				operationProgress.setProgress(100, 0);
				operationProgress.setDetails("Deploying stubs artifacts...");

				ArrayList<Package> packages = getImportSession().getServiceProfile().getService().getPackages();

				// Create POMs
				for (int i = 0; i < packages.size(); i++) {
					log.debug("Creating artifact POM for stub #" + i);
					artifactsPomFiles.add(fileManager.createPomFile(getPOM(getImportSession().getServiceProfile()
							.getService().getPackages().get(i))));
					log.debug("POM created for stub #" + i);
				}

				primaryArtifactFile = getImportSession().getServiceProfile().getService().getPackages().get(0)
						.getFilesContainer().getFilesWithFileType(GarFileType.NAME).get(0).getFile();
				primaryArtifactPomFile = artifactsPomFiles.get(0);

				for (int i = 1; i < packages.size(); i++) {

					log.debug("Deploying artifact #" + i + "on maven repository " + targetRepository.getId());

					mavenDeployer.deploy(targetRepository, getImportSession().getServiceProfile().getService()
							.getPackages().get(i).getFilesContainer().getFilesWithFileType(JarFileType.NAME).get(0)
							.getFile(), artifactsPomFiles.get(i), true);
				}

				// Deploy service archive
				operationProgress.setProgress(100, 25);
				operationProgress.setDetails("Creating Service Archive...");

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
				log.debug("Artifacts deployed");

				// Register Profile
				operationProgress.setProgress(100, 75);
				operationProgress.setDetails("Registering Service Profile...");

				log.debug("Registering Service Profile on software gateway...");
				sgRegistrationManager.registerProfile(getServiceProfile(true), getImportSession().getScope());
				log.debug("Service Profile registered on software gateway");

				operationProgress.setProgress(100, 100);
				operationProgress.setState(OperationState.COMPLETED);

				log.debug("Deploy completed succesfully");
			} catch (Exception e) {
				log.error("Error encountered during software submission.", e);
				operationProgress.setProgress(100, 0);
				operationProgress.setState(OperationState.FAILED);
				operationProgress.setDetails("Error encountered during software submission. " + e.getMessage());
			} finally {
				// Delete garbage
				if (serviceArchiveFile != null)
					serviceArchiveFile.delete();
				if (serviceArchivePomFile != null)
					serviceArchivePomFile.delete();
				for (int i = 0; i < artifactsPomFiles.size(); i++) {
					if (artifactsPomFiles.get(i) != null)
						artifactsPomFiles.get(i).delete();
				}
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

	@Override
	public String getServiceProfile(boolean withHeader) throws Exception {

		ServiceData serviceData = getImportSession().getServiceProfile().getService().getData();
		Package mainSoftwarePackage = getImportSession().getServiceProfile().getService().getPackages().get(0);
		PackageData mainSoftwarePackageData = mainSoftwarePackage.getData();

		Software softwareResource = new org.gcube.common.resources.gcore.Software();
		softwareResource.newProfile().softwareName(serviceData.getName());
		softwareResource.profile().description(serviceData.getDescription());
		softwareResource.profile().softwareClass(serviceData.getClazz());

		Group<SoftwarePackage<?>> packages = softwareResource.profile().packages();
		ServicePackage servicePackage = packages.add(ServicePackage.class);

		servicePackage.name(mainSoftwarePackageData.getName());
		servicePackage.description(mainSoftwarePackageData.getDescription());

		if (getScopeInfrastructure().equals(ASLSessionManager.GCUBE_INFRASTRUCTURE))
			servicePackage.version(mainSoftwarePackageData.getVersion().toString() + SNAPSHOT_SUFFIX);
		else if (getScopeInfrastructure().equals(ASLSessionManager.D4SCIENCE_INFRASTRUCTURE))
			servicePackage.version(mainSoftwarePackageData.getVersion().toString());
		else
			throw new RuntimeException("Unmanaged scope infrastructure, unable to evaluate main package version");

		MavenCoordinates mavenCoordinates = getMavenCoordinates(mainSoftwarePackage);
		servicePackage.newCoordinates().artifactId(mavenCoordinates.getArtifactId());
		servicePackage.coordinates().groupId(mavenCoordinates.getGroupId());
		servicePackage.coordinates().version(mavenCoordinates.getVersion());

		servicePackage.targetPlatform().name("Tomcat");
		servicePackage.targetPlatform().version((short) 6);
		servicePackage.targetPlatform().minorVersion((short) 0);
		servicePackage.multiVersion(true);

		servicePackage.newMandatory(); // Set Scope.NONE

		Requirement ghnRequirement = new Requirement();
		ghnRequirement.category("Site");
		ghnRequirement.operator(OpType.ge);
		ghnRequirement.requirement("string");
		ghnRequirement.value("java1.6");
		servicePackage.ghnRequirements().add(ghnRequirement);

		// Main Package - GAR Archive

		List<SoftwareFile> garFiles = mainSoftwarePackage.getFilesContainer().getFilesWithFileType(GarFileType.NAME);
		if (garFiles.size() != 1)
			throw new Exception("Number of GAR files is " + garFiles.size());
		servicePackage.archive(garFiles.get(0).getFilename());

		// Main Package - Port types
		for (org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.PortType pt : mainSoftwarePackageData
				.getPortTypes()) {
			PortType tmp = new PortType();
			tmp.name(pt.getName());
			servicePackage.portTypes().add(tmp);
		}

		// Main Package - Scripts

		// Install
		for (SoftwareFile file : mainSoftwarePackage.getFilesContainer().getFilesWithFileType(
				InstallScriptFileType.NAME))
			servicePackage.installScripts().add(file.getFilename());

		// Uninstall
		for (SoftwareFile file : mainSoftwarePackage.getFilesContainer().getFilesWithFileType(
				UninstallScriptFileType.NAME))
			servicePackage.installScripts().add(file.getFilename());

		// Reboot
		for (SoftwareFile file : mainSoftwarePackage.getFilesContainer()
				.getFilesWithFileType(RebootScriptFileType.NAME))
			servicePackage.installScripts().add(file.getFilename());

		/** End of Main Package section **/

		/** Start of Stubs Package section **/

		// Stubs Package
		Package stubsSoftwarePackage = getImportSession().getServiceProfile().getService().getPackages().get(1);
		PackageData stubsSoftwarePackageData = stubsSoftwarePackage.getData();

		GenericPackage stubPackage = packages.add(GenericPackage.class);

		stubPackage.name(stubsSoftwarePackageData.getName());
		stubPackage.description(stubsSoftwarePackageData.getDescription());

		if (getScopeInfrastructure().equals(ASLSessionManager.GCUBE_INFRASTRUCTURE))
			stubPackage.version(stubsSoftwarePackageData.getVersion().toString() + SNAPSHOT_SUFFIX);
		else if (getScopeInfrastructure().equals(ASLSessionManager.D4SCIENCE_INFRASTRUCTURE))
			stubPackage.version(stubsSoftwarePackageData.getVersion().toString());
		else
			throw new RuntimeException("Unmanaged scope infrastructure, unable to evaluate main package version");

		stubPackage.type(Type.library);

		// Stubs Package - Maven coordinates
		mavenCoordinates = getMavenCoordinates(stubsSoftwarePackage);
		stubPackage.newCoordinates().artifactId(mavenCoordinates.getArtifactId());
		stubPackage.coordinates().groupId(mavenCoordinates.getGroupId());
		stubPackage.coordinates().version(mavenCoordinates.getVersion());

		// Stubs Package - Files

		for (SoftwareFile file : stubsSoftwarePackage.getFilesContainer().getFiles()) {
			stubPackage.files().add(file.getFilename());
		}

		/** Additional packages section **/

		if (getImportSession().getServiceProfile().getService().getPackages().size() > 2) {
			// For each additional package
			for (int i = 2; i < getImportSession().getServiceProfile().getService().getPackages().size(); i++) {

				Package additionalSoftwarePackage = getImportSession().getServiceProfile().getService().getPackages()
						.get(i);
				PackageData additionalSoftwarePackageData = additionalSoftwarePackage.getData();

				// Stubs Package
				GenericPackage additionalPackage = softwareResource.profile().packages().add(GenericPackage.class);

				additionalPackage.name(additionalSoftwarePackageData.getName());
				additionalPackage.description(additionalSoftwarePackageData.getDescription());

				if (getScopeInfrastructure().equals(ASLSessionManager.GCUBE_INFRASTRUCTURE))
					additionalPackage.version(additionalSoftwarePackageData.getVersion().toString() + SNAPSHOT_SUFFIX);
				else if (getScopeInfrastructure().equals(ASLSessionManager.D4SCIENCE_INFRASTRUCTURE))
					additionalPackage.version(additionalSoftwarePackageData.getVersion().toString());
				else
					throw new RuntimeException(
							"Unmanaged scope infrastructure, unable to evaluate main package version");

				additionalPackage.type(Type.library);
				// Stubs Package - Maven coordinates

				mavenCoordinates = getMavenCoordinates(additionalSoftwarePackage);
				additionalPackage.newCoordinates().artifactId(mavenCoordinates.getArtifactId());
				additionalPackage.coordinates().groupId(mavenCoordinates.getGroupId());
				additionalPackage.coordinates().version(mavenCoordinates.getVersion());

				// Stubs Package - Files

				for (SoftwareFile file : additionalSoftwarePackage.getFilesContainer().getFiles()) {
					additionalPackage.files().add(file.getFilename());
				}
			}
			/** End of additional packages section **/
		}

		return SerializationUtil.serialize(softwareResource);
	}

}
