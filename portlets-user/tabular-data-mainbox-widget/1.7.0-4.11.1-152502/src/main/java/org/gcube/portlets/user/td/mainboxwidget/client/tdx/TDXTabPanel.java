package org.gcube.portlets.user.td.mainboxwidget.client.tdx;

import org.gcube.portlets.user.td.mainboxwidget.client.resources.MainboxResources;
import org.gcube.portlets.user.td.resourceswidget.client.ResourcesPanel;
import org.gcube.portlets.user.td.widgetcommonevent.shared.dataview.TabularResourceDataView;
import org.gcube.portlets.user.tdwx.client.TabularDataX;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class TDXTabPanel extends TabPanel {
	private static final int TAB_WIDTH = 96;

	private EventBus eventBus;
	private TDXGridPanel gridPanel;
	//private ResourcesListViewPanel resourcesPanel;
	private ResourcesPanel resourcesPanel;

	private TDXTabPanelMessages msgs;

	public TDXTabPanel(EventBus eventBus) {
		super();
		// GWT.<TabPanelAppearance> create(BlueTabPanelAppearance.class));
		Log.debug("Create TDXTabPanel");
		this.eventBus = eventBus;
		msgs = GWT.create(TDXTabPanelMessages.class);
		
		
		init();
		create();
		forceLayout();
	}

	protected void init() {
		setBodyBorder(false);
		setBorders(false);
		setAnimScroll(false);
		setTabScroll(false);
		setCloseContextMenu(false);
		setResizeTabs(true);
		setTabWidth(TAB_WIDTH);
		

	}

	protected void create() {
		TabItemConfig gridItemConf = new TabItemConfig(msgs.tabGridLabel(), false);
		gridItemConf.setIcon(MainboxResources.INSTANCE
				.table());
		
		
		gridPanel = new TDXGridPanel(eventBus);

		add(gridPanel, gridItemConf);

		TabItemConfig resourcesItemConf = new TabItemConfig(msgs.tabResourcesLabel(), false);
		resourcesItemConf.setIcon(MainboxResources.INSTANCE
				.resources());
		
		
		//resourcesPanel = new ResourcesListViewPanel(eventBus);
		resourcesPanel = new ResourcesPanel(eventBus);

		add(resourcesPanel, resourcesItemConf);

		setActiveWidget(gridPanel);
	}

	public void open(TabularResourceDataView dataView) {
		gridPanel.open(dataView);
		resourcesPanel.open(dataView.getTrId());
		setCurrentWidgetActive(dataView);

	}

	public void update(TabularResourceDataView dataView) {
		gridPanel.update(dataView);
		resourcesPanel.open(dataView.getTrId());
		setCurrentWidgetActive(dataView);

	}
	
	protected void setCurrentWidgetActive(TabularResourceDataView dataView) {
		if(dataView!=null&&dataView.getDataViewType()!=null){
			switch (dataView.getDataViewType()) {
			case RESOURCES:
				setActiveWidget(resourcesPanel);
				break;
			case GRID:
				setActiveWidget(gridPanel);
				break;
			default:
				break;
			}
		}
		forceLayout();
	}

	

	public boolean isValidDataViewRequest(
			TabularResourceDataView dataViewRequest) {
		return gridPanel.isValidDataViewRequest(dataViewRequest);
	}

	public TabularDataX getTabularData() {
		return gridPanel.getTabularData();
	}

	public TabularResourceDataView getTabularResourceDataView() {
		return gridPanel.getTabularResourceDataView();
	}

}
