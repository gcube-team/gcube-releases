/**
 *
 */

package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.datacatalogue.ckanutillibrary.shared.RolesCkanGroupOrOrg;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.GCubeCkanDataCatalog;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowRevertOperationWidgetEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.resource.CkanPortletResources;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared.BeanUserInOrgGroupRole;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared.CkanConnectorAccessPoint;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Cookies;
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
	private CkanGroupsPanel ckanGroupsPanel;
	private Image loading = new Image(CkanPortletResources.ICONS.loading());
	private RootPanel rootPanel;
	private HandlerManager eventBus;
	private CkanConnectorAccessPoint ckanAccessPoint;
	private boolean isManageProductToShow = false;
	private static String latestSelectedProductIdentifier;
	public static final int IFRAME_FIX_HEIGHT = 1800;
	private JSONObject obj;

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
		
		//postMessage(obj.toString(),  ckanFramePanel.getFrame());
		// send message about gateway url
		obj = new JSONObject();
		String landingPageVREs = Window.Location.getProtocol() + "//" + Window.Location.getHostName() + "/explore";
		JSONString value = new JSONString(landingPageVREs);
		obj.put("explore_vres_landing_page", value);
		
		ckanFramePanel = new CkanFramePanel(eventBus);
		managementPanel = new CkanMetadataManagementPanel(eventBus);
		ckanOrganizationsPanel = new CkanOrganizationsPanel(this);
		ckanGroupsPanel = new CkanGroupsPanel(this);
		initPanel();
		setTopPanelVisible(true);
		
		// decode parameters (they could have been encoded)
		final Map<String, String> paramsMap = new HashMap<String, String>(2);
		String queryParameters = Window.Location.getQueryString();
		if(queryParameters != null && !queryParameters.isEmpty()){
			String decoded = URL.decodeQueryString(queryParameters); // equals should be encoded too (%3D)
			String[] params = decoded.substring(decoded.indexOf("?") + 1).split("&");
			for (int i = 0; i < params.length; i++) {
				String[] queryAndValue = params[i].split("=");
				paramsMap.put(queryAndValue[0], queryAndValue[1]);
			}
			GWT.log("Extracted parameters are " + paramsMap);
		}

		String pathParameter = paramsMap.get(GCubeCkanDataCatalog.GET_PATH_PARAMETER); //Window.Location.getParameter(GCubeCkanDataCatalog.GET_PATH_PARAMETER);
		String queryParameter = paramsMap.get(GCubeCkanDataCatalog.GET_QUERY_PARAMETER);// Window.Location.getParameter(GCubeCkanDataCatalog.GET_QUERY_PARAMETER);

		GCubeCkanDataCatalog.service.getCKanConnector(
				pathParameter, queryParameter,
				new AsyncCallback<CkanConnectorAccessPoint>() {

					@Override
					public void onSuccess(CkanConnectorAccessPoint ckan) {

						if(ckan.isOutsideFromPortal()){

							// the portlet is outside the portal and no user is logged
							// in show only home and statistics
							managementPanel.doNotShowUserRelatedInfo();

							// set the cookie as session cookie (removed on browser close)
							Cookies.setCookie("ckan_hide_header", "true", null, ".d4science.org", "/", false);

						}

						// set the iframe url
						ckanAccessPoint = ckan;
						instanceCkanFrame(ckan.buildURI());
						GCubeCkanDataCatalogPanel.this.rootPanel.remove(loading);

						if(!ckan.isOutsideFromPortal()){

							// MANAGE CKAN MANAGEMENT PANEL ACCORDING TO MY ROLE
							GCubeCkanDataCatalog.service.getMyRole(new AsyncCallback<RolesCkanGroupOrOrg>() {

								@Override
								public void onFailure(Throwable caught) {
									showEditInsertButtons(false);
								}

								@Override
								public void onSuccess(RolesCkanGroupOrOrg result) {
									switch (result) {
									case ADMIN:
										showEditInsertButtons(true);
										break;
									case EDITOR:
										showEditInsertButtons(true);
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
							
							// check if management buttons need to be removed
							GCubeCkanDataCatalog.service.isViewPerVREEnabled(new AsyncCallback<String>() {
								
								@Override
								public void onSuccess(String result) {
									
									if(result != null && !result.isEmpty()){
										// hide all management buttons
										managementPanel.removeGenericManagementButtons();
										
										// set real relative path
										ckanAccessPoint.addPathInfo(result);
									}
									
								}
								
								@Override
								public void onFailure(Throwable caught) {
									
									// ?
									
								}
							});
							
							// retrieve organizations
							GCubeCkanDataCatalog.service.getCkanOrganizationsNamesAndUrlsForUser(new AsyncCallback<List<BeanUserInOrgGroupRole>>() {

								@Override
								public void onSuccess(List<BeanUserInOrgGroupRole> result) {
									ckanOrganizationsPanel.setOrganizations(result);
								}

								@Override
								public void onFailure(Throwable caught) {
									// an error message will be displayed
									ckanOrganizationsPanel.setOrganizations(null);
								}
							});

							// retrieve groups
							GCubeCkanDataCatalog.service.getCkanGroupsNamesAndUrlsForUser(new AsyncCallback<List<BeanUserInOrgGroupRole>>() {

								@Override
								public void onSuccess(List<BeanUserInOrgGroupRole> result) {
									ckanGroupsPanel.setGroups(result);
								}

								@Override
								public void onFailure(Throwable caught) {
									ckanGroupsPanel.setGroups(null);
								}
							});
							
							// check if the url encodes a revert operation to be performed
							if(paramsMap.containsKey(GCubeCkanDataCatalog.REVERT_QUERY_PARAM) && 
									paramsMap.get(GCubeCkanDataCatalog.REVERT_QUERY_PARAM).equals("true")){
								
								eventBus.fireEvent(new ShowRevertOperationWidgetEvent(Window.Location.getHref()));
								
							}
								
							/**
							 * Just check if it is enabled.. then we need to listen for dom events coming
							 */
							GCubeCkanDataCatalog.service.isManageProductEnabled(new AsyncCallback<Boolean>() {

								@Override
								public void onSuccess(Boolean result) {
									isManageProductToShow = result;
									managementPanel.showManageProductButton(isManageProductToShow);

								}

								@Override
								public void onFailure(Throwable caught) {
									isManageProductToShow = false;
									managementPanel.showManageProductButton(isManageProductToShow);
								}
							});

						}

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

		// listen for DOM messages
		listenForPostMessage();

	}

	public static String getLatestSelectedProductIdentifier(){
		return latestSelectedProductIdentifier;
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
		ckanGroupsPanel.setVisible(false);
		return ckanFramePanel.instanceFrame(ckanUrlConnector, obj.toString(), ckanAccessPoint.getBaseUrl());
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
		containerIntoScrollPanel.add(ckanGroupsPanel);
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
	 * Print a message
	 * @param string
	 */
	protected native void printString(String string) /*-{
		console.log(string);
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
		printString("Read data: "+data+", from origin: "+origin);
		printString("Ckan base url: "+ckanAccessPoint.getBaseUrl());

		// parsing data.. it is a json bean of the type
		printString("Incoming message is " + data + " from " + origin);

		if (ckanAccessPoint.getBaseUrl().indexOf(origin)>=0) {
			// The data has been sent from your site
			// The data sent with postMessage is stored in event.data
			String height = null;
			String productId = null;
			boolean isProductKeyMissing = false;

			try{
				JSONValue parsedJSON = JSONParser.parseStrict(data);
				JSONObject object = parsedJSON.isObject();
				GWT.log("Object is " + object);
				if(object != null){
					height = object.get("height").isString().stringValue();
					if(object.containsKey("product"))
						productId = object.get("product").isString().stringValue();
					else
						isProductKeyMissing = true;
				}
			}catch(Exception e){
				GWT.log("Exception is " + e);
			}

			if(height != null)
				setIFrameHeight(height.toString());

			// show or hide the manage product button
			if(!isProductKeyMissing){
				latestSelectedProductIdentifier = productId.toString();
				managementPanel.enableShareItemButton(productId != null && !productId.isEmpty());
				managementPanel.enableManageProductButton(productId != null && !productId.isEmpty() && isManageProductToShow);
			}
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
	 * Return the catalogue url (e.g. http://ckan-d-d4s.d4science.org:443/)
	 * @return
	 */
	public String getCatalogueUrl(){

		printString("Base url for iframe is " + ckanAccessPoint.getCatalogueBaseUrl());
		return ckanAccessPoint.getCatalogueBaseUrl();
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
		ckanGroupsPanel.setVisible(false);
		ckanFramePanel.setVisible(false);
	}

	/**
	 * Show the groups panel.
	 */
	public void showGroups() {

		ckanGroupsPanel.setVisible(true);
		ckanOrganizationsPanel.setVisible(false);
		ckanFramePanel.setVisible(false);
	}
	
	/**
	 * Show management panel
	 * @param show
	 */
	public void showManagementPanel(boolean show){
		
		managementPanel.showManageProductButton(show);
		
	}
}
