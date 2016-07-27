package org.gcube.portlets.user.td.chartswidget.client;

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
public class ChartsWidgetTDEntry  implements EntryPoint  {

	
	public void onModuleLoad() {
		TRId trId=new TRId("58",TabResourceType.STANDARD, "1286");
		UserInfo userInfo=new UserInfo();
		userInfo.setUsername("test.user");
		
		SimpleEventBus eventBus=new SimpleEventBus();
		ChartsWidgetTD chartCreationWizard= new ChartsWidgetTD(trId, userInfo,"Chart Creation",eventBus); 
		Log.info(chartCreationWizard.getId());
		
		
	}
}
