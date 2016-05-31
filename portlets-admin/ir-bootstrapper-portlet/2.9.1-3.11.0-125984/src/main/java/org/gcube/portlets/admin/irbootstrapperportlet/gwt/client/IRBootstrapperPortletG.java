package org.gcube.portlets.admin.irbootstrapperportlet.gwt.client;

import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.IRBootstrapperService;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.IRBootstrapperServiceAsync;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.shared.UnavailableScopeException;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.RootPanel;
import com.gwtext.client.widgets.LoadMask;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.event.TabPanelListenerAdapter;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class IRBootstrapperPortletG implements EntryPoint  {

	/** An GWT RPC interface to the IRBootstrapperService */
	public static IRBootstrapperServiceAsync bootstrapperService = (IRBootstrapperServiceAsync) GWT.create(IRBootstrapperService.class);
	private static ServiceDefTarget bootstrapperServiceEndpoint = (ServiceDefTarget) bootstrapperService;

	/** The main tabPanel */
	private TabPanel tabPanel;

	/** The viewer panel */
	private BootstrapperViewer viewer;

	/** The designer panel */
	private BootstrapperDesigner designer;
	
	private JobsMonitoring monitor;

	/** The mask used while loading data */
	private LoadMask loadMask;

	/**
	 * Class constructor
	 */
	public IRBootstrapperPortletG() {
		String moduleRelativeURL = GWT.getModuleBaseURL() + "/IRBootstrapperServiceImpl";
		bootstrapperServiceEndpoint.setServiceEntryPoint(moduleRelativeURL);
	}

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		/* Create the main tab panel */
		tabPanel = new TabPanel();
		// TODO:
		tabPanel.setWidth(1200);
		//tabPanel.setWidth("99%");  
		tabPanel.setHeight("600px");

		/* Create the viewer */
		viewer = new BootstrapperViewer(this);
		final Panel viewerTab = new Panel();  
		viewerTab.setTitle("Job execution");
		viewerTab.add(viewer);
		viewerTab.setClosable(false);

		/* Create the viewer */
		designer = new BootstrapperDesigner(this);
		final Panel designerTab = new Panel();  
		designerTab.setTitle("Job designer");
		designerTab.add(designer);
		designerTab.setClosable(false);
		
		/* Create the monitoring */
		monitor = new JobsMonitoring(this);
		final Panel monitorTab = new Panel();
		monitorTab.setTitle("Submitted Jobs Info");
		monitorTab.add(monitor);
		monitorTab.setClosable(false);

		/* Add the viewer and the designer panels as tabs */
		tabPanel.add(viewerTab);
		tabPanel.add(designerTab);
		tabPanel.add(monitorTab);
		tabPanel.setActiveTab(0);
		tabPanel.addListener(new TabPanelListenerAdapter() {
			public boolean doBeforeTabChange(TabPanel source, Panel newPanel, Panel oldPanel) {
				/* If the user navigates from the designer tab to the viewer tab, the
				 * bootstrapper configuration has to be reloaded (IF the designer has
				 * saved any changes before).
				 */
				if (newPanel==viewerTab && oldPanel==designerTab) {
					if (designer.getJobEditor().hasMadeAnyChangesToJobConfiguration()) {
						initialize();
					}
				}
				return true;
			}
		});
		/* Add the tab panel to the root panel */
		getRootPanel().add(tabPanel);

		/* Create the load mask */
		loadMask = new LoadMask(getRootPanel().getElement(), "Loading, please wait...");

		/* Perform initialization tasks */
		initialize();

		updateSize();
		
		/* Add a handler for the resizing of the window */
		com.google.gwt.user.client.Window.addResizeHandler(new ResizeHandler(){

			public void onResize(ResizeEvent event) {
				updateSize();
			}

		});
	}

	/**
	 * Initialize the portlet and servlet
	 */
	private void initialize() {
		/* The callback object for invoking the 'initialize' servlet method */
		AsyncCallback<Void> initCallback = new AsyncCallback<Void>() {

			public void onFailure(Throwable arg0) {
				hideLoadMask();
				if (arg0 instanceof UnavailableScopeException)
					Window.alert("Session has expired. Please logout and login again");
				else
					Window.alert("Error while initializing portlet." + arg0);
			}

			public void onSuccess(Void arg0) {
				viewer.initialize();
				designer.initialize();
				monitor.initialize();
				hideLoadMask();
			}
		};

		showLoadMask();
		IRBootstrapperPortletG.bootstrapperService.initialize(initCallback);
	}

	BootstrapperDesigner getDesigner() {
		return this.designer;
	}

	BootstrapperViewer getViewer() {
		return this.viewer;
	}

	private void updateSize() {
		com.google.gwt.user.client.ui.Panel root = getRootPanel();
		int leftBorder = root.getAbsoluteLeft();
		int rightScrollBar = 17;
		int rootWidth = com.google.gwt.user.client.Window.getClientWidth() - 2* leftBorder - rightScrollBar;
		tabPanel.setWidth(rootWidth);
	}

	/**
	 * Returns the root panel of this portlet.
	 * @return the root panel
	 */
	public com.google.gwt.user.client.ui.Panel getRootPanel() {
		return RootPanel.get("IRBootstrapper");
	}

	/**
	 * Shows the load mask
	 */
	public void showLoadMask() {
		loadMask.show();
	}

	/**
	 * Hides the load mask
	 */
	public void hideLoadMask() {
		loadMask.hide();
	}
}
