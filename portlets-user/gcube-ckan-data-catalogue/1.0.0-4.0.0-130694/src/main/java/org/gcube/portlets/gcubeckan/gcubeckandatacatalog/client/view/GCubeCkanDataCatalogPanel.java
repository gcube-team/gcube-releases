/**
 *
 */

package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.view;

import java.util.Map;

import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.GCubeCkanDataCatalog;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.resource.CkanPortletResources;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared.CkanConnectorAccessPoint;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared.CkanRole;
import org.gcube.portlets.widgets.sessionchecker.client.CheckSession;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * The Class GCubeCkanDataCatalogPanel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Jun 9, 2016
 */
public class GCubeCkanDataCatalogPanel extends BaseViewTemplate {

	private CkanMetadataManagementPanel managementPanel;
	private ScrollPanel centerScrollable = new ScrollPanel();
	private CkanFramePanel ckanFramePanel;
	private CkanOrganizationsPanel ckanOrganizationsPanel;
	private Image loading = new Image(CkanPortletResources.ICONS.loading());
	private RootPanel rootPanel;
	private HandlerManager eventBus;
	private CkanConnectorAccessPoint ckanAccessPoint;
	public static final int IFRAME_FIX_HEIGHT = 1800;

	/**
	 * Instantiates a new g cube ckan data catalog panel.
	 *
	 * @param rootPanel
	 *            the root panel
	 * @param eventManager
	 *            the event manager
	 */
	public GCubeCkanDataCatalogPanel(
			RootPanel rootPanel, HandlerManager eventManager) {

		this.rootPanel = rootPanel;
		this.eventBus = eventManager;
		ckanFramePanel = new CkanFramePanel(eventBus);
		managementPanel = new CkanMetadataManagementPanel(eventBus);
		ckanOrganizationsPanel = new CkanOrganizationsPanel(this);
		initPanel();
		setTopPanelVisible(true);
		String pathParameter =
				Window.Location.getParameter(GCubeCkanDataCatalog.GET_PATH_PARAMETER);
		String queryParameter =
				Window.Location.getParameter(GCubeCkanDataCatalog.GET_QUERY_PARAMETER);

		GCubeCkanDataCatalog.service.getCKanConnector(
				pathParameter, queryParameter,
				new AsyncCallback<CkanConnectorAccessPoint>() {

					@Override
					public void onSuccess(CkanConnectorAccessPoint ckan) {

						ckanAccessPoint = ckan;
						instanceCkanFrame(ckan.buildURI());
						GCubeCkanDataCatalogPanel.this.rootPanel.remove(loading);

						// now perform the other requests
						GCubeCkanDataCatalog.service.outsidePortal(new AsyncCallback<Boolean>() {

							@Override
							public void onSuccess(Boolean result) {

								if (result) {
									// the portlet is outside the portal and no user is logged
									// in
									// show only home and statistics
									managementPanel.doNotShowUserRelatedInfo();
								}
								else {
									// polling for session expired check
									CheckSession.getInstance().startPolling();

									// RETRIEVE USER'S ORGANIZATIONS
									GCubeCkanDataCatalog.service.getCkanOrganizationsNamesAndUrlsForUser(new AsyncCallback<Map<String, String>>() {

										@Override
										public void onSuccess(Map<String, String> result) {

											if (result != null)
												ckanOrganizationsPanel.setOrganizations(result);
										}

										@Override
										public void onFailure(Throwable caught) {

										}
									});

									// MANAGE CKAN MANAGEMENT PANEL ACCORDING TO MY ROLE
									GCubeCkanDataCatalog.service.getMyRole(new AsyncCallback<CkanRole>() {

										@Override
										public void onFailure(Throwable caught) {
											showEditInsertButtons(false); 
										}

										@Override
										public void onSuccess(CkanRole result) {
											switch (result) {
											case ADMIN:
												showEditInsertButtons(true);
												break;
											case EDITOR:
												showEditInsertButtons(false); // because the editor has some limitations TODO
												break;
											case MEMBER:
												showEditInsertButtons(false);
												break;
											default:
												showEditInsertButtons(false); 
												break;
											}
										}
									});
								}
							}

							@Override
							public void onFailure(Throwable caught) {

								// the portlet is outside the portal and no user is logged in
								// show only home and statistics
								managementPanel.doNotShowUserRelatedInfo();
							}
						});
					}

					@Override
					public void onFailure(Throwable caught) {

						GCubeCkanDataCatalogPanel.this.rootPanel.remove(loading);
						Window.alert("Sorry, An error occurred during contacting Gcube Ckan Data Catalogue!");
					}
				});


		Window.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {

				GWT.log("onWindowResized width: " + event.getWidth() +
						" height: " + event.getHeight());
				updateSize();
			}
		});

		rootPanel.add(loading);
		rootPanel.add(this);
		updateSize();

		listenForPostMessage();
	}

	/**
	 * Gets the top panel height.
	 *
	 * @return the top panel height
	 */
	public int getTopPanelHeight() {

		if (managementPanel.isVisible())
			return managementPanel.getCurrentHeight();
		return 0;
	}

	/**
	 * Sets the top panel visible.
	 *
	 * @param bool
	 *            the new top panel visible
	 */
	public void setTopPanelVisible(boolean bool) {

		managementPanel.setVisible(bool);
		updateSize();
	}

	/**
	 * show or hide edit/insert buttons according to the role.
	 *
	 * @param show
	 *            the show
	 */
	public void showEditInsertButtons(boolean show) {

		managementPanel.showInsertAndEditProductButtons(show);
	}

	/**
	 * Instance ckan frame.
	 *
	 * @param ckanUrlConnector
	 *            the ckan url connector
	 * @return the frame
	 */
	public Frame instanceCkanFrame(String ckanUrlConnector) {

		ckanFramePanel.setVisible(true);
		ckanOrganizationsPanel.setVisible(false);
		return ckanFramePanel.instanceFrame(ckanUrlConnector);
	}

	/**
	 * Inits the panel.
	 */
	private void initPanel() {

		setTopPanelVisible(false);
		addToTop(managementPanel);
		VerticalPanel containerIntoScrollPanel = new VerticalPanel();
		containerIntoScrollPanel.setWidth("100%");
		containerIntoScrollPanel.add(ckanFramePanel);
		containerIntoScrollPanel.add(ckanOrganizationsPanel);
		centerScrollable.add(containerIntoScrollPanel);
		ckanOrganizationsPanel.setVisible(false);
		addToMiddle(centerScrollable);
	}

	/**
	 * Update window size.
	 */
	public void updateSize() {
		/*RootPanel workspace = rootPanel;
		int topBorder = workspace.getAbsoluteTop();
		GWT.log("top: "+topBorder);
		int footer = 30; // 85 footer is bottombar + sponsor
		int rootHeight = Window.getClientHeight() - topBorder - 5 - footer;
		int height = rootHeight - getTopPanelHeight();
		if (ckanFramePanel.getFrame() != null) {
			int newH =managementPanel != null && managementPanel.getCurrentHeight() > 0 ? managementPanel.getOffsetHeight() + height : height;
			ckanFramePanel.getFrame().setHeight(2000+"px");
		}*/

		RootPanel workspace = this.rootPanel;
		int topBorder = workspace.getAbsoluteTop();
		int footer = 55;
		int rootHeight = Window.getClientHeight() - topBorder - 5 - footer;
		int height = rootHeight - getTopPanelHeight();
		if (this.ckanFramePanel.getFrame() != null) {
			int newH = this.managementPanel != null &&this.managementPanel.getCurrentHeight() > 0? this.managementPanel.getOffsetHeight() + height : height;
			this.ckanFramePanel.getFrame().setHeight(newH + "px");
		}
		//		workspace.setHeight(height+"px");
	}

	/**
	 * Post message.
	 *
	 * @param msg the msg
	 */
	protected native void postMessage(String msg) /*-{
	  $wnd.postMessage(msg, "*");
	}-*/;

	/**
	 * Listen for post message.
	 */
	private final native void listenForPostMessage() /*-{
	  var that = this;
	  $wnd.addEventListener("message", function(msg) {
	  	console.log("read message...");
	  	that.@org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.view.GCubeCkanDataCatalogPanel::onPostMessage(Ljava/lang/String;Ljava/lang/String;)(msg.data, msg.origin);
	  });
	}-*/;

	/**
	 * On post message.
	 *
	 * @param data the data
	 * @param origin the origin
	 */
	private void onPostMessage(String data, String origin) {
		GWT.log("Read data: "+data+", from origin: "+origin);
		GWT.log("Ckan base url: "+ckanAccessPoint.getBaseUrl());

		if (ckanAccessPoint.getBaseUrl().indexOf(origin)>=0) {
			GWT.log("Data has been sent by ckan "+origin +", Is it the height?");
			// The data has been sent from your site
			// The data sent with postMessage is stored in event.data
			setIFrameHeight(data);
		} else {
			// The data hasn't been sent from your site!
			// Be careful! Do not use it.
			return;
		}

	}

	/**
	 * Sets the i frame height.
	 *
	 * @param height the new i frame height
	 */
	private void setIFrameHeight(String height){
		String parsedHeight = null;
		if(height==null || height.isEmpty())
			return;

		if(height.contains("px")){
			parsedHeight = height;
		}else{
			try{
				int intH = Integer.parseInt(height);
				parsedHeight = intH + " px";
			}catch(Exception e ){

			}
		}

		if(parsedHeight!=null){
			GWT.log("Setting new height for ckan iFrame: "+height);
			this.ckanFramePanel.getFrame().setHeight(height);
		}
	}

	/**
	 * Gets the base urlckan connector.
	 *
	 * @return the base urlckan connector
	 */
	public String getBaseURLCKANConnector() {

		return ckanAccessPoint.getBaseUrlWithContext();
	}

	/**
	 * Gets the gcube token value to ckan connector.
	 *
	 * @return the gcube token value to ckan connector
	 */
	public String getGcubeTokenValueToCKANConnector() {

		return ckanAccessPoint.getGcubeTokenValue();
	}

	/**
	 * Gets the path info.
	 *
	 * @return the path info
	 */
	public String getPathInfo() {

		return ckanAccessPoint.getPathInfoParameter();
	}

	/**
	 * Show the organizations panel.
	 */
	public void showOrganizations() {

		ckanOrganizationsPanel.setVisible(true);
		ckanFramePanel.setVisible(false);
	}
}
