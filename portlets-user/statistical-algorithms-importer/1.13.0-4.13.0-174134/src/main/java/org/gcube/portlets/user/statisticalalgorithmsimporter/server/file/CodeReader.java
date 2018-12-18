package org.gcube.portlets.user.statisticalalgorithmsimporter.server.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import org.gcube.portlets.user.statisticalalgorithmsimporter.server.storage.FilesStorage;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.util.ServiceCredentials;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.code.CodeData;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception.StatAlgoImporterServiceException;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.MainCode;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.Project;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.ProjectSupportBashEdit;
//import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.ProjectSupportBlackBox;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.ProjectSupportREdit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CodeReader
 * 
 * Read code and convert it in ArrayList
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class CodeReader {
	private Logger logger = LoggerFactory.getLogger(CodeReader.class);
	private ArrayList<CodeData> code;
	private Project project;
	private ServiceCredentials serviceCredentials;

	public CodeReader(Project project, ServiceCredentials serviceCredentials) {
		this.project = project;
		this.serviceCredentials = serviceCredentials;

	}

	public ArrayList<CodeData> getCodeList() throws StatAlgoImporterServiceException {
		retrieveCode();
		logCode();
		return code;
	}

	private void logCode() {
		if (code != null) {
			for (CodeData codeData : code) {
				logger.debug("" + codeData.getId() + " " + codeData.getCodeLine());
			}
		} else {
			logger.debug("no code!");
		}
	}

	private void retrieveCode() throws StatAlgoImporterServiceException {
		if (project != null) {
			if (project.getProjectConfig() != null && project.getProjectConfig().getProjectSupport() != null) {
				if (project.getProjectConfig().getProjectSupport() instanceof ProjectSupportREdit) {
					logger.debug("Project Support REdit");
					MainCode mainCode = project.getMainCode();
					if (mainCode == null || mainCode.getItemDescription() == null
							|| mainCode.getItemDescription().getId() == null
							|| mainCode.getItemDescription().getId().isEmpty()) {
						throw new StatAlgoImporterServiceException("No main code set!");
					} else {
						String itemId = project.getMainCode().getItemDescription().getId();
						readCodeFromItem(itemId);
					}
				} else {
					if (project.getProjectConfig().getProjectSupport() instanceof ProjectSupportBashEdit) {
						logger.debug("Project Support BashEdit");
						ProjectSupportBashEdit projectSupportBashEdit = (ProjectSupportBashEdit) project
								.getProjectConfig().getProjectSupport();
						if (projectSupportBashEdit == null || projectSupportBashEdit.getBinaryItem() == null
								|| projectSupportBashEdit.getBinaryItem().getId() == null
								|| projectSupportBashEdit.getBinaryItem().getId().isEmpty()) {
							throw new StatAlgoImporterServiceException("No binary code set!");
						} else {
							String itemId = projectSupportBashEdit.getBinaryItem().getId();
							readCodeFromItem(itemId);
						}
					} else {
						/**
						 * TODO
						 * 
						 * Check how if open a project after another already open if we write this code we create a lock
						 * this create a lock on workspace when we save the project.
						 * The Workspace does not have a fix for this issue, 
						 * so I add this exception how workaround.
						 *
						 * 
						 * if (project.getProjectConfig().getProjectSupport()
						 * instanceof ProjectSupportBlackBox) {
						 * 
						 * logger.debug("Project Support BlackBox"); code = new
						 * ArrayList<>(); } else {
						 */
						throw new StatAlgoImporterServiceException("Error reading code!");
						// }
					}
				}
			} else {
				throw new StatAlgoImporterServiceException("Error in project config!");
			}
		} else {
			code = new ArrayList<>();
		}

	}

	private void readCodeFromItem(String itemId) throws StatAlgoImporterServiceException {
		try {

			code = new ArrayList<CodeData>();
			FilesStorage filesStorage = new FilesStorage();
			InputStream is = filesStorage.getFileOnWorkspace(serviceCredentials.getUserName(), itemId);

			InputStreamReader isr = new InputStreamReader(is);

			BufferedReader br = new BufferedReader((Reader) isr);

			String s;
			int i = 1;
			while ((s = br.readLine()) != null) {
				CodeData codeData = new CodeData(i, s);
				code.add(codeData);
				i++;
			}
			is.close();
			logger.trace("Code size: " + code.size());
		} catch (IOException e) {
			e.printStackTrace();
			new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}
	}

}
