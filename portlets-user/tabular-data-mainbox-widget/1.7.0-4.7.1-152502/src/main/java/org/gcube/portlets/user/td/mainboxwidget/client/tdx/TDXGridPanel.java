package org.gcube.portlets.user.td.mainboxwidget.client.tdx;

import org.gcube.portlets.user.td.mainboxwidget.client.grid.GridContextMenu;
import org.gcube.portlets.user.td.mainboxwidget.client.grid.GridHeaderColumnMenu;
import org.gcube.portlets.user.td.widgetcommonevent.shared.Constants;
import org.gcube.portlets.user.td.widgetcommonevent.shared.dataview.TabularResourceDataView;
import org.gcube.portlets.user.tdwx.client.TabularDataX;
import org.gcube.portlets.user.tdwx.client.TabularDataXGridPanel;
import org.gcube.portlets.user.tdwx.client.event.FailureEvent;
import org.gcube.portlets.user.tdwx.client.event.FailureEvent.FailureEventHandler;
import org.gcube.portlets.user.tdwx.shared.model.TableId;

import com.allen_sauer.gwt.log.client.Log;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.info.Info;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class TDXGridPanel extends FramedPanel {

	// private static final String HEIGHT = "600px";
	private EventBus eventBus;
	private TabularResourceDataView tabularResourceDataView;
	private TabularDataX tabularData;

	public TDXGridPanel(EventBus eventBus) {
		super();
		this.eventBus = eventBus;
		init();
		addGrid();
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

	protected void addGrid() {

		try {
			tabularData = new TabularDataX(Constants.TDX_DATASOURCE_FACTORY_ID);
			tabularData.addFailureHandler(new FailureEventHandler() {

				public void onFailure(FailureEvent event) {
					Throwable e = event.getCaught();
					Info.display("Error: " + event.getMessage(),
							e.getLocalizedMessage());
					Log.error("Error: " + e.getLocalizedMessage());
					e.printStackTrace();
				}
			});

			// Grid Panel
			TabularDataXGridPanel gridPanel = tabularData.getGridPanel();

			GridHeaderColumnMenu columnHeaderMenu = new GridHeaderColumnMenu();
			gridPanel.addGridHeaderContextMenuItems(columnHeaderMenu.getMenu(),
					eventBus);

			GridContextMenu gridContextMenu = new GridContextMenu(gridPanel,
					eventBus);
			gridPanel.setGridContextMenu(gridContextMenu.getMenu());

			gridPanel.setSelectionModel(SelectionMode.MULTI);

			add(gridPanel, new MarginData());

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void open(TabularResourceDataView dataView) {
		tabularResourceDataView = dataView;
		TableId tableOpening = new TableId(Constants.TDX_DATASOURCE_FACTORY_ID,
				tabularResourceDataView.getTrId().getTableId());
		tabularData.openTable(tableOpening);
		//forceLayout();

	}

	public void update(TabularResourceDataView dataView) {
		if (isValidDataViewRequest(dataView)) {
			tabularResourceDataView = dataView;
			TableId tableOpening = new TableId(
					Constants.TDX_DATASOURCE_FACTORY_ID,
					tabularResourceDataView.getTrId().getTableId());
			tabularData.openTable(tableOpening);
			//forceLayout();
		}

	}

	public boolean isValidDataViewRequest(TabularResourceDataView dataViewRequest) {
		if (dataViewRequest != null
				&& dataViewRequest.getTrId() != null
				&& dataViewRequest.getTrId().getId().compareTo(tabularResourceDataView
						.getTrId().getId())==0) {
			return true;
		} else {
			return false;
		}
	}

	public TabularDataX getTabularData() {
		return tabularData;
	}

	public TabularResourceDataView getTabularResourceDataView() {
		return tabularResourceDataView;
	}
	
	

}
