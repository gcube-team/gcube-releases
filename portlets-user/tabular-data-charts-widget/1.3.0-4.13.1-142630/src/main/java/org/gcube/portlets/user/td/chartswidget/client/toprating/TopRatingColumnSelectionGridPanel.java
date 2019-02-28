package org.gcube.portlets.user.td.chartswidget.client.toprating;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.td.chartswidget.client.grid.ColumnDataProperties;
import org.gcube.portlets.user.td.gwtservice.shared.chart.ChartSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnTypeCode;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
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
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class TopRatingColumnSelectionGridPanel extends ContentPanel implements
		HasSelectionHandlers<ColumnData> {
	//private static final String GRID_WIDTH  ="524px";
	private static final String GRID_HEIGHT = "340px";
	private static final ColumnDataProperties props = GWT
			.create(ColumnDataProperties.class);
	private final CheckBoxSelectionModel<ColumnData> sm;

	private final Grid<ColumnData> grid;
	@SuppressWarnings("unused")
	private TopRatingColumnSelectionCard parent;

	
	/**
	 * 
	 * @param parent
	 */
	public TopRatingColumnSelectionGridPanel(TopRatingColumnSelectionCard parent, ChartSession chartSession) {
		this.parent=parent;
		Log.debug("TopRatingColumnSelectionGridPanel");
		setHeadingText("Dimension Colums");

		ColumnConfig<ColumnData, String> labelCol = new ColumnConfig<ColumnData, String>(
				props.label());

		IdentityValueProvider<ColumnData> identity = new IdentityValueProvider<ColumnData>();

		sm = new CheckBoxSelectionModel<ColumnData>(identity);

		List<ColumnConfig<ColumnData, ?>> l = new ArrayList<ColumnConfig<ColumnData, ?>>();
		l.add(sm.getColumn());
		l.add(labelCol);
		ColumnModel<ColumnData> cm = new ColumnModel<ColumnData>(l);

		ListStore<ColumnData> store = new ListStore<ColumnData>(props.id());
		ArrayList<ColumnData> coloumns=chartSession.getColumns();
		for(ColumnData col:coloumns){
			if(col.getTypeCode().compareTo(ColumnTypeCode.DIMENSION.toString())==0){
				store.add(col);
			}
		}

		grid = new Grid<ColumnData>(store, cm);
		sm.setSelectionMode(SelectionMode.SINGLE);
		grid.setSelectionModel(sm);
		grid.setHeight(GRID_HEIGHT);
		//grid.setWidth(GRID_WIDTH);
		// grid.getView().setAutoExpandColumn(labelCol);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.getView().setAutoFill(true);
		grid.setBorders(false);
		grid.setLoadMask(true);
		grid.setColumnReordering(false);

		VerticalLayoutContainer con = new VerticalLayoutContainer();
		con.setScrollMode(ScrollMode.AUTO);
		con.add(grid, new VerticalLayoutData(-1, -1, new Margins(0)));
		setWidget(con);

	}

	public Grid<ColumnData> getGrid() {
		return grid;
	}

	
	public ColumnData getSelectedItem() {
		return grid.getSelectionModel()
				.getSelectedItem();

	}

	public HandlerRegistration addSelectionHandler(
			SelectionHandler<ColumnData> handler) {
		return grid.getSelectionModel().addSelectionHandler(handler);
	}

}
