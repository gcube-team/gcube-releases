package org.gcube.portlets.user.statisticalalgorithmsimporter.server.storage;

import org.gcube.portlets.user.statisticalalgorithmsimporter.server.util.ServiceCredentials;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception.StatAlgoImporterServiceException;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.info.InfoData;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.Project;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.workspace.ItemDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class DeploySave {
	//private static final String ADMIN_USER = "statistical.manager";
	//private static final String CODE_JAR_MIMETYPE = "application/java-archive";
	//private static final String CODE_JAR_DESCRIPTION = "Statistical Algorithm Jar";
	//private static final String DESTINATION_FOLDER = "DataMinerAlgorithms";
	//private static final String TXT_MIMETYPE = "text/plain";
	//private static final String EXTENTION_TXT = ".txt";

	public static final Logger logger = LoggerFactory.getLogger(DeploySave.class);
	//private FilesStorage filesStorage;

	private ServiceCredentials serviceCredentials;
	private Project project;
	private InfoData infoData;
	private String infoText;
	//private ItemDescription codeJarAdminCopy;

	public DeploySave(ServiceCredentials serviceCredentials, Project project, InfoData infoData) {
		this.serviceCredentials = serviceCredentials;
		this.project = project;
		this.infoData = infoData;
		this.infoText = null;
		//this.codeJarAdminCopy = null;
		//filesStorage = new FilesStorage();

	}

	public void save() throws StatAlgoImporterServiceException {
		//ItemDescription codeJar = project.getProjectTarget().getProjectDeploy().getCodeJar();

		//InputStream codeJarInputStream = filesStorage.retrieveItemOnWorkspace(serviceCredentials.getUserName(),
		//		codeJar.getId());

		//codeJarAdminCopy = filesStorage.createItemOnWorkspaceHowAdmin(ADMIN_USER, codeJarInputStream, codeJar.getName(),
		//		CODE_JAR_DESCRIPTION, CODE_JAR_MIMETYPE, DESTINATION_FOLDER);

		createInfoText();
		/*
		try {
			int codeJarNamelenght = codeJar.getName().length();
			String codeJarName;
			if (codeJarNamelenght > 4) {
				codeJarName = codeJar.getName().substring(0, codeJarNamelenght - 4);
			} else {
				throw new StatAlgoImporterServiceException("Error in code jar name: " + codeJar.getName());
			}

			Path reportInstallTempFile = Files.createTempFile(codeJarName + "_install", EXTENTION_TXT);

			List<String> lines = Arrays.asList(infoText.split("\\n"));
			Files.write(reportInstallTempFile, lines, Charset.defaultCharset(), StandardOpenOption.WRITE);
			logger.debug(reportInstallTempFile.toString());
			
			InputStream reportInstallInputStream = Files.newInputStream(reportInstallTempFile);

			filesStorage.createItemOnWorkspaceHowAdmin(ADMIN_USER, reportInstallInputStream,
					codeJarName + "_install.txt", codeJarName + "_install.txt", TXT_MIMETYPE, DESTINATION_FOLDER);

		} catch (IOException e) {
			logger.error("Error writing report install information: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage(), e);
		}
		*/

	}

	private void createInfoText() {
		ItemDescription codeJar = project.getProjectTarget().getProjectDeploy().getCodeJar();

		infoText = "Username: " + serviceCredentials.getUserName() + "\nFull Name: " + serviceCredentials.getFullName()
				+ "\nEmail: " + serviceCredentials.getEmail() + "\n\nin VRE: " + serviceCredentials.getScope()
				+ "\n\nhas requested to publish the algorithm: " + "\nLanguage: " + infoData.getLanguage()
				+ "\nAlgorithm Name: " + infoData.getAlgorithmName() + "\nClass Name: " + infoData.getClassName()
				+ "\nAlgorithm Description: " + infoData.getAlgorithmDescription() + "\nAlgorithm Category: "
				+ infoData.getAlgorithmCategory() + "\n\nInterpreter Version: " + infoData.getInterpreterVersion()
				+ "\n\nwith the following original jar: "
				+ codeJar.getPublicLink() + "\nadmin copy jar: "
				+ codeJar.getPublicLink() + "\n\nInstaller: " + "\n./addAlgorithm.sh "
				+ infoData.getAlgorithmName() + " " + infoData.getAlgorithmCategory() + " " + infoData.getClassName()
				+ " " + serviceCredentials.getScope() + " transducerers N " + codeJar.getPublicLink() + " \""
				+ infoData.getAlgorithmDescription() + "\"";
	}

	public String getInfoText() {
		return infoText;
	}

	/*public ItemDescription getCodeJarAdminCopy() {
		return codeJarAdminCopy;
	}*/

	public InfoData getInfoData() {
		return infoData;
	}

}
