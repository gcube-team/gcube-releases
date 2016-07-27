/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.server;

import java.io.File;

import javax.servlet.http.HttpSession;

import org.gcube.portlets.user.csvimportwizard.client.progress.OperationProgress;
import org.gcube.portlets.user.csvimportwizard.client.progress.OperationState;
import org.gcube.portlets.user.csvimportwizard.server.csv.CSVParserConfiguration;
import org.gcube.portlets.user.csvimportwizard.server.csv.CSVTarget;

/**
 * @author ceras
 *
 */
public class StatisticalCSVTarget implements CSVTarget {


	@Override
	public String getId() {
		return "StatisticalCSVTarget";
	}


	@Override
	public void importCSV(HttpSession session, File csvFile, String fileName, CSVParserConfiguration csvParserConfiguration, boolean[] columnToImportMask, OperationProgress operationProgress) {
		session.setAttribute("csvParserConfiguration", csvParserConfiguration);
		session.setAttribute("csvImportFilePath", csvFile.getAbsolutePath());
		System.out.println("CSV IMPORT SERVER OK, fileName: "+csvFile.getName());
		operationProgress.setState(OperationState.COMPLETED);
	}

}
