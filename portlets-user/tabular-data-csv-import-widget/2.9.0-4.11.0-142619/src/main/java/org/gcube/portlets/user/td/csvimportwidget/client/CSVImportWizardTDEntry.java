package org.gcube.portlets.user.td.csvimportwidget.client;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class CSVImportWizardTDEntry  implements EntryPoint  {

	
	public void onModuleLoad() {
		SimpleEventBus eventBus=new SimpleEventBus();
		CSVImportWizardTD  importWizard= new CSVImportWizardTD("CSVImport",eventBus); 
		Log.info(importWizard.getId());
	}
}
