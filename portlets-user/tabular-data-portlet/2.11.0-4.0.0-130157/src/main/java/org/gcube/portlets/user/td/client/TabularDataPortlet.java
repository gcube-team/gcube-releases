package org.gcube.portlets.user.td.client;


import org.gcube.portlets.user.gcubewidgets.client.ClientScopeHelper;
import org.gcube.portlets.user.td.client.ribbon.TabularDataRibbon;
import org.gcube.portlets.user.td.client.rpc.TabularDataService;
import org.gcube.portlets.user.td.client.rpc.TabularDataServiceAsync;
import org.gcube.portlets.user.td.mainboxwidget.client.MainBoxPanel;
import org.gcube.portlets.user.td.toolboxwidget.client.ToolBoxPanel;
import org.gcube.portlets.user.td.wizardwidget.client.util.UtilsGXT3;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.Viewport;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class TabularDataPortlet implements EntryPoint {

	private static final String JSP_TAG_ID = "tdp";

	
	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	@SuppressWarnings("unused")
	private final TabularDataServiceAsync greetingService = GWT
			.create(TabularDataService.class);

	// Main Panel
	private static BorderLayoutContainer mainPanelLayout;

	
	/**
	 * {@inheritDoc}
	 */
	public void onModuleLoad() {

		/*
		 * Install an UncaughtExceptionHandler which will produce
		 * <code>FATAL</code> log messages
		 */
		Log.setUncaughtExceptionHandler();

		// use deferred command to catch initialization exceptions in
		// onModuleLoad2
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
				loadScope();
			}
		});

	}
	
	private void loadScope() {
		ClientScopeHelper.getService().setScope(Location.getHref(),
				new AsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
					    if(result){
					    	loadMainPanel();
					    } else {
					    	UtilsGXT3.info("Attention", "ClientScopeHelper has returned a false value!");
					    }
					}

					@Override
					public void onFailure(Throwable caught) {	
						UtilsGXT3.alert("Error", "Error setting scope: "+caught.getLocalizedMessage());
						caught.printStackTrace();
					}
				});

	}

	private void loadMainPanel() {		
		TabularDataController controller = new TabularDataController();
		EventBus eventBus = controller.getEventBus();

		// Layout
		mainPanelLayout = new BorderLayoutContainer();
		mainPanelLayout.setId("mainPanelLayout");
		mainPanelLayout.setBorders(true);

		// Ribbon Menu
		TabularDataRibbon tabularDataRibbon = new TabularDataRibbon(eventBus);
		TabPanel ribbonBarPanel = tabularDataRibbon.getContainer();

		BorderLayoutData ribbonBarData = new BorderLayoutData(105);
		ribbonBarData.setMargins(new Margins(5));
		ribbonBarData.setCollapsible(false);
		ribbonBarData.setSplit(false);

		mainPanelLayout.setNorthWidget(ribbonBarPanel, ribbonBarData);

		// MainBox Panel
		final MainBoxPanel mainBoxPanel = new MainBoxPanel("MainBoxPanel",
				eventBus);
		controller.setMainBoxPanel(mainBoxPanel);
		
		MarginData gridData = new MarginData();
		mainPanelLayout.setCenterWidget(mainBoxPanel, gridData);
		
		// ToolBox Panel
		final ToolBoxPanel toolBoxPanel = new ToolBoxPanel("ToolBoxPanel", eventBus);
		
		BorderLayoutData westData = new BorderLayoutData(310);
		westData.setCollapsible(true);
		westData.setSplit(false);
		westData.setFloatable(false);
		westData.setCollapseMini(true);
		westData.setMargins(new Margins(0, 5, 0, 5));
		westData.setCollapseHidden(true);
		
		
		mainPanelLayout.setWestWidget(toolBoxPanel, westData);
		toolBoxPanel.expand();
		toolBoxPanel.enable();

		controller.setToolBoxPanel(toolBoxPanel);
		controller.setWestData(westData);

		bind(mainPanelLayout);
		// menu(gridPanel);
		controller.restoreUISession();
	}

	private void bind(BorderLayoutContainer mainWidget) {
		try {
			RootPanel root = RootPanel.get(JSP_TAG_ID);
			Log.info("Root Panel: " + root);
			if (root == null) {
				Log.info("Div with id " + JSP_TAG_ID
						+ " not found, starting in dev mode");
				Viewport viewport = new Viewport();
				viewport.setWidget(mainWidget);
				viewport.onResize();
				RootPanel.get().add(viewport);
			} else {
				Log.info("Application div with id " + JSP_TAG_ID
						+ " found, starting in portal mode");
				PortalViewport viewport = new PortalViewport();
				Log.info("Created Viewport");
				viewport.setEnableScroll(false);
				viewport.setWidget(mainWidget);
				Log.info("Set Widget");
				Log.info("getOffsetWidth(): " + viewport.getOffsetWidth());
				Log.info("getOffsetHeight(): " + viewport.getOffsetHeight());
				viewport.onResize();
				root.add(viewport);
				Log.info("Added viewport to root");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("Error in attach viewport:" + e.getLocalizedMessage());
		}
	}

}
