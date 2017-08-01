package org.gcube.portlets.widgets.dataminermanagerwidget.client;

import org.gcube.portlets.widgets.dataminermanagerwidget.client.common.EventBusProvider;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.ExternalExecutionEvent;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.ExternalExecutionEvent.ExternalExecutionEventHandler;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.ExternalExecutionEvent.HasExternalExecutionEventHandler;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.ExternalExecutionRequestEvent;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.tr.TabularResourceData;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.widget.core.client.Window;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class DataMinerManagerDialog extends Window implements HasExternalExecutionEventHandler {
	// private DateTimeFormat dateTimeFormat=
	// DateTimeFormat.getFormat("yyyy/MM/dd HH:mm:ss SSS");
	private static final String WIDTH = "1024px";
	private static final String HEIGHT = "600px";
	// private static final String PANELWIDTH = "620px";
	// private static final String PANELHEIGHT = "308px";
	
	private DataMinerManagerController dataMinerManagerController;


	public DataMinerManagerDialog() {
		Log.debug("DataMinerManagerDialog");
		initWindow();
		create();
		bind();
	}

	private void initWindow() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setBodyBorder(false);
		setResizable(false);
		setModal(true);
		setClosable(true);
		setHeadingText("DataMiner Manager");
		setOnEsc(true);
	}

	private void create() {
		dataMinerManagerController = new DataMinerManagerController();
		DataMinerManagerPanel dataMinerManagerPanel = new DataMinerManagerPanel();
		setWidget(dataMinerManagerPanel);

	}

	public void setTabularResourceData(TabularResourceData tabularResourceData){
		dataMinerManagerController.setTabularResourceData(tabularResourceData);
	}
	
	private void bind() {
		EventBusProvider.INSTANCE
				.addHandler(
						ExternalExecutionRequestEvent.TYPE,
						new ExternalExecutionRequestEvent.ExternalExecutionRequestEventHandler() {
							
							@Override
							public void onSubmit(ExternalExecutionRequestEvent event) {
								Log.debug("Catch ExternalExecutionRequestEvent: "
										+ event);
								ExternalExecutionEvent ev = new ExternalExecutionEvent(event.getOp());
								fireEvent(ev);
								
							}
						});
	}

	@Override
	public HandlerRegistration addExternalExecutionEventHandler(
			ExternalExecutionEventHandler handler) {
		return addHandler(handler, ExternalExecutionEvent.getType());
	}

}