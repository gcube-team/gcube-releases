package org.gcube.portlets.user.dataminerexecutor.client;

import org.gcube.portlets.user.dataminerexecutor.client.common.EventBusProvider;
import org.gcube.portlets.user.dataminerexecutor.client.events.InvocationModelRequestEvent;
import org.gcube.portlets.user.dataminerexecutor.client.events.MenuSwitchEvent;
import org.gcube.portlets.user.dataminerexecutor.client.experiments.ExperimentPanel;
import org.gcube.portlets.user.dataminerexecutor.client.resources.Resources;

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
public class DataMinerExecutor implements EntryPoint {
	public static final Resources resources = GWT.create(Resources.class);

	private static final String SM_DIV = "contentDiv";
	private DataMinerExecutorController dataMinerExecutorController;

	private Header header;
	//private HomePanel homePanel;
	private ExperimentPanel experimentPanel;
	
	//private SimpleContainer previousPanel;
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
		Log.info("DataMinerExecutor");
		
		dataMinerExecutorController = new DataMinerExecutorController();
		//homePanel = new HomePanel();
		experimentPanel = new ExperimentPanel();
	
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

		if (dataMinerExecutorController.getDataMinerInvocationModelFileUrl() != null
				&& !dataMinerExecutorController.getDataMinerInvocationModelFileUrl().isEmpty()) {
			centerPanel.add(experimentPanel);
		} 
		

		bindWindow(mainPanelLayout);
		
		
		Scheduler.get().scheduleDeferred(new Command() {
			
			public void execute() {
				mainPanelLayout.forceLayout();
				EventBusProvider.INSTANCE.fireEvent(new InvocationModelRequestEvent());
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
		case EXPERIMENT:
			switchPanel(experimentPanel);
			break;
		default:
			break;

		}

	}

	/**
	 * 
	 * @param panel
	 */
	private void switchPanel(SimpleContainer panel) {
		/*centerPanel.remove(previousPanel);
		centerPanel.add(panel);
		centerPanel.forceLayout();
		previousPanel = panel;*/
	}

}
