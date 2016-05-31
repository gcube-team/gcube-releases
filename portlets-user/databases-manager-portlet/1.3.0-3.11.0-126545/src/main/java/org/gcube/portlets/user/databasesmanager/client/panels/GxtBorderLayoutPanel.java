package org.gcube.portlets.user.databasesmanager.client.panels;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.user.databasesmanager.client.GWTdbManagerServiceAsync;
import org.gcube.portlets.user.databasesmanager.client.datamodel.FileModel;
import org.gcube.portlets.user.databasesmanager.client.datamodel.Result;
import org.gcube.portlets.user.databasesmanager.client.datamodel.SamplingResultWithFileFromServlet;
import org.gcube.portlets.user.databasesmanager.client.datamodel.Row;
import org.gcube.portlets.user.databasesmanager.client.datamodel.SubmitQueryData;
import org.gcube.portlets.user.databasesmanager.client.datamodel.SubmitQueryResultWithFileFromServlet;
import org.gcube.portlets.user.databasesmanager.client.events.LoadTablesEvent;
import org.gcube.portlets.user.databasesmanager.client.events.RandomSamplingEvent;
import org.gcube.portlets.user.databasesmanager.client.events.SamplingEvent;
import org.gcube.portlets.user.databasesmanager.client.events.SelectedItemEvent;
import org.gcube.portlets.user.databasesmanager.client.events.SelectedTableEvent;
import org.gcube.portlets.user.databasesmanager.client.events.ShowCreateTableEvent;
import org.gcube.portlets.user.databasesmanager.client.events.SmartSamplingEvent;
import org.gcube.portlets.user.databasesmanager.client.events.SubmitQueryEvent;
import org.gcube.portlets.user.databasesmanager.client.events.interfaces.LoadTablesEventHandler;
import org.gcube.portlets.user.databasesmanager.client.events.interfaces.RandomSamplingEventHandler;
import org.gcube.portlets.user.databasesmanager.client.events.interfaces.SamplingEventHandler;
import org.gcube.portlets.user.databasesmanager.client.events.interfaces.SelectedItemEventHandler;
import org.gcube.portlets.user.databasesmanager.client.events.interfaces.ShowCreateTableEventHandler;
import org.gcube.portlets.user.databasesmanager.client.events.interfaces.SmartSamplingEventHandler;
import org.gcube.portlets.user.databasesmanager.client.events.interfaces.SubmitQueryEventHandler;
import org.gcube.portlets.user.databasesmanager.client.resources.Images;
import org.gcube.portlets.user.databasesmanager.client.toolbar.GxtToolbarFunctionality;
import org.gcube.portlets.user.databasesmanager.shared.ConstantsPortlet;
import org.gcube.portlets.user.databasesmanager.shared.SessionExpiredException;
import org.gcube.portlets.widgets.sessionchecker.client.CheckSession;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid.ClicksToEdit;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;

//class that represents the container of all components
public class GxtBorderLayoutPanel extends ContentPanel {

	/* Create Root Logger */
	private static Logger rootLogger = Logger.getLogger("GxtBorderLayoutPanel");
	private ContentPanel north;
	private ContentPanel west;
	private LayoutContainer center;
	private ContentPanel centerUpper;
	private ContentPanel centerBottom;
	private GxtTreePanel treePanel;
	// top toolbar that contains the buttons
	private GxtToolbarFunctionality toolbar;
	// private List<String> listAttributes;
	// bus to manage events
	private HandlerManager eventBus = null;
	// RPC service
	private GWTdbManagerServiceAsync RPCservice = null;
	// dialog used to display the tables list
	private Dialog tablesLoaded = null;
	// to keep track of selected table in the tables list
	private String selectedTable = "";
	private String currentselectedTable = "";
	private FileModel table = new FileModel("");
	// variables to perform the table search
	private String keyword = "";
	private boolean startSearchTable = false;
	// toolbar for table search functionality
	private ToolBar toolBarTop = null;

	public GxtBorderLayoutPanel(HandlerManager eBus,
			GWTdbManagerServiceAsync service) throws Exception {
		eventBus = eBus;
		RPCservice = service;
		north = new ContentPanel();
		west = new ContentPanel();
		center = new LayoutContainer();
		treePanel = new GxtTreePanel(eventBus, service);
		// toolbar = new GxtToolbarFunctionality(eventBus);
		toolbar = new GxtToolbarFunctionality(eventBus, RPCservice);
		this.setHeaderVisible(false); // hide the header of the panel

		initLayout();
		createLayouts();
		addHandler();
	}

	private void initLayout() {
//		north.setLayout(new FitLayout());
		north.setHeaderVisible(false); // hide the header of the north panel
		north.setTopComponent(toolbar.getToolBar());
		north.add(toolbar.getToolBar());
		west.setLayout(new FitLayout());
		west.setHeading("Databases Resources");
		west.add(treePanel);
		west.setScrollMode(Scroll.AUTO);
		center.setLayout(new BorderLayout());
	}

	public void createLayouts() {
		// Border layout for the external container
		final BorderLayout borderLayoutNordWest = new BorderLayout();
		setLayout(borderLayoutNordWest);

		// NORD
		BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH,
				25, 25, 70);
		northData.setSplit(false); // Split bar between regions
		// northData.setFloatable(true);
		// northData.setCollapsible(true);
		// northData.setHideCollapseTool(false);
		// northData.setSplit(true);
		northData.setMargins(new Margins(0, 0, 1, 0));

		// WEST
		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST,
				330, 330, 400);
		westData.setSplit(true);
		westData.setCollapsible(true);
		westData.setMargins(new Margins(0, 1, 0, 0));

		// CENTER
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
		centerData.setMargins(new Margins(0));
		// center.setHeading("Information");
		centerData.setSplit(true);
		centerData.setCollapsible(true);

		// CENTER UPPER
		centerUpper = new ContentPanel();
		centerUpper.setLayout(new FitLayout());
		centerUpper.setHeading("Database Information");
		centerUpper.setScrollMode(Scroll.NONE);
		// centerUpper.setBottomComponent(toolBar);
		// toolBar.hide();
		BorderLayoutData centerUpperData = new BorderLayoutData(
				LayoutRegion.NORTH, 250, 100, 250);
		centerUpperData.setSplit(true); // Split bar between regions
		// northData.setFloatable(true);
		centerUpperData.setCollapsible(false);
		// northData.setHideCollapseTool(false);
		// northData.setSplit(true);
		centerUpperData.setMargins(new Margins(0, 0, 1, 0));

		// CENTER BOTTOM
		centerBottom = new ContentPanel();
		centerBottom.setLayout(new FitLayout());
		centerBottom.setHeading("Information Details");
		centerBottom.setScrollMode(Scroll.NONE);
		BorderLayoutData centerBottomData = new BorderLayoutData(
				LayoutRegion.CENTER);
		centerBottomData.setSplit(true);
		centerBottomData.setCollapsible(false);
		centerBottomData.setMargins(new Margins(0));
		// centerBottomData.setMargins(new Margins(1, 0, 0, 0));

		// to add the central panels to the second container
		center.add(centerUpper, centerUpperData);
		center.add(centerBottom, centerBottomData);

		add(north, northData);
		add(west, westData);
		add(center, centerData);
		// add(east, eastData);
	}

	// to add handlers
	private void addHandler() {

		eventBus.addHandler(SelectedItemEvent.TYPE,
				new SelectedItemEventHandler() {
					public void onSelectedItem(
							SelectedItemEvent selectedItemEvent) {
						// force the clean of the content panel
						centerBottom.removeAll();
						centerBottom.layout(true);

						// to get the selected item with its depth
						List<FileModel> data = treePanel.getTreePanel()
								.getSelectionModel().getSelectedItems();
						FileModel selectedItem = data.get(0);
						int DepthSelectedItem = treePanel.getTreeStore()
								.getDepth(selectedItem);
						String text = selectedItem.getName();
						rootLogger.log(Level.INFO, "selected item: " + text);

						// disable table details and sampling buttons
						toolbar.disableButtonsOperationsOnTable();
						// enable appropriate buttons according the selected
						// item
						toolbar.enableButtonOnToolbar(DepthSelectedItem,
								selectedItem.isSchema(),
								selectedItem.getDatabaseType());

						// clean variable at each item selection event
						table = new FileModel("");

						// clean variable
						selectedTable = "";
						currentselectedTable = "";

						if (DepthSelectedItem != 3) {
							centerUpper.removeAll();
							centerUpper.layout(true);
						}
						if (DepthSelectedItem == 3) { // the item selected is a
							// database.

							// show database information in the panel
							// display information about the selected database
							displayDBInfo(data.get(0));
						}
					}
				});

		eventBus.addHandler(LoadTablesEvent.TYPE, new LoadTablesEventHandler() {
			@Override
			public void onLoadTables(LoadTablesEvent loadTablesEvent) {
				// load tables
				loadTables();
			}
		});

		eventBus.addHandler(SubmitQueryEvent.TYPE,
				new SubmitQueryEventHandler() {
					@Override
					public void onSubmitQuery(SubmitQueryEvent submitQueryEvent) {
						Integer dialogID = new Integer(submitQueryEvent
								.getDialogID());
						rootLogger.log(Level.INFO, "dialogID: " + dialogID);
						submitQuery(dialogID);
					}
				});

		eventBus.addHandler(SamplingEvent.TYPE, new SamplingEventHandler() {
			@Override
			public void onSampling(SamplingEvent samplingEvent) {
				sample();
			}
		});

		eventBus.addHandler(SmartSamplingEvent.TYPE,
				new SmartSamplingEventHandler() {
					@Override
					public void onSmartSampling(
							SmartSamplingEvent smartSamplingEvent) {
						smartSample();
					}
				});

		eventBus.addHandler(RandomSamplingEvent.TYPE,
				new RandomSamplingEventHandler() {
					@Override
					public void onRandomSampling(
							RandomSamplingEvent randomSamplingEvent) {
						randomSample();
					}
				});

		eventBus.addHandler(ShowCreateTableEvent.TYPE,
				new ShowCreateTableEventHandler() {
					@Override
					public void onShowCreateTable(
							ShowCreateTableEvent showCreateTableEvent) {
						// get the selected item in the tree
						List<FileModel> data = treePanel.getTreePanel()
								.getSelectionModel().getSelectedItems();

						// if the selected table is equal to the previous table
						// keep track that table details have been just
						// displayed
						if (!(table.getName().equals(currentselectedTable))) {
							table.setName(currentselectedTable);
							table.setTableDetailsDisplayed(false);
						}
						rootLogger.log(Level.INFO,
								"selected table: " + table.getName());
						// get table details
						getTableDetails(table);
					}
				});
	
//		eventBus.addHandler(RefreshDataEvent.TYPE,
//				new RefreshDataEventHandler() {
//					@Override
//					public void onRefreshData(RefreshDataEvent refreshDataEvent) {
//					}
//				});
	}

	// method to load the tables list
	private void loadTables() {
		// clean variable
		// selectedTable = null;

		// disable table details and sampling buttons
		// toolbar.disableButtonsOperationsOnTable();
		// clear the panel
		// centerBottom.removeAll();
		// centerBottom.layout(true);

		// get the selected item
		List<FileModel> data = treePanel.getTreePanel().getSelectionModel()
				.getSelectedItems();
		FileModel element = data.get(0);
		// recover data inputs
		final LinkedHashMap<String, String> dataInput = new LinkedHashMap<String, String>();
		// check if the table has an associated schema
		String resource = "";
		String database = "";
		String schema = "";

		final String elementType;

		if (element.isDatabase()) { // the selected item is a database
			rootLogger.log(Level.INFO, "selected element is a database ");
			database = element.getName();
			resource = treePanel.getTreeStore().getParent(element).getName();
			// add data
			dataInput.put("ResourceName", resource);
			dataInput.put("DatabaseName", database);
			dataInput.put("SchemaName", "");
			elementType = ConstantsPortlet.DATABASE;
		} else { // the selected item is a schema
			rootLogger.log(Level.INFO, "selected element is a schema ");
			FileModel db = treePanel.getTreeStore().getParent(element);
			database = db.getName();
			resource = treePanel.getTreeStore().getParent(db).getName();
			schema = element.getName();
			// add data
			dataInput.put("ResourceName", resource);
			dataInput.put("DatabaseName", database);
			dataInput.put("SchemaName", schema);
			elementType = ConstantsPortlet.SCHEMA;
		}

		// print check
		rootLogger.log(Level.INFO, "ResourceName: " + resource);
		rootLogger.log(Level.INFO, "DatabaseName: " + database);
		rootLogger.log(Level.INFO, "SchemaName: " + schema);

		// create RpcProxy object to use the load configuration
		RpcProxy<PagingLoadResult<Result>> proxy = new RpcProxy<PagingLoadResult<Result>>() {
			@Override
			protected void load(Object loadConfig,
					final AsyncCallback<PagingLoadResult<Result>> callback) {
				rootLogger.log(Level.SEVERE, "Start RPC - getTables");

				rootLogger.log(Level.INFO, "keyword rpc: " + keyword);
				rootLogger.log(Level.INFO, "Search Table rpc: "
						+ startSearchTable);
				// remote rpc
				RPCservice.LoadTables((PagingLoadConfig) loadConfig, dataInput,
						elementType, startSearchTable, keyword,
						new AsyncCallback<PagingLoadResult<Result>>() {
							@Override
							public void onFailure(Throwable caught) {
								rootLogger.log(Level.SEVERE,
										"FAILURE RPC LoadTables");

								if (caught instanceof SessionExpiredException) {
									rootLogger.log(Level.INFO,
											"Session expired");
									CheckSession.showLogoutDialog();
									return;
								}
								// caught.printStackTrace();
								callback.onFailure(caught);
								// hide the dialog
								tablesLoaded.hide();

								if (caught.getMessage().contains(
										"Result not available")) {
									MessageBox.alert("Warning ",
											"<br/>Message:"
													+ "no tables available",
											null);
								} else {
									MessageBox.alert("Error ", "<br/>Message:"
											+ caught.getMessage(), null);
								}
								if (keyword == null) {
									startSearchTable = false;
								}
							}

							@Override
							public void onSuccess(
									PagingLoadResult<Result> result) {
								rootLogger.log(Level.SEVERE,
										"SUCCESS RPC LoadTables");
								callback.onSuccess(result);

								if (keyword == null) {
									startSearchTable = false;
								}
								// enable toolbar in the dialog
								toolBarTop.enable();

								// if (result != null) {
								//
								// List<Result> data = result.getData();
								// if (data.size() == 0) {
								// MessageBox
								// .alert("Warning ",
								// "<br/>Message:"
								// + "no tables availables",
								// null);
								// return;
								// }
								// }

							}
						});
				// rootLogger.log(Level.SEVERE, "End RPC LoadTables");
			}
		};

		// loader to load page enabled set of data
		final PagingLoader<PagingLoadResult<Result>> loader = new BasePagingLoader<PagingLoadResult<Result>>(
				proxy);
		// loader.addListener(Loader.BeforeLoad, new Listener<LoadEvent>() {
		// public void handleEvent(LoadEvent be) {
		// BasePagingLoadConfig m = be.<BasePagingLoadConfig> getConfig();
		// m.set("start", m.get("offset"));
		// // m.set("ext", "js");
		// // m.set("lightWeight", true);
		// // m.set("sort", (m.get("sortField") == null) ? "" :
		// m.get("sortField"));
		// // m.set("dir", (m.get("sortDir") == null || (m.get("sortDir") !=
		// null && m.<SortDir> get("sortDir").equals(
		// // SortDir.NONE))) ? "" : m.get("sortDir"));
		// }
		// });
		// loader.setRemoteSort(true);

		// to create the listStore using the loader
		final ListStore<Result> result = new ListStore<Result>(loader);

		// bind the loader with a PagingToolBar.
		final PagingToolBar toolBar = new PagingToolBar(100);
		toolBar.bind(loader);

		// create the column configuration
		ColumnModel cm = createColumnModelForTables();

		// create the grid with a result list and the column model
		final EditorGrid<Result> grid = new EditorGrid<Result>(result, cm);
//		final Grid<Result> grid = new Grid<Result>(result, cm);
		// set the double click for row edit
		grid.setClicksToEdit(ClicksToEdit.TWO);
		// grid.setStateId("TablesList");
		// grid.setStateful(true);
		grid.setLoadMask(true);
		grid.setBorders(true);
		//to enable text selection
		grid.disableTextSelection(false);
		// grid.setAutoExpandColumn("comments");
		// grid.setStyleAttribute("borderTop", "none");
		grid.setStripeRows(true);
		// to manage the table selection in the grid
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		

		// add the search functionality
		// Top toolbar for search table functionality
		toolBarTop = new ToolBar();

		// TextField for specify the table name to search
		final TextField<String> searchTable = new TextField<String>();
		searchTable.setEmptyText("enter a text");
		searchTable.setToolTip("search a table in the database");
		searchTable.setAllowBlank(true);

		// add the button search
		final Button searchButton = new Button("", Images.iconSearch());
		searchButton.setToolTip("Search");

		// add the button cancel
		Button cancel = new Button("", Images.iconCancel());
		cancel.setToolTip("Cancel");

		// add Buttons and TextField to the toolbar
		toolBarTop.add(searchTable);
		toolBarTop.add(searchButton);
		toolBarTop.add(cancel);

		searchButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				// start search calling the rpc
				// get the keyword
				keyword = searchTable.getValue();
				startSearchTable = true;
				rootLogger.log(Level.INFO, "keyword: " + keyword);
				rootLogger.log(Level.INFO, "Search Table: " + startSearchTable);
				PagingLoadConfig config = new BasePagingLoadConfig();
				// The offset for the first record to retrieve.
				config.setOffset(0);
				// The number of records being requested.
				config.setLimit(100);

				// Map<String, Object> state = grid.getState();
				// if (state.containsKey("offset")) {
				// int offset = (Integer) state.get("offset");
				// int limit = (Integer) state.get("limit");
				// config.setOffset(offset);
				// config.setLimit(limit);
				// }
				// if (state.containsKey("sortField")) {
				// config.setSortField((String) state.get("sortField"));
				// config.setSortDir(SortDir.valueOf((String) state
				// .get("sortDir")));
				// }
				loader.load(config);
			}
		});

		cancel.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				// clear variables
				keyword = null;
				startSearchTable = false;
				// clear the textfield
				searchTable.clear();
				PagingLoadConfig config = new BasePagingLoadConfig();
				config.setOffset(0);
				config.setLimit(100);
				loader.load(config);
			}
		});

		// "Enter" listener for the search table functionality
		searchTable.addKeyListener(new KeyListener() {
			@Override
			public void componentKeyDown(ComponentEvent event) {
				super.componentKeyDown(event);
				if (event.getKeyCode() == KeyCodes.KEY_ENTER)
					searchButton.fireEvent(Events.Select);
			}
		});

		// listener to manage the table selection in the grid
		grid.addListener(Events.RowClick, new Listener<GridEvent<Result>>() {
			@Override
			public void handleEvent(final GridEvent<Result> be) {
				rootLogger.log(Level.INFO, "RowClick Event->table clicked: "
						+ grid.getSelectionModel().getSelectedItems().get(0)
								.getValue());
				// select the item
				grid.getSelectionModel().select(
						grid.getSelectionModel().getSelectedItems().get(0),
						true);

				// tablesLoaded.addListener(Events.Hide, new
				// Listener<WindowEvent>() {
				// @Override
				// public void handleEvent(WindowEvent be) {
				// // TODO Auto-generated method stub
				// System.out.println("closing the window");
				// }
				// });
			}
		});

		grid.addListener(Events.BeforeEdit, new Listener<GridEvent<Result>>() {
			@Override
			public void handleEvent(final GridEvent<Result> be) {
				rootLogger.log(Level.INFO, "table edited: "
						+ be.getValue().toString());
				// selectedTable = be.getValue().toString();
			}
		});

		// this listener catch the Attach event.It fires when
		// the Tables List button is selected.
		// it seems to fire when the event source is attached to the browser's
		// document or detached from it.
		grid.addListener(Events.Attach, new Listener<GridEvent<Result>>() {
			public void handleEvent(GridEvent<Result> be) {
				rootLogger.log(Level.INFO, "event Attach handled");
				// disable the top toolbar at the first tables loading in such a
				// way to disallow a user the search. It will be enabled in the
				// rpc when the tables are retrieved.
				toolBarTop.disable();

				PagingLoadConfig config = new BasePagingLoadConfig();
				// The offset for the first record to retrieve.
				config.setOffset(0);
				// The number of records being requested.
				config.setLimit(100);

				// if (state.containsKey("offset")) {
				// // rootLogger.log(Level.INFO, "state contains offset");
				// int offset = (Integer) state.get("offset");
				// rootLogger.log(Level.INFO, "offset: " + offset);
				// int limit = (Integer) state.get("limit");
				// rootLogger.log(Level.INFO, "limit: " + limit);
				// config.setOffset(offset);
				// config.setLimit(limit);
				// }
				// if (state.containsKey("sortField")) {
				// config.setSortField((String) state.get("sortField"));
				// config.setSortDir(SortDir.valueOf((String) state
				// .get("sortDir")));
				// }
				loader.load(config);
			}
		});

		tablesLoaded = new Dialog();
		tablesLoaded.setLayout(new FitLayout());
		// ADD CANCEL BUTTON
//		tablesLoaded.addButton(new Button("CANCEL"));
		tablesLoaded.setHeading("Tables");
		tablesLoaded.setResizable(false);
		tablesLoaded.setModal(true);
		tablesLoaded.setBlinkModal(true);
		tablesLoaded.setSize(600, 400);
		tablesLoaded.setScrollMode(Scroll.NONE);
		tablesLoaded.setHideOnButtonClick(true);
		tablesLoaded.setMaximizable(true);
		tablesLoaded.setMinimizable(false);
		tablesLoaded.setClosable(true);
		tablesLoaded.setBottomComponent(toolBar);
		// toolBarTop.getAriaSupport().setLabel("Table Search");

		// enter event in the search
		// TextField<String> searchTable = new TextField<String>();
		// searchTable.setFieldLabel("Search: ");
		// searchTable.addListener(Events.KeyPress, new Listener<FieldEvent>() {
		// @Override
		// public void handleEvent(FieldEvent be) {
		// // TODO Auto-generated method stub
		// // start search calling the rpc
		// // get the keyword
		// keyword = searchTable.getValue();
		// startSearchTable = true;
		// rootLogger.log(Level.INFO, "keyword: " + keyword);
		// rootLogger.log(Level.INFO, "Search Table: " + startSearchTable);
		// PagingLoadConfig config = new BasePagingLoadConfig();
		// config.setOffset(0);
		// config.setLimit(100);
		// loader.load(config);
		// }
		// });
		tablesLoaded.setTopComponent(toolBarTop);

		// listener to manage the table selection in the grid
		final Button ok = (Button) tablesLoaded.getButtonBar().getWidget(0);
		ok.disable();
		rootLogger.log(Level.INFO, "button: " + ok.getText());
		
//		Button canc = (Button) tablesLoaded.getButtonBar().getWidget(1);
//		rootLogger.log(Level.INFO, "button: " + canc.getText());

		// listener for buttons
		ok.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				keyword = null;
				startSearchTable = false;

				// if (selectedTable != null) {
				if (!currentselectedTable.equals(selectedTable)) {
					currentselectedTable = selectedTable;
					// refresh the content in the two panels
					centerBottom.removeAll();
					centerBottom.layout(true);
					centerUpper.removeAll();
					centerUpper.layout(true);

					// display the table in the panel "Database Information"
					displayTableName(currentselectedTable);

					// to get the selected item in the tree panel
					List<FileModel> data = treePanel.getTreePanel()
							.getSelectionModel().getSelectedItems();
					FileModel selectedItem = data.get(0);

					// fire event
					eventBus.fireEvent(new SelectedTableEvent(selectedItem,
							currentselectedTable));
				}
			}
		});

		//CANC BUTTON that closes the window
//		canc.addSelectionListener(new SelectionListener<ButtonEvent>() {
//			@Override
//			public void componentSelected(ButtonEvent ce) {
//				keyword = null;
//				startSearchTable = false;
//				// refresh the database information panel
//				// centerUpper.removeAll();
//				// centerUpper.layout(true);
//				tablesLoaded.hide();
//			}
//		});
		
		tablesLoaded.addListener(Events.Hide, new Listener<WindowEvent>() {
			@Override
			public void handleEvent(WindowEvent be) {
				
				keyword = null;
				startSearchTable = false;
				tablesLoaded.hide();
				
				rootLogger.log(Level.INFO, "Window Tables closed");
				
			}});

		// listener to manage the table selection in the grid
		grid.getSelectionModel().addSelectionChangedListener(
				new SelectionChangedListener<Result>() {
					@Override
					public void selectionChanged(
							SelectionChangedEvent<Result> se) {
						selectedTable = grid.getSelectionModel()
								.getSelectedItems().get(0).getValue();
						rootLogger.log(Level.INFO,
								"SelectionChangedListener->table selected: "
										+ selectedTable);

						ok.enable();
					}
				});
		tablesLoaded.add(grid);
		tablesLoaded.show();
	}

	// method to submit a query
	private void submitQuery(final Integer dialogID) {
		// get the selected item
		List<FileModel> data = treePanel.getTreePanel().getSelectionModel()
				.getSelectedItems();
		FileModel selectedItem = data.get(0);

		// get information related to the selected item
		// recover input data
		LinkedHashMap<String, String> dataForSubmitQuery = new LinkedHashMap<String, String>();
		dataForSubmitQuery.put("ResourceName", selectedItem.getResourceName());
		dataForSubmitQuery.put("DatabaseName", selectedItem.getDatabaseName());

		// print check
		// System.out.println("ResourceName" + selectedItem.getResourceName());
		// System.out.println("DatabaseName" + selectedItem.getDatabaseName());

		// get data list (inserted in the form)
		LinkedHashMap<Integer, SubmitQueryData> submitQueryDataList = toolbar
				.getSubmitQueryDataList();
		// get dialog list
		LinkedHashMap<Integer, Dialog> dialogList = toolbar.getDialogFormList();
		// get the dialog related to the ID
		final Dialog form = dialogList.get(dialogID);
		// get the data form related to the ID
		final SubmitQueryData dataQuery = submitQueryDataList.get(dialogID);
		// get the UID related to submitQuery operation
		LinkedHashMap<Integer, String> uidSubmitQueryList = toolbar
				.getUIDSubmitQueryList();
		final String UID = uidSubmitQueryList.get(dialogID);

		// rootLogger.log(Level.INFO, "query: "+ dataQuery.getQuery() +
		// "submitquery->dialogID: "
		// + dialogID);

		// determine the language
		String dialect;

		if (selectedItem.isDatabase()) { // the selected item is a database
			// System.out.println("the selected item is a database");
			rootLogger.log(Level.INFO, "the selected item is a database");
			// determine the dialect recovering the dialect
			dialect = selectedItem.getDBInfo().get(2).getValue();
			rootLogger.log(Level.INFO,
					"determined Dialect for smart correction: " + dialect);
		} else {
			FileModel parent1 = treePanel.getTreeStore()
					.getParent(selectedItem);
			if (parent1.isDatabase()) { // the selected item is a schema
				rootLogger.log(Level.INFO, "the selected item is a schema");
				// determine the dialect recovering the dialect
				dialect = parent1.getDBInfo().get(2).getValue();
				rootLogger.log(Level.INFO,
						"determined Dialect for smart correction: " + dialect);
			} else { // the selected item is a table
						// System.out.println("the selected item is a table");
				rootLogger.log(Level.INFO, "the selected item is a table");
				FileModel parent2 = treePanel.getTreeStore().getParent(parent1);
				// determine the dialect recovering the dialect
				dialect = parent2.getDBInfo().get(2).getValue();
				rootLogger.log(Level.INFO,
						"determined Dialect for smart correction: " + dialect);
			}
		}
		String language = ConstantsPortlet.NONE;

		if (dialect.toUpperCase().contains(ConstantsPortlet.POSTGRES)) {
			language = ConstantsPortlet.POSTGRES;
		}
		if (dialect.toUpperCase().contains(ConstantsPortlet.MYSQL)) {
			language = ConstantsPortlet.MYSQL;
		}
		rootLogger.log(Level.INFO, "Dialect used for smart correction: "
				+ language);

		// remote rpc
		RPCservice.submitQuery(dataForSubmitQuery, dataQuery.getQuery(), true,
				dataQuery.getSmartCorrection(), language, UID,
				new AsyncCallback<SubmitQueryResultWithFileFromServlet>() {

					// TODO: TO REMOVE data "true" as input if you manage the
					// read-only query in the form

					@Override
					public void onFailure(Throwable caught) {
						// Window.alert(caught.getMessage());
						rootLogger
								.log(Level.SEVERE, "FAILURE RPC submitQuery ");

						if (caught instanceof SessionExpiredException) {
							rootLogger.log(Level.INFO, "Session expired");
							CheckSession.showLogoutDialog();
							return;
						}

						// Listener<MessageBoxEvent> l = new
						// Listener<MessageBoxEvent>() {
						// public void handleEvent(MessageBoxEvent ce) {
						// // Button btn = ce.getButtonClicked();
						// // Info.display("MessageBox",
						// // "The '{0}' button was pressed",
						// // btn.getHtml());
						// // Button btn = ce.getButtonClicked();
						//
						// if (form.getBody().isMasked())
						// form.getBody().unmask();
						// }
						// };

						if (!(caught.getMessage().equals("ServerException"))) {
							if (caught.getMessage().contains(
									"Result not available")) {
								MessageBox.alert("Warning ", "<br/>Message:"
										+ "The query returned 0 values", null);
							} else {
								MessageBox.alert("Error ", "<br/>Message:"
										+ caught.getMessage(), null);
							}
						}

						// if (!(caught instanceof
						// javax.xml.ws.soap.SOAPFaultException)){
						// MessageBox.alert("Error ",
						// "<br/>Message:" + caught.getMessage(), l);
						// }

						if (form.getBody().isMasked())
							form.getBody().unmask();

						if (form.getButtonById(Dialog.OK).isEnabled() == false) {
							form.getButtonById(Dialog.OK).enable();
						}
					}

					@Override
					public void onSuccess(
							SubmitQueryResultWithFileFromServlet obj) {
						rootLogger.log(Level.SEVERE, "SUCCESS RPC submitQuery");

						if (obj != null) {
							// get list attributes
							List<String> listAttributes = obj.getListOutput();

							if (form.getBody().isMasked())
								form.getBody().unmask();

							if (form.getButtonById(Dialog.OK).isEnabled() == false) {
								form.getButtonById(Dialog.OK).enable();
							}

							// if listAttributes is null the computation has
							// been removed from the statisticalManager.In this
							// case no message is displayed.
							if (listAttributes == null) {
								return;
							}

							if (listAttributes.size() == 0) {
								// if (form.getBody().isMasked())
								// form.getBody().unmask();
								rootLogger.log(Level.SEVERE,
										"No results have been returned");
								MessageBox.alert("Warning ", "<br/>Message:"
										+ "The query returned 0 values", null);
								return;
							}

							String query = "";

							if (dataQuery.getSmartCorrection() == false) {
								query = dataQuery.getQuery();
							} else {
								query = obj.getConvertedQuery();
							}
							// if ((listAttributes == null)
							// || (listAttributes.size() == 0)) {
							//
							// // if (form.getBody().isMasked())
							// // form.getBody().unmask();
							//
							// rootLogger.log(Level.SEVERE,
							// "No results have been returned");
							//
							// MessageBox.alert("Error ", "<br/>Message:"
							// + "Result not available", null);
							// return;
							// }

							rootLogger.log(Level.SEVERE,
									"SUCCESS RPC submitQuery");
							rootLogger.log(Level.SEVERE, "output size: "
									+ listAttributes.size());

							// recover query
							// Result query = result.remove(0);
							// recover converted query
							// Result convertedQuery;

							// if (dataQuery.getSmartCorrection() == true) {
							// result.remove(0);
							// // convertedQuery = result.get(1);
							// }

							// get the attributes list for the result table
							// List<String> listAttributes = new
							// ArrayList<String>();
							// listAttributes = getListAttributes(result.get(0)
							// .getValue());
							// // remove the header in order to parse only the
							// result
							// result.remove(0);

							// parse the result in order to obtain a table
							// boolean submitQueryEventManaged = true;
							// parseResult(result, form, dialogID,
							// submitQueryEventManaged);

							// parseSubmitQueryResult(result, form, dialogID,
							// listAttributes, dataQuery.getQuery());

							// get path
//							String fileName = obj.getFileName();
							String urlFile = obj.getUrlFile();
							
							int submitQueryTotalRows = obj.getSubmitQueryTotalRows();
							parseSubmitQueryResult(form, dialogID,
									listAttributes, query, urlFile, UID, submitQueryTotalRows);

						}

					}
				});
	}

	// start the parsing of the submit result in order to obtain a table
	private void parseSubmitQueryResult(Dialog dialog, final int dialogID,
			final List<String> listAttributes, String query, final String urlFile,
			final String UID, int submitQueryTotalRows) {

		final Dialog form = dialog;
		// define the proxy and create the grid to display in the dialog
		// create RpcProxy object to use the load configuration
		RpcProxy<PagingLoadResult<Row>> proxy = new RpcProxy<PagingLoadResult<Row>>() {
			@Override
			protected void load(Object loadConfig,
					final AsyncCallback<PagingLoadResult<Row>> callback) {
				rootLogger.log(Level.SEVERE,
						"Start RPC - loadSubmitQueryResult");

				// remote rpc
				RPCservice.loadSubmitResult((PagingLoadConfig) loadConfig,
						listAttributes, UID,
						new AsyncCallback<PagingLoadResult<Row>>() {
							@Override
							public void onFailure(Throwable caught) {
								rootLogger.log(Level.SEVERE,
										"FAILURE RPC loadSubmitQueryResult");

								if (caught instanceof SessionExpiredException) {
									rootLogger.log(Level.INFO,
											"Session expired");
									CheckSession.showLogoutDialog();
									return;
								}

								Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {
									public void handleEvent(MessageBoxEvent ce) {
										if (form.getBody().isMasked())
											form.getBody().unmask();

										if (form.getButtonById(Dialog.OK)
												.isEnabled() == false) {
											form.getButtonById(Dialog.OK)
													.enable();
										}
									}
								};

								MessageBox.alert("Error ", "<br/>Message:"
										+ caught.getMessage(), l);
								callback.onFailure(caught);
							}

							@Override
							public void onSuccess(PagingLoadResult<Row> result) {
								rootLogger.log(Level.SEVERE,
										"SUCCESS RPC loadSubmitQueryResult");

								if (result != null) {
									callback.onSuccess(result);
									List<Row> rows = result.getData();

									if (form.getBody().isMasked())
										form.getBody().unmask();

									if (form.getButtonById(Dialog.OK)
											.isEnabled() == false) {
										form.getButtonById(Dialog.OK).enable();
									}

									if ((rows == null) || (rows.size() == 0)) {
										rootLogger
												.log(Level.SEVERE,
														"No results have been returned");

										MessageBox
												.alert("Warning ",
														"<br/>Message:"
																+ "The query returned 0 values",
														null);

										return;
									}
								}
								// if (result != null) {
								// rootLogger.log(Level.SEVERE,
								// "rows not null");
								// }
							}
						});
			}
		};

		// loader to load page enabled set of data
		final PagingLoader<PagingLoadResult<Row>> loader = new BasePagingLoader<PagingLoadResult<Row>>(
				proxy);

		// to create the listStore using the loader
		final ListStore<Row> store = new ListStore<Row>(loader);
		// bind the loader with a PagingToolBar.
		final PagingToolBar toolBar = new PagingToolBar(100);
		toolBar.bind(loader);

		// create the grid with a result list and the column model
		Grid<Row> grid;
		grid = new Grid<Row>(store, createColumnModel(listAttributes));
		grid.setLoadMask(true);
		grid.setBorders(true);
		grid.setStripeRows(true);
		// to manage the table selection in the grid
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		grid.addListener(Events.Attach, new Listener<GridEvent<Row>>() {
			public void handleEvent(GridEvent<Row> be) {
				rootLogger.log(Level.INFO, "event Attach handled");

				// disable the top toolbar at the first tables loading in such a
				// way to disallow a user the search. It will be enabled in the
				// rpc when the tables are retrieved.
				// toolBarTop.disable();

				PagingLoadConfig config = new BasePagingLoadConfig();
				// The offset for the first record to retrieve.
				config.setOffset(0);
				// The number of records being requested.
				config.setLimit(100);

				// if (state.containsKey("offset")) {
				// // rootLogger.log(Level.INFO, "state contains offset");
				// int offset = (Integer) state.get("offset");
				// rootLogger.log(Level.INFO, "offset: " + offset);
				// int limit = (Integer) state.get("limit");
				// rootLogger.log(Level.INFO, "limit: " + limit);
				// config.setOffset(offset);
				// config.setLimit(limit);
				// }
				// if (state.containsKey("sortField")) {
				// config.setSortField((String) state.get("sortField"));
				// config.setSortDir(SortDir.valueOf((String) state
				// .get("sortDir")));
				// }
				loader.load(config);
			}
		});

		// // Display the submit query result as a table
		// Dialog submitResult = new Dialog();
		// submitResult.setLayout(new FitLayout());
		//
		// TO MODIFY
		// submitResult.setHeading("Result");
		// submitResult.setSize(600, 400);
		//
		// submitResult.add(grid);
		// submitResult.show();

		// Display the submit query result as a table
		Dialog submitResult = new Dialog() {
			// override the maximize event modifying it with
			// a different behaviour if the mimimize event
			// occurs
			public void maximize() {
				if (isCollapsed()) {
					expand();
				} else {
					super.maximize();
				}
			}
		};

		// submitResult.setLayout(new FitLayout());
		submitResult.setHeading("Result Query " + dialogID+ "  (Total Rows: "+submitQueryTotalRows+")");
		submitResult.setResizable(false);
		submitResult.setSize(600, 400);
		submitResult.setScrollMode(Scroll.NONE);
		submitResult.setHideOnButtonClick(true);
		submitResult.setMaximizable(true);
		submitResult.setMinimizable(true);
		// submitResult.setBottomComponent(toolBar);

		submitResult.setLayout(new RowLayout(Style.Orientation.VERTICAL));
		// to add the panel of the converted query and the panel that display
		// the query's result
		ContentPanel q = new ContentPanel();
		q.setLayout(new FitLayout());
		q.setHeaderVisible(false);
		q.addText(query);
		submitResult.add(q, new RowData(1, 0.3));

		ContentPanel g = new ContentPanel();
		g.setLayout(new FitLayout());
		g.setHeaderVisible(false);
		g.setBottomComponent(toolBar);
		g.add(grid);
		g.layout(true);

		// mimimize event handled
		submitResult.addListener(Events.Minimize, new Listener<WindowEvent>() {
			@Override
			public void handleEvent(WindowEvent be) {
				// collapse the dialog
				be.getWindow().collapse();
			}
		});
		// maximize event handled
		submitResult.addListener(Events.Maximize, new Listener<WindowEvent>() {
			@Override
			public void handleEvent(WindowEvent be) {
				// expand the dialog
				if (be.getWindow().isCollapsed()) {
					be.getWindow().expand();
				}
			}
		});

		submitResult.addListener(Events.Hide, new Listener<WindowEvent>() {
			@Override
			public void handleEvent(WindowEvent be) {
				// call rpc to remove the stored result
				RPCservice.refreshDataOnServer(UID, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						rootLogger.log(Level.SEVERE,
								"FAILURE RPC refreshDataOnServer");

						if (caught instanceof SessionExpiredException) {
							rootLogger.log(Level.INFO, "Session expired");
							CheckSession.showLogoutDialog();
							return;
						}
					}

					@Override
					public void onSuccess(Void result) {
						rootLogger.log(Level.SEVERE,
								"SUCCESS RPC refreshDataOnServer");
					}
				});
			}
		});
		
		// listener on the dialog "ok" button
		submitResult.getButtonById(Dialog.OK).addSelectionListener(
				new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {

						rootLogger.log(Level.SEVERE,
								"button ok clicked");
						
						// call rpc to remove the stored result
						RPCservice.refreshDataOnServer(UID, new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								rootLogger.log(Level.SEVERE,
										"FAILURE RPC refreshDataOnServer");

								if (caught instanceof SessionExpiredException) {
									rootLogger.log(Level.INFO, "Session expired");
									CheckSession.showLogoutDialog();
									return;
								}
							}

							@Override
							public void onSuccess(Void result) {
								rootLogger.log(Level.SEVERE,
										"SUCCESS RPC refreshDataOnServer");
							}
						});
						
					}
				});

		// add the button to download the result
//		final String urlFile = Window.Location.getProtocol() + "//"
//				+ Window.Location.getHost() + fileName;

		Button download = new Button("Download",
				new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						Window.open(urlFile, "Result File", "_blank");
					}
				});
		submitResult.addButton(download);
		// submitResult.add(grid);
		submitResult.add(g, new RowData(1, 0.7));
		submitResult.show();
	}

	// method to perform the sample table
	private void sample() {
		final GxtBorderLayoutPanel obj = this;
		rootLogger.log(Level.SEVERE, "Start RPC - sample");

		// get the selected table
		List<FileModel> data = treePanel.getTreePanel().getSelectionModel()
				.getSelectedItems();
		// the selected item
		FileModel selectedItem = data.get(0);
		// recover data inputs
		final LinkedHashMap<String, String> dataInput = new LinkedHashMap<String, String>();
		// check if the table has an associated schema
		FileModel schema;
		FileModel database;
		FileModel resource;

		String elementType;

		if (selectedItem.isDatabase()) {
			// the table has not a schema because the selected item is a
			// database
			database = selectedItem;
			resource = treePanel.getTreeStore().getParent(database);

			dataInput.put("ResourceName", resource.getName());
			dataInput.put("DatabaseName", database.getName());
			dataInput.put("SchemaName", "");
			dataInput.put("TableName", currentselectedTable);

			rootLogger.log(Level.INFO, "ResourceName: " + resource.getName());
			rootLogger.log(Level.INFO, "DatabaseName: " + database.getName());
			rootLogger.log(Level.INFO, "SchemaName: " + "");
			rootLogger.log(Level.INFO, "TableName: " + currentselectedTable);

			elementType = ConstantsPortlet.DATABASE;
		} else {
			// the table has a schema because the selected item is a schema
			schema = selectedItem;
			database = treePanel.getTreeStore().getParent(schema);
			resource = treePanel.getTreeStore().getParent(database);

			dataInput.put("ResourceName", resource.getName());
			dataInput.put("DatabaseName", database.getName());
			dataInput.put("SchemaName", schema.getName());
			dataInput.put("TableName", currentselectedTable);

			rootLogger.log(Level.INFO, "ResourceName: " + resource.getName());
			rootLogger.log(Level.INFO, "DatabaseName: " + database.getName());
			rootLogger.log(Level.INFO, "SchemaName: " + schema.getName());
			rootLogger.log(Level.INFO, "TableName: " + currentselectedTable);

			elementType = ConstantsPortlet.SCHEMA;
		}

		// to mask the entire content panel
		obj.mask("Sampling the table", "x-mask-loading");

		// call remote service
		RPCservice.sample(dataInput, elementType,
				new AsyncCallback<SamplingResultWithFileFromServlet>() {
					@Override
					public void onFailure(Throwable caught) {
						// Window.alert(caught.getMessage());
						rootLogger.log(Level.SEVERE, "FAILURE RPC sample");

						if (caught instanceof SessionExpiredException) {
							rootLogger.log(Level.INFO, "Session expired");
							CheckSession.showLogoutDialog();
							return;
						}

						if (caught.getMessage()
								.contains("Result not available")) {
							MessageBox.alert("Warning ", "<br/>Message:"
									+ "The table has 0 rows", null);
						} else {
							MessageBox.alert("Error ",
									"<br/>Message:" + caught.getMessage(), null);
						}
						if (obj.isMasked()) {
							obj.unmask();
						}
					}

					@Override
					public void onSuccess(
							SamplingResultWithFileFromServlet samplingResult) {
						rootLogger.log(Level.SEVERE, "SUCCESS RPC sample");

						if (samplingResult != null) {
							// get data
							List<Result> result = samplingResult
									.getListOutput();
							// get the attributes list for the result
							// table
							List<String> listAttributes = new ArrayList<String>();
							listAttributes = getListAttributes(result.get(0)
									.getValue());
							// remove the header in order to parse only
							// the result
							result.remove(0);
							rootLogger.log(Level.SEVERE, "output size: "
									+ result.size());

							// get path
//							String fileName = samplingResult.getFileName();
							String urlFile = samplingResult.getUrlFile();
							// parse the result in order to obtain a
							// table
							parseResult(result, listAttributes, urlFile);

						} else {
							MessageBox.alert("Error ", "<br/>Message: "
									+ "no data available", null);

							if (obj.isMasked()) {
								obj.unmask();
							}

						}

					}
				});

	}

	// method that performs the smart sample
	private void smartSample() {
		final GxtBorderLayoutPanel obj = this;
		rootLogger.log(Level.SEVERE, "Start RPC - smartSample");
		// get the selected table
		List<FileModel> data = treePanel.getTreePanel().getSelectionModel()
				.getSelectedItems();
		// the selected item
		FileModel selectedItem = data.get(0);
		// recover data inputs for the algorithm
		final LinkedHashMap<String, String> dataInput = new LinkedHashMap<String, String>();
		// check if the table has an associated schema
		FileModel schema;
		FileModel database;
		FileModel resource;

		String elementType;

		if (selectedItem.isDatabase()) {
			// the table has not a schema
			database = selectedItem;
			resource = treePanel.getTreeStore().getParent(database);

			dataInput.put("ResourceName", resource.getName());
			dataInput.put("DatabaseName", database.getName());
			dataInput.put("SchemaName", "");
			dataInput.put("TableName", currentselectedTable);

			rootLogger.log(Level.INFO, "ResourceName: " + resource.getName());
			rootLogger.log(Level.INFO, "DatabaseName: " + database.getName());
			rootLogger.log(Level.INFO, "SchemaName: " + "");
			rootLogger.log(Level.INFO, "TableName: " + currentselectedTable);
			elementType = ConstantsPortlet.DATABASE;
		} else {
			// the table has a schema
			schema = selectedItem;
			database = treePanel.getTreeStore().getParent(schema);
			resource = treePanel.getTreeStore().getParent(database);

			dataInput.put("ResourceName", resource.getName());
			dataInput.put("DatabaseName", database.getName());
			dataInput.put("SchemaName", schema.getName());
			dataInput.put("TableName", currentselectedTable);

			rootLogger.log(Level.INFO, "ResourceName: " + resource.getName());
			rootLogger.log(Level.INFO, "DatabaseName: " + database.getName());
			rootLogger.log(Level.INFO, "SchemaName: " + schema.getName());
			rootLogger.log(Level.INFO, "TableName: " + currentselectedTable);
			elementType = ConstantsPortlet.SCHEMA;
		}
		// to mask the entire content panel
		obj.mask("Sampling the table", "x-mask-loading");

		// call remote service
		RPCservice.smartSample(dataInput, elementType,
				new AsyncCallback<SamplingResultWithFileFromServlet>() {
					@Override
					public void onFailure(Throwable caught) {
						// Window.alert(caught.getMessage());
						rootLogger.log(Level.SEVERE, "FAILURE RPC smartSample");

						if (caught instanceof SessionExpiredException) {
							rootLogger.log(Level.INFO, "Session expired");
							CheckSession.showLogoutDialog();
							return;
						}
						if (caught.getMessage()
								.contains("The table has 0 rows")) {
							MessageBox.alert("Warning ", "<br/>Message:"
									+ "The table has 0 rows", null);
						} else {
							MessageBox.alert("Error ",
									"<br/>Message:" + caught.getMessage(), null);
						}
						if (obj.isMasked()) {
							obj.unmask();
						}
					}

					@Override
					public void onSuccess(
							SamplingResultWithFileFromServlet samplingResult) {
						rootLogger.log(Level.SEVERE, "SUCCESS RPC smartSample");

						if (samplingResult != null) {
							// get data
							List<Result> result = samplingResult
									.getListOutput();
							// get the attributes list for the result
							// table
							List<String> listAttributes = new ArrayList<String>();
							listAttributes = getListAttributes(result.get(0)
									.getValue());
							// remove the header in order to parse only
							// the result
							result.remove(0);
							rootLogger.log(Level.SEVERE, "output size: "
									+ result.size());
							// get path
//							String fileName = samplingResult.getFileName();
							String urlFile = samplingResult.getUrlFile();
							// parse the result in order to obtain a
							// table
							parseResult(result, listAttributes, urlFile);
						} else {
							MessageBox.alert("Error ", "<br/>Message: "
									+ "no data available", null);

							if (obj.isMasked()) {
								obj.unmask();
							}

						}
					}
				});
	}

	// method to perform the random sample
	private void randomSample() {
		final GxtBorderLayoutPanel obj = this;
		rootLogger.log(Level.SEVERE, "Start RPC - randomSample");
		// get the selected table
		List<FileModel> data = treePanel.getTreePanel().getSelectionModel()
				.getSelectedItems();
		// the selected item
		FileModel selectedItem = data.get(0);
		// recover data inputs for the algorithm
		final LinkedHashMap<String, String> dataInput = new LinkedHashMap<String, String>();

		// check if the table has an associated schema
		FileModel schema;
		FileModel database;
		FileModel resource;

		String elementType;

		if (selectedItem.isDatabase()) {
			// the table has not a schema
			database = selectedItem;
			resource = treePanel.getTreeStore().getParent(database);

			dataInput.put("ResourceName", resource.getName());
			dataInput.put("DatabaseName", database.getName());
			dataInput.put("SchemaName", "");
			dataInput.put("TableName", currentselectedTable);

			rootLogger.log(Level.INFO, "ResourceName: " + resource.getName());
			rootLogger.log(Level.INFO, "DatabaseName: " + database.getName());
			rootLogger.log(Level.INFO, "SchemaName: " + "");
			rootLogger.log(Level.INFO, "TableName: " + currentselectedTable);
			elementType = ConstantsPortlet.DATABASE;
		} else {
			// the table has a schema
			schema = selectedItem;
			database = treePanel.getTreeStore().getParent(schema);
			resource = treePanel.getTreeStore().getParent(database);

			dataInput.put("ResourceName", resource.getName());
			dataInput.put("DatabaseName", database.getName());
			dataInput.put("SchemaName", schema.getName());
			dataInput.put("TableName", currentselectedTable);

			rootLogger.log(Level.INFO, "ResourceName: " + resource.getName());
			rootLogger.log(Level.INFO, "DatabaseName: " + database.getName());
			rootLogger.log(Level.INFO, "SchemaName: " + schema.getName());
			rootLogger.log(Level.INFO, "TableName: " + currentselectedTable);
			elementType = ConstantsPortlet.SCHEMA;
		}

		// to mask the entire content panel
		obj.mask("Sampling the table", "x-mask-loading");

		// call remote service
		RPCservice.randomSample(dataInput, elementType,
				new AsyncCallback<SamplingResultWithFileFromServlet>() {
					@Override
					public void onFailure(Throwable caught) {
						// Window.alert(caught.getMessage());
						rootLogger
								.log(Level.SEVERE, "FAILURE RPC randomSample");

						if (caught instanceof SessionExpiredException) {
							rootLogger.log(Level.INFO, "Session expired");
							CheckSession.showLogoutDialog();
							return;
						}

						if (caught.getMessage()
								.contains("Result not available")) {
							MessageBox.alert("Warning ", "<br/>Message:"
									+ "The table has 0 rows", null);
						} else {
							MessageBox.alert("Error ",
									"<br/>Message:" + caught.getMessage(), null);
						}
						if (obj.isMasked()) {
							obj.unmask();
						}
					}

					@Override
					public void onSuccess(
							SamplingResultWithFileFromServlet samplingResult) {
						rootLogger
								.log(Level.SEVERE, "SUCCESS RPC randomSample");

						if (samplingResult != null) {
							// get data
							List<Result> result = samplingResult
									.getListOutput();
							// get the attributes list for the result
							// table
							List<String> listAttributes = new ArrayList<String>();
							listAttributes = getListAttributes(result.get(0)
									.getValue());
							// remove the header in order to parse only
							// the result
							result.remove(0);
							rootLogger.log(Level.SEVERE, "output size: "
									+ result.size());
							// get path
//							String fileName = samplingResult.getFileName();
							String urlFile = samplingResult.getUrlFile();
							// parse the result in order to obtain a
							// table
							parseResult(result, listAttributes, urlFile);
						} else {
							MessageBox.alert("Error ", "<br/>Message: "
									+ "no data available", null);

							if (obj.isMasked()) {
								obj.unmask();
							}

						}
					}
				});
	}

	// start the parsing of the submit result in order to obtain a table
	private void parseResult(List<Result> result,
			final List<String> listAttributes, final String urlFile) {
		// to unmask the entire content panel
		final GxtBorderLayoutPanel obj = this;
		// final Dialog form = dialog;

		RPCservice.parseCVSString(result, listAttributes,
				new AsyncCallback<List<Row>>() {
					@Override
					public void onFailure(Throwable caught) {
						// Window.alert(caught.getMessage());
						rootLogger.log(Level.SEVERE, "FAILURE RPC parseResult");

						if (caught instanceof SessionExpiredException) {
							rootLogger.log(Level.INFO, "Session expired");
							CheckSession.showLogoutDialog();
							return;
						}

						MessageBox.alert("Error ",
								"<br/>Message:Error in server while loading data. Exception: " + caught.getMessage(), null);

						// if the submit query event has been performed the
						// dialog form is unmasked otherwise if a sampling
						// operation is performed the entire panel is masked.
						if (obj.isMasked()) {
							obj.unmask();
						}
						// if (submitQueryEventManaged == true) {
						//
						// if (form.getBody().isMasked())
						// form.getBody().unmask();
						// } else {
						// if (obj.isMasked()) {
						// obj.unmask();
						// }
						// }
					}

					@Override
					public void onSuccess(List<Row> rows) {
						rootLogger.log(Level.SEVERE, "SUCCESS RPC parseResult");

						if (rows != null) {
							// Display the submit query result as a table
							Dialog sampleResult = new Dialog() {

								// override the maximize event modifying it with
								// a different behaviour if the mimimize event
								// occurs
								public void maximize() {
									if (isCollapsed()) {
										expand();
									} else {
										super.maximize();
									}
								}

							};

							// if (submitQueryEventManaged == true) {
							// submitResult.setHeading("Result Query "
							// + dialogID);
							// } else {
							// submitResult.setHeading("Result");
							// }

							sampleResult.setLayout(new FitLayout());
							sampleResult.setHeading("Result");
							sampleResult.setResizable(false);
							// submitResult.setHeading("Submit Query");
							// submitResult.setWidth(290);
							// submitResult.setHeight(250);
							// submitResult.setModal(true);
							// submitResult.setBlinkModal(true);
							// submitResult.setBodyStyle("padding:9px;");
							sampleResult.setSize(600, 400);
							// submitResult.setScrollMode(Scroll.AUTO);
							sampleResult.setScrollMode(Scroll.NONE);
							sampleResult.setHideOnButtonClick(true);
							sampleResult.setMaximizable(true);
							sampleResult.setMinimizable(true);
							// submitResult.addText("Result Table");

							ListStore<Row> store = new ListStore<Row>();
							store.add(rows);

							Grid<Row> grid;
							grid = new Grid<Row>(store,
									createColumnModel(listAttributes));

							// grid.setAutoExpandColumn("value");
							grid.setBorders(true);
							// grid.setAutoWidth(true);
							RowData data = new RowData(.5, 1);
							data.setMargins(new Margins(6));

							// mimimize event handled
							sampleResult.addListener(Events.Minimize,
									new Listener<WindowEvent>() {
										@Override
										public void handleEvent(WindowEvent be) {
											// collapse the dialog
											be.getWindow().collapse();
										}
									});
							// maximize event handled
							sampleResult.addListener(Events.Maximize,
									new Listener<WindowEvent>() {
										@Override
										public void handleEvent(WindowEvent be) {
											// expand the dialog
											if (be.getWindow().isCollapsed()) {
												be.getWindow().expand();
											}
										}
									});

							// add the button to download the result
//							final String urlFile = Window.Location
//									.getProtocol()
//									+ "//"
//									+ Window.Location.getHost() + fileName;

							Button download = new Button("Download",
									new SelectionListener<ButtonEvent>() {
										@Override
										public void componentSelected(
												ButtonEvent ce) {
											Window.open(urlFile, "Result File",
													"_blank");
										}
									});
							sampleResult.addButton(download);
							sampleResult.add(grid, data);
							sampleResult.show();

							// if the submit query event has been performed the
							// dialog form is unmasked otherwise if a sampling
							// operation is performed the entire panel is
							// masked.

							// if (submitQueryEventManaged == true) {
							// if (form.getBody().isMasked())
							// form.getBody().unmask();
							// } else {
							// if (obj.isMasked()) {
							// obj.unmask();
							// }
							// }

							if (obj.isMasked()) {
								obj.unmask();
							}
						} else {
							// if (submitQueryEventManaged == true) {
							// if (form.getBody().isMasked())
							// form.getBody().unmask();
							// } else {
							// if (obj.isMasked()) {
							// obj.unmask();
							// }
							// }

							if (obj.isMasked()) {
								obj.unmask();
							}
						}
					}
				});
	}

	// get attributes list for display the result in a table
	private List<String> getListAttributes(String value) {
		List<String> listAttributes = new ArrayList<String>();
		// recover attribute fields for the result table
		String headers = value;
		// rootLogger.log(Level.INFO, "Headers fields table: " + headers);
		listAttributes = parseAttributesTableResult(headers);
		rootLogger.log(Level.INFO,
				"attributes number: " + listAttributes.size());

		// rootLogger.log(Level.INFO, "attributes list: ");
		// print check
		// for (int i = 0; i < listAttributes.size(); i++) {
		// rootLogger.log(Level.INFO, "attribute: " + listAttributes.get(i));
		// }
		return listAttributes;
	}

	// create column configuration for the grid
	private ColumnModel createColumnModel(List<String> listAttributes) {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		ColumnConfig column;
		column = new ColumnConfig();
		// column.setId("index");
		// column.setHeader("Index");
		// column.setWidth(100);
		// column.setSortable(false);
		// configs.add(column);

		for (int i = 0; i < listAttributes.size(); i++) {
			// rootLogger.log(Level.INFO, "attribute: " +
			// listAttributes.get(i));
			column = new ColumnConfig();
			column.setId(listAttributes.get(i));
			// column.setId("\"" + fields.get(i) + "\"");
			column.setHeader(listAttributes.get(i));
			column.setWidth(100);
			column.setSortable(false);
			configs.add(column);
		}
		return new ColumnModel(configs);
	}

	private List<String> parseAttributesTableResult(String phrase) {
		String delimiter = ",";
		List<String> elements = new ArrayList<String>();
		int idxdelim = -1;
		phrase = phrase.trim();

		while ((idxdelim = phrase.indexOf(delimiter)) >= 0) {
			elements.add(phrase.substring(0, idxdelim));
			phrase = phrase.substring(idxdelim + 1).trim();
		}
		elements.add(phrase);
		return elements;
	}

	// create column configuration for the grid
	private ColumnModel createColumnModelForDBInfo(List<Result> result) {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		ColumnConfig column = new ColumnConfig();
		column.setId("index");
		column.setHeader("Property");
		column.setWidth(100);
		// column.setSortable(false);

//		TextField<String> textProperty = new TextField<String>();
//		// text.setAllowBlank(false);
//		column.setEditor(new CellEditor(textProperty));
		configs.add(column);

		column = new ColumnConfig();
		column.setId("value");
		column.setHeader("Value");
		column.setWidth(600);
		// column.setSortable(false);

//		TextField<String> textValue = new TextField<String>();
//		// text.setAllowBlank(false);
//		column.setEditor(new CellEditor(textValue));
		configs.add(column);

//		ListStore<Result> store = new ListStore<Result>();
//		store.add(result);
		return new ColumnModel(configs);
	}

	// create column configuration for the grid
	private ColumnModel createColumnModelForTables() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		ColumnConfig column = new ColumnConfig();
		// column.setId("index");
		// column.setHeader("Index");
		// column.setWidth(100);
		// configs.add(column);

//		column = new ColumnConfig();
		column.setId("value");
		column.setHeader("Name");
		column.setWidth(600);
		// column.setSortable(false);

//		TextField<String> textValue = new TextField<String>();
//		// text.setAllowBlank(false);
//		column.setEditor(new CellEditor(textValue));
		configs.add(column);
		// ListStore<Result> store = new ListStore<Result>();
		// store.add(result);
		return new ColumnModel(configs);
	}

	private void displayDBInfo(FileModel element) {
		rootLogger.log(Level.INFO, "displaying info");
		List<Result> dataDB = element.getDBInfo();

		if (dataDB != null) {
			rootLogger.log(Level.INFO, "info size: " + dataDB.size());
			ListStore<Result> store = new ListStore<Result>();
			store.add(dataDB);
//			EditorGrid<Result> grid = new EditorGrid<Result>(store,
//					createColumnModelForDBInfo(dataDB));
			Grid<Result> grid = new Grid<Result>(store,
					createColumnModelForDBInfo(dataDB));
			// grid.setAutoExpandColumn("name");
			grid.setBorders(true);
			grid.disableTextSelection(false);
			// grid.setAutoWidth(true);
			// RowData data = new RowData(.5, 1);
			// data.setMargins(new Margins(6));
			centerUpper.removeAll();
			// centerUpper.add(grid, data);
			centerUpper.add(grid);
			centerUpper.layout(true);
		}
	}

	// method to get the table details
	private void getTableDetails(final FileModel Table) {
		// to unmask the entire content panel
		final GxtBorderLayoutPanel obj = this;
		rootLogger.log(Level.SEVERE, "Start RPC - getTableDetails");

		// get the selected table
		List<FileModel> data = treePanel.getTreePanel().getSelectionModel()
				.getSelectedItems();
		// the selected item
		FileModel selectedItem = data.get(0);
		// recover data inputs fo the algorithm
		final LinkedHashMap<String, String> dataInput = new LinkedHashMap<String, String>();
		// check if the table has an associated schema
		FileModel schema;
		FileModel database;
		FileModel resource;

		if (selectedItem.isDatabase()) {
			// the table has not a schema
			database = selectedItem;
			resource = treePanel.getTreeStore().getParent(database);

			dataInput.put("ResourceName", resource.getName());
			dataInput.put("DatabaseName", database.getName());
			dataInput.put("SchemaName", "");
			dataInput.put("TableName", currentselectedTable);

			rootLogger.log(Level.INFO, "ResourceName: " + resource.getName());
			rootLogger.log(Level.INFO, "DatabaseName: " + database.getName());
			rootLogger.log(Level.INFO, "SchemaName: " + "");
			rootLogger.log(Level.INFO, "TableName: " + currentselectedTable);
		} else {
			// the table has a schema
			schema = selectedItem;
			database = treePanel.getTreeStore().getParent(schema);
			resource = treePanel.getTreeStore().getParent(database);

			dataInput.put("ResourceName", resource.getName());
			dataInput.put("DatabaseName", database.getName());
			dataInput.put("SchemaName", schema.getName());
			dataInput.put("TableName", currentselectedTable);

			rootLogger.log(Level.INFO, "ResourceName: " + resource.getName());
			rootLogger.log(Level.INFO, "DatabaseName: " + database.getName());
			rootLogger.log(Level.INFO, "SchemaName: " + schema.getName());
			rootLogger.log(Level.INFO, "TableName: " + currentselectedTable);
		}

		// details for the table are displayed if they have not been displayed
		// yet
		if (Table.isTableDetailsDisplayed()) {
			displayTableDetails(Table);
		} else {
			// to mask the entire content panel
			obj.mask("Loading details", "x-mask-loading");

			// call remote service
			RPCservice.getTableDetails(dataInput,
					new AsyncCallback<LinkedHashMap<String, FileModel>>() {
						@Override
						public void onFailure(Throwable caught) {
							rootLogger.log(Level.SEVERE,
									"FAILURE RPC getTableDetails");

							if (caught instanceof SessionExpiredException) {
								rootLogger.log(Level.INFO, "Session expired");
								CheckSession.showLogoutDialog();
								return;
							}

							MessageBox.alert("Error ",
									"<br/>Message:" + caught.getMessage(), null);

							if (obj.isMasked()) {
								obj.unmask();
							}
						}

						@Override
						public void onSuccess(
								LinkedHashMap<String, FileModel> result) {
							rootLogger.log(Level.SEVERE,
									"SUCCESS RPC getTableDetails");

							// details are recovered about
							// the selected table
							if (result.size() != 0) {
								// recover keys from the
								// result
								Set<String> keys = result.keySet();
								Object[] array = keys.toArray();

								// recover details
								List<Result> TableDetails = new ArrayList<Result>();

								for (int i = 0; i < result.size(); i++) {
									if (array[i].toString().contains(
											"CreateTable")) {
										// recover the
										// showCreateTable
										// statement
										Result row = new Result(
												"Create statement", result.get(
														array[i].toString())
														.getName());
										TableDetails.add(row);
									}

									if (array[i].toString().contains(
											"Column Names")) {
										// recover the
										// column names
										Result row = new Result("Column names",
												result.get(array[i].toString())
														.getName());
										TableDetails.add(row);
									}

									if (array[i].toString().contains(
											"NumberRows")) {
										// recover the
										// column names
										Result row = new Result(
												"Number of rows", result.get(
														array[i].toString())
														.getName());
										TableDetails.add(row);
									}
								}
								Table.setTableDetails(TableDetails);
								displayTableDetails(Table);
								Table.setTableDetailsDisplayed(true);
							}
						}
					});
		}
	}

	private void displayTableDetails(FileModel table) {
		rootLogger.log(Level.INFO, "displaying table details");

		List<Result> tableDetails = table.getTableDetails();
		rootLogger.log(Level.INFO, "details size: " + tableDetails.size());

		ListStore<Result> store = new ListStore<Result>();
		store.add(tableDetails);
//		EditorGrid<Result> grid = new EditorGrid<Result>(store,
//				createColumnModelForDBInfo(tableDetails));
		Grid<Result> grid = new Grid<Result>(store,
				createColumnModelForDBInfo(tableDetails));
		// grid.setAutoExpandColumn("name");
		grid.setBorders(true);
		grid.disableTextSelection(false);
		// grid.setAutoWidth(true);
		// RowData data = new RowData(.5, .1);
		// data.setMargins(new Margins(6));
		centerBottom.removeAll();
		// centerBottom.add(grid, data);
		centerBottom.add(grid);
		centerBottom.layout(true);

		// to unmask the entire content panel
		if (this.isMasked()) {
			this.unmask();
		}
	}

	private void displayTableName(String tableName) {
		rootLogger.log(Level.INFO, "displaying table name " + tableName
				+ " in the panel");
		final Result table = new Result("Selected table", tableName);
		final ListStore<Result> store = new ListStore<Result>();
		store.add(table);

		// create column configuration
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		ColumnConfig column = new ColumnConfig();
		column.setId("index");
		column.setHeader("Description");
		column.setWidth(100);
		configs.add(column);

		column = new ColumnConfig();
		column.setId("value");
		column.setHeader("Value");
		column.setWidth(600);

//		TextField<String> textValue = new TextField<String>();
//		column.setEditor(new CellEditor(textValue) {
//			// public Object preProcessValue(Object value) {
//			// System.out.println("value preProcess: " +
//			// store.getModels().get(0).getValue());
//			// return store.getModels().get(0).getValue();
//			// }
//			public Object postProcessValue(Object value) {
//				// System.out.println("value postProcess: " +
//				// store.findModel(table).getValue());
//				return store.findModel(table).getValue();
//			}
//		});

		configs.add(column);
		ColumnModel cm = new ColumnModel(configs);

		// grid
//		EditorGrid<Result> grid = new EditorGrid<Result>(store, cm);
		Grid<Result> grid = new Grid<Result>(store, cm);
		grid.disableTextSelection(false);
		// display information in the panel
		centerUpper.removeAll();
		centerUpper.add(grid);
		centerUpper.layout(true);
	}
}
