package org.gcube.portlets.user.dataminermanager.client;

import org.gcube.portlets.user.dataminermanager.client.common.EventBusProvider;
import org.gcube.portlets.user.dataminermanager.client.computations.ComputationsPanel;
import org.gcube.portlets.user.dataminermanager.client.dataspace.DataSpacePanel;
import org.gcube.portlets.user.dataminermanager.client.events.DataMinerWorkAreaRequestEvent;
import org.gcube.portlets.user.dataminermanager.client.events.MenuSwitchEvent;
import org.gcube.portlets.user.dataminermanager.client.experiments.ExperimentPanel;
import org.gcube.portlets.user.dataminermanager.client.resources.Resources;
import org.gcube.portlets.user.dataminermanager.client.type.DataMinerWorkAreaRegionType;
import org.gcube.portlets.user.dataminermanager.client.type.DataMinerWorkAreaRequestEventType;
import org.gcube.portlets.user.dataminermanager.client.type.MenuType;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.RootPanel;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.Viewport;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class DataMinerManager implements EntryPoint {
	public static final Resources resources = GWT.create(Resources.class);

	private static final String SM_DIV = "contentDiv";
	private DataMinerManagerController dataMinerManagerController;

	private Header header;
	private HomePanel homePanel;
	private DataSpacePanel dataSpacePanel;
	private ExperimentPanel experimentPanel;
	private ComputationsPanel computationsPanel;

	private SimpleContainer previousPanel;
	private SimpleContainer centerPanel;

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
				// loadScope();
				loadMainPanel();
			}
		});

	}

	/*
	 * private void loadScope() {
	 * ClientScopeHelper.getService().setScope(Location.getHref(), new
	 * AsyncCallback<Boolean>() {
	 * 
	 * @Override public void onSuccess(Boolean result) { if (result) {
	 * loadMainPanel(); } else { UtilsGXT3 .info("Attention",
	 * "ClientScopeHelper has returned a false value!"); } }
	 * 
	 * @Override public void onFailure(Throwable caught) {
	 * UtilsGXT3.alert("Error", "Error setting scope: " +
	 * caught.getLocalizedMessage()); caught.printStackTrace(); } });
	 * 
	 * }
	 */

	private void loadMainPanel() {
		dataMinerManagerController = new DataMinerManagerController();
		homePanel = new HomePanel();
		dataSpacePanel = new DataSpacePanel();
		experimentPanel = new ExperimentPanel();
		computationsPanel = new ComputationsPanel();

		bind();

		// Layout
		final BorderLayoutContainer mainPanelLayout = new BorderLayoutContainer();
		mainPanelLayout.setId("mainPanelLayout");
		mainPanelLayout.setBorders(false);
		mainPanelLayout.setResize(true);
		mainPanelLayout.getElement().getStyle().setBackgroundColor("#FFFFFF");

		// Center
		centerPanel = new SimpleContainer();
		MarginData mainData = new MarginData(new Margins(0));
		mainPanelLayout.setCenterWidget(centerPanel, mainData);

		// Menu
		header = new Header();

		BorderLayoutData menuData = new BorderLayoutData(40);
		menuData.setMargins(new Margins(5));
		menuData.setCollapsible(false);
		menuData.setSplit(false);

		mainPanelLayout.setNorthWidget(header, menuData);

		//

		if (dataMinerManagerController.getOperatorId() != null
				&& !dataMinerManagerController.getOperatorId().isEmpty()) {
			header.setMenu(MenuType.EXPERIMENT);
			centerPanel.add(experimentPanel);
			previousPanel = experimentPanel;

		} else {
			centerPanel.add(homePanel);
			previousPanel = homePanel;

		}

		bindWindow(mainPanelLayout);

		Scheduler.get().scheduleDeferred(new Command() {
			public void execute() {
				mainPanelLayout.forceLayout();
			}
		});

	}

	private void bind() {
		EventBusProvider.INSTANCE.addHandler(MenuSwitchEvent.TYPE,
				new MenuSwitchEvent.MenuSwitchEventHandler() {

					@Override
					public void onSelect(MenuSwitchEvent event) {
						Log.debug("Catch MenuSwitchEvent");
						menuSwitch(event);

					}
				});
	}

	/**
	 * 
	 * @param mainWidget
	 */
	private void bindWindow(BorderLayoutContainer mainWidget) {
		try {
			RootPanel root = RootPanel.get(SM_DIV);
			Log.info("Root Panel: " + root);
			if (root == null) {
				Log.info("Div with id " + SM_DIV
						+ " not found, starting in dev mode");
				Viewport viewport = new Viewport();
				viewport.setWidget(mainWidget);
				viewport.onResize();
				RootPanel.get().add(viewport);
			} else {
				Log.info("Application div with id " + SM_DIV
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

	/**
	 * 
	 * @param event
	 */
	private void menuSwitch(MenuSwitchEvent event) {
		if (event == null || event.getMenuType() == null) {
			return;
		}

		switch (event.getMenuType()) {
		case COMPUTATIONS:
			switchPanel(computationsPanel);
			fireDataMinerWorkAreareRequestUpdate(DataMinerWorkAreaRegionType.Computations);
			break;
		case DATA_SPACE:
			switchPanel(dataSpacePanel);
			fireDataMinerWorkAreareRequestUpdate(DataMinerWorkAreaRegionType.DataSets);
			break;
		case EXPERIMENT:
			switchPanel(experimentPanel);
			break;
		case HOME:
			switchPanel(homePanel);
			break;
		default:
			break;

		}

	}

	private void fireDataMinerWorkAreareRequestUpdate(
			DataMinerWorkAreaRegionType dataMinerWorkAreaRegionType) {
		DataMinerWorkAreaRequestEvent event = new DataMinerWorkAreaRequestEvent(
				DataMinerWorkAreaRequestEventType.UPDATE,
				dataMinerWorkAreaRegionType);
		EventBusProvider.INSTANCE.fireEvent(event);
	}

	/**
	 * 
	 * @param panel
	 */
	private void switchPanel(SimpleContainer panel) {
		centerPanel.remove(previousPanel);
		centerPanel.add(panel);
		centerPanel.forceLayout();
		previousPanel = panel;
	}

}
