package org.gcube.portlets.user.statisticalalgorithmsimporter.client.dminfo;

import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.Constants;

import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;

/**
 * 
 * Simple file show dialog
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class ServiceInfoDialog extends Dialog {
	private EventBus eventBus;
	private ServiceInfoPanel serviceInfoPanel;
	
	public ServiceInfoDialog(EventBus eventBus) {
		super();
		this.eventBus=eventBus;
		init();
		create();
	}

	private void init() {
		setWidth("640px");
		setHeight("500px");
		setResizable(true);
		setHeadingText(Constants.DATA_MINER_SERVICE_INFO_TITLE);
		setModal(true);
		setMaximizable(true);
		setPredefinedButtons(PredefinedButton.CLOSE);
		setButtonAlign(BoxLayoutPack.CENTER);
	}

	private void create() {	
		serviceInfoPanel=new ServiceInfoPanel(eventBus);
		add(serviceInfoPanel);
	}
	
	
}