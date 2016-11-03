/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.server.csv;

import java.io.File;

import javax.servlet.http.HttpSession;

import org.gcube.portlets.user.csvimportwizard.client.progress.OperationProgress;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public interface CSVTarget {
	
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
	public void importCSV(HttpSession session, File csvFile, String fileName, CSVParserConfiguration parserConfiguration, boolean[] columnToImportMask, OperationProgress operationProgress);

}
