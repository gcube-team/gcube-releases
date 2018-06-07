package org.gcube.portlets.user.statisticalalgorithmsimporter.server.generator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.blackbox.MainGenerator;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.storage.FilesStorage;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.util.ServiceCredentials;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.Constants;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.descriptor.SAIDescriptor;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception.StatAlgoImporterServiceException;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.Project;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.ProjectCompile;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.ProjectDeploy;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.ProjectSupportBashEdit;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.ProjectSupportBlackBox;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.ProjectSupportREdit;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.ProjectTarget;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.workspace.ItemDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ProjectBuilder {
	private static final String STATISTICAL_ALGORITHM_TARGET_FOLDER_NAME = "Target";
	private static final String STATISTICAL_ALGORITHM_TARGET_FOLDER_DESCRIPTION = "Project Target";
	private static final String STATISTICAL_ALGORITHM_DEPLOY_FOLDER_NAME = "Deploy";
	private static final String STATISTICAL_ALGORITHM_DEPLOY_FOLDER_DESCRIPTION = "Project Deploy";
	private static final String STATISTICAL_ALGORITHM_COMPILE_FOLDER_NAME = "Compile";
	private static final String STATISTICAL_ALGORITHM_COMPILE_FOLDER_DESCRIPTION = "Project Compile";
	private static final String STATISTICAL_ALGORITHM_BACKUP_FOLDER_NAME = "Backup";
	private static final String STATISTICAL_ALGORITHM_BACKUP_FOLDER_DESCRIPTION = "Project Backup";

	private static final String ALGORITHM_MIMETYPE = "text/plain";
	private static final String ALGORITHM_DESCRIPTION = "Statistical Algorithm Java Code";
	private static final String ALGORITHM_EXTENTION = ".java";
	private static final String INFO_NAME = "Info";
	private static final String INFO_MIMETYPE = "text/plain";
	private static final String INFO_DESCRIPTION = "Info";
	private static final String INFO_EXTENTION = ".txt";
	private static final String PROJECT_PACKAGE_MIMETYPE = "application/zip";
	private static final String PROJECT_PACKAGE_DESCRIPTION = "Statical Algorithm Project Package";
	private static final String PROJECT_PACKAGE_EXTENTION = ".zip";
	private static final String LOG_TXT = "log.txt";
	private static final String LOG_JAR_TXT = "logjar.txt";
	private static final String ECOLOGICAL_ENGINE_JAR = "ecological-engine.jar";
	private static final String ECOLOGICAL_ENGINE_JAR_URL = "http://data.d4science.org/id?fileName=ecological-engine.jar&smp-id=56952e9ce4b0e2fd6457272c&contentType=application%2Fjava-archive";
	private static final String ECOLOGICAL_ENGINE_SMART_EXECUTOR_JAR_URL = "http://data.d4science.org/id?fileName=ecological-engine-smart-executor.jar&smp-id=56952e9ce4b0e2fd6457272e&contentType=application%2Fjava-archive";
	private static final String ECOLOGICAL_ENGINE_SMART_EXECUTOR_JAR = "ecological-engine-smart-executor.jar";

	private static final String CODE_JAR_MIMETYPE = "application/java-archive";
	private static final String CODE_JAR_DESCRIPTION = "Statistical Algorithm Jar";
	private static final String JAR_EXTENTION = ".jar";

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");

	public static final Logger logger = LoggerFactory.getLogger(ProjectBuilder.class);

	private Project project;
	private String backupFolderId;
	private ServiceCredentials serviceCredentials;
	private SAIDescriptor saiDescriptor;
	private Path algorithmJava;
	private Path infoTXT;

	public ProjectBuilder(Project project, ServiceCredentials serviceCredentials, SAIDescriptor saiDescriptor) {
		this.project = project;
		this.serviceCredentials = serviceCredentials;
		this.saiDescriptor = saiDescriptor;
	}

	public Project buildTarget() throws StatAlgoImporterServiceException {
		checkInfoForBuild();
		createShareInfo();
		createBackup();
		createMainCodeIfRequest();
		createTargetFolder();
		createDeployFolder();
		createProjectPackage();
		createCompileFolder();
		createAlgorithm();
		createIntegrationInfo();
		createProjectJarFile();
		try {
			if (algorithmJava != null)
				Files.delete(algorithmJava);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			if (infoTXT != null)
				Files.delete(infoTXT);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return project;

	}

	public Project buildDeploy() throws StatAlgoImporterServiceException {
		checkInfoForDeploy();
		copyJarInDeploy();
		return project;
	}

	public Project buildRepackage() throws StatAlgoImporterServiceException {
		checkInfoForRepackage();
		createBackupOfPackageProject();
		createMainCodeIfRequest();
		repackageProjectPackage();
		return project;
	}

	private void createBackup() throws StatAlgoImporterServiceException {
		createBackupOfPackageProject();
	}

	private void createMainCodeIfRequest() throws StatAlgoImporterServiceException {
		if (project.getProjectConfig() != null && project.getProjectConfig().getProjectSupport() != null
				&& (project.getProjectConfig().getProjectSupport() instanceof ProjectSupportBlackBox
						|| project.getProjectConfig().getProjectSupport() instanceof ProjectSupportBashEdit)) {
			if (project.getMainCode() != null && project.getMainCode().getItemDescription() != null) {
				ItemDescription mainCodeItemDescription = project.getMainCode().getItemDescription();
				if (mainCodeItemDescription.getId() != null && !mainCodeItemDescription.getId().isEmpty()) {
					FilesStorage filesStorage = new FilesStorage();
					filesStorage.deleteItemOnFolder(serviceCredentials.getUserName(), mainCodeItemDescription.getId());
				}
			}
			MainGenerator mainGenerator = new MainGenerator();
			if (saiDescriptor != null && saiDescriptor.getRemoteTemplateFile() != null
					&& !saiDescriptor.getRemoteTemplateFile().isEmpty()) {
				mainGenerator.createMain(serviceCredentials, project, saiDescriptor.getRemoteTemplateFile());
			} else {
				mainGenerator.createMain(serviceCredentials, project, Constants.REMOTE_TEMPLATE_FILE);
			}
		}

	}

	private void repackageProjectPackage() throws StatAlgoImporterServiceException {
		List<String> idsToExclude = new ArrayList<String>();
		idsToExclude.add(backupFolderId);

		FilesStorage filesStorage = new FilesStorage();

		if (project.getProjectTarget() != null && project.getProjectTarget().getFolder() != null
				&& project.getProjectTarget().getFolder().getId() != null
				&& !project.getProjectTarget().getFolder().getId().isEmpty()) {
			idsToExclude.add(project.getProjectTarget().getFolder().getId());
		}

		File projectPackageFile = filesStorage.zipFolder(serviceCredentials.getUserName(),
				project.getProjectFolder().getFolder().getId(), idsToExclude);

		InputStream inputStream;
		try {
			inputStream = Files.newInputStream(projectPackageFile.toPath(), StandardOpenOption.READ);
		} catch (IOException e) {
			logger.error("Error input stream generation for project package file: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}

		filesStorage.saveInputStreamInItem(serviceCredentials.getUserName(),
				project.getProjectTarget().getProjectDeploy().getPackageProject().getId(), inputStream);

		WorkspaceItem packageProject = filesStorage.retrieveItemInfoOnWorkspace(serviceCredentials.getUserName(),
				project.getProjectTarget().getProjectDeploy().getPackageProject().getId());

		ItemDescription packageProjectItemDescription;
		try {
			packageProjectItemDescription = new ItemDescription(packageProject.getId(), packageProject.getName(),
					packageProject.getOwner().getPortalLogin(), packageProject.getPath(),
					packageProject.getType().name());
			packageProjectItemDescription.setPublicLink(packageProject.getPublicLink(false));
		} catch (InternalErrorException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());

		}

		project.getProjectTarget().getProjectDeploy().setPackageProject(packageProjectItemDescription);
		logger.debug("ProjectDeploy: " + project.getProjectTarget().getProjectDeploy());

	}

	private void createBackupOfPackageProject() throws StatAlgoImporterServiceException {
		logger.debug("Create Backup of PackageProject");
		if (project.getProjectTarget() != null && project.getProjectTarget().getProjectDeploy() != null
				&& project.getProjectTarget().getProjectDeploy().getPackageProject() != null
				&& project.getProjectTarget().getProjectDeploy().getPackageProject().getId() != null
				&& !project.getProjectTarget().getProjectDeploy().getPackageProject().getId().isEmpty()) {

			createBackupFolder();

			FilesStorage filesStorage = new FilesStorage();

			GregorianCalendar now = new GregorianCalendar();

			String packageProjectNewName = project.getInputData().getProjectInfo().getAlgorithmNameToClassName()
					+ "_backup_" + sdf.format(now.getTime()) + PROJECT_PACKAGE_EXTENTION;

			filesStorage.copyItemOnFolderWithNewName(serviceCredentials.getUserName(),
					project.getProjectTarget().getProjectDeploy().getPackageProject().getId(), backupFolderId,
					packageProjectNewName);
		}

	}

	private void copyJarInDeploy() throws StatAlgoImporterServiceException {
		FilesStorage filesStorage = new FilesStorage();
		if (project.getProjectTarget().getProjectDeploy().getCodeJar() != null
				&& project.getProjectTarget().getProjectDeploy().getCodeJar().getId() != null
				&& !project.getProjectTarget().getProjectDeploy().getCodeJar().getId().isEmpty()) {
			filesStorage.deleteItemOnFolder(serviceCredentials.getUserName(),
					project.getProjectTarget().getProjectDeploy().getCodeJar().getId());
		}

		WorkspaceItem deployableCodeJarItem = filesStorage.copyItemOnFolder(serviceCredentials.getUserName(),
				project.getProjectTarget().getProjectCompile().getCodeJar().getId(),
				project.getProjectTarget().getProjectDeploy().getFolder().getId());

		logger.debug("DeployableCodeJarItem:" + deployableCodeJarItem);

		ItemDescription dCodeJar;
		try {
			dCodeJar = new ItemDescription(deployableCodeJarItem.getId(), deployableCodeJarItem.getName(),
					deployableCodeJarItem.getOwner().getPortalLogin(), deployableCodeJarItem.getPath(),
					deployableCodeJarItem.getType().name());
			dCodeJar.setPublicLink(deployableCodeJarItem.getPublicLink(false));
		} catch (InternalErrorException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());

		}

		project.getProjectTarget().getProjectDeploy().setCodeJar(dCodeJar);
		logger.debug("ProjectDeploy: " + project.getProjectTarget().getProjectDeploy());
	}

	private void checkInfoForRepackage() throws StatAlgoImporterServiceException {
		if (project == null) {
			throw new StatAlgoImporterServiceException("Attention No Project Found!");
		}

		if (project.getProjectFolder() == null) {
			throw new StatAlgoImporterServiceException("Attention No Project Folder Found!");
		}

		if (project.getProjectTarget() == null) {
			throw new StatAlgoImporterServiceException("Attention No Project Target Found!");
		}

		if (project.getProjectTarget().getFolder() == null) {
			throw new StatAlgoImporterServiceException("Attention No Target Folder Found!");
		}

		if (project.getProjectTarget().getProjectDeploy() == null) {
			throw new StatAlgoImporterServiceException("Attention No Project Deploy Found!");
		}

		if (project.getProjectTarget().getProjectDeploy().getFolder() == null) {
			throw new StatAlgoImporterServiceException("Attention No Deploy Folder Found!");
		}

		if (project.getProjectTarget().getProjectDeploy().getPackageProject() == null
				|| project.getProjectTarget().getProjectDeploy().getPackageProject().getId() == null
				|| project.getProjectTarget().getProjectDeploy().getPackageProject().getId().isEmpty()) {
			throw new StatAlgoImporterServiceException(
					"Attention No Project Package Found. Try to create software before!");
		}

	}

	private void checkInfoForDeploy() throws StatAlgoImporterServiceException {
		if (project == null) {
			throw new StatAlgoImporterServiceException("Attention No Project Found!");
		}

		if (project.getProjectFolder() == null) {
			throw new StatAlgoImporterServiceException("Attention No Project Folder Found!");
		}

		if (project.getProjectTarget() == null) {
			throw new StatAlgoImporterServiceException("Attention No Project Target Found!");
		}

		if (project.getProjectTarget().getFolder() == null) {
			throw new StatAlgoImporterServiceException("Attention No Target Folder Found!");
		}

		if (project.getProjectTarget().getProjectDeploy() == null) {
			throw new StatAlgoImporterServiceException("Attention No Project Deploy Found!");
		}

		if (project.getProjectTarget().getProjectDeploy().getFolder() == null) {
			throw new StatAlgoImporterServiceException("Attention No Deploy Folder Found!");
		}

		if (project.getProjectTarget().getProjectDeploy().getPackageProject() == null) {
			throw new StatAlgoImporterServiceException("Attention No Project Package Found!");
		}

		if (project.getProjectTarget().getProjectCompile().getFolder() == null) {
			throw new StatAlgoImporterServiceException("Attention No Compile Folder Found!");
		}

		if (project.getProjectTarget().getProjectCompile().getCodeSource() == null) {
			throw new StatAlgoImporterServiceException("Attention No Source Code Found!");
		}

		if (project.getProjectTarget().getProjectCompile().getCodeJar() == null) {
			throw new StatAlgoImporterServiceException("Attention No Jar Code Found!");
		}

		if (project.getProjectTarget().getProjectCompile().getIntegrationInfo() == null) {
			throw new StatAlgoImporterServiceException("Attention No Integration Info Found!");
		}

	}

	private void createDeployFolder() throws StatAlgoImporterServiceException {
		FilesStorage filesStorage = new FilesStorage();
		filesStorage.deleteFolder(serviceCredentials.getUserName(), project.getProjectTarget().getFolder().getId(),
				STATISTICAL_ALGORITHM_DEPLOY_FOLDER_NAME);

		WorkspaceFolder deployFolder = filesStorage.createFolder(serviceCredentials.getUserName(),
				project.getProjectTarget().getFolder().getId(), STATISTICAL_ALGORITHM_DEPLOY_FOLDER_NAME,
				STATISTICAL_ALGORITHM_DEPLOY_FOLDER_DESCRIPTION);
		logger.debug("PublicFolder:" + deployFolder);

		ItemDescription pFolder;
		try {
			pFolder = new ItemDescription(deployFolder.getId(), deployFolder.getName(),
					deployFolder.getOwner().getPortalLogin(), deployFolder.getPath(), deployFolder.getType().name());
		} catch (InternalErrorException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());

		}
		ProjectDeploy projectDeploy = new ProjectDeploy(pFolder);
		project.getProjectTarget().setProjectDeploy(projectDeploy);

	}

	private void checkInfoForBuild() throws StatAlgoImporterServiceException {
		if (project == null) {
			throw new StatAlgoImporterServiceException("Attention No Project Found!");
		}

		if (project.getProjectFolder() == null || project.getProjectFolder().getFolder() == null
				|| project.getProjectFolder().getFolder().getId() == null
				|| project.getProjectFolder().getFolder().getId().isEmpty()) {
			throw new StatAlgoImporterServiceException("Attention No Project Folder Found!");
		}

		if (project.getInputData() == null) {
			throw new StatAlgoImporterServiceException("Attention No Input Set!");
		}

		if (project.getInputData().getProjectInfo() == null) {
			throw new StatAlgoImporterServiceException("Attention No Project Information Set!");
		}

		if (project.getInputData().getProjectInfo().getAlgorithmName() == null
				|| project.getInputData().getProjectInfo().getAlgorithmName().isEmpty()) {
			throw new StatAlgoImporterServiceException("Attention No Info Name Set!");
		}

		if (project.getInputData().getProjectInfo().getAlgorithmDescription() == null
				|| project.getInputData().getProjectInfo().getAlgorithmDescription().isEmpty()) {
			throw new StatAlgoImporterServiceException("Attention No Info Description Set!");
		}

		if (project.getInputData().getInterpreterInfo() == null) {
			throw new StatAlgoImporterServiceException("Attention No Interpreter Info Set!");
		}

		if (project.getInputData().getInterpreterInfo().getVersion() == null
				|| project.getInputData().getInterpreterInfo().getVersion().isEmpty()) {
			throw new StatAlgoImporterServiceException("Attention No Interpreter Version Set!");
		}

		if (project.getInputData().getListInputOutputVariables() == null
				|| project.getInputData().getListInputOutputVariables().size() < 1) {
			throw new StatAlgoImporterServiceException("Attention No Input/Output Set!");
		}

		if (project.getProjectConfig() == null || project.getProjectConfig().getProjectSupport() == null) {
			throw new StatAlgoImporterServiceException("Attention Invalid Project Configuration");
		} else {
			if (project.getProjectConfig().getProjectSupport() instanceof ProjectSupportREdit) {
				if (project.getMainCode() == null || project.getMainCode().getItemDescription() == null) {
					throw new StatAlgoImporterServiceException("Attention No Main Code Set");
				}
			} else {
				if (project.getProjectConfig().getProjectSupport() instanceof ProjectSupportBlackBox) {
					ProjectSupportBlackBox projectSupportBlackBox = (ProjectSupportBlackBox) project.getProjectConfig()
							.getProjectSupport();

					if (projectSupportBlackBox.getBinaryItem() == null) {
						throw new StatAlgoImporterServiceException("Attention No Code Set");
					}
				} else {
					if (project.getProjectConfig().getProjectSupport() instanceof ProjectSupportBashEdit) {
						ProjectSupportBashEdit projectSupportBashEdit = (ProjectSupportBashEdit) project
								.getProjectConfig().getProjectSupport();

						if (projectSupportBashEdit.getBinaryItem() == null) {
							throw new StatAlgoImporterServiceException("Attention No Code Set");
						}
					}
				}
			}
		}

	}
	
	private void createShareInfo() throws StatAlgoImporterServiceException {
		ProjectShareInfoBuilder projectShareInfoBuilder=new ProjectShareInfoBuilder(serviceCredentials, project);
		projectShareInfoBuilder.create();
		
	}
	

	private void createProjectPackage() throws StatAlgoImporterServiceException {
		List<String> idsToExclude = new ArrayList<String>();
		idsToExclude.add(backupFolderId);

		FilesStorage filesStorage = new FilesStorage();

		if (project.getProjectTarget() != null && project.getProjectTarget().getFolder() != null
				&& project.getProjectTarget().getFolder().getId() != null
				&& !project.getProjectTarget().getFolder().getId().isEmpty()) {
			idsToExclude.add(project.getProjectTarget().getFolder().getId());
		}

		File projectPackageFile = filesStorage.zipFolder(serviceCredentials.getUserName(),
				project.getProjectFolder().getFolder().getId(), idsToExclude);

		InputStream inputStream;
		try {
			inputStream = Files.newInputStream(projectPackageFile.toPath(), StandardOpenOption.READ);
		} catch (IOException e) {
			logger.error("Error input stream generation for project package file: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}

		WorkspaceItem projectPackageItem;

		projectPackageItem = filesStorage.createItemOnWorkspace(serviceCredentials.getUserName(), inputStream,
				project.getInputData().getProjectInfo().getAlgorithmNameToClassName() + PROJECT_PACKAGE_EXTENTION,
				PROJECT_PACKAGE_DESCRIPTION, PROJECT_PACKAGE_MIMETYPE,
				project.getProjectTarget().getProjectDeploy().getFolder().getId());

		logger.debug("ProjectPackageItem:" + projectPackageItem);

		ItemDescription packageUrl;
		try {
			packageUrl = new ItemDescription(projectPackageItem.getId(), projectPackageItem.getName(),
					projectPackageItem.getOwner().getPortalLogin(), projectPackageItem.getPath(),
					projectPackageItem.getType().name());
			packageUrl.setPublicLink(projectPackageItem.getPublicLink(false));
		} catch (InternalErrorException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());

		}
		project.getProjectTarget().getProjectDeploy().setPackageProject(packageUrl);
	}

	private void createTargetFolder() throws StatAlgoImporterServiceException {
		FilesStorage filesStorage = new FilesStorage();
		filesStorage.deleteFolder(serviceCredentials.getUserName(), project.getProjectFolder().getFolder().getId(),
				STATISTICAL_ALGORITHM_TARGET_FOLDER_NAME);

		WorkspaceFolder targetFolder = filesStorage.createFolder(serviceCredentials.getUserName(),
				project.getProjectFolder().getFolder().getId(), STATISTICAL_ALGORITHM_TARGET_FOLDER_NAME,
				STATISTICAL_ALGORITHM_TARGET_FOLDER_DESCRIPTION);
		logger.debug("TargetFolder:" + targetFolder);

		ItemDescription tFolder;
		try {
			tFolder = new ItemDescription(targetFolder.getId(), targetFolder.getName(),
					targetFolder.getOwner().getPortalLogin(), targetFolder.getPath(), targetFolder.getType().name());
		} catch (InternalErrorException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());

		}
		ProjectTarget projectTarget = new ProjectTarget(tFolder);
		project.setProjectTarget(projectTarget);

	}

	private void createCompileFolder() throws StatAlgoImporterServiceException {
		FilesStorage filesStorage = new FilesStorage();
		filesStorage.deleteFolder(serviceCredentials.getUserName(), project.getProjectTarget().getFolder().getId(),
				STATISTICAL_ALGORITHM_COMPILE_FOLDER_NAME);

		WorkspaceFolder compileFolder = filesStorage.createFolder(serviceCredentials.getUserName(),
				project.getProjectTarget().getFolder().getId(), STATISTICAL_ALGORITHM_COMPILE_FOLDER_NAME,
				STATISTICAL_ALGORITHM_COMPILE_FOLDER_DESCRIPTION);
		logger.debug("CompileFolder:" + compileFolder);

		ItemDescription cFolder;
		try {
			cFolder = new ItemDescription(compileFolder.getId(), compileFolder.getName(),
					compileFolder.getOwner().getPortalLogin(), compileFolder.getPath(), compileFolder.getType().name());
		} catch (InternalErrorException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());

		}

		ProjectCompile projectCompile = new ProjectCompile(cFolder);
		project.getProjectTarget().setProjectCompile(projectCompile);
	}

	private void createBackupFolder() throws StatAlgoImporterServiceException {
		FilesStorage filesStorage = new FilesStorage();

		WorkspaceItem backupFolder = filesStorage.find(serviceCredentials.getUserName(),
				project.getProjectFolder().getFolder().getId(), STATISTICAL_ALGORITHM_BACKUP_FOLDER_NAME);

		if (backupFolder == null) {
			WorkspaceFolder newBackupFolder = filesStorage.createFolder(serviceCredentials.getUserName(),
					project.getProjectFolder().getFolder().getId(), STATISTICAL_ALGORITHM_BACKUP_FOLDER_NAME,
					STATISTICAL_ALGORITHM_BACKUP_FOLDER_DESCRIPTION);
			logger.debug("BackupFolder:" + backupFolder);

			try {
				backupFolderId = newBackupFolder.getId();
			} catch (InternalErrorException e) {
				logger.error("Error retrieving Backup Folder Id: " + e.getLocalizedMessage());
				e.printStackTrace();
				throw new StatAlgoImporterServiceException("Error retrieving Backup Folder Id", e);
			}

		} else {
			try {
				backupFolderId = backupFolder.getId();
			} catch (InternalErrorException e) {
				logger.error("Error retrieving Backup Folder Id: " + e.getLocalizedMessage());
				e.printStackTrace();
				throw new StatAlgoImporterServiceException("Error retrieving Backup Folder Id", e);
			}
		}

	}

	private void createAlgorithm() throws StatAlgoImporterServiceException {
		AlgorithmGenerator algorithmGenerator = new AlgorithmGenerator(project,serviceCredentials);
		algorithmJava = algorithmGenerator.createAlgorithm();

		FilesStorage filesStorage = new FilesStorage();
		WorkspaceItem algorithmItem;

		try {
			algorithmItem = filesStorage.createItemOnWorkspace(serviceCredentials.getUserName(),
					Files.newInputStream(algorithmJava, StandardOpenOption.READ),
					project.getInputData().getProjectInfo().getAlgorithmNameToClassName() + ALGORITHM_EXTENTION,
					ALGORITHM_DESCRIPTION, ALGORITHM_MIMETYPE,
					project.getProjectTarget().getProjectCompile().getFolder().getId());
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage(), e);
		}

		ItemDescription codeSource;
		try {
			codeSource = new ItemDescription(algorithmItem.getId(), algorithmItem.getName(),
					algorithmItem.getOwner().getPortalLogin(), algorithmItem.getPath(), algorithmItem.getType().name());
			codeSource.setPublicLink(algorithmItem.getPublicLink(false));
		} catch (InternalErrorException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());

		}

		project.getProjectTarget().getProjectCompile().setCodeSource(codeSource);
		logger.debug("ProjectCompile: " + project.getProjectTarget().getProjectCompile());

	}

	private void createIntegrationInfo() throws StatAlgoImporterServiceException {
		InfoGenerator integrationInfoGenerator = new InfoGenerator(project, serviceCredentials);
		infoTXT = integrationInfoGenerator.createInfo();

		FilesStorage filesStorage = new FilesStorage();
		WorkspaceItem infoItem;

		try {
			infoItem = filesStorage.createItemOnWorkspace(serviceCredentials.getUserName(),
					Files.newInputStream(infoTXT, StandardOpenOption.READ), INFO_NAME + INFO_EXTENTION,
					INFO_DESCRIPTION, INFO_MIMETYPE,
					project.getProjectTarget().getProjectCompile().getFolder().getId());
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage(), e);
		}

		ItemDescription integrationItemDescription;
		try {
			integrationItemDescription = new ItemDescription(infoItem.getId(), infoItem.getName(),
					infoItem.getOwner().getPortalLogin(), infoItem.getPath(), infoItem.getType().name());
			integrationItemDescription.setPublicLink(infoItem.getPublicLink(false));
		} catch (InternalErrorException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());

		}

		project.getProjectTarget().getProjectCompile().setIntegrationInfo(integrationItemDescription);
		logger.debug("ProjectCompile: " + project.getProjectTarget().getProjectCompile());

	}

	private void createProjectJarFile() throws StatAlgoImporterServiceException {

		FilesStorage storage = new FilesStorage();

		Path tempDirectory;
		try {
			tempDirectory = Files.createTempDirectory("StatAlgorithmsJar");
		} catch (IOException e) {
			logger.error("Error creating temporal directory: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage(), e);
		}
		logger.debug("TempDir: " + tempDirectory);

		File packageHome = new File(
				tempDirectory.toAbsolutePath().toString() + File.separator + "org" + File.separator + "gcube"
						+ File.separator + "dataanalysis" + File.separator + "executor" + File.separator + "rscripts");
		packageHome.mkdirs();
		Path packageHomeDir = packageHome.toPath();

		Path ecologicalEngineJar = new File(tempDirectory.toFile(), ECOLOGICAL_ENGINE_JAR).toPath();
		storage.downloadInputFile(ECOLOGICAL_ENGINE_JAR_URL, ecologicalEngineJar);
		Path ecologicalEngineSmartExecutorJar = new File(tempDirectory.toFile(), ECOLOGICAL_ENGINE_SMART_EXECUTOR_JAR)
				.toPath();
		storage.downloadInputFile(ECOLOGICAL_ENGINE_SMART_EXECUTOR_JAR_URL, ecologicalEngineSmartExecutorJar);

		Path algorithmTempFile = new File(packageHomeDir.toFile(),
				project.getInputData().getProjectInfo().getAlgorithmNameToClassName() + ALGORITHM_EXTENTION).toPath();
		try {
			Files.copy(algorithmJava, algorithmTempFile);
		} catch (IOException e) {
			logger.error("Error in alogrithm java copy in package directory: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage(), e);
		}

		Path infoTempFile = new File(tempDirectory.toFile(), INFO_NAME + INFO_EXTENTION).toPath();
		try {
			Files.copy(infoTXT, infoTempFile);
		} catch (IOException e) {
			logger.error("Error in info copy in temp directory: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage(), e);
		}

		createClassFile(tempDirectory, packageHomeDir);

		try {
			Files.delete(algorithmTempFile);
		} catch (IOException e) {
			logger.error("Error in delete java file in package directory: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage(), e);
		}

		createJarFile(tempDirectory, infoTempFile);
		copyJarOnWorkspace(tempDirectory);

		try {
			FileUtils.cleanDirectory(tempDirectory.toFile());
			FileUtils.deleteDirectory(tempDirectory.toFile());
		} catch (IOException e) {
			logger.error("Error in delete temp directory: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage(), e);
		}

		return;

	}

	private void copyJarOnWorkspace(Path tempDirectory) throws StatAlgoImporterServiceException {
		Path codeJar = Paths.get(tempDirectory.toString(),
				project.getInputData().getProjectInfo().getAlgorithmNameToClassName() + JAR_EXTENTION);

		FilesStorage filesStorage = new FilesStorage();
		WorkspaceItem codeJarItem;

		try {
			codeJarItem = filesStorage.createItemOnWorkspace(serviceCredentials.getUserName(),
					Files.newInputStream(codeJar, StandardOpenOption.READ),
					project.getInputData().getProjectInfo().getAlgorithmNameToClassName() + JAR_EXTENTION,
					CODE_JAR_DESCRIPTION, CODE_JAR_MIMETYPE,
					project.getProjectTarget().getProjectCompile().getFolder().getId());
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage(), e);
		}

		ItemDescription codeJarItemDescription;
		try {
			codeJarItemDescription = new ItemDescription(codeJarItem.getId(), codeJarItem.getName(),
					codeJarItem.getOwner().getPortalLogin(), codeJarItem.getPath(), codeJarItem.getType().name());
			codeJarItemDescription.setPublicLink(codeJarItem.getPublicLink(false));
		} catch (InternalErrorException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());

		}

		project.getProjectTarget().getProjectCompile().setCodeJar(codeJarItemDescription);
		logger.debug("ProjectCompile: " + project.getProjectTarget().getProjectCompile());

	}

	private void createClassFile(Path tempDirectory, Path packageHome) throws StatAlgoImporterServiceException {

		try {

			ProcessBuilder pb = new ProcessBuilder("javac", "-encoding", "utf-8", "-cp",
					tempDirectory.toAbsolutePath().toString() + "/*",
					packageHome.toAbsolutePath().toString() + File.separator
							+ project.getInputData().getProjectInfo().getAlgorithmNameToClassName()
							+ ALGORITHM_EXTENTION);
			pb.directory(tempDirectory.toFile());
			Path logTXT = new File(tempDirectory.toFile(), LOG_TXT).toPath();

			pb.redirectErrorStream(true);
			pb.redirectOutput(Redirect.appendTo(logTXT.toFile()));
			logger.debug("Process: " + pb.toString());
			Process process = pb.start();

			// Wait to get exit value
			int exitValue = process.waitFor();
			logger.debug("Create Algo Class: Exit Value is " + exitValue);

			DirectoryStream<Path> packageHomeStream = Files.newDirectoryStream(packageHome);
			boolean createdFilesClass = false;
			for (Path path : packageHomeStream) {
				if (path.toString().endsWith(".class")) {
					createdFilesClass = true;
					break;
				}
			}

			if (!createdFilesClass) {
				throw new StatAlgoImporterServiceException("Error in the creation of the class files!");
			}

		} catch (IOException | InterruptedException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage(), e);
		}

	}

	private void createJarFile(Path tempDirectory, Path infoTempFile) throws StatAlgoImporterServiceException {
		try {
			List<String> commands = new ArrayList<>();
			commands.add("jar");
			commands.add("-cvf");
			commands.add(project.getInputData().getProjectInfo().getAlgorithmNameToClassName() + JAR_EXTENTION);

			/*
			 * DirectoryStream<Path> directoryStream = Files
			 * .newDirectoryStream(tempDirectory); for (Path path :
			 * directoryStream) { if (path.toString().endsWith(".class"))
			 * commands.add(path.getFileName().toString());
			 * 
			 * }
			 */

			commands.add("org");
			commands.add(infoTempFile.getFileName().toString());

			ProcessBuilder pb = new ProcessBuilder(commands);
			pb.directory(tempDirectory.toFile());
			Path logTXT = new File(tempDirectory.toFile(), LOG_JAR_TXT).toPath();

			pb.redirectErrorStream(true);
			pb.redirectOutput(Redirect.appendTo(logTXT.toFile()));
			logger.debug("Process: " + pb.toString());
			Process process = pb.start();

			// Wait to get exit value
			int exitValue = process.waitFor();
			logger.debug("Create Algo Jar: Exit Value is " + exitValue);

		} catch (IOException | InterruptedException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage(), e);
		}

	}

}
