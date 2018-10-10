package org.gcube.portlets.user.databasesmanager.client;

import org.gcube.portlets.user.databasesmanager.client.panels.GxtBorderLayoutPanel;
import org.gcube.portlets.user.databasesmanager.client.resources.Resources;
import org.gcube.portlets.user.databasesmanager.shared.Constants;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author "Loredana Liccardo  loredana.liccardo@isti.cnr.it"
 * 
 */

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class DatabasesManager implements EntryPoint {

	private GxtBorderLayoutPanel borderLayout;
	public static Resources resources = GWT.create(Resources.class);

	/**
	 * This is the entry point method.
	 */

	public void onModuleLoad() {
		
		//CheckSession.getInstance().startPolling();

		HandlerManager eventBus = new HandlerManager(this);

		// create the RPC service

		GWTdbManagerServiceAsync service = (GWTdbManagerServiceAsync) GWT
				.create(GWTdbManagerService.class);

		ServiceDefTarget serviceDef = (ServiceDefTarget) service;
		serviceDef.setServiceEntryPoint(GWT.getModuleBaseURL()
				+ "dbManagerService");

		try {
			borderLayout = new GxtBorderLayoutPanel(eventBus, service);

			RootPanel.get(Constants.CONTENTDIV).add(borderLayout);

			Window.addResizeHandler(new ResizeHandler() {
				 @Override
				public void onResize(ResizeEvent event) {
					 //print check
//					System.out.println("onWindowResized width: "
//							+ event.getWidth() + " height: "
//							+ event.getHeight());
					 
					updateSize();
				}
			});

			updateSize();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Update window size
	 */
	public void updateSize() {

		RootPanel rootPanel = RootPanel.get(Constants.CONTENTDIV);

		int topBorder = rootPanel.getAbsoluteTop();

		int leftBorder = rootPanel.getAbsoluteLeft();

		int footer = 85; // footer is bottombar + sponsor

		// int rootHeight = (Window.getClientHeight() - topBorder - 4) ;// -
		// ((footer == null)?0:(footer.getOffsetHeight()-15));

		// if (rootHeight > 550)
		// rootHeight = 550;

		int rootHeight = (Window.getClientHeight() - topBorder - 4 - footer);// -
																				// ((footer
																				// ==
																				// null)?0:(footer.getOffsetHeight()-15));

		if (rootHeight < 550)
			rootHeight = 550;

		int rootWidth = Window.getClientWidth() - 2 * leftBorder; // -
																	// rightScrollBar;
        //print check
//		System.out.println("New workspace dimension Height: " + rootHeight
//				+ " Width: " + rootWidth);

		borderLayout.setHeight(rootHeight);
		borderLayout.setWidth(rootWidth);

	}
}
