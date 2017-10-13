package org.gcube.portlets.user.td.informationwidget.client;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;


public class InformationEntry implements EntryPoint {

	public void onModuleLoad() {

		EventBus eventBus = new SimpleEventBus();
		
		TabularResourceProperties trProperties = new TabularResourceProperties(
				"TRProperties", eventBus);

		trProperties.setHeadingText("Tabular Resource Properties");
		RootPanel.get().add(trProperties);
		Log.info("" + trProperties);

	}
}
