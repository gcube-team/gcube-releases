package org.gcube.portlets.user.td.columnwidget.client;


import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class PositionColumnDialog extends Window {
	private static final int WIDTH=400;
	private static final int HEIGHT=120;
	private PositionColumnMessages msgs;
	
	public PositionColumnDialog(TRId trId,  EventBus eventBus) {
		initMessages();
		initWindow();
		
		PositionColumnPanel changeColumnsPositionPanel= new PositionColumnPanel(trId, eventBus);
		add(changeColumnsPositionPanel);
	}
	
	protected void initMessages(){
		msgs = GWT.create(PositionColumnMessages.class);
	}
	
	protected void initWindow() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setBodyBorder(false);
		setResizable(false);
		setHeadingText(msgs.dialogHeadingText());
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
