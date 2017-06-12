/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.server.csv;

import java.io.File;
import java.util.Arrays;
import java.util.Random;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.portlets.user.csvimportwizard.client.progress.OperationProgress;
import org.gcube.portlets.user.csvimportwizard.client.progress.OperationState;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class DemoCSVTarget implements CSVTarget {
	
	protected Logger logger = LoggerFactory.getLogger(DemoCSVTarget.class);
	/**
	 * {@inheritDoc}
	 */
	public String getId() {
		return "DemoCSVTarget";
	}

	/**
	 * {@inheritDoc}
	 */
	public void importCSV(HttpSession session, File csvFile, String fileName, CSVParserConfiguration parserConfiguration, boolean[] columnToImportMask, final OperationProgress operationProgress) {
		
		logger.trace("importCSV csvFile: "+csvFile+" fileName: "+fileName+" parserConfiguration: "+parserConfiguration+" columnToImportMask: "+Arrays.toString(columnToImportMask));

		
		
		Thread th = new Thread(new Runnable() {
			
			public void run() {
				operationProgress.setTotalLenght(100);
				operationProgress.setState(OperationState.INPROGRESS);
				Random r = new Random();
				for (int i =0; i<100;i+=r.nextInt(101-i)) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					operationProgress.setElaboratedLenght(i);
				}
				operationProgress.setState(OperationState.COMPLETED);
			}
		});
		
		th.start();

	}

}
