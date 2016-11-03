
package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client;

import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.view.CKanLeaveFrame;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.view.GCubeCkanDataCatalogPanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.ui.RootPanel;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GCubeCkanDataCatalog implements EntryPoint {

	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	public static final GcubeCkanDataCatalogServiceAsync service = GWT.create(GcubeCkanDataCatalogService.class);
	public static final String CKAN_LOGUT_SERVICE = GWT.getModuleBaseURL() + "gcubeckanlogout";

	private final String DIV_PORTLET_ID = "gCubeCkanDataCatalog";
	private CkanEventHandlerManager eventManager = new CkanEventHandlerManager();
	private CKanLeaveFrame frame;
	public static final String GET_PATH_PARAMETER = "path";
	public static final String GET_QUERY_PARAMETER = "query";
	public static final String GCUBE_CKAN_IFRAME = "gcube-ckan-iframe";

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		Window.addWindowClosingHandler(new Window.ClosingHandler() {

			@Override
			public void onWindowClosing(ClosingEvent closingEvent) {
				// invoke logout
				frame.setUrl(GCubeCkanDataCatalog.CKAN_LOGUT_SERVICE);
			}
		});

		GCubeCkanDataCatalogPanel panel = new GCubeCkanDataCatalogPanel(RootPanel.get(DIV_PORTLET_ID), eventManager.getEventBus());
		eventManager.setPanel(panel);

		frame = new CKanLeaveFrame();
		DOM.appendChild(RootPanel.getBodyElement(), frame.getElement());
	}
}
