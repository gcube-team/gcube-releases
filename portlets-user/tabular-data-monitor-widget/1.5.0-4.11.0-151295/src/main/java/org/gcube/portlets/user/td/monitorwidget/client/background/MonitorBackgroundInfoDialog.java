package org.gcube.portlets.user.td.monitorwidget.client.background;


import org.gcube.portlets.user.td.gwtservice.shared.monitor.BackgroundOperationMonitor;

import com.allen_sauer.gwt.log.client.Log;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * Background Operations Monitor
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class MonitorBackgroundInfoDialog extends Window {

	private static final String WIDTH = "640px";
	private static final String HEIGHT = "428px";
	
	private EventBus eventBus;
	private MonitorBackgroundInfoPanel monitorBackgroundInfoPanel;
	private BackgroundOperationMonitor backgroundOperationMonitor;

	public MonitorBackgroundInfoDialog(BackgroundOperationMonitor backgroundOperationMonitor, EventBus eventBus) {
		super();
		
		Log.debug("MonitorBackgroundInfoDialog");
		this.eventBus = eventBus;
		this.backgroundOperationMonitor =backgroundOperationMonitor;
		initWindow();
		create();
	}

	protected void initWindow() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setBodyBorder(false);
		setResizable(false);
		setModal(false);
		setClosable(true);
		setHeadingText(backgroundOperationMonitor.getTabularResourceName());
	}

	protected void create() {
		monitorBackgroundInfoPanel = new MonitorBackgroundInfoPanel(this, backgroundOperationMonitor,eventBus);
		add(monitorBackgroundInfoPanel);
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initTools() {
		
		super.initTools();

		closeBtn.setVisible(true);

		closeBtn.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				close();
			}
		});

	}

	protected void close() {
		if(monitorBackgroundInfoPanel!=null){
			monitorBackgroundInfoPanel.cancelMonitorBackgroundInfoUpdater();
		}
		hide();
		

	}
	


}
