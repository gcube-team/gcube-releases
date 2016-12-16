package org.gcube.portlets.user.td.mapwidget.client.grid;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.td.mapwidget.client.MapWidgetConfigCard;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ColumnDataGridPanel extends ContentPanel implements
		HasSelectionHandlers<ColumnData> {
	// private static final String GRID_WIDTH ="524px";
	private static final String GRID_HEIGHT = "320px";
	protected static final ColumnDataProperties props = GWT
			.create(ColumnDataProperties.class);
	protected final CheckBoxSelectionModel<ColumnData> sm;

	protected final Grid<ColumnData> grid;
	protected MapWidgetConfigCard parent;

	/**
	 * 
	 * @param parent
	 */
	public ColumnDataGridPanel(MapWidgetConfigCard parent) {
		this.parent = parent;
		Log.debug("ColumnDataGridPanel");
		setHeadingText("Columns");

		ColumnConfig<ColumnData, String> labelCol = new ColumnConfig<ColumnData, String>(
				props.label());

		IdentityValueProvider<ColumnData> identity = new IdentityValueProvider<ColumnData>();

		sm = new CheckBoxSelectionModel<ColumnData>(identity);

		List<ColumnConfig<ColumnData, ?>> l = new ArrayList<ColumnConfig<ColumnData, ?>>();
		l.add(sm.getColumn());
		l.add(labelCol);
		ColumnModel<ColumnData> cm = new ColumnModel<ColumnData>(l);

		ListStore<ColumnData> store = new ListStore<ColumnData>(props.id());
		
		ArrayList<ColumnData> cols=parent.getMapCreationSession().getColumns();
		ArrayList<ColumnData> noGeometryCols=new ArrayList<ColumnData>();
		for(ColumnData c:cols){
			ColumnDataType type=ColumnDataType.getColumnDataTypeFromId(c.getDataTypeName());
			if(type.compareTo(ColumnDataType.Geometry)!=0){
				noGeometryCols.add(c);
			}
		}
		
		store.addAll(noGeometryCols);

		grid = new Grid<ColumnData>(store, cm);
		sm.setSelectionMode(SelectionMode.SINGLE);
		grid.setSelectionModel(sm);
		grid.setHeight(GRID_HEIGHT);
		// grid.setWidth(GRID_WIDTH);
		// grid.getView().setAutoExpandColumn(labelCol);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.getView().setAutoFill(true);
		grid.setBorders(false);
		grid.setLoadMask(true);
		grid.setColumnReordering(false);

		VerticalLayoutContainer con = new VerticalLayoutContainer();
		//con.setScrollMode(ScrollMode.AUTO);
		con.add(grid, new VerticalLayoutData(1, -1, new Margins(0)));
		setWidget(con);

	}

	public Grid<ColumnData> getGrid() {
		return grid;
	}

	public ColumnData getSelectedItem() {
		return grid.getSelectionModel().getSelectedItem();
	}

	public ArrayList<ColumnData> getSelectedItems() {
		return new ArrayList<ColumnData>(grid.getSelectionModel()
				.getSelectedItems());

	}

	public HandlerRegistration addSelectionHandler(
			SelectionHandler<ColumnData> handler) {
		return grid.getSelectionModel().addSelectionHandler(handler);
	}

}
