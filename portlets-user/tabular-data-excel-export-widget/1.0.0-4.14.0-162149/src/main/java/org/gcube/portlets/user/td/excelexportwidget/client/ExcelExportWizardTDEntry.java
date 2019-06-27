package org.gcube.portlets.user.td.excelexportwidget.client;



import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 * 
 * @author Giancarlo Panichi 
 * 
 *
 */
public class ExcelExportWizardTDEntry  implements EntryPoint  {


	public void onModuleLoad() {
		SimpleEventBus eventBus=new SimpleEventBus();
		ExcelExportWizardTD exportWizard= new ExcelExportWizardTD("ExcelExport",eventBus); 
		Log.info(exportWizard.getId());
	}
}
