package org.gcube.portlets.user.td.sdmximportwidget.client;



import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class SDMXImportWizardTDEntry  implements EntryPoint  {

	
	public void onModuleLoad() {
		SimpleEventBus eventBus=new SimpleEventBus();
		SDMXImportWizardTD importWizard= new SDMXImportWizardTD("SDMXImport",eventBus); 
		Log.info(importWizard.getId());
	}
}
