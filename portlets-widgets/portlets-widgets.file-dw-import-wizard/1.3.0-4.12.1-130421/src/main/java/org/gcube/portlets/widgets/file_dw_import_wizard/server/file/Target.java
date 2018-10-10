/**
 * 
 */
package org.gcube.portlets.widgets.file_dw_import_wizard.server.file;



import java.io.File;

import javax.servlet.http.HttpSession;

import org.gcube.portlets.widgets.file_dw_import_wizard.client.progress.OperationProgress;
import org.gcube.portlets.widgets.file_dw_import_wizard.shared.FileType;



public interface Target {
	
	/**
	 * Returns the target id.
	 * @return
	 */
	public String getId();
	
	/**
	 * Start the CSV import.
	 * @param session the current session.
	 * @param csvFile the CSV file.
	 * @param fileName the source file name.
	 * @param parserConfiguration the parser configuration.
	 * @param columnToImportMask a mask for column to import selection
	 * @param operationProgress
	 */
	public void importFile(HttpSession session, File file, String fileName, FileType type , File generatedTaxa ,File generatedVernacular,boolean[] columnToImportMask, OperationProgress operationProgress);



}
