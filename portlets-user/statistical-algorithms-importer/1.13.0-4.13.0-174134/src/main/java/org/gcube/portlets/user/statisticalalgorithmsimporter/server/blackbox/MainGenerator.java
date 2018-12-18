package org.gcube.portlets.user.statisticalalgorithmsimporter.server.blackbox;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.portlets.user.statisticalalgorithmsimporter.server.storage.FilesStorage;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.util.ServiceCredentials;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception.StatAlgoImporterServiceException;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.InputOutputVariables;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.MainCode;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.Project;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.ProjectSupportBashEdit;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.ProjectSupportBlackBox;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.workspace.ItemDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class MainGenerator {

	private static final Logger logger = LoggerFactory.getLogger(MainGenerator.class);
	private static final String R_MIMETYPE = "text/plain";
	private static final String R_DESCRIPTION = "R script for ";

	public void createMain(ServiceCredentials serviceCredentials, Project project, String remoteTemplateFile)
			throws StatAlgoImporterServiceException {

		logger.debug("Project: " + project);
		if (project == null || project.getProjectConfig() == null
				|| project.getProjectConfig().getProjectSupport() == null) {
			String error = "Error invalid project support!";
			logger.error(error);
			throw new StatAlgoImporterServiceException(error);
		}

		ItemDescription binarySoftware = retrieveBinarySoftware(project);

		try {

			logger.debug("Language: " + project.getProjectConfig().getLanguage());
			logger.debug("Binary software: " + binarySoftware);

			GeneralPurposeScriptProducer s = new GeneralPurposeScriptProducer(remoteTemplateFile);

			List<GeneralPurposeScriptProducer.Triple> input = new ArrayList<>();
			List<GeneralPurposeScriptProducer.Triple> output = new ArrayList<>();

			for (InputOutputVariables ioV : project.getInputData().getListInputOutputVariables()) {
				switch (ioV.getIoType()) {
				case INPUT:
					input.add(s.new Triple(ioV.getName(), ioV.getDefaultValue(), ioV.getDataType().getId()));
					break;
				case OUTPUT:
					output.add(s.new Triple(ioV.getName(), ioV.getDefaultValue(), ioV.getDataType().getId()));
					break;
				default:
					break;

				}
			}

			logger.debug("Inputs: " + Arrays.toString(input.toArray()));
			logger.debug("Outputs: " + Arrays.toString(output.toArray()));

			Path producedScript = null;

			// if (project.getProjectConfig().getProjectSupport() instanceof
			// ProjectSupportBashEdit){
			// logger.debug("Generate Script R with public link:
			// "+binarySoftware.getPublicLink());
			// producedScript = s.generateScript(input, output,
			// binarySoftware.getName(),
			// project.getProjectConfig().getLanguage(),
			// binarySoftware.getPublicLink());
			// } else {

			logger.debug("Generate Script R");
			producedScript = s.generateScript(input, output, binarySoftware.getName(),
					project.getProjectConfig().getLanguage());
			// }

			if (producedScript == null || !Files.exists(producedScript)) {
				String error = "Error creating script: file not exists!";
				logger.error(error);
				throw new StatAlgoImporterServiceException(error);
			}

			//////
			FilesStorage filesStorage = new FilesStorage();
			ItemDescription mainItemDescription;

			try {
				mainItemDescription = filesStorage.createItemOnWorkspace(serviceCredentials.getUserName(),
						Files.newInputStream(producedScript, StandardOpenOption.READ),

						"Main.R", R_DESCRIPTION + project.getProjectConfig().getLanguage(), R_MIMETYPE,
						project.getProjectFolder().getFolder().getId());
				String pLink=filesStorage.getPublicLink(serviceCredentials.getUserName(), mainItemDescription.getId());
				mainItemDescription.setPublicLink(pLink);
			} catch (IOException e) {
				logger.error(e.getLocalizedMessage());
				e.printStackTrace();
				throw new StatAlgoImporterServiceException(e.getLocalizedMessage(), e);
			}

			MainCode mainCode = new MainCode(mainItemDescription);

			project.setMainCode(mainCode);
			logger.debug("MainCode: " + project.getMainCode());

		} catch (StatAlgoImporterServiceException e) {
			throw e;
		} catch (Throwable e) {
			logger.error("Error gerating main code: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException("Error gerating main code: " + e.getLocalizedMessage(), e);
		}

	}

	private ItemDescription retrieveBinarySoftware(Project project) throws StatAlgoImporterServiceException {
		if (project.getProjectConfig().getProjectSupport() instanceof ProjectSupportBlackBox) {
			ProjectSupportBlackBox projectSupportBlackBox = (ProjectSupportBlackBox) project.getProjectConfig()
					.getProjectSupport();
			return projectSupportBlackBox.getBinaryItem();
		} else {
			if (project.getProjectConfig().getProjectSupport() instanceof ProjectSupportBashEdit) {
				ProjectSupportBashEdit projectSupportBashEdit = (ProjectSupportBashEdit) project.getProjectConfig()
						.getProjectSupport();
				return projectSupportBashEdit.getBinaryItem();
			} else {
				String error = "Error invalid project support!";
				logger.error(error);
				throw new StatAlgoImporterServiceException(error);
			}
		}

	}

}
