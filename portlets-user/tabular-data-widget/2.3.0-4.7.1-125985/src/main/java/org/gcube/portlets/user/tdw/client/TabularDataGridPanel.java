/**
 * 
 */
package org.gcube.portlets.user.tdw.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.tdw.client.config.Row;
import org.gcube.portlets.user.tdw.client.config.TableViewConfig;
import org.gcube.portlets.user.tdw.client.config.TabularDataGridViewConfig;
import org.gcube.portlets.user.tdw.client.event.CloseTableEvent;
import org.gcube.portlets.user.tdw.client.event.CloseTableEventHandler;
import org.gcube.portlets.user.tdw.client.event.OpenTableEvent;
import org.gcube.portlets.user.tdw.client.event.OpenTableEventHandler;
import org.gcube.portlets.user.tdw.client.event.TableReadyEvent;
import org.gcube.portlets.user.tdw.client.event.TableReadyEventHandler;
import org.gcube.portlets.user.tdw.client.model.grid.DataRowModelKeyProvider;
import org.gcube.portlets.user.tdw.client.model.grid.DataRowPagingReader;
import org.gcube.portlets.user.tdw.client.model.util.ColumnConfigGenerator;
import org.gcube.portlets.user.tdw.client.util.ColumnPositionComparator;
import org.gcube.portlets.user.tdw.client.util.PagingLoadUrlEncoder;
import org.gcube.portlets.user.tdw.shared.ServletParameters;
import org.gcube.portlets.user.tdw.shared.model.ColumnDefinition;
import org.gcube.portlets.user.tdw.shared.model.ColumnKey;
import org.gcube.portlets.user.tdw.shared.model.ColumnType;
import org.gcube.portlets.user.tdw.shared.model.DataRow;
import org.gcube.portlets.user.tdw.shared.model.TableDefinition;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.RequestBuilder;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.data.client.loader.HttpProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadConfigBean;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.LiveGridView;
import com.sencha.gxt.widget.core.client.grid.LiveToolItem;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * 
 */
public class TabularDataGridPanel extends ContentPanel {

	protected int tdSessionId;

	protected EventBus eventBus;

	protected Grid<DataRow> grid;
	protected VerticalLayoutContainer container;

	protected LiveGridView<DataRow> liveGridView;

	protected DataRowPagingReader reader;

	protected PagingLoader<PagingLoadConfig, PagingLoadResult<DataRow>> loader;

	protected TableViewConfig tableViewConfig;

	protected Menu contextMenu;

	protected TableDefinition currentTableDefinition;
	protected Map<String, ColumnKey> keys;

	/**
	 * @param eventBus
	 */
	protected TabularDataGridPanel(int tdSessionId, EventBus eventBus) {
		super();
		this.tdSessionId = tdSessionId;
		this.eventBus = eventBus;
		bindEventBus();
		container = new VerticalLayoutContainer();
		setWidget(container);
	}

	/**
	 * @return the tableViewConfig
	 */
	public TableViewConfig getTableViewConfig() {
		return tableViewConfig;
	}

	/**
	 * @param tableViewConfig
	 *            the tableViewConfig to set
	 */
	public void setTableViewConfig(TableViewConfig tableViewConfig) {
		this.tableViewConfig = tableViewConfig;
	}

	/**
	 * @return the grid
	 */
	public Grid<DataRow> getGrid() {
		return grid;
	}

	/**
	 * Returns the selected row.
	 * 
	 * @return the selected row, <code>null</code> if no row is selected.
	 */
	public Row getSelectedRow() {
		if (grid != null) {
			DataRow dataRow = grid.getSelectionModel().getSelectedItem();
			if (dataRow != null)
				return new Row(dataRow, keys);
		}

		return null;
	}

	/**
	 * @author "Giancarlo Panichi" <a
	 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
	 * 
	 *         Set SelectionMode
	 * 
	 *         The selection model supports 3 different selection modes: SINGLE
	 *         - Only single selections allowed SIMPLE - Multiple selections
	 *         without having to use the control and shift keys MULTI - Multiple
	 *         selections
	 * 
	 * 
	 * @param mode
	 */
	public void setSelectionModel(SelectionMode mode) {
		if (grid != null && grid.getSelectionModel()!=null) {
			grid.getSelectionModel().setSelectionMode(mode);
		}
	}

	/**
	 * @author "Giancarlo Panichi" <a
	 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
	 * 
	 *         getSelectedRows
	 * 
	 * @return List<Row> when multiple selection is enable
	 */
	public List<Row> getSelectedRows() {

		if (grid != null && grid.getSelectionModel()!=null) {
			List<DataRow> dataRows = grid.getSelectionModel()
					.getSelectedItems();
			List<Row> rows = new ArrayList<Row>();
			for (DataRow dataRow : dataRows) {
				if (dataRow != null) {
					rows.add(new Row(dataRow, keys));
				}
			}
			return rows;
		}

		return null;
	}

	protected void bindEventBus() {
		eventBus.addHandler(OpenTableEvent.TYPE, new OpenTableEventHandler() {

			
			public void onOpenTable(OpenTableEvent event) {
				doOpenTable();
			}
		});

		eventBus.addHandler(TableReadyEvent.TYPE, new TableReadyEventHandler() {

			public void onTableReady(TableReadyEvent event) {
				doTableReady(event.getTableDefinition());
			}
		});

		eventBus.addHandler(CloseTableEvent.TYPE, new CloseTableEventHandler() {

			public void onCloseTable(CloseTableEvent event) {
				doCloseTable();
			}
		});
	}

	protected void doOpenTable() {
		mask();
	}

	protected void doTableReady(TableDefinition definition) {
		Log.trace("table ready, setting grid up");
		mask("Loading table " + definition.getName() + "... ");
		setupGrid(definition);
		this.currentTableDefinition = definition;
		keys = new HashMap<String, ColumnKey>();
		for (ColumnDefinition column : definition.getColumnsAsList())
			keys.put(column.getLabel(), column.getKey());
		unmask();
	}

	protected void doCloseTable() {
		mask();
		grid = null;
		this.currentTableDefinition = null;
		this.keys.clear();
		container.clear();
		unmask();
	}

	protected void setupGrid(TableDefinition tableDefinition) {

		ColumnDefinition modelKeyColumn = tableDefinition.getModelKeyColumn();
		ListStore<DataRow> store = new ListStore<DataRow>(
				new DataRowModelKeyProvider(modelKeyColumn.getKey()));

		List<ColumnConfig<DataRow, ?>> columnsConfig = new ArrayList<ColumnConfig<DataRow, ?>>();

		List<ColumnDefinition> columns = tableDefinition.getColumnsAsList();
		Collections.sort(columns, new ColumnPositionComparator(false));

		for (ColumnDefinition columnDefinition : columns) {
			if (columnDefinition.getType() == ColumnType.USER)
				columnsConfig.add(ColumnConfigGenerator
						.generateConfiguration(columnDefinition));
		}

		// IdentityValueProvider<DataRow> identity = new
		// IdentityValueProvider<DataRow>();

		// LiveRowNumberer<DataRow> numberer = new
		// LiveRowNumberer<DataRow>(identity);

		// columnsConfig.add(numberer);

		ColumnModel<DataRow> columnModel = new ColumnModel<DataRow>(
				columnsConfig);

		if (grid == null) {
			reader = new DataRowPagingReader(tableDefinition);

			String path = GWT.getModuleBaseURL() + "tdwdata";
			RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
					path);
			builder.setHeader(ServletParameters.TD_SESSION_ID,
					String.valueOf(tdSessionId));
			HttpProxy<PagingLoadConfig> proxy = new HttpProxy<PagingLoadConfig>(
					builder);
			proxy.setWriter(new PagingLoadUrlEncoder());

			loader = new PagingLoader<PagingLoadConfig, PagingLoadResult<DataRow>>(
					proxy, reader);
			loader.setRemoteSort(true);

			liveGridView = new LiveGridView<DataRow>();
			grid = new Grid<DataRow>(store, columnModel);
			grid.setLoadMask(true);
			grid.setLoader(loader);
			grid.setView(liveGridView);

			// grid.setBorders(true);
			// TODO remove(0);
			container.add(grid, new VerticalLayoutData(1, 1));

			// numberer.initPlugin(grid);

			ToolBar toolBar = new ToolBar();
			toolBar.add(new LiveToolItem(grid));
			// toolBar.add(new LabelToolItem("my label"));
			container.add(toolBar, new VerticalLayoutData(1, 25));
			// toolBar.addStyleName(ThemeStyles.getStyle().borderTop());
			// toolBar.getElement().getStyle().setProperty("borderBottom",
			// "none");

			container.forceLayout();

			Scheduler.get().scheduleDeferred(new ScheduledCommand() {

				public void execute() {
					container.forceLayout();
				}
			});

			loader.load(0, liveGridView.getCacheSize());

		} else {
			// TODO we need to manually reset the sort
			loader.clearSortInfo();
			reader.setDefinition(tableDefinition);
			grid.reconfigure(store, columnModel);
			// TODO workaround: the loader is called only if the scroll bar is
			// in the middle
			if (loader.getOffset() == 0)
				loader.load(new PagingLoadConfigBean(0, liveGridView
						.getCacheSize()));
		}

		if (tableViewConfig != null) {
			if (tableViewConfig.getRowStyleProvider() != null) {
				grid.getView().setViewConfig(
						new TabularDataGridViewConfig(tableViewConfig,
								tableDefinition));
			}
		} else {
			grid.getView().setViewConfig(null);
		}

		if (contextMenu != null) {
			grid.setContextMenu(contextMenu);
		} else
			grid.setContextMenu(null);
	}

}
