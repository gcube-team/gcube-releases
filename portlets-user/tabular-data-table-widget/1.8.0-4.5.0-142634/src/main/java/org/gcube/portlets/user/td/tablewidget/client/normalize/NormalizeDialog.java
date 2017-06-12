package org.gcube.portlets.user.td.tablewidget.client.normalize;

import org.gcube.portlets.user.td.columnwidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.tablewidget.client.TableWidgetMessages;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class NormalizeDialog extends Window {
	private static final String WIDTH = "650px";
	private static final String HEIGHT = "530px";
	private NormalizePanel normalizationPanel;
	private TableWidgetMessages msgs;

	public NormalizeDialog(TRId trId, EventBus eventBus) {
		super();
		Log.debug("AddColumnDialog");
		initMessages();
		initWindow();
		normalizationPanel = new NormalizePanel(this, trId, eventBus);
		add(normalizationPanel);
	}

	protected void initMessages() {
		msgs = GWT.create(TableWidgetMessages.class);

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
		setHeadingText(msgs.normalizeDialogHead());
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
