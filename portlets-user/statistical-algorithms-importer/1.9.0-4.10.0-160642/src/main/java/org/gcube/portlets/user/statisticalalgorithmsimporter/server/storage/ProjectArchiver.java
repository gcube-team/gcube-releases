package org.gcube.portlets.user.statisticalalgorithmsimporter.server.storage;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
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

	public static final Logger logger = LoggerFactory
			.getLogger(ProjectArchiver.class);

	public static void archive(Project project, ServiceCredentials serviceCredentials)
			throws StatAlgoImporterServiceException {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		XMLEncoder xmlEncoder = new XMLEncoder(byteArrayOutputStream);
		xmlEncoder.writeObject(project);
		xmlEncoder.close();
		logger.debug("Archived:" + byteArrayOutputStream);

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
				byteArrayOutputStream.toByteArray());
		FilesStorage filesStorage = new FilesStorage();
		filesStorage.saveStatisticalAlgorithmProject(serviceCredentials.getUserName(),
				byteArrayInputStream, project.getProjectFolder()
						.getFolder().getId());

	}

	public static boolean existProjectInFolder(
			ItemDescription newProjectFolder, ServiceCredentials serviceCredentials)
			throws StatAlgoImporterServiceException {
		FilesStorage filesStorage = new FilesStorage();
		return filesStorage.existProjectItemOnWorkspace(
				serviceCredentials.getUserName(), newProjectFolder.getId());

	}

	public static Project readProject(ItemDescription newProjectFolder,
			ServiceCredentials serviceCredentials) throws StatAlgoImporterServiceException {
		FilesStorage filesStorage = new FilesStorage();
		InputStream inputStream = filesStorage.retrieveProjectItemOnWorkspace(
				serviceCredentials.getUserName(), newProjectFolder.getId());

		XMLDecoder xmlDecoder = new XMLDecoder(inputStream);
		Project project = (Project) xmlDecoder.readObject();
		xmlDecoder.close();

		WorkspaceItem projectFolderItem = filesStorage
				.retrieveItemInfoOnWorkspace(serviceCredentials.getUserName(),
						newProjectFolder.getId());
		try {
			newProjectFolder.setId(projectFolderItem.getId());
			newProjectFolder.setName(projectFolderItem.getName());
			newProjectFolder.setOwner(projectFolderItem.getOwner()
					.getPortalLogin());
			newProjectFolder.setPath(projectFolderItem.getPath());
			newProjectFolder.setType(projectFolderItem.getType().name());
		} catch (InternalErrorException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());

		}

		if (project != null
				&& project.getProjectFolder() != null
				&& newProjectFolder.compareInfo(project.getProjectFolder()
						.getFolder())) {

		} else {
			project.setProjectFolder(new ProjectFolder(newProjectFolder));
			archive(project, serviceCredentials);
		}

		if (project != null && project.getMainCode() != null
				&& project.getMainCode().getItemDescription() != null) {
			WorkspaceItem mainCodeItem = filesStorage
					.retrieveItemInfoOnWorkspace(serviceCredentials.getUserName(),
							project.getMainCode().getItemDescription().getId());
			ItemDescription newMainCodeItemDescription;
			try {
				newMainCodeItemDescription = new ItemDescription(
						mainCodeItem.getId(), mainCodeItem.getName(),
						mainCodeItem.getOwner().getPortalLogin(),
						mainCodeItem.getPath(), mainCodeItem.getType().name());
			} catch (InternalErrorException e) {
				logger.error(e.getLocalizedMessage());
				e.printStackTrace();
				throw new StatAlgoImporterServiceException(
						e.getLocalizedMessage());

			}
			if (newMainCodeItemDescription.compareInfo(project.getMainCode()
					.getItemDescription())) {
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
