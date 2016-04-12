package org.gcube.portlets.admin.wfroleseditor.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.RootPanel;
/**
 * <code> WfRoles </code> class is the entrypoint component of this webapp
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version Feb 2012 (1.0) 
 */
public class WfRoles implements EntryPoint {
	public static final String PORTLET_DIV = "WfRoles";
	public void onModuleLoad() {
		WfRolesServiceAsync rpcService = GWT.create(WfRolesService.class);
		HandlerManager eventBus = new HandlerManager(null);
		AppController appViewer = new AppController(rpcService, eventBus);
		appViewer.go(RootPanel.get(PORTLET_DIV));

	}
}
