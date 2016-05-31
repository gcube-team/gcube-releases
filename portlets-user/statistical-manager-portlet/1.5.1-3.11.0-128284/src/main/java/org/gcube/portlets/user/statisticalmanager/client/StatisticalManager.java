package org.gcube.portlets.user.statisticalmanager.client;

import java.util.List;

import org.gcube.portlets.user.gcubewidgets.client.ClientScopeHelper;
import org.gcube.portlets.user.statisticalmanager.client.bean.OperatorsClassification;
import org.gcube.portlets.user.statisticalmanager.client.events.MaskEvent;
import org.gcube.portlets.user.statisticalmanager.client.events.ResubmitJobEvent;
import org.gcube.portlets.user.statisticalmanager.client.events.SessionExpiredEvent;
import org.gcube.portlets.user.statisticalmanager.client.experimentArea.ExperimentPanel;
import org.gcube.portlets.user.statisticalmanager.client.inputSpaceArea.InputSpacePanel;
import org.gcube.portlets.user.statisticalmanager.client.jobsArea.JobsPanel;
import org.gcube.portlets.user.statisticalmanager.client.resources.Resources;
import org.gcube.portlets.user.statisticalmanager.client.util.EventBusProvider;
import org.gcube.portlets.user.tdw.client.TabularData;
import org.gcube.portlets.widgets.sessionchecker.client.CheckSession;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class StatisticalManager implements EntryPoint {

	public static Resources resources = GWT.create(Resources.class);
	private static List<OperatorsClassification> operatorsClassifications = null;
	private static final StatisticalManagerPortletServiceAsync statisticalService = GWT
			.create(StatisticalManagerPortletService.class);
	private static final String SM_DIV = "contentDiv";

	// private VerticalPanel verticalPanel = new VerticalPanel();
	private LayoutContainer menu;

	private InputSpacePanel inputSpacePanel = new InputSpacePanel();
	private ExperimentPanel experimentPanel = new ExperimentPanel();
	private JobsPanel jobsPanel = new JobsPanel();

	private LayoutContainer previousPanel;
	private LayoutContainer centerPanel = new LayoutContainer(new FitLayout());
	private BorderLayoutData centerPanelData;
	private LayoutContainer statisticalManagerLayout;

	public enum MenuItem {
		INPUT_SPACE, EXPERIMENT, COMPUTATIONS
	};

	private static TabularData tabularData;

	private Header header;

	private void updateSize() {
		RootPanel smDiv = RootPanel.get(SM_DIV);

		int topBorder = smDiv.getAbsoluteTop();
		int leftBorder = smDiv.getAbsoluteLeft();

		int rootHeight = Window.getClientHeight() - topBorder - 4;// - ((footer
																	// ==
																	// null)?0:(footer.getOffsetHeight()-15));
		int rootWidth = Window.getClientWidth() - 2 * leftBorder - 5; // -
																		// rightScrollBar;

		// System.out.println("New Statistical Manager dimension Width: "+rootWidth+"; Height: "+rootHeight);

		if (previousPanel == menu)
			statisticalManagerLayout.setSize(rootWidth, 700);
		else
			statisticalManagerLayout.setSize(rootWidth, rootHeight);
	}

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
					    	Window.alert("Attention, ClientScopeHelper has returned a false value!");
					    }
					}

					@Override
					public void onFailure(Throwable caught) {	
						Window.alert("Error, setting scope: "+caught.getLocalizedMessage());
						caught.printStackTrace();
					}
				});

	}
	
	public void loadMainPanel() {
		//Log.info("StatisticalManager module start!");
		
		bind();

		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				updateSize();
			}
			


		});
//		Timer t = new Timer() {			
//			@Override
//			public void run() {
//				statisticalService.checkSession(new AsyncCallback<Void>() {
//					@Override
//					public void onFailure(Throwable caught) {
//						Window.alert("Failed check session on server" + caught.getMessage());
//						Window.Location.reload();			
//					}
//					@Override
//					public void onSuccess(Void result) {						
//					}
//				});
// 
//			}
//		};
//		
//		t.scheduleRepeating(10000);
		CheckSession.getInstance().startPolling();

		// Viewport viewport = new Viewport();
		statisticalManagerLayout = new LayoutContainer();
		statisticalManagerLayout.setLayout(new BorderLayout());
		statisticalManagerLayout.setStyleAttribute("background-color",
				"#FFFFFF");
		// globalLayout.setStyleAttribute("border", "1px solid black");

		// NORD: HEADER
		BorderLayoutData northPanelData = new BorderLayoutData(
				LayoutRegion.NORTH, 50);
		northPanelData.setCollapsible(false);
		northPanelData.setFloatable(false);
		northPanelData.setHideCollapseTool(true);
		northPanelData.setSplit(false);
		northPanelData.setMargins(new Margins(0, 0, 5, 0));

		header = new Header() {
			@Override
			public void select(MenuItem menuItem) {
				if (menuItem == null)
					switchTo(menu);
				else if (menuItem == MenuItem.INPUT_SPACE)
					switchTo(inputSpacePanel);
				else if (menuItem == MenuItem.EXPERIMENT)
					switchTo(experimentPanel);
				else if (menuItem == MenuItem.COMPUTATIONS)
					switchTo(jobsPanel);
			}
		};
		statisticalManagerLayout.add(header, northPanelData);

		// CENTER
		centerPanelData = new BorderLayoutData(LayoutRegion.CENTER);
		centerPanelData.setMargins(new Margins(0));
		centerPanel.setStyleAttribute("padding-bottom", "15px");
		// centerPanel.setBorders(true);

		// centerPanel.setStyleAttribute("padding-right", "13px");
		statisticalManagerLayout.add(centerPanel, centerPanelData);

		menu = createMenuPanel();
		centerPanel.add(menu);
		previousPanel = menu;

		// menu = createMenuPanel();
		// verticalPanel.add(menu);

		RootPanel.get(SM_DIV).add(statisticalManagerLayout);

		updateSize();

		/*
		 * statisticalService.test(new AsyncCallback<TableResource>() {
		 * 
		 * @Override public void onSuccess(TableResource result) {
		 * System.out.println("Result: "+result);
		 * 
		 * }
		 * 
		 * @Override public void onFailure(Throwable caught) { // TODO
		 * Auto-generated method stub
		 * 
		 * } });
		 */
	}

	/**
	 * 
	 */
	private void bind() {
		EventBusProvider.getInstance().addHandler(MaskEvent.getType(),
				new MaskEvent.MaskHandler() {
					@Override
					public void onMask(MaskEvent event) {
						if (statisticalManagerLayout == null)
							return;

						String message = event.getMessage();
						if (message == null)
							statisticalManagerLayout.unmask();
						else
							statisticalManagerLayout.mask(message,
									Constants.maskLoadingStyle);
					}
				});

		EventBusProvider.getInstance().addHandler(
				SessionExpiredEvent.getType(),
				new SessionExpiredEvent.SessionExpiredHandler() {
					@Override
					public void onSessionExpired(SessionExpiredEvent event) {
						Window.alert("The session has expired. Please refresh the page.");
					}
				});

		EventBusProvider.getInstance().addHandler(ResubmitJobEvent.getType(),
				new ResubmitJobEvent.ResubmitJobHandler() {
					@Override
					public void onResubmitJob(ResubmitJobEvent event) {
						switchTo(experimentPanel);
						header.setMenuSelected(MenuItem.EXPERIMENT);
					}
				});
	}

	/**
	 * @return
	 */
	private LayoutContainer createMenuPanel() {
		LayoutContainer topLc = new LayoutContainer();
		LayoutContainer lc = new LayoutContainer();
		// lc.setSize(640, 400);
		lc.addStyleName("smLayoutContainer");
		lc.addStyleName("smMenu");

		LayoutContainer itemInputSpace = createMenuItem(
				"Access to the Data Space",
				"The data space contains the set of input and output data sets of the users. It is possible to upload and share tables. Data sources can be chosen from those hosted by the infrastructure. Outputs of the computations can be even saved in this space.",
				resources.inputSpaceIcon(), new Listener<BaseEvent>() {
					@Override
					public void handleEvent(BaseEvent be) {
						switchTo(inputSpacePanel);
						header.setMenuSelected(MenuItem.INPUT_SPACE);
					}
				});
		lc.add(itemInputSpace);

		LayoutContainer itemExperiment = createMenuItem(
				"Execute an Experiment",
				"This section allows to execute or prepare a Niche Modeling experiment. The section is endowed with a list of algorithms for training and executing statistical models for biological applications. Evaluation of the performances is possible by means of several kinds of measurement systems and processes.",
				resources.computationIcon(), new Listener<BaseEvent>() {
					@Override
					public void handleEvent(BaseEvent be) {
						switchTo(experimentPanel);
						header.setMenuSelected(MenuItem.EXPERIMENT);
					}
				});
		lc.add(itemExperiment);

		LayoutContainer itemJobs = createMenuItem(
				"Check the Computations",
				"This section allows to check the status of the computation. A list of processes launched by the user is shown along with meta-information. By clicking on the completed jobs it is possible to visualize the data set contents.",
				resources.jobsIcon(), new Listener<BaseEvent>() {
					@Override
					public void handleEvent(BaseEvent be) {
						switchTo(jobsPanel);
						header.setMenuSelected(MenuItem.COMPUTATIONS);
					}
				});
		lc.add(itemJobs);

		topLc.add(lc);
		return topLc;
	}

	private HorizontalPanel createMenuItem(String title, String description,
			ImageResource imgResource, Listener<BaseEvent> listener) {
		HorizontalPanel hp = new HorizontalPanel();
		hp.addStyleName("smMenuItem");
		hp.addListener(Events.OnClick, listener);

		Image img = new Image(imgResource);

		Html text = new Html("<b>" + title + "</b><br>" + description);
		text.addStyleName("smMenuItemText");

		hp.add(text, new TableData("350px", "100px"));
		hp.add(img);
		return hp;
	}

	/**
	 * 
	 */
	protected void switchTo(LayoutContainer lc) {
		// boolean updt = false;
		boolean updt = (previousPanel != lc && (previousPanel == menu || lc == menu));

		centerPanel.remove(previousPanel);
		centerPanel.add(lc);
		centerPanel.layout();
		previousPanel = lc;

		if (updt)
			updateSize();
	}

	/**
	 * @return the operatorsClassification
	 */
	public static List<OperatorsClassification> getOperatorsClassifications() {
		return operatorsClassifications;
	}

	/**
	 * @param operatorsClassification
	 *            the operatorsClassification to set
	 */
	public static void setOperatorsClassifications(
			List<OperatorsClassification> operatorsClassifications) {
		StatisticalManager.operatorsClassifications = operatorsClassifications;
	}

	public static OperatorsClassification getDefaultOperatorsClassification() {
		if (operatorsClassifications == null)
			return null;
		OperatorsClassification find = null;
		for (OperatorsClassification oc : operatorsClassifications)
			if (oc.getName().equals(Constants.computationClassificationName))
				find = oc;
		return find;
	}

	public static OperatorsClassification getOperatorsClassificationByName(
			String classificationName) {
		if (operatorsClassifications == null)
			return null;
		OperatorsClassification find = null;
		for (OperatorsClassification oc : operatorsClassifications)
			if (oc.getName().equals(classificationName))
				find = oc;
		return (find == null ? getDefaultOperatorsClassification() : find);
	}

	public static StatisticalManagerPortletServiceAsync getService() {
		return statisticalService;
	}

	/**
	 * @return
	 */
	public static TabularData getTabularData() {
		if (tabularData == null)
			tabularData = new TabularData(Constants.TD_DATASOURCE_FACTORY_ID);
		return tabularData;
	}

}
