package org.gcube.portlets.user.td.columnwidget.client;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * Delete Column Dialog
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class LabelColumnDialog extends Window {
	private static final int WIDTH=400;
	private static final int HEIGHT=120;
	private LabelColumnMessages msgs;
	
	
	public LabelColumnDialog(TRId trId,  EventBus eventBus) {
		create(trId, null, eventBus);
	}

	public LabelColumnDialog(TRId trId, String columnName,  EventBus eventBus) {
		create(trId, columnName, eventBus);
	}

	protected void create(TRId trId, String columnName,  EventBus eventBus) {
		initMessages();
		initWindow();
		
		LabelColumnPanel labelColumnPanel= new LabelColumnPanel(trId, columnName, eventBus);
		add(labelColumnPanel);
	}
	
	protected void initMessages(){
		msgs = GWT.create(LabelColumnMessages.class);
	}
	
	
	
	protected void initWindow() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setBodyBorder(false);
		setResizable(false);
		setHeadingText(msgs.dialogHeadingText());
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
