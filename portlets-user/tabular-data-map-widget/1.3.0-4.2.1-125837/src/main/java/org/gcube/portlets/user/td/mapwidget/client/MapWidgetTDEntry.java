package org.gcube.portlets.user.td.mapwidget.client;

import org.gcube.portlets.user.td.gwtservice.shared.user.UserInfo;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.TabResourceType;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class MapWidgetTDEntry  implements EntryPoint  {

	
	public void onModuleLoad() {
		TRId trId=new TRId("10",TabResourceType.STANDARD, "20");
		UserInfo userInfo=new UserInfo();
		userInfo.setUsername("test.user");
		
		SimpleEventBus eventBus=new SimpleEventBus();
		MapWidgetTD  mapCreationWizard= new MapWidgetTD(trId, userInfo,"Map Creation",eventBus); 
		Log.info(mapCreationWizard.getId());
	}
}
