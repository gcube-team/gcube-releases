package org.gcube.portlets.user.td.sdmxexportwidget.client;



import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class SDMXExportWizardTDEntry  implements EntryPoint  {


	public void onModuleLoad() {
		SimpleEventBus eventBus=new SimpleEventBus();
		SDMXExportWizardTD exportWizard= new SDMXExportWizardTD("SDMXExport",eventBus); 
		Log.info(exportWizard.getId());
	}
}
