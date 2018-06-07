package org.gcube.portlets.widgets.file_dw_import_wizard.server.file;
/**
 * 
 */


import java.io.File;

import org.gcube.portlets.widgets.file_dw_import_wizard.client.progress.OperationProgress;
import org.gcube.portlets.widgets.file_dw_import_wizard.shared.FileType;



public class ImportSessions {
	
	
	/**
	 * The import session id.
	 */
	protected String id;
	
	/**
	 * The import state.
	 */
	protected FileType type;
	protected ImportStatus status;
	protected File generatedTaxa;
	protected File generatedVernacular;
	/**
	 * The  import progress (in the webserver).
	 */
	protected OperationProgress uploadProgress;

	protected File File;
	protected String Name;
	
	
	protected OperationProgress importProgress;
	
	protected Target target;
	
	

	

	public ImportSessions(String id, Target target, FileType type) {
		this.id = id;
		this.type=type;
		this.target = target;
		this.status = ImportStatus.CREATED;
		this.uploadProgress = new OperationProgress();
		this.importProgress = new OperationProgress();
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	public File getFile() {
		return File;
	}

	public void setFile(File File) {
		this.File = File;
	}
	public File getGeneratedTaxaFile() {
		return generatedTaxa;
	}

	public void setGeneratedTaxaFile(File File) {
		this.generatedTaxa = File;
	}
	public File getGeneratedVernacularFile() {
		return generatedVernacular;
	}

	public void setGeneratedVernacular(File File) {
		this.generatedVernacular = File;
	}

	public String getName() {
		return Name;
	}

	public void setName(String Name) {
		this.Name = Name;
	}
	public FileType getType() {
		return type;
	}

	public void   setType(FileType type) {
		this.type = type;
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
	public void setStatus(ImportStatus status) {
		this.status = status;
	}

//	/**
//	 * @return the parserConfiguration
//	 */
////	public CSVParserConfiguration getParserConfiguration() {
////		return parserConfiguration;
////	}
////
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
	public Target getTarget() {
		return target;
	}
}
