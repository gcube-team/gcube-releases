package org.gcube.portlets.user.td.tablewidget.client;



import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class TableWidgetEntry  implements EntryPoint  {

	
	public void onModuleLoad() {
		Log.info("Hello!");
		EventBus eventBus= new SimpleEventBus();
		TRId trId=new TRId("1");
		
		
		CloneTabularResource cloneTabularResource= new CloneTabularResource(trId, eventBus);
		cloneTabularResource.cloneTR();
		
		
	}
}
