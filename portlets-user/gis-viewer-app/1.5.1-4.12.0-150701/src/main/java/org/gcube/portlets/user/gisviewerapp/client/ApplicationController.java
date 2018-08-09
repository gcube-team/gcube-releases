/**
 *
 */
package org.gcube.portlets.user.gisviewerapp.client;

import java.util.Date;

import org.gcube.portlets.user.gisviewer.client.Constants;
import org.gcube.portlets.user.gisviewer.client.DataPanelOpenListener;
import org.gcube.portlets.user.gisviewer.client.GisViewerPanel;
import org.gcube.portlets.user.gisviewer.client.GisViewerParameters;
import org.gcube.portlets.user.gisviewerapp.client.resources.Images;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.fx.Resizable;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.RootPanel;


/**
 * The Class ApplicationController.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 17, 2017
 */
public class ApplicationController {

	/**
	 *
	 */
	private GisViewerPanel gisViewerPanel; // for normal mode
//	private GCubeGisViewerPanel gisViewerPanel; // for gcube mode
	private ContentPanel gisViewerContentPanel;
	private LayoutContainer mainPanel;
	private GisViewerParameters gisViewerParameters;
	private BaloonPanel balloonWMS;
	private LayoutContainer lcWMS = new LayoutContainer();
	private WmsRequestConverter wmsRequestConverter;
	@SuppressWarnings("unused")
	private final ApplicationController INSTANCE = this;
	@SuppressWarnings("unused")
	private RootPanel gwtRootPanel;
	private static final String GCUBE_COOKIE_SHOW_WARNING_FOR_WPS_DATA_POINT_QUERY= "GCUBE-Cookie_GeoWPSQuery_DataPoint_Authorization";
	private static final String GCUBE_COOKIE_SHOW_WARNING_FOR_WPS_BOX_QUERY = "GCUBE-Cookie_GeoWPSQuery_Box_Authorization";
	public static final long MILLISECS_PER_DAY = 1000L * 60L * 60L * 24L;

	/**
	 * Instantiates a new application controller.
	 */
	public ApplicationController() {
		mainPanel = new LayoutContainer();
		mainPanel.setLayout(new FitLayout());
		initGisViewerParameters();
//		gisViewerPanel = new GCubeGisViewerPanel(gisViewerParameters);
		gisViewerPanel = new GisViewerPanel(gisViewerParameters);
		initGisViewerContentPanel();
		gisViewerContentPanel.add(gisViewerPanel);
		wmsRequestConverter = new WmsRequestConverter(gisViewerPanel);
		mainPanel.add(gisViewerContentPanel);
	}

	/**
	 * Inits the gis viewer parameters.
	 */
	private void initGisViewerParameters(){

		gisViewerParameters = new GisViewerParameters();
		gisViewerParameters.setProjection("");
		gisViewerParameters.setOpenDataPanelAtStart(false);

		gisViewerParameters.setDataPanelOpenListener(new DataPanelOpenListener() {
			@Override
			public void dataPanelOpen(boolean isOpen, int panelHeight) {
				int dataPanelHeight = isOpen ? panelHeight+24 : 24;
				int gisViewerHeight;

				if(gisViewerContentPanel.getHeight()>Constants.minGisViewerHeight) {
					gisViewerHeight = gisViewerContentPanel.getHeight();
				}
				else {
					gisViewerHeight = Constants.minGisViewerHeight+dataPanelHeight;
				}
				gisViewerContentPanel.setHeight(gisViewerHeight);
			}
		});

	}

	/**
	 * Inits the gis viewer content panel.
	 */
	private void initGisViewerContentPanel(){

		gisViewerContentPanel = new ContentPanel() {

			@Override
			protected void onExpand() {
				super.onExpand();
				if (gisViewerPanel!=null) {
					gisViewerPanel.updateOpenLayersSize();
				}
			}

			@Override
			protected void onCollapse() {
				super.onCollapse();
			}
		};

		gisViewerContentPanel.setLayout(new FitLayout());
		gisViewerContentPanel.setHeading(ConstantGisViewerApp.GIS_VIEWER_APP);
		gisViewerContentPanel.setHeaderVisible(false);
		gisViewerContentPanel.setCollapsible(true);

		@SuppressWarnings("unused")
		Resizable r = new Resizable(gisViewerContentPanel, "s");
	}

	/**
	 * Go.
	 *
	 * @param rootPanel the root panel
	 */
	public void go(final RootPanel rootPanel) {
		this.gwtRootPanel = rootPanel;
		rootPanel.add(mainPanel);

		String wmsRequest = Window.Location.getParameter(ConstantGisViewerApp.GET_WMS_PARAMETER);
		String uuid = Window.Location.getParameter(ConstantGisViewerApp.GET_UUID_PARAMETER);
		String layerTitle = Window.Location.getParameter(ConstantGisViewerApp.GET_LAYER_TITLE);
		GWT.log(ConstantGisViewerApp.GET_WMS_PARAMETER+ " = "+wmsRequest);
		GWT.log(ConstantGisViewerApp.GET_UUID_PARAMETER+ " = "+uuid);
		GWT.log(ConstantGisViewerApp.GET_LAYER_TITLE+ " = "+layerTitle);

		boolean displayWarning = readCookieWPSQueryAuthorization(GCUBE_COOKIE_SHOW_WARNING_FOR_WPS_DATA_POINT_QUERY);
		GWT.log("Display "+GCUBE_COOKIE_SHOW_WARNING_FOR_WPS_DATA_POINT_QUERY+"? "+displayWarning);

		if(displayWarning){
			gisViewerPanel.getToolBarPanel().getClickDataToggle().addSelectionListener(new SelectionListener<ButtonEvent>() {

				@Override
				public void componentSelected(ButtonEvent ce) {

					if(!gisViewerPanel.getToolBarPanel().getClickDataToggle().isPressed())
						return;

					boolean warning = readCookieWPSQueryAuthorization(GCUBE_COOKIE_SHOW_WARNING_FOR_WPS_DATA_POINT_QUERY);
					if(warning)
						showWarningForWPSService("Warning: gCube authorization required", "You must be authorized to perform the 'Data point query'. Some data could be not available because you are not authorized to contact gCube WPS service", GCUBE_COOKIE_SHOW_WARNING_FOR_WPS_DATA_POINT_QUERY);
				}
			});
		}

		 displayWarning = readCookieWPSQueryAuthorization(GCUBE_COOKIE_SHOW_WARNING_FOR_WPS_BOX_QUERY);
		 GWT.log("Display "+GCUBE_COOKIE_SHOW_WARNING_FOR_WPS_BOX_QUERY+"? "+displayWarning);

		 if(displayWarning){
			gisViewerPanel.getToolBarPanel().getBoxDataToggle().addSelectionListener(new SelectionListener<ButtonEvent>() {

				@Override
				public void componentSelected(ButtonEvent ce) {

					if(!gisViewerPanel.getToolBarPanel().getBoxDataToggle().isPressed())
						return;

					boolean warning = readCookieWPSQueryAuthorization(GCUBE_COOKIE_SHOW_WARNING_FOR_WPS_BOX_QUERY);
					if(warning)
						showWarningForWPSService("Warning: gCube authorization required", "You must be authorized to perform the 'Data box query'. Some data could be not available because you are not authorized to contact gCube WPS service", GCUBE_COOKIE_SHOW_WARNING_FOR_WPS_BOX_QUERY);

				}
			});
		}

		if(wmsRequest!=null && !wmsRequest.isEmpty()){

			try {
				wmsRequestConverter.addRequestToGisViewer(wmsRequest,layerTitle, uuid);
			} catch (Exception e) {
				GWT.log("An error occurred on adding wmsrequest :"+wmsRequest);
				e.printStackTrace();
			}
		}

		rootPanel.add(lcWMS);
		lcWMS.setId("WMS");

		Command cmd = new Command() {

			@Override
			public void execute() {
				final DialogBox box = new DialogBox(true);
				box.setText("Add External WMS Layer");
				box.getElement().getStyle().setZIndex(10000);

				WmsForm form = new WmsForm() {

					@Override
					public void closeHandler() {
						box.hide();
					}

					@Override
					public void subtmitHandler(String title, String name, String wmsRequest) {

						//TODO UPDATE
						gisViewerPanel.addLayerByWms(title, name, wmsRequest, false, false, null);
						box.hide();
					}
				};
				box.setWidget(form);
//				box.setSize("auto", "auto");
				box.center();
			}
		};

		balloonWMS =  new BaloonPanel("+WMS", false, cmd);

		SetZIndex setZIndex = new SetZIndex();
		safeFunctionCallOn(lcWMS,setZIndex);
	}


	/**
	 * Show warning for wps service.
	 *
	 * @param title the title
	 * @param msg the msg
	 * @param cookieName the cookie name
	 */
	private void showWarningForWPSService(String title, String msg, final String cookieName){

		final DialogResult dialog = new DialogResult(Images.iconWarning().createImage(), title, msg);
		dialog.getElement().getStyle().setZIndex(10000);

		dialog.getCloseButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				if(dialog.getCheckShowAgain().getValue())
					setCookie(cookieName, "false", 30);
			}
		});
		dialog.center();
	}

	/**
	 * Read cookie workspace available features.
	 *
	 * @param cookieName the cookie name
	 * @return true if exists a cookie with msg as true value (or not exists the cookie), false otherwise
	 */
	private boolean readCookieWPSQueryAuthorization(String cookieName) {

		//get the cookie with name GCBUEWorkspaceGridViewSetting
		String msg = Cookies.getCookie(cookieName);
		//if null, there was no cookie
		if(msg == null){
			setCookie(cookieName, "true", 30);
			return true;
		}

		if(msg.compareTo("true")==0)
			return true;

		return false;
	}

	/**
	 * Sets the cookie.
	 *
	 * @param name the name
	 * @param value the value
	 * @param days the days
	 */
	public static void setCookie(String name, String value, int days) {

		if (value == null) {
			Cookies.removeCookie(name);
			return;
		}

		// Now
		Date expiringDate = new Date();
		// Now + days
		expiringDate.setTime(expiringDate.getTime() + MILLISECS_PER_DAY * days);
		Cookies.setCookie(name, value, expiringDate);
	}


	/**
	 * Decode url with param delimiter.
	 *
	 * @param wmsRequest the wms request
	 * @param delimiter the delimiter
	 * @return the string
	 */
	@SuppressWarnings("unused")
	private String decodeURLWithParamDelimiter(String wmsRequest, String delimiter){
		return wmsRequest.replaceAll(delimiter, "&");
	}

	/**
	 * Gets the main panel.
	 *
	 * @return the main panel
	 */
	public LayoutContainer getMainPanel() {
		return mainPanel;
	}

	/**
	 * Gets the gis viewer panel.
	 *
	 * @return the gis viewer panel
	 */
	public GisViewerPanel getGisViewerPanel() {
		return gisViewerPanel;
	}

	/**
	 * Move wms balloon position.
	 */
	public void moveWMSBalloonPosition(){
		balloonWMS.hide();
		balloonWMS.setVisible(false);
		balloonWMS.showRelativeTo(lcWMS);
		balloonWMS.setVisible(true);
	}

	/**
	 * Gets the baloon wms.
	 *
	 * @return the baloonWMS
	 */
	public BaloonPanel getBaloonWMS() {
		return balloonWMS;
	}

	/**
	 * Safe function call on a component, which was rendered or not.
	 *
	 * @param c
	 *            Component object that must be not null.
	 * @param f
	 *            Function object with the function that must be called.
	 */
	public static void safeFunctionCallOn(final Component c, final Function f) {
		c.enableEvents(true);
		if (c.isRendered()) {
			GWT.log("fire c.isRendered()");
			f.execute();
		} else {

			final Listener<ComponentEvent> lsnr = new Listener<ComponentEvent>() {

				@Override
				public void handleEvent(ComponentEvent be) {
					GWT.log("fire function.execute");
					f.execute();

				}
			};
			c.addListener(Events.Render, lsnr);
		}
	}

	/**
	 * The Interface Function.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * May 17, 2017
	 */
	public interface Function {

		/**
		 * Execute.
		 */
		public void execute();
	}

	/**
	 * The Class SetZIndex.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * May 17, 2017
	 */
	class SetZIndex implements Function {

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * org.gcube.portlets.user.gisviewerapp.client.ApplicationController
		 * .Function#execute()
		 */
		@Override
		public void execute() {

			final Timer tm = new Timer() {

				@Override
				public void run() {

					int zi = lcWMS.el().getZIndex();
					GWT.log("zindex: "+zi);
					int zIndex = zi<300?300:zi;
					balloonWMS.getElement().getStyle().setZIndex(zIndex);
				}
			};

			tm.schedule(1000);
		}
	}

}
