package org.gcube.portlets.user.csvimportwizard.server;

import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.portlets.user.csvimportwizard.client.data.AvailableCharsetList;
import org.gcube.portlets.user.csvimportwizard.client.data.CSVRowError;
import org.gcube.portlets.user.csvimportwizard.client.progress.OperationProgress;
import org.gcube.portlets.user.csvimportwizard.client.rpc.CSVImportService;
import org.gcube.portlets.user.csvimportwizard.client.rpc.CSVImportServiceException;
import org.gcube.portlets.user.csvimportwizard.server.csv.CSVFileUtil;
import org.gcube.portlets.user.csvimportwizard.server.csv.CSVImportSession;
import org.gcube.portlets.user.csvimportwizard.server.csv.CSVImportSessionManager;
import org.gcube.portlets.user.csvimportwizard.server.csv.CSVTarget;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class CSVImportServiceImpl extends RemoteServiceServlet implements CSVImportService {

	private static final long serialVersionUID = 1733737412247481074L;

	protected Logger logger = LoggerFactory.getLogger(CSVImportServiceImpl.class);

	
	/**
	 * {@inheritDoc}
	 * @throws CSVImportServiceException 
	 */
	public String createCSVSessionId(String targetId) throws CSVImportServiceException {
		logger.trace("createCSVSessionId targetId: "+targetId);
		try {
			return CSVImportSessionManager.getInstance().createImportSession(targetId).getId();
		} catch (Exception e)
		{
			logger.error("An error occurred creating the session", e);
			throw new CSVImportServiceException("An error occurred creating the session" + e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public OperationProgress getLocalUploadStatus(String sessionId) {
		logger.debug("getLocalUploadStatus sessionId: "+sessionId);
		CSVImportSession session = CSVImportSessionManager.getInstance().getSession(sessionId);
		return session.getUploadProgress();
	}

	/**
	 * {@inheritDoc}
	 */
	public AvailableCharsetList getAvailableCharset(String sessionId) {
		String defaultEncoding = Charset.defaultCharset().displayName();
		ArrayList<String> charsetList = new ArrayList<String>(Charset.availableCharsets().keySet());
		return new AvailableCharsetList(charsetList, defaultEncoding);
	}



	/**
	 * {@inheritDoc}
	 * @throws CSVImportServiceException 
	 */
	public ArrayList<String> configureCSVParser(String sessionId, String encoding, boolean hasHeader, char delimiter, char comment) throws CSVImportServiceException {
		logger.trace("configureCSVParser sessionId: "+sessionId+" encoding: "+encoding+" hasHeader: "+hasHeader+" delimiter: "+delimiter+" comment: "+comment);

		CSVImportSession session = CSVImportSessionManager.getInstance().getSession(sessionId);
		session.getParserConfiguration().update(encoding, delimiter, comment, hasHeader);

		try {
			return CSVFileUtil.getHeader(session.getCsvFile(), session.getParserConfiguration());
		} catch (Exception e) {
			logger.error("Error retrieving the CSV header", e);
			throw new CSVImportServiceException("Error calculating the CSV header: "+e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 * @throws CSVImportServiceException 
	 */
	public ArrayList<CSVRowError> checkCSV(String sessionId, long errorsLimit) throws CSVImportServiceException {
		logger.trace("checkCSV sessionId: "+sessionId+" errorsLimit: "+errorsLimit);

		CSVImportSession session = CSVImportSessionManager.getInstance().getSession(sessionId);

		try {
			return CSVFileUtil.checkCSV(session.getCsvFile(), session.getParserConfiguration(), errorsLimit);
		} catch (Exception e) {
			logger.error("Error checking the CSV file", e);
			throw new CSVImportServiceException("Error checking the CSV file: "+e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public OperationProgress getImportStatus(String sessionId) {
		logger.trace("getImportStatus sessionId: "+sessionId);
		CSVImportSession session = CSVImportSessionManager.getInstance().getSession(sessionId);
		OperationProgress progress = session.getImportProgress();
		logger.trace("progress: "+progress);
		return progress;
	}

	/**
	 * {@inheritDoc}
	 */
	public void startImport(String sessionId, boolean[] columnToImportMask) {
		logger.trace("startImport sessionId: "+sessionId);
		CSVImportSession session = CSVImportSessionManager.getInstance().getSession(sessionId);
		CSVTarget csvTarget = session.getTarget();
		HttpSession httpSession = getThreadLocalRequest().getSession();
		csvTarget.importCSV(httpSession, session.getCsvFile(), session.getCsvName(), session.getParserConfiguration(), columnToImportMask, session.getImportProgress());
	}

}
