package org.gcube.portlets.user.statisticalalgorithmsimporter.server.generator;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception.StatAlgoImporterServiceException;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.InterpreterPackageInfo;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class InfoGenerator {
	private static final String INFO_NAME = "Info";
	private static final String INFO_EXTENTION = ".txt";
	public static final Logger logger = LoggerFactory
			.getLogger(InfoGenerator.class);

	private Project project;

	public InfoGenerator(Project project) {
		super();
		this.project = project;
	}

	@Override
	public String toString() {
		return "IntegrationInfoGenerator [project=" + project + "]";
	}

	public Path createInfo() throws StatAlgoImporterServiceException {

		try {
			Path tempFile = Files.createTempFile(INFO_NAME, INFO_EXTENTION);

			List<String> lines = createInfoData();
			Files.write(tempFile, lines, Charset.defaultCharset(),
					StandardOpenOption.WRITE);
			logger.debug(tempFile.toString());
			return tempFile;

		} catch (IOException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage(),
					e);
		}

	}

	private List<String> createInfoData() {
		ArrayList<String> infos = new ArrayList<String>();
		if (project.getInputData() != null) {
			if (project.getInputData().getProjectInfo() != null) {
				if (project.getInputData().getProjectInfo().getAlgorithmName() != null) {
					infos.add("Algorithm Name: "
							+ project.getInputData().getProjectInfo()
									.getAlgorithmNameToUpper());
					infos.add("Class Name: org.gcube.dataanalysis.executor.rscripts."+ project.getInputData().getProjectInfo()
							.getAlgorithmNameToClassName());
				
				} else {
					infos.add("Algorithm Name: ");
					infos.add("Class Name: ");
				}
				if (project.getInputData().getProjectInfo().getAlgorithmDescription() != null) {
					infos.add("Algorithm Description: "
							+ project.getInputData().getProjectInfo()
									.getAlgorithmDescription());
				} else {
					infos.add("Algorithm Description: ");
				}
				if (project.getInputData().getProjectInfo().getAlgorithmCategory() != null) {
					infos.add("Algorithm Category: "
							+ project.getInputData().getProjectInfo()
									.getAlgorithmCategory());
				} else {
					infos.add("Algorithm Category: ");
				}
				
				infos.add("");

				/*
				if (project.getInputData().getProjectInfo()
						.getListRequestedVRE() != null
						&& project.getInputData().getProjectInfo()
								.getListRequestedVRE().size() > 0) {
					infos.add("Deployable VRE:");
					for (RequestedVRE deployableVRE : project.getInputData()
							.getProjectInfo().getListRequestedVRE()) {
						infos.add("" + deployableVRE.getName() + " " + deployableVRE.getDescription());
					}
					infos.add("");
				}*/
				

			}

			if (project.getInputData().getInterpreterInfo() != null) {
				if (project.getInputData().getInterpreterInfo().getVersion() != null) {
					infos.add("Interpreter Version: "
							+ project.getInputData().getInterpreterInfo()
									.getVersion());
				} else {
					infos.add("Interpreter Version: Any");
				}
				infos.add("");
				if (project.getInputData().getInterpreterInfo()
						.getInterpreterPackagesInfo() != null
						&& project.getInputData().getInterpreterInfo()
								.getInterpreterPackagesInfo().size() > 0) {
					infos.add("Packages:");
					for (InterpreterPackageInfo info : project.getInputData()
							.getInterpreterInfo().getInterpreterPackagesInfo()) {
						infos.add("" + info.getName() + " " + info.getVersion());
					}
				}

			}
		}

		return infos;
	}
}
