package org.gcube.portlets.user.td.columnwidget.client.create;

import org.gcube.portlets.user.td.columnwidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * 
 * @author "Giancarlo Panichi"
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class AddColumnDialog extends Window {
	private static final String WIDTH = "650px";
	private static final String HEIGHT = "530px";
	private AddColumnPanel addColumnPanel;
	private AddColumnMessages msgs;

	public AddColumnDialog(TRId trId, EventBus eventBus) {
		super();
		Log.debug("AddColumnDialog");
		initMessages();
		initWindow();
		addColumnPanel = new AddColumnPanel(this, trId,eventBus);
		add(addColumnPanel);
	}
	
	protected void initMessages(){
		msgs = GWT.create(AddColumnMessages.class);
	}
	
	protected void initWindow() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setBodyBorder(false);
		setResizable(false);

		setClosable(true);
		setModal(true);
		forceLayoutOnResize = true;
		getHeader().setIcon(ResourceBundle.INSTANCE.columnValue());
		setHeadingText(msgs.dialogHead());
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
