package org.gcube.portlets.user.statisticalalgorithmsimporter.client;

import org.gcube.portlets.user.statisticalalgorithmsimporter.client.maindata.MainDataPanel;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.resource.StatAlgoImporterResources;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.ribbon.StatAlgoImporterRibbon;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.rpc.StatAlgoImporterService;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.rpc.StatAlgoImporterServiceAsync;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.tools.ToolsPanel;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.RootPanel;
import com.sencha.gxt.core.client.util.Margins;
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
public class StatAlgoImporter implements EntryPoint {

	private static final String JSP_TAG_ID = "StatAlgoImporterPortlet";

	private static final int RIBBON_HEIGHT = 104;

	@SuppressWarnings("unused")
	private final StatAlgoImporterServiceAsync statAlgoImporterService = GWT
			.create(StatAlgoImporterService.class);

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
				loadMainPanel();
			}
		});

	}

	protected void loadMainPanel() {
		StatAlgoImporterResources.INSTANCE.saiStyles().ensureInjected();
		// ScriptInjector.fromString(AccountingManagerResources.INSTANCE.jqueryJs().getText()).setWindow(ScriptInjector.TOP_WINDOW).inject();

		StatAlgoImporterController controller = new StatAlgoImporterController();
		EventBus eventBus = controller.getEventBus();

		// Layout
		mainPanelLayout = new BorderLayoutContainer();
		mainPanelLayout.setId("mainPanelLayout");
		mainPanelLayout.setBorders(false);
		mainPanelLayout.setResize(true);
		
		// mainPanelLayout.getElement().getStyle().setBackgroundColor("rgb(3, 126, 207)");

		// Main
		final MainDataPanel mainDataPanel = new MainDataPanel(eventBus);
		MarginData mainData = new MarginData(new Margins(2));
		mainPanelLayout.setCenterWidget(mainDataPanel, mainData);

		// Menu
		StatAlgoImporterRibbon accountingManagerMenu = new StatAlgoImporterRibbon(
				eventBus);

		BorderLayoutData ribbonData = new BorderLayoutData(RIBBON_HEIGHT);
		ribbonData.setMargins(new Margins(2));
		ribbonData.setCollapsible(false);
		ribbonData.setSplit(false);

		mainPanelLayout.setNorthWidget(accountingManagerMenu.getContainer(),
				ribbonData);

		// Right
		ToolsPanel toolsPanel = new ToolsPanel(eventBus);
		BorderLayoutData eastData = new BorderLayoutData(500);
		eastData.setCollapsible(true);
		eastData.setSplit(false);
		eastData.setFloatable(false);
		eastData.setCollapseMini(true);
		eastData.setMargins(new Margins(0, 5, 0, 5));
		eastData.setCollapseHidden(false);
		
		
		// estData.setMaxSize(510);
		// estData.setMinSize(310);
		mainPanelLayout.setEastWidget(toolsPanel, eastData);
		toolsPanel.enable();
		toolsPanel.collapse();
		
		bind(mainPanelLayout);
		controller.setMainPanelLayout(mainPanelLayout);
		controller.restoreUISession();

	}

	protected void bind(BorderLayoutContainer mainWidget) {
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
