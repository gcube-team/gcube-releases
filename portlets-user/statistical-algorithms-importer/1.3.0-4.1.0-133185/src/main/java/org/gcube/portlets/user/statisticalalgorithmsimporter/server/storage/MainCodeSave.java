package org.gcube.portlets.user.statisticalalgorithmsimporter.server.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception.StatAlgoImporterServiceException;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.Project;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.workspace.ItemDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class MainCodeSave {
	private static final String ALGORITHM_DESCRIPTION = "Algorithm";
	private static final String ALGORITHM_MIMETYPE = "text/plain";

	public static final Logger logger = LoggerFactory
			.getLogger(MainCodeSave.class);

	public MainCodeSave() {

	}

	/**
	 * 
	 * @param aslSession
	 * @param file
	 * @param code
	 * @param project
	 * @throws StatAlgoImporterServiceException
	 */
	public void save(ASLSession aslSession, ItemDescription file,
			String code, Project project) throws StatAlgoImporterServiceException{
		FilesStorage filesStorage = new FilesStorage();
		filesStorage.saveStringInItem(aslSession.getUsername(), file.getId(), code);
	}
	
	
	/**
	 * 
	 * @param aslSession
	 * @param file description of destination file
	 * @param code code to insert in the file
	 * @param project 
	 * @return ItemDescription
	 * @throws StatAlgoImporterServiceException
	 */
	public ItemDescription saveNew(ASLSession aslSession, ItemDescription file,
			String code, Project project)
			throws StatAlgoImporterServiceException {
		Path tempFile = createTempFile(file, code);
		ItemDescription mainCode = saveInWorkspace(tempFile, aslSession, file,
				project);
		return mainCode;
	}


	private Path createTempFile(ItemDescription file, String code)
			throws StatAlgoImporterServiceException {
		try {
			Path tempFile = Files.createTempFile(file.getName(), "");

			Files.write(tempFile, code.getBytes(), StandardOpenOption.WRITE);
			logger.debug(tempFile.toString());

			return tempFile;

		} catch (IOException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage(),
					e);
		}
	}

	private ItemDescription saveInWorkspace(Path tempFile,
			ASLSession aslSession, ItemDescription file, Project project)
			throws StatAlgoImporterServiceException {
		FilesStorage filesStorage = new FilesStorage();
		WorkspaceItem mainCodeItem;

		try {
			mainCodeItem = filesStorage.createItemOnWorkspace(
					aslSession.getUsername(),
					Files.newInputStream(tempFile, StandardOpenOption.READ),
					file.getName(), ALGORITHM_DESCRIPTION, ALGORITHM_MIMETYPE,
					project.getProjectFolder().getFolder().getId());
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage(),
					e);
		}

		ItemDescription mainCode;
		try {
			mainCode = new ItemDescription(mainCodeItem.getId(),
					mainCodeItem.getName(), mainCodeItem.getOwner()
							.getPortalLogin(), mainCodeItem.getPath(),
					mainCodeItem.getType().name());
		} catch (InternalErrorException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());

		}

		return mainCode;

	}

}
