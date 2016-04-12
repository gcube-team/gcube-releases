package org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.gcube.common.resources.gcore.Resources;
import org.gcube.common.resources.gcore.Software;
import org.gcube.common.resources.gcore.Software.Profile.GenericPackage;
import org.gcube.common.resources.gcore.Software.Profile.GenericPackage.Type;
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
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.registrationmanagers.ISoftwareSubmissionManager;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.registrationmanagers.ISoftwareSubmissionTask;
import org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.softwaregateway.ISoftwareGatewayRegistrationManager;
import org.gcube.portlets.admin.software_upload_wizard.server.softwareprofile.Package;
import org.gcube.portlets.admin.software_upload_wizard.server.softwareprofile.ServiceProfile;
import org.gcube.portlets.admin.software_upload_wizard.server.util.ScopeUtil;
import org.gcube.portlets.admin.software_upload_wizard.shared.IOperationProgress;
import org.gcube.portlets.admin.software_upload_wizard.shared.OperationProgress;
import org.gcube.portlets.admin.software_upload_wizard.shared.OperationState;
import org.gcube.portlets.admin.software_upload_wizard.shared.filetypes.FileType;
import org.gcube.portlets.admin.software_upload_wizard.shared.filetypes.InstallScriptFileType;
import org.gcube.portlets.admin.software_upload_wizard.shared.filetypes.MiscFileType;
import org.gcube.portlets.admin.software_upload_wizard.shared.filetypes.RebootScriptFileType;
import org.gcube.portlets.admin.software_upload_wizard.shared.filetypes.UninstallScriptFileType;
import org.gcube.portlets.admin.software_upload_wizard.shared.filetypes.WarFileType;
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

import com.google.inject.Inject;

public class WebAppSoftwareManager extends AbstractSoftwareManager implements ISoftwareSubmissionManager {

	
	private Logger log = LoggerFactory.getLogger(WebAppSoftwareManager.class);

	@Inject
	IMavenDeployer mavenDeployer;

	@Inject
	private IMavenRepositoryIS mavenRepositoryIS;

	@Inject
	private FileManager fileManager;

	@Inject
	private ISoftwareGatewayRegistrationManager sgRegistrationManager;

	private static final SoftwareTypeCode CODE = SoftwareTypeCode.WebApp;
	private static final String NAME = "Web Application";
	private static final String DESCRIPTION = "<h1>Web Application</h1>"
			+ "<p>A java application utilizing web browser technologies to accomplish one or more tasks over a network, through a web browser. Third party applications are supported.</p>"
			+ "<p>The user provided war archive and the generated Service Archive will be registered on a gCube Maven repository. A Service Profile with a single service package will be created and registered on the Software Gateway.</p>"
			+ "<h2>Wizard steps</h2>"
			+ "<ul>"
			+ "<li>User enters Service Profile data</li>"
			+ "<li>User uploads several package related files</li>"
			+ "<li>User enters generic software info, documentation and source code/binary URLs</li>"
			+ "<li>User specifies package maintainers and software changes</li>"
			+ "<li>User enters package installation, uninstallation and configuration notes and specifies dependencies</li>"
			+ "<li>User enters license agreement</li>"
			+ "<li>User reviews XML Service Profile and generated deliverables and submits the software to the platform.</li>"
			+ "</ul>" + "<h2>Requirements</h2>" + "<ul>"
			+ "<li>The web application must be compatible with Tomcat 6.0 platform</li>"
			+ "<li>The web application must be compatible with JRE 1.6</li>" + "</ul>";

	public static final String CLASS = "WebApp";

	@Override
	public ServiceProfile generateInitialSoftwareProfile() {
		ServiceProfile profile = new ServiceProfile();
		// profile.getService().getData().setClazz("WebApp");

		ArrayList<FileType> allowedFileTypes = new ArrayList<FileType>();
		allowedFileTypes.add(new WarFileType(false, true));
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

	// @Override
	// public String getPOM(Package softwarePackage) throws Exception {
	// ArrayList<Package> packages = getImportSession().getServiceProfile()
	// .getService().getPackages();
	// if (!(softwarePackage == packages.get(0)))
	// throw new Exception(
	// "Cannot accept package for POM creation, invalid package");
	//
	// StringBuilder stringBuilder = new StringBuilder();
	// stringBuilder
	// .append("<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n");
	// stringBuilder.append("\t<modelVersion>4.0.0</modelVersion>\n");
	// stringBuilder.append("\t<groupId>"
	// + getMavenCoordinates(softwarePackage).getGroupId()
	// + "</groupId>\n");
	// stringBuilder.append("\t<artifactId>"
	// + getMavenCoordinates(softwarePackage).getArtifactId()
	// + "</artifactId>\n");
	// stringBuilder.append("\t<version>"
	// + getMavenCoordinates(softwarePackage).getVersion()
	// + "</version>\n");
	// stringBuilder.append("\t<packaging>war</packaging>\n");
	// if (softwarePackage.getMavenDependencies().size() > 0) {
	// stringBuilder.append("\t<dependencies>\n");
	// for (MavenCoordinates dep : softwarePackage.getMavenDependencies()) {
	// stringBuilder.append("\t\t<dependency>\n");
	// stringBuilder.append("\t\t\t<groupId>" + dep.getGroupId()
	// + "</groupId>\n");
	// stringBuilder.append("\t\t\t<artifactId>" + dep.getArtifactId()
	// + "</artifactId>\n");
	// stringBuilder.append("\t\t\t<version>" + dep.getVersion()
	// + "</version>\n");
	// stringBuilder.append("\t\t\t<type>war</type>\n");
	// stringBuilder.append("\t\t</dependency>\n");
	// }
	// stringBuilder.append("\t</dependencies>\n");
	// }
	// stringBuilder.append("</project>");
	// return stringBuilder.toString();
	//
	// }

	@Override
	public MavenCoordinates getMavenCoordinates(Package softwarePackage) throws Exception {
		PackageData mainPackage = softwarePackage.getData();

		String artifactId = mainPackage.getName().toLowerCase();
		String version = mainPackage.getVersion().toString();

		// third party software
		if (getImportSession().getServiceProfile().isThirdPartySoftware())
			return new MavenCoordinates("org.gcube.externals", artifactId, version, "war");

		// gcube infrastructure

		if (ScopeUtil.getInfrastructure(getImportSession().getScope()).equals(ASLSessionManager.GCUBE_INFRASTRUCTURE))
			return new MavenCoordinates("org.gcube.webapps", artifactId, version + SNAPSHOT_SUFFIX, "war");

		// d4science infrastructure
		if (ScopeUtil.getInfrastructure(getImportSession().getScope()).equals(
				ASLSessionManager.D4SCIENCE_INFRASTRUCTURE))
			return new MavenCoordinates("org.gcube.webapps", artifactId, version, "war");

		throw new Exception("Unmanaged scope infrastructure");
	}
	
	@Override
	public String getServiceProfile(boolean withHeader) throws Exception {
		log.debug("Generating service profile for webapp");
		ServiceData serviceData = getImportSession().getServiceProfile().getService().getData();
		Package mainSoftwarePackage = getImportSession().getServiceProfile().getService().getPackages().get(0);
		PackageData mainSoftwarePackageData = mainSoftwarePackage.getData();
		

		Software softwareProfile = new Software();
		softwareProfile.newProfile();
		softwareProfile.profile().softwareName(serviceData.getName());
		softwareProfile.profile().description(serviceData.getDescription());
		softwareProfile.profile().softwareClass(serviceData.getClazz());
		//Version cannot be set with new model
		
		
		Group<SoftwarePackage<?>> packages = softwareProfile.profile().packages();
		GenericPackage genericPackage = packages.add(GenericPackage.class);
		genericPackage.name(mainSoftwarePackageData.getName());
		genericPackage.description(mainSoftwarePackageData.getDescription());
		
		if(getImportSession().getServiceProfile().isThirdPartySoftware())
			genericPackage.version(mainSoftwarePackageData.getVersion().toString());
		else if (getScopeInfrastructure().equals(ASLSessionManager.GCUBE_INFRASTRUCTURE))
			genericPackage.version(mainSoftwarePackageData.getVersion().toString() + SNAPSHOT_SUFFIX);
		 else if(getScopeInfrastructure().equals(ASLSessionManager.D4SCIENCE_INFRASTRUCTURE))
				genericPackage.version(mainSoftwarePackageData.getVersion().toString());
		else
			throw new RuntimeException("Unmanaged scope infrastructure");
		
		org.gcube.common.resources.gcore.Software.Profile.SoftwarePackage.MavenCoordinates mavenCoordinates = genericPackage
				.newCoordinates();
		
		mavenCoordinates.artifactId(getMavenCoordinates(mainSoftwarePackage).getArtifactId());
		mavenCoordinates.groupId(getMavenCoordinates(mainSoftwarePackage).getGroupId());
		mavenCoordinates.version(getMavenCoordinates(mainSoftwarePackage).getVersion());
		
		genericPackage.newTargetPlatform().name("Tomcat");
		genericPackage.targetPlatform().version((short)6);
		genericPackage.targetPlatform().minorVersion((short)0);
		genericPackage.multiVersion(true);
		
		genericPackage.newMandatory();
		
		Requirement ghnRequirement = new Requirement();
		ghnRequirement.category("Site");
		ghnRequirement.operator(OpType.ge);
		ghnRequirement.requirement("string");
		ghnRequirement.value("java1.6");
		genericPackage.ghnRequirements().add(ghnRequirement);
		
		genericPackage.type(Type.webapplication);
		
		genericPackage.entryPoints().addAll(mainSoftwarePackageData.getEntrypoints());
		
		Collection<String> files = genericPackage.files();
		for (SoftwareFile file : mainSoftwarePackage.getFilesContainer().getFiles())
			files.add(file.getFilename());

		Collection<String> installScripts = genericPackage.installScripts();
		for (SoftwareFile file : mainSoftwarePackage.getFilesContainer().getFilesWithFileType(
				 InstallScriptFileType.NAME))
			installScripts.add(file.getFilename());
		
		Collection<String> uninstallScripts = genericPackage.uninstallScripts();
		for (SoftwareFile file : mainSoftwarePackage.getFilesContainer().getFilesWithFileType(
				 UninstallScriptFileType.NAME))
			uninstallScripts.add(file.getFilename());
		
		Collection<String> rebootScripts = genericPackage.rebootScripts();
		for (SoftwareFile file : mainSoftwarePackage.getFilesContainer().getFilesWithFileType(
				 RebootScriptFileType.NAME))
			rebootScripts.add(file.getFilename());

		ByteArrayOutputStream baos = Resources.marshal(softwareProfile, new ByteArrayOutputStream());
		String profile = baos.toString();
		log.debug("Generated profile:\n" + profile);
		return profile;
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
	public boolean isAvailableForScope(String scope) {
		return true;
	}

	@Override
	protected ISoftwareSubmissionTask createSofwareSubmissionTask() {
		try {
			ISoftwareSubmissionTask task = new WebAppSubmissionTask();
			task.setTargetRepository(getTargetRepository());
			return task;
		} catch (Exception ex) {
			log.error("Error occurred while creating software submission task.", ex);
			return null;
		}
	}

	private class WebAppSubmissionTask implements ISoftwareSubmissionTask {

		private IOperationProgress operationProgress = new OperationProgress();
		private IMavenRepositoryInfo targetRepository;

		@Override
		public void run() {
			File serviceArchiveFile = null;
			File primaryArtifactPomFile = null;
			File serviceArchivePomFile = null;
			try {
				log.debug("Starting software deployment");
				// Deploy primary artifact
				operationProgress.setProgress(100, 0);
				operationProgress.setDetails("Creating primary artifact POM...");

				log.debug("Creating primary artifact POM...");
				primaryArtifactPomFile = fileManager.createPomFile(getPOM(getImportSession().getServiceProfile()
						.getService().getPackages().get(0)));

				// Deploy service archive
				operationProgress.setProgress(100, 25);
				operationProgress.setDetails("Creating Service Archive...");

				serviceArchiveFile = fileManager.createServiveArchive(getServiceProfile(true), getMiscFiles(),
						getImportSession().getServiceProfile());

				// Publish artifacts
				operationProgress.setProgress(100, 50);
				operationProgress.setDetails("Publishing artifacts...");

				log.debug("Deploying artifacts on maven repository " + targetRepository.getId());
				if (getImportSession().getServiceProfile().getService().getPackages().size() != 1) throw new Exception("Service has an invalid number of packages: " + getImportSession().getServiceProfile().getService().getPackages().size() );
				if (getImportSession().getServiceProfile().getService().getPackages().get(0).getFilesContainer().getFilesWithFileType(WarFileType.NAME).size() <= 0) throw new Exception("No Web Archives were provided for the Service package");
				File primaryArtifact = getImportSession().getServiceProfile().getService().getPackages().get(0)
						.getFilesContainer().getFilesWithFileType(WarFileType.NAME).get(0).getFile();
				PrimaryArtifactAttachment serviceArchiveAttachment = new PrimaryArtifactAttachment(serviceArchiveFile,
						SERVICEARCHIVE_CLASSIFIER, SERVICEARCHIVE_TYPE);

				mavenDeployer.deploy(targetRepository, primaryArtifact, primaryArtifactPomFile,
						serviceArchiveAttachment);

				// Register Profile
				operationProgress.setProgress(100, 75);
				operationProgress.setDetails("Registering Service Profile...");

				log.trace("Registering Service Profile on software gateway...");
				sgRegistrationManager.registerProfile(getServiceProfile(true), getImportSession().getScope());

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
				if (primaryArtifactPomFile != null)
					primaryArtifactPomFile.delete();
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
