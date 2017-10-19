package org.gcube.portlets.user.td.columnwidget.client;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * Dialog for Change Column Type
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ChangeColumnTypeDialog extends Window {
	private static final String WIDTH = "650px";
	private static final String HEIGHT = "530px";
	private ChangeColumnTypeMessages msgs;
	

	public ChangeColumnTypeDialog(TRId trId, EventBus eventBus) {
		create(trId, null, eventBus);
	}

	public ChangeColumnTypeDialog(TRId trId, String columnName,
			EventBus eventBus) {
		create(trId, columnName, eventBus);
	}

	protected void create(TRId trId, String columnName, EventBus eventBus) {
		initMessages();
		initWindow();
		ChangeColumnTypePanel changeColumnTypePanel = new ChangeColumnTypePanel(
				trId, columnName, eventBus);
		add(changeColumnTypePanel);

	}

	protected void initMessages(){
		msgs = GWT.create(ChangeColumnTypeMessages.class);
	}
	
	protected void initWindow() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setBodyBorder(false);
		setResizable(false);
		setHeadingText(msgs.dialogHeadingText());
		// getHeader().setIcon(Resources.IMAGES.side_list());
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
