package org.gcube.portlets.user.td.metadatawidget.client;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.sencha.gxt.widget.core.client.ContentPanel;


public class MetadataEntry implements EntryPoint {


	public void onModuleLoad() {

		EventBus eventBus = new SimpleEventBus();
		TRId trId=new TRId();
		trId.setTableId("1");
		
		TableMetadataAccordionPanel metadataAccordion = new TableMetadataAccordionPanel(
				"MetadataTree",trId, eventBus);

		ContentPanel panel=new ContentPanel();
		panel.add(metadataAccordion);
		
		RootPanel.get().add(panel);
		Log.info("" + metadataAccordion);

	}
}
