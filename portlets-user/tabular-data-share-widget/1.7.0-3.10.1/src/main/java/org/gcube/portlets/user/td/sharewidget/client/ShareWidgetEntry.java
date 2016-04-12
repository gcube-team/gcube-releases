package org.gcube.portlets.user.td.sharewidget.client;



import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class ShareWidgetEntry  implements EntryPoint  {

	
	public void onModuleLoad() {
		TRId trId=new TRId("1");
		EventBus eventBus=new SimpleEventBus();
		
		@SuppressWarnings("unused")
		TRShare trShare=new TRShare(trId,eventBus);
		Log.info("Hello!");
	}
}
