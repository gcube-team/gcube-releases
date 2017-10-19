package org.gcube.portlets.widgets.file_dw_import_wizard.server.file;
/**
 * 
 */


import java.io.File;
import java.util.Random;

import javax.servlet.http.HttpSession;

import org.gcube.portlets.widgets.file_dw_import_wizard.client.progress.OperationProgress;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.progress.OperationState;
import org.gcube.portlets.widgets.file_dw_import_wizard.shared.FileType;




public class DemoCSVTarget implements Target {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getId() {
		return "DemoCSVTarget";
	}







	@Override
	public void importFile(HttpSession session, File file, String fileName,
			FileType type, File generatedTaxa, File generatedVernacular,
			boolean[] columnToImportMask, final OperationProgress operationProgress) {

		Thread th = new Thread(new Runnable() {
			
			@Override
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
