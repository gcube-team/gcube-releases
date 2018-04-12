package org.gcube.portlets.user.td.resourceswidget.client.charts;

import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.ResourceTDDescriptor;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * Delete Column Dialog
 * 
 * @author Giancarlo Panichi 
 * 
 *
 */
public class ChartViewerDialog extends Window {
	private static final String WIDTH = "822px";
	private static final String HEIGHT = "460px";
	private ChartViewerMessages msgs;
	
	public ChartViewerDialog(ResourceTDDescriptor resourceTDDescriptor, TRId trId,  EventBus eventBus) {
		this(resourceTDDescriptor, trId,eventBus, false);
	}
	
	public ChartViewerDialog(ResourceTDDescriptor resourceTDDescriptor, TRId trId,  EventBus eventBus, boolean test) {
		initMessages();
		initWindow();
		
		ChartViewerPanel chartPanel= new ChartViewerPanel(this, resourceTDDescriptor, trId, eventBus,test);
		add(chartPanel);
	}
	
	protected void initMessages(){
		msgs = GWT.create(ChartViewerMessages.class);
	}
	
	
	protected void initWindow() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setBodyBorder(false);
		setResizable(false);
		setHeadingText(msgs.dialogHead());
		//getHeader().setIcon(Resources.IMAGES.side_list());
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initTools() {
		super.initTools();

		closeBtn.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				close();
			}
		});

	}

	public void close() {
		hide();

	}
	
}
