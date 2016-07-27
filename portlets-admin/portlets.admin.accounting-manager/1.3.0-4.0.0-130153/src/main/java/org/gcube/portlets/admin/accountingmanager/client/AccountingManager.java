package org.gcube.portlets.admin.accountingmanager.client;

import org.gcube.portlets.admin.accountingmanager.client.filters.FiltersPanel;
import org.gcube.portlets.admin.accountingmanager.client.maindata.MainDataPanel;
import org.gcube.portlets.admin.accountingmanager.client.menu.AccountingManagerMenu;
import org.gcube.portlets.admin.accountingmanager.client.resource.AccountingManagerResources;
import org.gcube.portlets.admin.accountingmanager.client.rpc.AccountingManagerService;
import org.gcube.portlets.admin.accountingmanager.client.rpc.AccountingManagerServiceAsync;
import org.gcube.portlets.admin.accountingmanager.client.utils.UtilsGXT3;
import org.gcube.portlets.user.gcubewidgets.client.ClientScopeHelper;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
public class AccountingManager implements EntryPoint {

	private static final String JSP_TAG_ID = "AccountingManagerPortlet";

	@SuppressWarnings("unused")
	private final AccountingManagerServiceAsync accountingManagerService = GWT
			.create(AccountingManagerService.class);

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
						if (result) {
							loadMainPanel();
						} else {
							UtilsGXT3
									.info("Attention",
											"ClientScopeHelper has returned a false value!");
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						UtilsGXT3.alert("Error", "Error setting scope: "
								+ caught.getLocalizedMessage());
						caught.printStackTrace();
					}
				});

	}

	private void loadMainPanel() {
		AccountingManagerResources.INSTANCE.accountingManagerCSS()
				.ensureInjected();
		// ScriptInjector.fromString(AccountingManagerResources.INSTANCE.jqueryJs().getText()).setWindow(ScriptInjector.TOP_WINDOW).inject();

		AccountingManagerController controller = new AccountingManagerController();
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
		AccountingManagerMenu accountingManagerMenu = new AccountingManagerMenu(
				eventBus);

		BorderLayoutData menuData = new BorderLayoutData(58);
		menuData.setMargins(new Margins(5));
		menuData.setCollapsible(false);
		menuData.setSplit(false);

		mainPanelLayout.setNorthWidget(accountingManagerMenu, menuData);

		// Filters
		FiltersPanel filtersPanel = new FiltersPanel(eventBus);
		BorderLayoutData westData = new BorderLayoutData(410);
		westData.setCollapsible(false);
		westData.setSplit(false);
		westData.setFloatable(false);
		westData.setCollapseMini(false);
		westData.setMargins(new Margins(2, 7, 2, 7));
		westData.setCollapseHidden(true);
		westData.setMaxSize(410);
		westData.setMinSize(410);
		mainPanelLayout.setWestWidget(filtersPanel, westData);
		filtersPanel.expand();
		filtersPanel.enable();

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
