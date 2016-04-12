package org.gcube.datatransfer.portlets.user.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.info.Info;

public class SchedulerPortlet extends Common implements EntryPoint, IsWidget {
	
	public void onModuleLoad() {
		initialize();
	}

	private void initialize(){	
		portlet=this;
		rpCalls=new RPCalls();
		panels=new Panels();
		popups=new Popups();
		functions=new Functionality();
		
		folderSource = functions.makeFolder("Empty source");
		folderDestination = functions.makeFolder("Empty destination");

		functions.setSomeDefaultUris();
		
		// get user name and scope from the session
		rpCalls.getUserAndScopeAndRole();
		// set the auto Id for making folders in the tree
		autoId = 0;
		// create the dialog box general
		functions.setDialogBoxForMessages();
		// set the border layout
		RootPanel.get("mainContainer").add(panels.asWidgetLayout());
		totalWidth = RootPanel.get("mainContainer").getOffsetWidth();
		if (totalWidth < minGenWidth)
			totalWidth = minGenWidth;

		// set the west widget for main scheduler service
		west.add(panels.asWidgetScheduler());
		// set the central widget for choosing files
		east.clear();
		east.add(panels.asWidgetToolbar());
		east.add(panels.asWidgetListFiles());
		functions.designTransferGrid();

		// getting the agents
		Timer getAgentTimer = new Timer() {
			@Override
			public void run() {
				rpCalls.getAgents();
				Info.display("", "");
			}
		};
		getAgentTimer.schedule(2000);
		// getting the workspace variable in order to retrieve later the
		// workspace root folderSource
		rpCalls.getWorkspace();

		functions.initTimers();
		// resize handler
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				resizeTimer.schedule(400); // ms
			}
		});		

		popups.showGuidedTour();
	}

	/*
	 * reDraw input: Nothing -- returns: Nothing Redraw all the components with
	 * the new width depends on the window resize (MainContainer resize)
	 */
	public void reDraw() {
		// check if the scheduler widget is shown
		boolean showSchedulerWidget = false;
		if (west.getWidgetCount() > 0) {
			showSchedulerWidget = true;
		}
		RootPanel.get("mainContainer").clear();
		RootPanel.get("mainContainer").add(panels.asWidgetLayout());

		totalWidth = RootPanel.get("mainContainer").getOffsetWidth();
		if (totalWidth < minGenWidth)
			totalWidth = minGenWidth;

		Info.display("Resize", "width="
				+ RootPanel.get("mainContainer").getOffsetWidth()
				+ " - height="
				+ RootPanel.get("mainContainer").getOffsetHeight());
		// south
		functions.designTransferGrid();
		// west
		if (showSchedulerWidget) {
			west.add(panels.asWidgetScheduler());
		}
		// east
		east.add(panels.asWidgetToolbar());
		east.add(panels.asWidgetListFiles());

	}



	/*
	 * redrawEast input: Nothing -- returns: Nothing It redraws again the right
	 * side (east in the portlet)
	 */
	public void redrawEast() {
		east.clear();
		east.add(panels.asWidgetToolbar());
		east.add(panels.asWidgetListFiles());
	}



	/*
	 * inherited abstract method
	 * 
	 * @see com.google.gwt.user.client.ui.IsWidget#asWidget()
	 */
	public Widget asWidget() {
		return null;
	}


}
