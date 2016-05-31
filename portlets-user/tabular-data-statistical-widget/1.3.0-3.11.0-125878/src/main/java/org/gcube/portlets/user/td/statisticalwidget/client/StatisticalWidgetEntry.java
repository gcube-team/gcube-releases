package org.gcube.portlets.user.td.statisticalwidget.client;



import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class StatisticalWidgetEntry  implements EntryPoint  {

	
	public void onModuleLoad() {
		EventBus eventBus= new SimpleEventBus();
	
		
		TRId trId=new TRId();
		//For example Tabular Resource 7 and table 402
		trId.setId("7");
		trId.setTableId("402");
		
		new StatisticalWidget(trId,eventBus);
	
		
		Log.info("Hello!");
	}
}
