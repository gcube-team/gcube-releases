package org.gcube.portlets.user.statisticalalgorithmsimporter.server.storage;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.gcube.portlets.user.statisticalalgorithmsimporter.server.util.ServiceCredentials;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception.StatAlgoImporterServiceException;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.MainCode;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.Project;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.ProjectFolder;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.workspace.ItemDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ProjectArchiver {

	public static final Logger logger = LoggerFactory.getLogger(ProjectArchiver.class);

	public static void archive(Project project, ServiceCredentials serviceCredentials)
			throws StatAlgoImporterServiceException {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		XMLEncoder xmlEncoder = new XMLEncoder(byteArrayOutputStream);
		xmlEncoder.writeObject(project);
		xmlEncoder.close();
		logger.debug("Archived:" + byteArrayOutputStream);

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
		FilesStorage filesStorage = new FilesStorage();
		filesStorage.saveStatisticalAlgorithmProject(serviceCredentials.getUserName(), byteArrayInputStream,
				project.getProjectFolder().getFolder().getId());

	}

	public static boolean existProjectInFolder(ItemDescription newProjectFolder, ServiceCredentials serviceCredentials)
			throws StatAlgoImporterServiceException {
		FilesStorage filesStorage = new FilesStorage();
		return filesStorage.existProjectItemOnWorkspace(serviceCredentials.getUserName(), newProjectFolder.getId());

	}

	public static Project readProject(ItemDescription newProjectFolder, ServiceCredentials serviceCredentials)
			throws StatAlgoImporterServiceException {
		FilesStorage filesStorage = new FilesStorage();
		InputStream inputStream = filesStorage.getProjectItemOnWorkspace(serviceCredentials.getUserName(),
				newProjectFolder.getId());

		XMLDecoder xmlDecoder = new XMLDecoder(inputStream);
		Project project = (Project) xmlDecoder.readObject();
		xmlDecoder.close();

		newProjectFolder = filesStorage.getFolderInfoOnWorkspace(serviceCredentials.getUserName(),
				newProjectFolder.getId());

		if (project != null && project.getProjectFolder() != null
				&& newProjectFolder.compareInfo(project.getProjectFolder().getFolder())) {

		} else {
			project.setProjectFolder(new ProjectFolder(newProjectFolder));
			archive(project, serviceCredentials);
		}

		if (project != null && project.getMainCode() != null && project.getMainCode().getItemDescription() != null) {
			ItemDescription newMainCodeItemDescription = filesStorage.getFileInfoOnWorkspace(
					serviceCredentials.getUserName(), project.getMainCode().getItemDescription().getId());
			String pLink = filesStorage.getPublicLink(serviceCredentials.getUserName(),
					newMainCodeItemDescription.getId());
			newMainCodeItemDescription.setPublicLink(pLink);
			if (newMainCodeItemDescription.compareInfo(project.getMainCode().getItemDescription())) {
			} else {
				project.setMainCode(new MainCode(newMainCodeItemDescription));
				archive(project, serviceCredentials);
			}

		} else {
			project.setProjectFolder(new ProjectFolder(newProjectFolder));
			archive(project, serviceCredentials);
		}

		return project;
	}

}
