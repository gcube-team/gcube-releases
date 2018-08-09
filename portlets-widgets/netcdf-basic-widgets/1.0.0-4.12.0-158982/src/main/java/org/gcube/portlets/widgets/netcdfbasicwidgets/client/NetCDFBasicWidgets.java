package org.gcube.portlets.widgets.netcdfbasicwidgets.client;

import org.gcube.portlets.widgets.netcdfbasicwidgets.client.event.SelectVariableEvent;
import org.gcube.portlets.widgets.netcdfbasicwidgets.client.event.SelectVariableEvent.SelectVariableEventHandler;
import org.gcube.portlets.widgets.netcdfbasicwidgets.client.widgets.NetCDFPreviewDialog;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class NetCDFBasicWidgets implements EntryPoint {
	// private static final String example1 =
	// "http://data.d4science.org/Qnc4RXlNQmhlWS83NkFFb2dIU0hQMnhVTER1VEZjbGdHbWJQNStIS0N6Yz0";
	private static final String example2 = "http://data.d4science.org/L0FuZGNERGNFL1Y4bDRQdDFHSmdFUkN5V3VvZlF4L2lHbWJQNStIS0N6Yz0";
	// private static final String example3 =
	// "http://data.d4science.org/WXZFNjRXeE9XWGQ4bDRQdDFHSmdFWVBPd0FEK0VzdlRHbWJQNStIS0N6Yz0";

	/**
	 * {@inheritDoc}
	 */
	public void onModuleLoad() {

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
				loadMainPanel();
			}
		});

	}

	private void loadMainPanel() {
		GWT.log("NetcdfBasicWidgetsManager");

		// Example
		SelectVariableEventHandler handler = new SelectVariableEventHandler() {

			@Override
			public void onResponse(SelectVariableEvent event) {
				GWT.log("SelectVariable Response: " + event);

			}
		};

		NetCDFPreviewDialog dialog = new NetCDFPreviewDialog(example2);
		dialog.addSelectVariableEventHandler(handler);

	}

}
