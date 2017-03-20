package org.gcube.portlets.user.td.mainboxwidget.client.tdx;

import org.gcube.portlets.user.td.widgetcommonevent.shared.dataview.TabularResourceDataView;
import org.gcube.portlets.user.tdwx.client.TabularDataX;

import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.FramedPanel;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class TDXPanel extends FramedPanel {

	// private static final String HEIGHT = "600px";
	private TDXTabPanel tdxTabPanel;
	private EventBus eventBus;

	public TDXPanel(EventBus eventBus) {
		super();
		this.eventBus = eventBus;
		init();
		addTDXTabPanel();
		forceLayout();
	}

	protected void init() {
		// setWidth(WIDTH);
		// setHeight(HEIGHT);
		setHeaderVisible(false);
		setBodyBorder(false);
		setBorders(false);
		setResize(true);
		forceLayoutOnResize = true;
	}

	protected void addTDXTabPanel() {
		tdxTabPanel=new TDXTabPanel(eventBus);
		add(tdxTabPanel);
	}

	public void open(TabularResourceDataView dataView) {
		tdxTabPanel.open(dataView);
	}

	public void update(TabularResourceDataView dataView) {
		tdxTabPanel.update(dataView);

	}

	public boolean isValidDataViewRequest(TabularResourceDataView dataViewRequest) {
		return tdxTabPanel.isValidDataViewRequest(dataViewRequest);
	}

	public TabularDataX getTabularData() {
		return tdxTabPanel.getTabularData();
	}

	public TabularResourceDataView getTabularResourceDataView() {
		return tdxTabPanel.getTabularResourceDataView();
	}
	
}
