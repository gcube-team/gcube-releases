/**
 * 
 */
package org.gcube.portlets.admin.vredefinition.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author gioia
 *
 */
public class VREDefinition implements EntryPoint {
	
	public static final String CONTAINER_DIV = "VREDefinitionDIV";
		
	public void onModuleLoad() {
		
		HandlerManager eventBus = new HandlerManager(null);
		VREDefinitionServiceAsync rpcService = GWT.create(VREDefinitionService.class);
		AppController appController = new AppController(rpcService,eventBus);
		
		
		appController.go(RootPanel.get(CONTAINER_DIV));
		
		
	//	RootPanel.get(CONTAINER_DIV).add(view);
		
		//updateSize();
	}

	/**
	 * updateSize
	 */
	public void updateSize() {
		
		

	}
	
	
}
