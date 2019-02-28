package org.gcube.portlets.user.statisticalalgorithmsimporter.server.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.gcube.portlets.user.statisticalalgorithmsimporter.server.util.ServiceCredentials;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception.StatAlgoImporterServiceException;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.workspace.ItemDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class CodeSave {
	private static final String ALGORITHM_DESCRIPTION = "Algorithm";
	private static final String ALGORITHM_MIMETYPE = "text/plain";

	public static final Logger logger = LoggerFactory.getLogger(CodeSave.class);

	public CodeSave() {

	}

	public void save(ServiceCredentials serviceCredentials, ItemDescription file, String code)
			throws StatAlgoImporterServiceException {
		FilesStorage filesStorage = new FilesStorage();
		filesStorage.saveStringInItem(serviceCredentials.getUserName(), file.getId(), code);
	}

	public ItemDescription saveNew(ServiceCredentials serviceCredentials, ItemDescription file, String code,
			String folderId) throws StatAlgoImporterServiceException {
		Path tempFile = createTempFile(file, code);
		ItemDescription codeItem = saveInWorkspace(tempFile, serviceCredentials, file, folderId);
		return codeItem;
	}

	private Path createTempFile(ItemDescription file, String code) throws StatAlgoImporterServiceException {
		try {
			Path tempFile = Files.createTempFile(file.getName(), "");

			Files.write(tempFile, code.getBytes(), StandardOpenOption.WRITE);
			logger.debug(tempFile.toString());

			return tempFile;

		} catch (IOException e) {
			logger.error(e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage(), e);
		}
	}

	private ItemDescription saveInWorkspace(Path tempFile, ServiceCredentials serviceCredentials, ItemDescription file,
			String folderId) throws StatAlgoImporterServiceException {
		FilesStorage filesStorage = new FilesStorage();
		ItemDescription item;

		try {
			item = filesStorage.createItemOnWorkspace(serviceCredentials.getUserName(),
					Files.newInputStream(tempFile, StandardOpenOption.READ), file.getName(), ALGORITHM_DESCRIPTION,
					ALGORITHM_MIMETYPE, folderId);
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage(), e);
		}

		return item;

	}

}
