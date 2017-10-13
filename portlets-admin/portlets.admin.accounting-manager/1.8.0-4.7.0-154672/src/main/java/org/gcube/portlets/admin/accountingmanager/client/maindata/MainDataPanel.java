package org.gcube.portlets.admin.accountingmanager.client.maindata;

import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.FramedPanel;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class MainDataPanel extends FramedPanel {

	private EventBus eventBus;

	public MainDataPanel(EventBus eventBus) {
		super();
		this.eventBus = eventBus;
		// msgs = GWT.create(ServiceCategoryMessages.class);
		init();
		create();

	}

	protected void init() {
		forceLayoutOnResize = true;
		setBodyBorder(false);
		setBorders(false);
		setHeaderVisible(false);

	}

	protected void create() {
		/*ChartPanel chartPanel = new ChartPanel(eventBus);
		add(chartPanel);*/
		ChartViewerPanel hightChartPanel= new ChartViewerPanel(eventBus);
		add(hightChartPanel);
		

	}

}
