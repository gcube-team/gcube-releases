package org.gcube.portlets.admin.software_upload_wizard.client.util;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.dispatch.client.standard.StandardDispatchAsync;

import org.gcube.portlets.admin.software_upload_wizard.client.rpc.LoggingExceptionHandler;
import org.gcube.portlets.admin.software_upload_wizard.client.view.WizardWindow;
import org.gcube.portlets.admin.software_upload_wizard.client.wizard.IWizard;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.event.shared.HandlerManager;

public class Util {

	private static HandlerManager eventBus = null;
	private static WizardWindow window = null;
	private static DispatchAsync dispatcher;
	private static IWizard wizard = null;



	public static DispatchAsync getDispatcher() {
		if (dispatcher == null)
			dispatcher = new StandardDispatchAsync(
					new LoggingExceptionHandler());
		return dispatcher;
	}

	public static HandlerManager getEventBus() {
		return eventBus;
	}

	public static void setEventBus(HandlerManager eventBus) {
		Util.eventBus = eventBus;
	}

	public static WizardWindow getWindow() {
		return window;
	}

	public static void setWindow(WizardWindow window) {
		Util.window = window;
	}
	
	public static IWizard getWizard() {
		return wizard;
	}

	public static void setWizard(IWizard wizard) {
		Util.wizard = wizard;
	}
	
	public static void handleError(String errorTitle, String errorMsg,
			Throwable caught) {
		Log.error(errorMsg, caught);
		MessageBox.alert(errorTitle, errorMsg, null);
	}

}
