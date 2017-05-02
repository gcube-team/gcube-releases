package org.gcube.portlets.user.td.wizardwidget.client;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;


public class WizardEntry implements EntryPoint {

	
	public void onModuleLoad() {

		
		WizardWindow wizardWindow = new WizardWindow("TestWindow");
		wizardWindow.add(new SimpleWizardCard("Test Title","Test Footer", "This is a simple card test"));
		Log.info("Window Id: " + wizardWindow.getId());
		wizardWindow.show();
	}
}
