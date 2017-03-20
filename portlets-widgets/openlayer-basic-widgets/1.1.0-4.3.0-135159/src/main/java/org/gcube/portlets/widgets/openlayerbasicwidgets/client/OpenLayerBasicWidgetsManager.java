package org.gcube.portlets.widgets.openlayerbasicwidgets.client;

import org.gcube.portlets.widgets.openlayerbasicwidgets.client.event.SelectAreaDialogEvent;
import org.gcube.portlets.widgets.openlayerbasicwidgets.client.event.SelectAreaDialogEvent.SelectAreaDialogEventHandler;
import org.gcube.portlets.widgets.openlayerbasicwidgets.client.widgets.AreaSelectionDialog;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;

/**
 * 
 * @author Giancarlo Panichi
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class OpenLayerBasicWidgetsManager implements EntryPoint {

	/**
	 * {@inheritDoc}
	 */
	public void onModuleLoad() {

		/*
		 * Install an UncaughtExceptionHandler which will produce
		 * <code>FATAL</code> log messages
		 */

		// use deferred command to catch initialization exceptions in
		// onModuleLoad2
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
				loadMainPanel();
			}
		});

	}

	/*
	private void loadScope() {
		ClientScopeHelper.getService().setScope(Location.getHref(),
				new AsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						if (result) {
							loadMainPanel();
						} else {
							GWTMessages
									.alert("Attention",
											"ClientScopeHelper has returned a false value!",-1);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						GWTMessages.alert("Error", "Error setting scope: "
								+ caught.getLocalizedMessage(),-1);
						caught.printStackTrace();
					}
				});

	}*/

	private void loadMainPanel() {
		GWT.log("OpenLayerBasicWidgetsManager");
	
		// Example
		SelectAreaDialogEventHandler handler=new SelectAreaDialogEventHandler() {
			
			@Override
			public void onResponse(SelectAreaDialogEvent event) {
				GWT.log("SelectAreaDialog Response: "+event);
				
			}
		};
		
		//Use AreaSelectionDialog(GeometryType.Point) 
		//for specific Geometry
		AreaSelectionDialog dialog=new AreaSelectionDialog();
		dialog.addSelectAreaDialogEventHandler(handler);
			
	}

}
