package org.gcube.portlets.user.td.monitorwidget.client.background;

import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.shared.monitor.BackgroundOperationMonitor;

import com.allen_sauer.gwt.log.client.Log;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * Background Operations Monitor
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class MonitorBackgroundDialog extends Window {

	private static final String WIDTH = "640px";
	private static final String HEIGHT = "520px";

	private EventBus eventBus;
	private MonitorBackgroundPanel monitorBackgroundPanel;

	public MonitorBackgroundDialog(EventBus eventBus) {
		super();
		Log.debug("MonitorBackgroundDialog");
		this.eventBus = eventBus;
		initWindow();
		create();
	}

	protected void initWindow() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setBodyBorder(false);
		setResizable(true);
		setModal(false);
		setClosable(true);
		setHeadingText("Background Operations Monitor");
	}

	protected void create() {
		monitorBackgroundPanel = new MonitorBackgroundPanel(this, eventBus);
		add(monitorBackgroundPanel);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initTools() {
		/*
		 * if (backgroundBtnEnabled) { backgroundBtn = new
		 * ToolButton(ToolButton.CLOSE);
		 * backgroundBtn.setToolTip("Put in the background");
		 * backgroundBtn.addSelectHandler(new SelectHandler() {
		 * 
		 * @Override public void onSelect(SelectEvent event) { hide(); } });
		 * 
		 * header.addTool(backgroundBtn); }
		 */

		super.initTools();

		closeBtn.setVisible(true);

		closeBtn.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				close();
			}
		});

	}

	protected void close() {
		if (monitorBackgroundPanel != null) {
			monitorBackgroundPanel.cancelMonitorBackgroundUpdater();
		}
		hide();

	}
	
	public void updateBackgroundOperationMonitor(ArrayList<BackgroundOperationMonitor> operationMonitorList) {
		if (monitorBackgroundPanel != null) {
			monitorBackgroundPanel.operationMonitorListUpdated(operationMonitorList);
		}
		forceLayout();
	}

}
