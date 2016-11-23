package org.gcube.portlets.user.statisticalalgorithmsimporter.server.annotation;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.storage.FilesStorage;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception.StatAlgoImporterServiceException;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.DataType;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.IOType;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.InputOutputVariables;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.ProjectInfo;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.InputData;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.Project;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.workspace.ItemDescription;
import org.n52.wps.server.r.R_Config;
import org.n52.wps.server.r.metadata.RAnnotationParser;
import org.n52.wps.server.r.syntax.RAnnotation;
import org.n52.wps.server.r.syntax.RAnnotationException;
import org.n52.wps.server.r.syntax.RAnnotationType;
import org.n52.wps.server.r.syntax.RAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class WPS4RParser {

	public static final Logger logger = LoggerFactory
			.getLogger(WPS4RParser.class);

	private Project project;
	private ASLSession aslSession;

	public WPS4RParser(Project project, ASLSession aslSession) {
		this.project = project;
		this.aslSession = aslSession;
	}

	public Project parse() throws StatAlgoImporterServiceException {
		logger.debug("MainCode: " + project.getMainCode());
		ItemDescription mainCode = project.getMainCode().getItemDescription();
		FilesStorage fileStorage = new FilesStorage();
		InputStream is = fileStorage.retrieveItemOnWorkspace(
				aslSession.getUsername(), mainCode.getId());
		logger.debug("MainCode InputStream: " + is);
		
		Path tempFile=null;
		try {
			tempFile = Files.createTempFile("RCodeToParse", ".R");
			Files.copy(is, tempFile, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		logger.debug("" + tempFile);

		
		R_Config config = R_Config.getInstance();
		logger.debug("R_Config: " + config);
		List<RAnnotation> annotations;
		try {
			RAnnotationParser parser = new RAnnotationParser(config);
			logger.debug("RAnnotations Parser:" + parser);
			annotations = parser.parseAnnotationsfromScript(Files.newInputStream(tempFile, StandardOpenOption.READ));
		} catch (RAnnotationException e) {
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		} catch (Throwable e) {
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		} 
		
		try {
			Files.delete(tempFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		logger.debug("RAnnotations: " + annotations);
		
		WPSAlgorithmInfo wpsAlgorithmInfo = mapAnnotations(annotations);
		logger.debug("wpsAlgorithmInfo: " + wpsAlgorithmInfo);
		if (wpsAlgorithmInfo==null || wpsAlgorithmInfo.getAlgorithmName() == null)
			return project;
		else
			return mapToProject(wpsAlgorithmInfo);

	}

	private Project mapToProject(WPSAlgorithmInfo wpsAlgorithmInfo)
			throws StatAlgoImporterServiceException {
		if (project == null) {
			throw new StatAlgoImporterServiceException("Open project before!");
		}
		
		
		if (project.getInputData() == null) {
			ProjectInfo projectInfo = new ProjectInfo(
					wpsAlgorithmInfo.getAlgorithmName(),
					wpsAlgorithmInfo.getDescription(), null);
			InputData inputData = new InputData(projectInfo, null, null,
					wpsAlgorithmInfo.getInputOutputVariables());
			project.setInputData(inputData);
		} else {
			InputData inputData = project.getInputData();
			ProjectInfo projectInfo = new ProjectInfo(
					wpsAlgorithmInfo.getAlgorithmName(),
					wpsAlgorithmInfo.getDescription(), null);
			inputData.setProjectInfo(projectInfo);
			inputData.setListInputOutputVariables(wpsAlgorithmInfo
					.getInputOutputVariables());
			inputData.setListGlobalVariables(null);
		}
		return project;

	}

	private WPSAlgorithmInfo mapAnnotations(List<RAnnotation> annotations)
			throws StatAlgoImporterServiceException {
		try {

			WPSAlgorithmInfo wpsAlgorithmInfo = new WPSAlgorithmInfo();
			ArrayList<InputOutputVariables> inputOutputVariables = new ArrayList<>();
			int index = 1;
			for (RAnnotation rAnnotation : annotations) {
				logger.debug("RAnnotation: " + rAnnotation);
				if (rAnnotation.getType().equals(RAnnotationType.DESCRIPTION)) {
					wpsAlgorithmInfo.setVersion(rAnnotation
							.getStringValue(RAttribute.VERSION));
					wpsAlgorithmInfo.setDescription(rAnnotation
							.getStringValue(RAttribute.ABSTRACT));
					String algorithmName=rAnnotation
					.getStringValue(RAttribute.TITLE);
					algorithmName=algorithmName.replaceAll("[^A-Za-z0-9]", "_");
					wpsAlgorithmInfo.setAlgorithmName(algorithmName);
					wpsAlgorithmInfo.setVersion(rAnnotation
							.getStringValue(RAttribute.VERSION));
				} else if (rAnnotation.getType().equals(RAnnotationType.OUTPUT)
						|| rAnnotation.getType().equals(RAnnotationType.INPUT)) {
					// output, text, Random number list,

					String type = rAnnotation.getStringValue(RAttribute.TYPE);
					String name = rAnnotation
							.getStringValue(RAttribute.IDENTIFIER);
					String description = rAnnotation
							.getStringValue(RAttribute.TITLE);
					String defaultValue = rAnnotation
							.getStringValue(RAttribute.DEFAULT_VALUE);
					if (type == null)
						type = "string";
					if (name == null)
						name = "";
					if (description == null)
						description = "";
					if (defaultValue == null)
						defaultValue = "";

					IOType ioType = IOType.INPUT;

					if (rAnnotation.getType().equals(RAnnotationType.OUTPUT))
						ioType = IOType.OUTPUT;

					DataType dataType = WPStype2DataType(type);
					if (defaultValue != null && defaultValue.contains("|")
							&& dataType == DataType.STRING)
						dataType = DataType.ENUMERATED;

					InputOutputVariables ioVariable = new InputOutputVariables(
							index, name, description, defaultValue, dataType,
							ioType, "");
					inputOutputVariables.add(ioVariable);
					index++;
				}
			}
			wpsAlgorithmInfo.setInputOutputVariables(inputOutputVariables);
			return wpsAlgorithmInfo;

		} catch (Throwable e) {
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}
	}

	private static DataType WPStype2DataType(String type) {
		if (type.equalsIgnoreCase("double"))
			return DataType.DOUBLE;
		else if (type.equalsIgnoreCase("integer"))
			return DataType.INTEGER;
		else if (type.equalsIgnoreCase("string"))
			return DataType.STRING;
		else if (type.equalsIgnoreCase("boolean"))
			return DataType.BOOLEAN;
		else
			return DataType.FILE;
	}
}
