/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.server.csv;

import java.io.File;

import org.gcube.portlets.user.csvimportwizard.client.progress.OperationProgress;

/**
 * The CSV Wizard import session for the server side.
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class CSVImportSession {
	
	
	/**
	 * The import session id.
	 */
	protected String id;
	
	/**
	 * The import state.
	 */
	protected CSVImportStatus status;
	
	/**
	 * The csv import progress (in the webserver).
	 */
	protected OperationProgress uploadProgress;

	protected File csvFile;
	protected String csvName;
	
	protected CSVParserConfiguration parserConfiguration;
	
	protected OperationProgress importProgress;
	
	protected CSVTarget target;
	
	
	/*protected String lastErrorMessage;
	
	
	protected boolean[] columnsToImports;
	protected String csvTitle;
	
	protected long total;
	protected long progress;
	
	protected long numberOfLines;*/
	

	public CSVImportSession(String id, CSVTarget target) {
		this.id = id;
		this.target = target;
		this.status = CSVImportStatus.CREATED;
		this.uploadProgress = new OperationProgress();
		this.parserConfiguration = new CSVParserConfiguration();
		this.importProgress = new OperationProgress();
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	public File getCsvFile() {
		return csvFile;
	}

	public void setCsvFile(File csvFile) {
		this.csvFile = csvFile;
	}

	public String getCsvName() {
		return csvName;
	}

	public void setCsvName(String csvName) {
		this.csvName = csvName;
	}

	/**
	 * @return the uploadProgress
	 */
	public OperationProgress getUploadProgress() {
		return uploadProgress;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(CSVImportStatus status) {
		this.status = status;
	}

	/**
	 * @return the parserConfiguration
	 */
	public CSVParserConfiguration getParserConfiguration() {
		return parserConfiguration;
	}

	/**
	 * @return the importProgress
	 */
	public OperationProgress getImportProgress() {
		return importProgress;
	}

	/**
	 * @param importProgress the importProgress to set
	 */
	public void setImportProgress(OperationProgress importProgress) {
		this.importProgress = importProgress;
	}

	/**
	 * @return the target
	 */
	public CSVTarget getTarget() {
		return target;
	}
}
