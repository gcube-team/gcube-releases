
package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client;

import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.view.GCubeCkanDataCatalogPanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;


/**
 * The Class GCubeCkanDataCatalog.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 4, 2016
 */
public class GCubeCkanDataCatalog implements EntryPoint {

	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	public static final GcubeCkanDataCatalogServiceAsync service = GWT.create(GcubeCkanDataCatalogService.class);
	public static final String CKAN_LOGUT_SERVICE = GWT.getModuleBaseURL() +"gcubeckanlogout";
	private final String DIV_PORTLET_ID = "gCubeCkanDataCatalog";
	private CkanEventHandlerManager eventManager = new CkanEventHandlerManager();
	public static final String GET_PATH_PARAMETER = "path";
	public static final String GET_QUERY_PARAMETER = "query";
	public static final String REVERT_QUERY_PARAM = "manage";
	public static final String GCUBE_CKAN_IFRAME = "gcube-ckan-iframe";

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		GCubeCkanDataCatalogPanel panel =new GCubeCkanDataCatalogPanel(RootPanel.get(DIV_PORTLET_ID), eventManager.getEventBus());
		eventManager.setPanel(panel);
	}

}
