package org.gcube.portlets.user.td.columnwidget.client.create;

import org.gcube.portlets.user.td.columnwidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.TableType;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * Dialog to create a column definition
 * used in extract codelist widget
 * 
 * @author "Giancarlo Panichi"
 * 
 */
public class CreateDefColumnDialog extends Window {
	private static final String WIDTH = "460px";
	private static final String HEIGHT = "220px";
	private CreateDefColumnPanel createDefColumnPanel;
	private CreateDefColumnMessages msgs;

	public CreateDefColumnDialog(TableType tableType,
			EventBus eventBus) {
		super();
		Log.debug("CreateDefColumnDialog [tableType: "+tableType+"]");
		initMessages();
		initWindow();
		createDefColumnPanel = new CreateDefColumnPanel(this, tableType,
				eventBus);
		add(createDefColumnPanel);
	}
	
	protected void initMessages(){
		msgs = GWT.create(CreateDefColumnMessages.class);
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

	public void addListener(CreateDefColumnListener listener) {
		Log.debug("Add Listener" + listener);
		if (createDefColumnPanel != null) {
			createDefColumnPanel.addListener(listener);
		} else {
			Log.error("CreateDefColumnPanel is null");

		}
	}

	public void removeListener(CreateDefColumnListener listener) {
		Log.debug("Remove Listener" + listener);
		if (createDefColumnPanel != null) {
			createDefColumnPanel.removeListener(listener);
		} else {
			Log.error("CreateDefColumnPanel is null");

		}
	}

}
