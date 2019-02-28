package org.gcube.portlets.user.td.gwtservice.server.opexecution;

import java.util.HashMap;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.td.gwtservice.server.file.CSVFileUploadSession;
import org.gcube.portlets.user.td.gwtservice.server.storage.FilesStorage;
import org.gcube.portlets.user.td.gwtservice.server.trservice.OperationDefinitionMap;
import org.gcube.portlets.user.td.gwtservice.server.util.ServiceCredentials;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.csv.CSVImportSession;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.file.HeaderPresence;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation Execution for CSV Import
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class OpExecution4CSVImport extends OpExecutionBuilder {
	private static Logger logger = LoggerFactory
			.getLogger(OpExecution4CSVImport.class);

	private TabularDataService service;
	private CSVImportSession csvImportSession;
	private ServiceCredentials serviceCredentials;
	private CSVFileUploadSession fileUploadSession;

	public OpExecution4CSVImport(ServiceCredentials serviceCredentials,
			TabularDataService service, CSVImportSession csvImportSession,
			CSVFileUploadSession fileUploadSession) {
		this.service = service;
		this.csvImportSession = csvImportSession;
		this.serviceCredentials = serviceCredentials;
		this.fileUploadSession = fileUploadSession;
	}

	@Override
	public void buildOpEx() throws TDGWTServiceException {
		logger.debug("CSV Import: " + csvImportSession);

			String user = serviceCredentials.getUserName();
			logger.debug("Session User:" + user);

			String fileIdOnStorage = loadCSVFileOnStorage(user,
					fileUploadSession, csvImportSession);

			fileUploadSession.getCsvFile().delete();

			OperationDefinition operationDefinition = OperationDefinitionMap
					.map(OperationsId.CSVImport.toString(), service);

			Map<String, Object> map = new HashMap<String, Object>();
			map.put(Constants.PARAMETER_URL, fileIdOnStorage);
			map.put(Constants.PARAMETER_SEPARATOR, String
					.valueOf(fileUploadSession.getParserConfiguration()
							.getDelimiter()));// ','
			map.put(Constants.PARAMETER_ENCODING,
					fileUploadSession.getParserConfiguration().getCharset()
							.name());// "UTF-8"
			boolean hasHeader = true;
			if (fileUploadSession.getParserConfiguration().getHeaderPresence() == HeaderPresence.NONE) {
				hasHeader = false;
			}

			map.put(Constants.PARAMETER_HASHEADER, hasHeader);// true
			map.put(Constants.PARAMETER_FIELDMASK,
					csvImportSession.getColumnToImportMask());// Column Mask
			map.put(Constants.PARAMETER_SKIPERROR,
					csvImportSession.isSkipInvalidLines());

			OperationExecution invocation = new OperationExecution(
					operationDefinition.getOperationId(), map);

			operationExecutionSpec.setOp(invocation);

		
	}

	/**
	 * 
	 * @param user
	 * @param fileUploadSession
	 * @param csvImportSession
	 * @throws TDGWTServiceException
	 */
	private String loadCSVFileOnStorage(String user,
			CSVFileUploadSession fileUploadSession,
			CSVImportSession csvImportSession) throws TDGWTServiceException {
		String fileIdOnStorage = null;

		logger.debug("File Storage Access");

		logger.debug("CSVImportSession skip:"
				+ csvImportSession.isSkipInvalidLines());

		FilesStorage filesStorage = new FilesStorage();

		fileIdOnStorage = filesStorage.storageCSVTempFile(user,
				fileUploadSession.getCsvFile());
		logger.debug("File Url On Storage:" + fileIdOnStorage);

		if (fileIdOnStorage == null || fileIdOnStorage.isEmpty()) {
			throw new TDGWTServiceException(
					"Tabular Data Service error loading file on storage");
		}

		return fileIdOnStorage;
	}

}
