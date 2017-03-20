package org.gcube.portlets.user.td.resourceswidget.client;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * Delete Column Dialog
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ResourcesListViewDialog extends Window {
	private static final String WIDTH = "640px";
	private static final String HEIGHT = "480px";
	private ResourcesListViewPanel resourcesPanel;
	private ResourcesMessages msgs;

	public ResourcesListViewDialog(EventBus eventBus) {
		initMessages();
		initWindow();
		resourcesPanel = new ResourcesListViewPanel(
				eventBus);
		add(resourcesPanel);
		
		
	}
	
	protected void initMessages() {
		msgs = GWT.create(ResourcesMessages.class);
	}

	protected void initWindow() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setBodyBorder(false);
		setResizable(false);
		setHeadingText(msgs.resourcesListViewDialogHead());
		// getHeader().setIcon(Resources.IMAGES.side_list());
	}

	public void open(TRId trId){;
		resourcesPanel.open(trId);
		
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
