package org.gcube.portlets.admin.rolesmanagementportlet.gwt.client;

import org.gcube.portlets.admin.rolesmanagementportlet.gwt.client.interfaces.RolesManagementService;
import org.gcube.portlets.admin.rolesmanagementportlet.gwt.client.interfaces.RolesManagementServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.RootPanel;
import com.gwtext.client.widgets.Panel;

/**
 * The EntryPoint class
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class RolesManagement implements EntryPoint {

	public static RolesManagementServiceAsync rolesService = (RolesManagementServiceAsync) GWT.create(RolesManagementService.class);
	private static ServiceDefTarget endpoint = (ServiceDefTarget) rolesService;

	private Panel mainPanel = new Panel("Roles Management");

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "RolesManagementServlet");
		RootPanel root = RootPanel.get("RolesDiv");
		mainPanel.setWidth(1200);
		mainPanel.setAutoHeight(true);
		mainPanel.add(new AvailableRolesGrid());
		root.add(mainPanel);
		updateSize();

		 //Add a Resize handler for the window 
		com.google.gwt.user.client.Window.addResizeHandler(new ResizeHandler(){

			public void onResize(ResizeEvent event) {
				updateSize();
			}
		});
	}
	
	private void updateSize() {
		RootPanel root = RootPanel.get("RolesDiv");
		
		int leftBorder = root.getAbsoluteLeft();
		int rightScrollBar = 17;
		int rootWidth = com.google.gwt.user.client.Window.getClientWidth() - 2* leftBorder - rightScrollBar;
		
		if (rootWidth < 1200)
			mainPanel.setWidth(rootWidth);
		else
			mainPanel.setWidth(1200);
	}
	
	protected static void displayErrorWindow(String userMsg, Throwable caught) {
		ExceptionAlertWindow alertWindow = new ExceptionAlertWindow(userMsg, true);
		alertWindow.addDock(caught);
		int left = com.google.gwt.user.client.Window.getClientWidth()/2;
		int top = com.google.gwt.user.client.Window.getClientHeight()/2;
		alertWindow.setPopupPosition(left, top);
		alertWindow.show();
	}
}
