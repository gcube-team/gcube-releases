/**
 * 
 */
package org.gcube.portlets.user.tdwx.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.td.widgetcommonevent.client.event.GridHeaderColumnMenuItemEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.GridHeaderColumnMenuItemType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.CellData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.grid.model.RowRaw;
import org.gcube.portlets.user.tdwx.client.config.GridAndCellSelectionModel;
import org.gcube.portlets.user.tdwx.client.config.Row;
import org.gcube.portlets.user.tdwx.client.config.TableViewConfig;
import org.gcube.portlets.user.tdwx.client.config.TabularDataGridViewConfig;
import org.gcube.portlets.user.tdwx.client.event.CloseTableEvent;
import org.gcube.portlets.user.tdwx.client.event.CloseTableEvent.CloseTableEventHandler;
import org.gcube.portlets.user.tdwx.client.event.GridReadyEvent;
import org.gcube.portlets.user.tdwx.client.event.OpenTableEvent;
import org.gcube.portlets.user.tdwx.client.event.OpenTableEvent.OpenTableEventHandler;
import org.gcube.portlets.user.tdwx.client.event.TableReadyEvent;
import org.gcube.portlets.user.tdwx.client.event.TableReadyEvent.TableReadyEventHandler;
import org.gcube.portlets.user.tdwx.client.filter.ExtendedGridFilters;
import org.gcube.portlets.user.tdwx.client.filter.FiltersGenerator;
import org.gcube.portlets.user.tdwx.client.model.grid.DataRowColumnConfig;
import org.gcube.portlets.user.tdwx.client.model.grid.DataRowModelKeyProvider;
import org.gcube.portlets.user.tdwx.client.model.grid.DataRowPagingReader;
import org.gcube.portlets.user.tdwx.client.model.util.ColumnConfigGenerator;
import org.gcube.portlets.user.tdwx.client.style.DefaultRowStyle;
import org.gcube.portlets.user.tdwx.client.util.ColumnPositionComparator;
import org.gcube.portlets.user.tdwx.client.util.PagingLoadUrlEncoder;
import org.gcube.portlets.user.tdwx.shared.ServletParameters;
import org.gcube.portlets.user.tdwx.shared.StaticFilterInformation;
import org.gcube.portlets.user.tdwx.shared.model.ColumnDefinition;
import org.gcube.portlets.user.tdwx.shared.model.ColumnKey;
import org.gcube.portlets.user.tdwx.shared.model.ColumnType;
import org.gcube.portlets.user.tdwx.shared.model.DataRow;
import org.gcube.portlets.user.tdwx.shared.model.TableDefinition;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.RequestBuilder;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.client.loader.HttpProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfigBean;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.HeaderContextMenuEvent;
import com.sencha.gxt.widget.core.client.event.HeaderContextMenuEvent.HeaderContextMenuHandler;
import com.sencha.gxt.widget.core.client.event.HeaderMouseDownEvent;
import com.sencha.gxt.widget.core.client.event.HeaderMouseDownEvent.HeaderMouseDownHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.filters.Filter;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.menu.SeparatorMenuItem;
import com.sencha.gxt.widget.core.client.selection.CellSelection;
import com.sencha.gxt.widget.core.client.toolbar.PagingToolBar;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 *         Defines the panel that will contain the grid
 * 
 */
public class TabularDataXGridPanel extends ContentPanel {

	private static final int PAGINGDIMENSION = 300;

	private int tdSessionId;

	private EventBus eventBus;

	private List<MenuItem> headerColumnMenuItems;

	private com.google.web.bindery.event.shared.EventBus externalBus;

	private ListStore<DataRow> store;
	private Grid<DataRow> grid;
	
	private TableDefinition tableDefinition;

	private VerticalLayoutContainer container;

	//private ExtendedLiveGridView<DataRow> liveGridView;

	private DataRowPagingReader reader;

	private PagingLoader<FilterPagingLoadConfig, PagingLoadResult<DataRow>> loader;

	private ExtendedGridFilters<DataRow> gridFilters;

	private ArrayList<StaticFilterInformation> staticFilters;

	private TableViewConfig tableViewConfig;

	private Menu contextMenu;

	private Map<String, ColumnKey> keys;

	private String visibleOnlyColumn;

	private boolean errorNotColored = false;

	private ColumnModel<DataRow> columnModel;

	private GridAndCellSelectionModel<DataRow> sm;

	private PagingToolBarX pagingToolBar;

	
	/**
	 * @param eventBus
	 */
	protected TabularDataXGridPanel(int tdSessionId, EventBus eventBus) {
		super();
		this.tdSessionId = tdSessionId;
		this.eventBus = eventBus;
		setBodyBorder(false);
		setBorders(false);
		setHeaderVisible(false);
		forceLayoutOnResize = true;
		setResize(true);
		bindEventBus();
		container = new VerticalLayoutContainer();
		container.setBorders(false);
		add(container, new MarginData(0));
	
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
	 * Return the context menu of grid
	 * 
	 * @return context menu of grid
	 */
	public Menu getGridContextMenu() {
		return contextMenu;
	}

	/**
	 * Add context menu of grid
	 * 
	 */
	public void setGridContextMenu(Menu contextMenu) {
		this.contextMenu = contextMenu;
	}

	/**
	 * Return table definition
	 * 
	 * @return
	 */
	public TableDefinition getTableDefinition() {
		return tableDefinition;
	}

	/**
	 * Get static filters
	 * 
	 * @return
	 */
	public ArrayList<StaticFilterInformation> getStaticFilters() {
		return staticFilters;
	}

	/**
	 * Set static filters
	 * 
	 * @param staticFilters
	 */
	public void setStaticFilters(
			ArrayList<StaticFilterInformation> staticFilters) {
		this.staticFilters = staticFilters;
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
	 * 
	 * 
	 * Set SelectionMode
	 * 
	 * The selection model supports 3 different selection modes: SINGLE - Only
	 * single selections allowed SIMPLE - Multiple selections without having to
	 * use the control and shift keys MULTI - Multiple selections
	 * 
	 * 
	 * @param mode
	 */
	public void setSelectionModel(SelectionMode mode) {
		if (grid != null && grid.getSelectionModel() != null) {
			grid.getSelectionModel().setSelectionMode(mode);
		}
	}

	/**
	 * 
	 * 
	 * getSelectedRows
	 * 
	 * @return List<Row> when multiple selection is enable
	 */
	public List<Row> getSelectedRows() {

		if (grid != null && grid.getSelectionModel() != null) {
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

	/**
	 * 
	 * 
	 * @return Selected Rows as List<RowRaw>
	 */
	public ArrayList<RowRaw> getSelectedRowsAsRaw() {
		if (grid != null && grid.getSelectionModel() != null) {
			ArrayList<RowRaw> listRowRaw = new ArrayList<RowRaw>();
			List<DataRow> dataRows = grid.getSelectionModel()
					.getSelectedItems();
			List<Row> rows = new ArrayList<Row>();
			for (DataRow dataRow : dataRows) {
				if (dataRow != null) {
					rows.add(new Row(dataRow, keys));
				}
			}

			for (Row row : rows) {
				HashMap<String, String> map = new HashMap<String, String>();
				ColumnModel<DataRow> columnModel = grid.getColumnModel();
				List<ColumnConfig<DataRow, ?>> columns = columnModel
						.getColumns();
				DataRowColumnConfig<?> columnDataRow = null;
				String rowId = null;
				for (ColumnConfig<DataRow, ?> col : columns) {
					columnDataRow = (DataRowColumnConfig<?>) col;
					ColumnDefinition colDef = columnDataRow.getDefinition();
					if (colDef != null) {
						String value;
						if (colDef.getColumnDataType().compareTo("Date") == 0) {
							value = row.getFieldAsDate(colDef
									.getColumnLocalId());
						} else {
							value = row.getFieldAsText(colDef
									.getColumnLocalId());
						}
						map.put(colDef.getColumnLocalId(), value);
						ColumnType ctype = colDef.getType();
						if (ctype == ColumnType.COLUMNID) {
							rowId = value;
						}

					}
				}
				if (rowId != null && !rowId.isEmpty() && map.size() > 0) {
					RowRaw rr = new RowRaw(rowId, map);
					listRowRaw.add(rr);
				}

			}
			return listRowRaw;
		}

		return null;
	}

	/**
	 * Retrieve selected cell value
	 * 
	 * @return
	 */
	public CellData getSelectedCell() {

		if (grid != null && grid.getSelectionModel() != null) {

			CellSelection<DataRow> cell = ((GridAndCellSelectionModel<DataRow>) grid
					.getSelectionModel()).getCellSelected();
			if (cell != null) {
				DataRow dataRow = cell.getModel();
				Row row = new Row(dataRow, keys);

				ColumnModel<DataRow> columnModel = grid.getColumnModel();
				List<ColumnConfig<DataRow, ?>> columns = columnModel
						.getColumns();

				// Retrieve ColumnId
				DataRowColumnConfig<?> columnId = null;
				boolean columnIdRetrieved = false;
				for (ColumnConfig<DataRow, ?> col : columns) {
					columnId = (DataRowColumnConfig<?>) col;
					ColumnType ctype = columnId.getDefinition().getType();
					if (ctype == ColumnType.COLUMNID) {
						columnIdRetrieved = true;
						break;
					}
				}
				if (columnIdRetrieved) {
					Log.debug("ColumnId Retrieved");
					ColumnDefinition columnIdSelected = columnId
							.getDefinition();
					// Retrieve ColumnSelected
					ColumnConfig<DataRow, ?> colSelected = columns.get(cell
							.getCell());
					DataRowColumnConfig<?> cSelected = (DataRowColumnConfig<?>) colSelected;
					ColumnDefinition cdSelected = cSelected.getDefinition();

					String cellValue;
					if (cdSelected.getColumnDataType().compareTo("Date") == 0) {
						cellValue = row.getFieldAsDate(cdSelected
								.getColumnLocalId());
					} else {
						cellValue = row.getFieldAsText(cdSelected
								.getColumnLocalId());
					}

					String columnIdValue = row.getFieldAsText(columnIdSelected
							.getColumnLocalId());

					CellData tdCell = new CellData(cellValue,
							cdSelected.getId(), cdSelected.getColumnLocalId(),
							cdSelected.getLabel(), columnIdValue,
							cell.getRow(), cell.getCell());

					Log.debug("Selected Cell: " + tdCell);
					return tdCell;
				} else {
					Log.debug("No ColumnId Retrieved");
					return null;
				}
			} else {
				Log.debug("No cell selected");
			}
		} else {
			Log.debug("No gridSelectionModel set");
		}

		return null;
	}

	/**
	 * Set one and only one visible column
	 * 
	 * @param columnLocalId
	 */
	public void setVisibleOnlyColumn(String columnLocalId) {
		Log.debug("setVisibleOnlyColumn: " + columnLocalId);
		visibleOnlyColumn = columnLocalId;

	}

	/**
	 * 
	 * @return
	 */
	public boolean isErrorNotColored() {
		return errorNotColored;
	}

	/**
	 * 
	 * @param errorNotColored
	 *            if true set background withe for rows with error
	 */
	public void setErrorNotColored(boolean errorNotColored) {
		Log.debug("ErrorNotColored set :" + errorNotColored);
		this.errorNotColored = errorNotColored;
	}

	/**
	 * 
	 * @param columnModel
	 * @return
	 */
	protected ColumnModel<DataRow> checkOnlyColumn(
			ColumnModel<DataRow> columnModel) {
		if (visibleOnlyColumn != null && !visibleOnlyColumn.isEmpty()) {
			Log.debug("setVisibleOnlyColumn: Grid not null");

			List<ColumnConfig<DataRow, ?>> columns = columnModel.getColumns();

			List<ColumnConfig<DataRow, ?>> columnsNew = new ArrayList<ColumnConfig<DataRow, ?>>();
			
			DataRowColumnConfig<?> columnTarget = null;
			for (ColumnConfig<DataRow, ?> col : columns) {
				columnTarget = (DataRowColumnConfig<?>) col;
				String columnLocal = columnTarget.getDefinition()
						.getColumnLocalId();
				if (visibleOnlyColumn.compareTo(columnLocal) == 0) {
					
					columnTarget.setHidden(false);
					columnsNew.add(columnTarget);
				} else {
					columnTarget.setHidden(true);
					columnsNew.add(columnTarget);
				}
			}
			columnModel = new ColumnModel<DataRow>(columnsNew);

		} else {
			Log.debug("visibleOnlyColumn null");
		}
		return columnModel;

	}

	/**
	 * Retrieve selected cell value
	 * 
	 * @param columnLocalId
	 * @return
	 */
	public ArrayList<String> getCellValue(String columnLocalId) {
		ArrayList<String> value = null;
		if (grid != null) {
			ColumnModel<DataRow> columnModel = grid.getColumnModel();

			List<ColumnConfig<DataRow, ?>> columns = columnModel.getColumns();

			// Retrieve Column with conlumnLocalId
			DataRowColumnConfig<?> columnTarget = null;
			boolean columnTargetRetrieved = false;
			for (ColumnConfig<DataRow, ?> col : columns) {
				columnTarget = (DataRowColumnConfig<?>) col;
				String columnLocal = columnTarget.getDefinition()
						.getColumnLocalId();
				if (columnLocalId.compareTo(columnLocal) == 0) {
					columnTargetRetrieved = true;
					break;
				}
			}

			if (columnTargetRetrieved) {
				ColumnDefinition cd = columnTarget.getDefinition();
				Log.debug("CD - Definition:" + cd.getId() + " ColumnLocalId:"
						+ cd.getColumnLocalId() + " Label:" + cd.getLabel()
						+ " Key:" + cd.getKey());
				List<Row> rowsSelected = getSelectedRows();
				Log.debug("Retriving rows selected");
				String rowS = "";
				ArrayList<String> rows = new ArrayList<String>();
				if (rowsSelected != null) {
					for (Row row : rowsSelected) {
						rowS = row.getFieldAsText(cd.getColumnLocalId());
						Log.debug("Selected Row:" + rowS);
						rows.add(rowS);
					}
				} else {
					Log.debug("no selected rows retrieved");
				}
				return rows;
			} else {
				Log.debug("no column target retrieved");
			}
		}
		return value;
	}

	/**
	 * getSelectedRowsId
	 * 
	 * @return List<String> list of identifiers of the selected rows
	 */
	public ArrayList<String> getSelectedRowsId() {
		if (grid != null) {
			ColumnModel<DataRow> columnModel = grid.getColumnModel();

			List<ColumnConfig<DataRow, ?>> columns = columnModel.getColumns();
			DataRowColumnConfig<?> c = null;
			boolean columnIdRetrieved = false;
			for (ColumnConfig<DataRow, ?> col : columns) {
				c = (DataRowColumnConfig<?>) col;
				ColumnType ctype = c.getDefinition().getType();
				if (ctype == ColumnType.COLUMNID) {
					columnIdRetrieved = true;
					break;
				}
			}

			if (columnIdRetrieved) {
				ColumnDefinition cd = c.getDefinition();
				Log.debug("CD - Definition:" + cd.getId() + " ColumnLocalId:"
						+ cd.getColumnLocalId() + " Label:" + cd.getLabel()
						+ " Key:" + cd.getKey());
				List<Row> rowsSelected = getSelectedRows();
				Log.debug("Retriving rows selected");
				String rowS = "";
				ArrayList<String> rows = new ArrayList<String>();
				if (rowsSelected != null) {
					for (Row row : rowsSelected) {
						rowS = row.getFieldAsText(cd.getColumnLocalId());
						Log.debug("Selected Row:" + rowS);
						rows.add(rowS);
					}
				} else {
					Log.debug("no selected rows retrieved");
				}
				return rows;
			} else {
				Log.debug("no COLUMNID retrieved");
				return null;
			}

		} else {
			return null;
		}

	}

	/**
	 * 
	 */
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
		keys = new HashMap<String, ColumnKey>();
		for (ColumnDefinition column : definition.getColumnsAsList())
			keys.put(column.getColumnLocalId(), column.getKey());
		unmask();
	}

	protected void doCloseTable() {
		mask();
		grid = null;
		keys.clear();
		container.clear();
		unmask();
	}

	/**
	 * 
	 * @param handler
	 */
	protected void addHeaderMouseDownHandler(HeaderMouseDownHandler handler) {
		if (grid != null) {

			if (handler == null) {
				handler = new HeaderMouseDownHandler() {

					@Override
					public void onHeaderMouseDown(HeaderMouseDownEvent event) {
						Log.debug("HeaderMouseDownEvent :"
								+ event.toDebugString());
						if (event.getEvent().getButton() == NativeEvent.BUTTON_RIGHT) {
							event.getEvent().stopPropagation();
						} else {

						}

					}
				};
			}

			grid.addHeaderMouseDownHandler(handler);
		}
	}

	/**
	 * Define the grid
	 * 
	 * 
	 * @param tableDefinition
	 */
	protected void setupGrid(TableDefinition tableDefinition) {
		this.tableDefinition = tableDefinition;

		ColumnDefinition modelKeyColumn = tableDefinition.getModelKeyColumn();
		store = new ListStore<DataRow>(new DataRowModelKeyProvider(
				modelKeyColumn.getKey()));

		store.addStoreUpdateHandler(new StoreUpdateEvent.StoreUpdateHandler<DataRow>() {

			@Override
			public void onUpdate(StoreUpdateEvent<DataRow> event) {
				Log.debug("UPDATE ROWS");
				Log.debug(event.getItems().toString());
			}
		});

		List<ColumnConfig<DataRow, ?>> columnsConfig = new ArrayList<ColumnConfig<DataRow, ?>>();

		List<ColumnDefinition> columns = tableDefinition.getColumnsAsList();
		Collections.sort(columns, new ColumnPositionComparator(false));

		for (ColumnDefinition columnDefinition : columns) {
			if (columnDefinition.getType() != ColumnType.SYSTEM)
				columnsConfig.add(ColumnConfigGenerator
						.generateConfiguration(columnDefinition));
		}


		columnModel = new ColumnModel<DataRow>(columnsConfig);

		columnModel = checkOnlyColumn(columnModel);

		/*
		columnModel
				.addColumnMoveHandler(new ColumnMoveEvent.ColumnMoveHandler() {

					@Override
					public void onColumnMove(ColumnMoveEvent event) {
						int columnIndex = event.getIndex();
						@SuppressWarnings("unchecked")
						ColumnConfig<DataRow, ?> col = (ColumnConfig<DataRow, ?>) event
								.getColumnConfig();
						DataRowColumnConfig<?> columnDataRow = (DataRowColumnConfig<?>) col;
						ColumnDefinition colDef = columnDataRow.getDefinition();

						Log.debug("Column Reordering", "Index: " + columnIndex
								+ " Label: " + colDef.getLabel()
								+ " Position: " + colDef.getPosition()
								+ " ColumnId: " + colDef.getColumnLocalId());

						if (colDef.isViewColumn()) {
							Info.display("Attention",
									"The view columns can not be moved");
							// ColumnHeader<DataRow>
							// colHeader=grid.getView().getHeader();

							// event.getSource().moveColumn(columnIndex,
							// colDef.getPosition());
						} else {
							ColumnsReorderingConfig columnsReorderingConfig = new ColumnsReorderingConfig(
									columnIndex, colDef);
							ColumnsReorderingEvent columnsReorderingEvent = new ColumnsReorderingEvent(
									columnsReorderingConfig);
							eventBus.fireEvent(columnsReorderingEvent);
						}
					}
				});
		*/
		if (grid == null) {
			Log.debug("Setup reader");

			reader = new DataRowPagingReader(tableDefinition);

			String path = GWT.getModuleBaseURL() + "tdwxdata";
			RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,
					path);
			builder.setHeader(ServletParameters.TD_SESSION_ID,
					String.valueOf(tdSessionId));
			

			HttpProxy<FilterPagingLoadConfig> proxy = new HttpProxy<FilterPagingLoadConfig>(
					builder);
			proxy.setWriter(new PagingLoadUrlEncoder(staticFilters));


			loader = new PagingLoader<FilterPagingLoadConfig, PagingLoadResult<DataRow>>(
					proxy, reader) {
				@Override
				protected FilterPagingLoadConfig newLoadConfig() {
					return new FilterPagingLoadConfigBean();
				}
			};

			loader.setRemoteSort(true);

			loader.addLoadHandler(new LoadResultListStoreBinding<FilterPagingLoadConfig, DataRow, PagingLoadResult<DataRow>>(
					store));

			pagingToolBar = new PagingToolBarX(PAGINGDIMENSION);
			pagingToolBar.getElement().getStyle()
					.setProperty("borderBottom", "none");			
			pagingToolBar.fixPageTextWidth();
			pagingToolBar.setItemId("TDMPagingToolBarX");
			pagingToolBar.bind(loader);


			grid = new Grid<DataRow>(store, columnModel) {
				@Override
				protected void onAfterFirstAttach() {
					super.onAfterFirstAttach();
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						@Override
						public void execute() {
							loader.load();
						}
					});
				}
			};
			sm = new GridAndCellSelectionModel<DataRow>();

			grid.setSelectionModel(sm);
			grid.setLoadMask(true);
			grid.setLoader(loader);
			
			grid.setBorders(false);
			grid.setColumnReordering(false);

			// Filter
			ArrayList<Filter<DataRow, ?>> filters = FiltersGenerator
					.generate(columnModel);
			gridFilters = new ExtendedGridFilters<DataRow>(loader);

			gridFilters.initPlugin(grid);
			gridFilters.setLocal(false);
			Log.debug("Filters: " + filters.size());
			for (Filter<DataRow, ?> filterGeneric : filters) {
				gridFilters.addFilter(filterGeneric);
			}

			//
			container.add(grid, new VerticalLayoutData(1, 1, new Margins(0)));
			container.add(pagingToolBar, new VerticalLayoutData(1, -1));

			
			if (contextMenu != null) {
				grid.setContextMenu(contextMenu);
			} else
				grid.setContextMenu(null);

			if (headerColumnMenuItems != null) {
				setHeaderContextMenuHandler();
			}

		} else {
			Log.debug("Setup grid not null");
			
			reader.setDefinition(tableDefinition);
			loader.clearSortInfo();

			loader.addLoadHandler(new LoadResultListStoreBinding<FilterPagingLoadConfig, DataRow, PagingLoadResult<DataRow>>(
					store));

			grid.reconfigure(store, columnModel);
			

			// Filter
			ArrayList<Filter<DataRow, ?>> filters = FiltersGenerator
					.generate(columnModel);

			
			Log.debug("Filters: " + filters.size());
			for (Filter<DataRow, ?> filterGeneric : filters) {
				gridFilters.addFilter(filterGeneric);
			}

			loader.load();
			
		}

		if (tableViewConfig != null) {
			Log.debug("Use specific grid View");
			if (tableViewConfig.getRowStyleProvider() != null) {
				grid.getView().setViewConfig(
						new TabularDataGridViewConfig(tableViewConfig,
								tableDefinition));
			}
		} else {
			Log.debug("Use default grid View");
			tableViewConfig = new TableViewConfig();
			Log.debug("Error Not Colored: " + errorNotColored);
			DefaultRowStyle rowStyle = new DefaultRowStyle(errorNotColored);

			tableViewConfig.setRowStyleProvider(rowStyle);
			grid.getView().setViewConfig(
					new TabularDataGridViewConfig(tableViewConfig,
							tableDefinition));
			
		}

		container.forceLayout();

		eventBus.fireEvent(new GridReadyEvent());

	}

	/**
	 * 
	 * Add Items to menu of Columns
	 * 
	 * @param items
	 * @param externalBus
	 */
	public void addGridHeaderContextMenuItems(final List<MenuItem> items,
			com.google.web.bindery.event.shared.EventBus externalBus) {
		this.headerColumnMenuItems = items;
		this.externalBus = externalBus;
	}

	/**
	 * Set Items on Menu of Columns
	 * 
	 */
	protected void setHeaderContextMenuHandler() {

		HeaderContextMenuHandler headerContextMenuHandler = new HeaderContextMenuEvent.HeaderContextMenuHandler() {

			public void onHeaderContextMenu(HeaderContextMenuEvent event) {
				Log.debug("Header Menu");
				final Menu menu = event.getMenu();
				final int colIndex = event.getColumnIndex();
				SelectionHandler<Item> handlerHeaderContextMenu = new SelectionHandler<Item>() {

					public void onSelection(SelectionEvent<Item> event) {
						Log.debug("Selected: "
								+ event.getSelectedItem().getId());
						if (event.getSelectedItem() instanceof MenuItem) {
							Log.debug("Event instanceof MenuItem");
							MenuItem menuItem = (MenuItem) event
									.getSelectedItem();
							if (headerColumnMenuItems.contains(menuItem)) {
								Log.debug("Event Fire on EventBus");
								externalBus
										.fireEvent(new GridHeaderColumnMenuItemEvent(
												GridHeaderColumnMenuItemType.SELECTED,
												menuItem.getId(), colIndex));

								menu.hide();
							}
						}
					}
				};

				menu.addSelectionHandler(handlerHeaderContextMenu);

				Log.debug("Adding Items to menu");
				SeparatorMenuItem separatorItem = new SeparatorMenuItem();
				menu.add(separatorItem);
				for (MenuItem m : headerColumnMenuItems) {
					menu.add(m);
				}
				menu.show();
			}
		};
		Log.debug("Created Handler");

		grid.addHeaderContextMenuHandler(headerContextMenuHandler);
		Log.debug("Header Column Menu Added");

	}

	/**
	 * 
	 * @param i
	 *            index of column in ColumnModel
	 * @return id of column and equals to column name on service
	 */
	public String getColumnName(int i) {
		Log.debug("Retrive Column Id of column:" + i);
		String columnName = null;
		if (grid != null) {
			ColumnModel<DataRow> columnModel = grid.getColumnModel();
			List<ColumnConfig<DataRow, ?>> columns = columnModel.getColumns();

			DataRowColumnConfig<?> dc = (DataRowColumnConfig<?>) columns.get(i);

			columnName = dc.getDefinition().getId();
			Log.debug("Column - Definition: Id: " + columnName);

		}
		return columnName;
	}

	/**
	 * 
	 * @param i
	 *            index of column in ColumnModel
	 * @return id of column and equals to column name on service
	 */
	public String getColumnLocalId(int i) {
		Log.debug("Retrive Column Local Id of column:" + i);
		String columnLocalId = null;
		if (grid != null) {
			ColumnModel<DataRow> columnModel = grid.getColumnModel();
			List<ColumnConfig<DataRow, ?>> columns = columnModel.getColumns();

			DataRowColumnConfig<?> dc = (DataRowColumnConfig<?>) columns.get(i);

			columnLocalId = dc.getDefinition().getColumnLocalId();
			Log.debug("Column - Definition: Column Local Id: " + columnLocalId);

		}
		return columnLocalId;
	}

}
