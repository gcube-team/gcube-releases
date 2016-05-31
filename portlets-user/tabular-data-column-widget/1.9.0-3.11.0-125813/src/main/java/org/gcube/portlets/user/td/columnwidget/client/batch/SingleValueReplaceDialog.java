package org.gcube.portlets.user.td.columnwidget.client.batch;

import java.util.ArrayList;

import org.gcube.portlets.user.td.columnwidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class SingleValueReplaceDialog extends Window {
	private String WIDTH = "500px";
	private String HEIGHT = "150px";
	//private SingleValueReplacePanel ReplacePanel;
	private EventBus eventBus;
	private String value;
	private String replaceValue;
	private ColumnData column;
	private ArrayList<SingleValueReplaceListener> listeners;
	private SingleValueReplaceMessages msgs;

	public SingleValueReplaceDialog(String value, String replaceValue,
			ColumnData column, EventBus eventBus) {
		listeners = new ArrayList<SingleValueReplaceListener>();
		this.value = value;
		this.replaceValue = replaceValue;
		this.column = column;
		this.eventBus = eventBus;
		initMessages();
		initWindow();
		create();

	}
	
	protected void initMessages(){
		msgs = GWT.create(SingleValueReplaceMessages.class);
	}

	protected void initWindow() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setBodyBorder(false);
		setResizable(false);
		setHeadingText(msgs.dialogReplaceValue());
		setClosable(true);
		getHeader().setIcon(ResourceBundle.INSTANCE.columnValue());

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

	protected void create() {
		SingleValueReplacePanel replacePanel = new SingleValueReplacePanel(
					this, value, replaceValue, column, eventBus);
		add(replacePanel);
	}

	protected void close() {
		hide();
	}

	public void addListener(SingleValueReplaceListener listener) {
		listeners.add(listener);
	}

	public void removeListener(SingleValueReplaceListener listener) {
		listeners.remove(listener);
	}

	public void fireCompleted(String replaceValue) {
		for (SingleValueReplaceListener listener : listeners)
			listener.selectedSingleValueReplace(replaceValue);
		hide();
	}

	public void fireAborted() {
		for (SingleValueReplaceListener listener : listeners)
			listener.abortedSingleValueReplace();
		hide();
	}

	public void fireFailed(String reason, String detail) {
		for (SingleValueReplaceListener listener : listeners)
			listener.failedSingleValueReplace(reason, detail);
		hide();
	}

}
