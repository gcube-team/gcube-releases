package org.gcube.portlets.user.td.columnwidget.client.replace;

import org.gcube.portlets.user.td.columnwidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.widgetcommonevent.shared.CellData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class ReplaceAllDialog extends Window {
	private static final String WIDTH = "500px";
	private static final String HEIGHT = "150px";
	private CellData cellData;
	private TRId trId;
	private EventBus eventBus;
	private ReplaceAllMessages msgs;

	
	public ReplaceAllDialog(CellData cellData, TRId trId, EventBus eventBus) {
		initMessages();
		initWindow();
		this.cellData = cellData;
		this.trId = trId;
		this.eventBus = eventBus;
		create();

	}
	
	protected void initMessages(){
		msgs = GWT.create(ReplaceAllMessages.class);
	}

	protected void initWindow() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setBodyBorder(false);
		setResizable(false);
		setHeadingText(msgs.dialogHead());
		setClosable(true);
		getHeader().setIcon(ResourceBundle.INSTANCE.replaceAll());

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
		ReplaceAllPanel replacePanel = new ReplaceAllPanel(this, trId, cellData,
				eventBus);
		add(replacePanel);
	}

	protected void close() {
		hide();
	}

	

}
