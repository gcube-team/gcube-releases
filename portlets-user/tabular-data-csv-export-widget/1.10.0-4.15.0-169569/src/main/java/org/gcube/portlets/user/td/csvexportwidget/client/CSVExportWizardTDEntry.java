package org.gcube.portlets.user.td.csvexportwidget.client;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 *
 * @author Giancarlo Panichi
 *   
 *
 */
public class CSVExportWizardTDEntry  implements EntryPoint  {
	
	
	private CSVExportWizardTDMessages msgs;

	public void onModuleLoad() {
		initMessage();
		SimpleEventBus eventBus=new SimpleEventBus();
		CSVExportWizardTD  exportWizard= new CSVExportWizardTD(msgs.csvExportWizardHead(),eventBus); 
		Log.info(exportWizard.getId());
	}
	
	protected void initMessage(){
		msgs = GWT.create(CSVExportWizardTDMessages.class);
	}
}
