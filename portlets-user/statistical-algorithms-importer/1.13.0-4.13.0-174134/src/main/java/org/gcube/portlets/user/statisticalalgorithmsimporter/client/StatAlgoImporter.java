package org.gcube.portlets.user.statisticalalgorithmsimporter.client;

import org.gcube.portlets.user.statisticalalgorithmsimporter.client.resource.StatAlgoImporterResources;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.ribbon.StatAlgoImporterRibbon;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.rpc.StatAlgoImporterService;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.rpc.StatAlgoImporterServiceAsync;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.workarea.WorkAreaPanel;

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
 * @author Giancarlo Panichi
 *
 * 
 */
public class StatAlgoImporter implements EntryPoint {

	private static final String JSP_TAG_ID = "StatAlgoImporterPortlet";

	private static final int RIBBON_HEIGHT = 104;

	@SuppressWarnings("unused")
	private final StatAlgoImporterServiceAsync statAlgoImporterService = GWT
			.create(StatAlgoImporterService.class);

	// Main Panel
	private static BorderLayoutContainer workAreaPanelLayout;

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
				//loadScope();
				loadMainPanel();
			}
		});

	}


	private void loadMainPanel() {
		StatAlgoImporterResources.INSTANCE.saiStyles().ensureInjected();
		// ScriptInjector.fromString(AccountingManagerResources.INSTANCE.jqueryJs().getText()).setWindow(ScriptInjector.TOP_WINDOW).inject();

		StatAlgoImporterController controller = new StatAlgoImporterController();
		EventBus eventBus = controller.getEventBus();

		// Layout
		workAreaPanelLayout = new BorderLayoutContainer();
		//workAreaPanelLayout.setId("workAreaPanelLayout");
		workAreaPanelLayout.setBorders(false);
		workAreaPanelLayout.setResize(true);

		// mainPanelLayout.getElement().getStyle().setBackgroundColor("rgb(3, 126, 207)");

		// Main
		final WorkAreaPanel workAreaPanel = new WorkAreaPanel(eventBus);
		MarginData workAreaData = new MarginData(new Margins(2));
		workAreaPanelLayout.setCenterWidget(workAreaPanel, workAreaData);

		// Menu
		StatAlgoImporterRibbon accountingManagerMenu = new StatAlgoImporterRibbon(
				eventBus);

		BorderLayoutData ribbonData = new BorderLayoutData(RIBBON_HEIGHT);
		ribbonData.setMargins(new Margins(2));
		ribbonData.setCollapsible(false);
		ribbonData.setSplit(false);

		workAreaPanelLayout.setNorthWidget(accountingManagerMenu.getContainer(),
				ribbonData);
		
		//
		bind(workAreaPanelLayout);
		controller.setMainPanelLayout(workAreaPanelLayout);
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
