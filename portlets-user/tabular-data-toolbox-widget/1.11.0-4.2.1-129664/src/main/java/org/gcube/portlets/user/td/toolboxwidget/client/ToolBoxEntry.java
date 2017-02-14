package org.gcube.portlets.user.td.toolboxwidget.client;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class ToolBoxEntry implements EntryPoint {

	
	public void onModuleLoad() {

		EventBus eventBus = new SimpleEventBus();
		
		ToolBoxPanel toolBoxPanel = new ToolBoxPanel("ToolBoxPanel", eventBus);
		
		RootPanel.get().add(toolBoxPanel);
		Log.info("ToolBoxPanel Added:" + toolBoxPanel);

	}
}
