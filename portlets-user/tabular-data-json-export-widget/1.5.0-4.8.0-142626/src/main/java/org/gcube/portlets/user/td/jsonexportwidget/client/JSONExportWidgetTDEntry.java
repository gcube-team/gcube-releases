package org.gcube.portlets.user.td.jsonexportwidget.client;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class JSONExportWidgetTDEntry  implements EntryPoint  {
	
	private JSONExportWizardTDMessages msgs;
	
	public void onModuleLoad() {
		initMessages();
		SimpleEventBus eventBus=new SimpleEventBus();
		JSONExportWidgetTD  exportWizard= new JSONExportWidgetTD(msgs.jsonExportWizardHead(),eventBus); 
		Log.info(exportWizard.getId());
	}
	
	protected void initMessages(){
		msgs = GWT.create(JSONExportWizardTDMessages.class);
	}
	
}
