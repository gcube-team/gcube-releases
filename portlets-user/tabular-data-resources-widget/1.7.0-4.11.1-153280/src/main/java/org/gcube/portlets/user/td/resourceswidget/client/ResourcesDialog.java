package org.gcube.portlets.user.td.resourceswidget.client;


import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

import com.allen_sauer.gwt.log.client.Log;
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
public class ResourcesDialog extends Window {
	private static final String WIDTH = "640px";
	private static final String HEIGHT = "400px";
	private ResourcesMessages msgs;
	private ResourcesPanel resourcesPanel;
	
	public ResourcesDialog(EventBus eventBus) {
		initMessages();
		initWindow();
		resourcesPanel= new ResourcesPanel(this, eventBus);
		add(resourcesPanel);
	}
	
	public void open(TRId trId){
		Log.debug("Open: "+trId);
		resourcesPanel.open(trId);
	}
	
	protected void initMessages(){
		msgs = GWT.create(ResourcesMessages.class);
	}
	
	protected void initWindow() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setBodyBorder(false);
		setResizable(false);
		setHeadingText(msgs.resourcesDialogHead());
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
