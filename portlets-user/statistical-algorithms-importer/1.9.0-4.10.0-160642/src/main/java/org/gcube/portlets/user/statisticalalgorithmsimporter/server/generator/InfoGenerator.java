package org.gcube.portlets.user.statisticalalgorithmsimporter.server.generator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.statisticalalgorithmsimporter.server.storage.FilesStorage;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.util.ServiceCredentials;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception.StatAlgoImporterServiceException;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.info.InfoData;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.InterpreterPackageInfo;
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
public class InfoGenerator {
	private static final String INFO_NAME = "Info";
	private static final String INFO_TXT_EXTENTION = ".txt";
	public static final Logger logger = LoggerFactory.getLogger(InfoGenerator.class);

	private Project project;
	private ServiceCredentials serviceCredentials;

	public InfoGenerator(Project project, ServiceCredentials serviceCredentials) {
		super();
		this.project = project;
		this.serviceCredentials = serviceCredentials;
	}

	@Override
	public String toString() {
		return "IntegrationInfoGenerator [project=" + project + "]";
	}

	public Path createInfo() throws StatAlgoImporterServiceException {

		try {
			Path tempFile = Files.createTempFile(INFO_NAME, INFO_TXT_EXTENTION);

			List<String> lines = createInfoTxtData();
			Files.write(tempFile, lines, Charset.defaultCharset(), StandardOpenOption.WRITE);
			logger.debug(tempFile.toString());
			return tempFile;

		} catch (IOException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage(), e);
		}

	}

	private List<String> createInfoTxtData() {
		ArrayList<String> infos = new ArrayList<String>();
		if (serviceCredentials != null) {
			if (serviceCredentials.getUserName() != null && !serviceCredentials.getUserName().isEmpty()) {
				infos.add("Username: " + serviceCredentials.getUserName());
			} else {
				infos.add("Username: ");
			}
			if (serviceCredentials.getFullName() != null) {
				infos.add("Full Name: " + serviceCredentials.getFullName());
			} else {
				infos.add("Full Name: ");
			}
			if (serviceCredentials.getEmail() != null) {
				infos.add("Email: " + serviceCredentials.getEmail());
			} else {
				infos.add("Email: ");
			}

		} else {
			infos.add("Username: ");
			infos.add("Full Name: ");
			infos.add("Email: ");
		}
		infos.add("");

		if (project.getProjectConfig() != null) {
			if (project.getProjectConfig().getLanguage() != null) {
				infos.add("Language: " + project.getProjectConfig().getLanguage());
			} else {
				infos.add("Language: ");
			}
		} else {
			infos.add("Language: ");
		}

		if (project.getInputData() != null) {
			if (project.getInputData().getProjectInfo() != null) {
				if (project.getInputData().getProjectInfo().getAlgorithmName() != null) {
					infos.add("Algorithm Name: " + project.getInputData().getProjectInfo().getAlgorithmNameToUpper());
					infos.add("Class Name: org.gcube.dataanalysis.executor.rscripts."
							+ project.getInputData().getProjectInfo().getAlgorithmNameToClassName());

				} else {
					infos.add("Algorithm Name: ");
					infos.add("Class Name: ");
				}
				if (project.getInputData().getProjectInfo().getAlgorithmDescription() != null) {
					infos.add("Algorithm Description: "
							+ project.getInputData().getProjectInfo().getAlgorithmDescription());
				} else {
					infos.add("Algorithm Description: ");
				}
				if (project.getInputData().getProjectInfo().getAlgorithmCategory() != null) {
					infos.add("Algorithm Category: " + project.getInputData().getProjectInfo().getAlgorithmCategory());
				} else {
					infos.add("Algorithm Category: ");
				}

				infos.add("");

				/*
				 * if (project.getInputData().getProjectInfo()
				 * .getListRequestedVRE() != null &&
				 * project.getInputData().getProjectInfo()
				 * .getListRequestedVRE().size() > 0) {
				 * infos.add("Deployable VRE:"); for (RequestedVRE deployableVRE
				 * : project.getInputData()
				 * .getProjectInfo().getListRequestedVRE()) { infos.add("" +
				 * deployableVRE.getName() + " " +
				 * deployableVRE.getDescription()); } infos.add(""); }
				 */

			}

			if (project.getInputData().getInterpreterInfo() != null) {
				if (project.getInputData().getInterpreterInfo().getVersion() != null) {
					infos.add("Interpreter Version: " + project.getInputData().getInterpreterInfo().getVersion());
				} else {
					infos.add("Interpreter Version: ");
				}
				infos.add("");
				if (project.getInputData().getInterpreterInfo().getInterpreterPackagesInfo() != null
						&& project.getInputData().getInterpreterInfo().getInterpreterPackagesInfo().size() > 0) {
					infos.add("Packages:");
					for (InterpreterPackageInfo info : project.getInputData().getInterpreterInfo()
							.getInterpreterPackagesInfo()) {
						if(info.getName()!=null&& !info.getName().isEmpty()){
							String pName=info.getName().trim();
							if(pName!=null&& !pName.isEmpty()){		
								infos.add("Package Name: " + pName);
							}
						}
					}
				}

			}
		}

		return infos;
	}

	public InfoData readInfo() throws StatAlgoImporterServiceException {
		ItemDescription infoItem = project.getProjectTarget().getProjectCompile().getIntegrationInfo();
		FilesStorage filesStorage = new FilesStorage();
		InputStream inputStream = filesStorage.retrieveItemOnWorkspace(serviceCredentials.getUserName(),
				infoItem.getId());
		Reader inputReader = new InputStreamReader(inputStream);
		BufferedReader br = new BufferedReader(inputReader);

		String thisLine = null;
		InfoData infoData = new InfoData();
		try {
			while ((thisLine = br.readLine()) != null) {
				if (!thisLine.isEmpty()) {
					if (thisLine.startsWith("Username: ")) {
						infoData.setUsername(thisLine.substring(10));
					} else {
						if (thisLine.startsWith("Full Name: ")) {
							infoData.setFullname(thisLine.substring(11));
						} else {
							if (thisLine.startsWith("Email: ")) {
								infoData.setEmail(thisLine.substring(7));
							} else {
								if (thisLine.startsWith("Language: ")) {
									infoData.setLanguage(thisLine.substring(10));
								} else {
									if (thisLine.startsWith("Algorithm Name: ")) {
										infoData.setAlgorithmName(thisLine.substring(16));
									} else {
										if (thisLine.startsWith("Class Name: ")) {
											infoData.setClassName(thisLine.substring(12));
										} else {
											if (thisLine.startsWith("Algorithm Description: ")) {
												infoData.setAlgorithmDescription(thisLine.substring(23));
											} else {
												if (thisLine.startsWith("Algorithm Category: ")) {
													infoData.setAlgorithmCategory(thisLine.substring(20));
												} else {
													if (thisLine.startsWith("Interpreter Version: ")) {
														infoData.setInterpreterVersion(thisLine.substring(21));
													} else {
														
													}
												}
											}
										}
									}
								}
							}
						}
					}
				} else {
				}
			}
		} catch (IOException e) {
			logger.error("Error reading info.txt file: " + e.getLocalizedMessage(), e);
		}
		
		return infoData;

	}

}
