package org.gcube.portlets.user.td.openwidget.client;



import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class TDOpenEntry  implements EntryPoint  {

	
	public void onModuleLoad() {
		TRId trId=new TRId();
		SimpleEventBus eventBus=new SimpleEventBus();
		TDOpen tdopen= new TDOpen(trId,"Open Tabular Resource",eventBus); 
		Log.info(tdopen.getId());
	}
}
