package org.gcube.portlets.user.td.tablewidget.client.normalize;

import org.gcube.portlets.user.td.columnwidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

import com.allen_sauer.gwt.log.client.Log;
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
public class NormalizeDialog extends Window {
	protected String WIDTH = "650px";
	protected String HEIGHT = "530px";
	protected EventBus eventBus;
	protected NormalizePanel normalizationPanel;
	protected TRId trId;

	public NormalizeDialog(TRId trId, EventBus eventBus) {
		super();
		Log.debug("AddColumnDialog");
		this.eventBus = eventBus;
		this.trId=trId;
		initWindow();
		normalizationPanel = new NormalizePanel(this, trId,eventBus);
		add(normalizationPanel);
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
		setHeadingText("Normalization");
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
