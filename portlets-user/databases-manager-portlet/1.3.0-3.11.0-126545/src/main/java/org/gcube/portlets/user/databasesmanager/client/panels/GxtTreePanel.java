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
import org.gcube.portlets.user.databasesmanager.client.events.RefreshDataEvent;
import org.gcube.portlets.user.databasesmanager.client.events.SelectedItemEvent;
import org.gcube.portlets.user.databasesmanager.client.events.interfaces.RefreshDataEventHandler;
import org.gcube.portlets.user.databasesmanager.client.resources.Images;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.layout.FlowData;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import org.gcube.portlets.user.databasesmanager.client.datamodel.GeneralOutputFromServlet;
import org.gcube.portlets.user.databasesmanager.shared.ConstantsPortlet;
import org.gcube.portlets.user.databasesmanager.shared.SessionExpiredException;
import org.gcube.portlets.widgets.sessionchecker.client.CheckSession;

//class that implements the tree object
public class GxtTreePanel extends LayoutContainer {
	// to manage the tree
	private TreeStore<FileModel> store;
	private TreePanel<FileModel> treePanel;
	// to manage events
	private HandlerManager eventBus = null;
	private String value = "";
	private int treeDepthSelectedItem;
	// the rpc remote service
	private GWTdbManagerServiceAsync RPCservice = null;
	// to keep track of the current selected item
	private FileModel currentSelectedItem = null;
	// the GWT logger
	private static Logger rootLogger = Logger.getLogger("GxtTreePanel");

	// constructor
	public GxtTreePanel(HandlerManager eBus, GWTdbManagerServiceAsync service)
			throws Exception {
		RPCservice = service;
		eventBus = eBus;
		store = new TreeStore<FileModel>();
		// remove the scroll mode (the scroll of the panel that contains the
		// tree is used)
		this.setScrollMode(Scroll.NONE);
		this.initLayout();
		addHandler();
	}

	private void initLayout() throws Exception {
		setLayout(new FlowLayout(10));
		treePanel = new TreePanel<FileModel>(store) {
			@Override
			public boolean hasChildren(FileModel parent) {
				if (parent.isExpanded() == true) {
					return true;
				} else {
					return false;
				}
			}

			public void onComponentEvent(ComponentEvent ce) {
				super.onComponentEvent(ce);
				TreePanelEvent<FileModel> tpe = (TreePanelEvent) ce;
				// EventType typeEvent = tpe.getType();

				// boolean rightClick = false;
				// if (typeEvent == Events.OnMouseDown) {
				// if (ce.isRightClick()){
				// rightClick = true;
				// }
				// }

				int type = ce.getEventTypeInt();
				switch (type) {
				case Event.ONCLICK:
					onRightClick(tpe);
					break;
				// case Event.ONDBLCLICK:
				// onDoubleClick(tpe);
				// break;
				// case Event.ONSCROLL:
				// onScroll(tpe);
				// break;
				// case Event.ONFOCUS:
				// onFocus(ce);
				// break;
				}
				// view.onEvent(tpe);
			}
		};

		treePanel.setDisplayProperty("name");
		// set icons for elements in tree panel
		treePanel.setIconProvider(new ModelIconProvider<FileModel>() {

			@Override
			public AbstractImagePrototype getIcon(FileModel model) {
				if (model.isDatabase()) { // database
					return Images.iconDatabase();
				}
				if (model.isSchema()) { // schema
					return Images.iconSchema();
				}
				return null;
			}
		});

		// load the root
		loadRootItemTree();

		// set single selection Mode
		treePanel.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		// select root item
		treePanel.getSelectionModel().select(store.getRootItems().get(0), true);

		// set the current selected item
		currentSelectedItem = store.getRootItems().get(0);

		// expand level 1
		treePanel.setExpanded(store.getRootItems().get(0), true);

		add(treePanel, new FlowData(10));
		addListeners();
	}

	private void addListeners() {
		// to manage item click event
		treePanel.addListener(Events.OnClick,
				new Listener<TreePanelEvent<FileModel>>() {
					@Override
					public void handleEvent(TreePanelEvent<FileModel> be) {
						// get the selected item
						FileModel selectedItem = (FileModel) be.getNode()
								.getModel();

						// System.out.println("selected item ID: " +
						// selectedItem.getId());
						// System.out.println("current selected item ID: " +
						// currentSelectedItem.getId());

						// update the current selected item considering the id
						if (selectedItem.getId() != currentSelectedItem.getId()) {
							// System.out.println("selected item changed");
							currentSelectedItem = selectedItem;
							// deselect the previous item
							treePanel.getSelectionModel().deselectAll();
							// select the current item
							treePanel.getSelectionModel().select(
									currentSelectedItem, true);
						}

						rootLogger.log(
								Level.INFO,
								"GxtTreePanel-> item clicked: "
										+ selectedItem.getName());

						// get the depth level
						treeDepthSelectedItem = store.getDepth(selectedItem);
						rootLogger.log(Level.INFO, "selectedItem level depth: "
								+ treeDepthSelectedItem);

						// if the the selected item was collapsed, it should not
						// expanded otherwise it is expanded
						if (!(selectedItem.getIsCollapsed())) {
							if (!(be.getNode().isExpanded())) {
								// expand level of the element selected
								treePanel.setExpanded(selectedItem, true);
							}
						}

						// do not make a rpc call if it has already been called
						if ((selectedItem.isLoaded() == true)) {
							// set appropriate information
							setInfoOnSelectedItem(selectedItem);

							// fire event when an item has been selected in the
							// tree
							eventBus.fireEvent(new SelectedItemEvent());

						} else if (selectedItem.isDatabase()) {
							// set appropriate information
							setInfoOnSelectedItem(selectedItem);

							// fire event when an item has been selected in the
							// tree
							eventBus.fireEvent(new SelectedItemEvent());

						} else if (selectedItem.isSchema()) {
							// set appropriate information
							setInfoOnSelectedItem(selectedItem);

							// fire event when an item has been selected in the
							// tree
							eventBus.fireEvent(new SelectedItemEvent());
						}
					}
				});

		treePanel.addListener(Events.Collapse,
				new Listener<TreePanelEvent<FileModel>>() {
					public void handleEvent(final TreePanelEvent<FileModel> be) {
						// get the item
						FileModel selectedItem = (FileModel) (be.getNode()
								.getModel());
						// set the collapsed state to true value
						selectedItem.setIsCollapsed(true);
						rootLogger.log(Level.INFO, "collapsed item: "
								+ selectedItem.getName());
					}
				});

		treePanel.addListener(Events.Expand,
				new Listener<TreePanelEvent<FileModel>>() {
					public void handleEvent(final TreePanelEvent<FileModel> be) {
						// rootLogger.log(Level.INFO, "Expand event");
						// get the selected item
						FileModel selectedItem = (FileModel) (be.getNode()
								.getModel());
						// set the collapsed state of the item to false value
						selectedItem.setIsCollapsed(false);

						// System.out.println("selected item ID: " +
						// selectedItem.getId());
						// System.out.println("current selected item ID: "
						// + currentSelectedItem.getId());

						// update the current selected item considering the id
						if (selectedItem.getId() != currentSelectedItem.getId()) {
							currentSelectedItem = selectedItem;
							// deselect the previous item
							treePanel.getSelectionModel().deselectAll();
							// select the current item
							treePanel.getSelectionModel().select(
									currentSelectedItem, true);
						}

						rootLogger.log(Level.INFO, "expanded item: "
								+ selectedItem.getName());

						// get children number
						int numChildrenFolder = store
								.getChildCount(selectedItem);

						// get the depth level
						treeDepthSelectedItem = store.getDepth(selectedItem);
						rootLogger.log(Level.INFO, "selectedItem level depth: "
								+ treeDepthSelectedItem);

						// set appropriate information
						setInfoOnSelectedItem(selectedItem);

						// check to make the RPC call only one time. The
						// selected item has not loaded
						if ((numChildrenFolder == 0)
								&& (selectedItem.isLoaded() != true)) {
							// disable events on the tree panel until the call
							// has ended
							if (treeDepthSelectedItem != 1) {
								treePanel.disableEvents(true);
							}

							switch (treeDepthSelectedItem) {

							case 2: // the selected item is the resource
								// to mask the tree panel
								treePanel.mask("Loading", "x-mask-loading");
								// load databases information
								loadDBInfo(selectedItem, treePanel);
								break;

							case 3: // the selected item is a database

								if (selectedItem.getDatabaseType().equals(
										ConstantsPortlet.POSTGRES)) {
									// to mask the tree panel
									treePanel.mask("Loading", "x-mask-loading");
									// load schema
									loadSchema(selectedItem);
								}
								break;
							}
							// fire event when an item has been selected in the
							// tree
							eventBus.fireEvent(new SelectedItemEvent());
						}
					}
				});
	}

	private void addHandler() {
		eventBus.addHandler(RefreshDataEvent.TYPE,
				new RefreshDataEventHandler() {

					@Override
					public void onRefreshData(RefreshDataEvent refreshDataEvent) {

						refreshData();
					}
				});
	}

	// load the root
	private void loadRootItemTree() throws Exception {
		this.mask("Loading", "x-mask-loading");
		final FileModel root = new FileModel("Resources", 0);
		store.insert(root, 0, true);
		// store.add(root, true);
		// load resources
		loadResources(root, this);
	}

	public String getValue() {
		return value;
	}

	// load resources
	private void loadResources(final FileModel element, final GxtTreePanel tree) {
		rootLogger.log(Level.SEVERE, "Start RPC - getResource");

		// call rpc remote service
		RPCservice.getResource(new AsyncCallback<List<FileModel>>() {
			@Override
			public void onFailure(Throwable caught) {
				// Window.alert(caught.getMessage());
				rootLogger.log(Level.SEVERE, "FAILURE RPC getResource");

				if (caught instanceof SessionExpiredException) {
					rootLogger.log(Level.INFO, "Session expired");
					CheckSession.showLogoutDialog();
					return;
				}

				MessageBox.alert("Error ",
						"<br/>Message:" + caught.getMessage(), null);

				if (tree.isMasked()) {
					tree.unmask();
				}
				// in order to enable the refresh cache button
				// eventBus.fireEvent(new LoadingTreeFinishedEvent());
			}

			@Override
			public void onSuccess(List<FileModel> result) {
				rootLogger.log(Level.SEVERE, "SUCCESS RPC getResource");

				addChildren(element, result);
				element.setIsLoaded(true);

				if (result.size() == 0) {
					element.setIsExpanded(false);
					treePanel.setExpanded(element, false);
				}

				if (tree.isMasked())
					tree.unmask();
				// in order to enable the refresh cache button
				// eventBus.fireEvent(new LoadingTreeFinishedEvent());
			}
		});
		// rootLogger.log(Level.SEVERE, "End RPC - getResource");
	}

	// load information for a database
	private void loadDBInfo(final FileModel element,
			final TreePanel<FileModel> tree) {
		rootLogger.log(Level.SEVERE, "Start RPC - getDBInfo");

		// call remote service
		RPCservice.getDBInfo(element.getName(),
				new AsyncCallback<LinkedHashMap<String, FileModel>>() {
					@Override
					public void onFailure(Throwable caught) {
						// Window.alert(caught.getMessage());
						rootLogger.log(Level.SEVERE, "FAILURE RPC getDBInfo");

						if (caught instanceof SessionExpiredException) {
							rootLogger.log(Level.INFO, "Session expired");
							CheckSession.showLogoutDialog();
							return;
						}

						MessageBox.alert("Error ",
								"<br/>Message:" + caught.getMessage(), null);

						if (treePanel.isMasked())
							treePanel.unmask();

						tree.enableEvents(true); // enable events on the tree
					}

					@Override
					public void onSuccess(
							LinkedHashMap<String, FileModel> result) {
						rootLogger.log(Level.SEVERE, "SUCCESS RPC getDBInfo");

						if (result.size() != 0) {
							// recover keys from the result
							Set<String> keys = result.keySet();
							Object[] array = keys.toArray();

							// recover information for each database
							List<FileModel> children = new ArrayList<FileModel>();
							int numIterations = (result.size()) / 5;

							int i = 0;
							int j = 0;
							for (i = 0; i < numIterations; i++) {

								String DatabaseType = null;
								// System.out.println("index i: " + i);
								String DBName = null;
								List<Result> DBInfo = new ArrayList<Result>();
								FileModel child = null;

								for (j = (i * 5); j < (i + 1) * 5; j++) {
									// System.out.println("index j: " + j);
									if (array[j].toString().contains(
											"Database Name")) {
										// recover the database name
										DBName = result
												.get(array[j].toString())
												.getName();

										Result row = new Result(
												"Database Name", result.get(
														array[j].toString())
														.getName());
										child = result.get(array[j].toString());
										DBInfo.add(row);
									}

									if (array[j].toString().contains("URL")) {
										Result row = new Result("URL", result
												.get(array[j].toString())
												.getName());
										DBInfo.add(row);
									}

									if (array[j].toString().contains(
											"Driver Name")) {
										Result row = new Result("Driver Name",
												result.get(array[j].toString())
														.getName());

										String driver = result.get(
												array[j].toString()).getName();
										DBInfo.add(row);

										if (driver.toUpperCase().contains(
												ConstantsPortlet.POSTGRES)) {
											DatabaseType = ConstantsPortlet.POSTGRES;
										}

										if (driver.toUpperCase().contains(
												ConstantsPortlet.MYSQL)) {
											DatabaseType = ConstantsPortlet.MYSQL;
										}
									}

									if (array[j].toString().contains(
											"Dialect Name")) {
										Result row = new Result("Dialect Name",
												result.get(array[j].toString())
														.getName());
										DBInfo.add(row);
									}

									if (array[j].toString().contains(
											"Platform Name")) {
										Result row = new Result(
												"Platform Name", result.get(
														array[j].toString())
														.getName());
										DBInfo.add(row);
									}
								}

								// FileModel child = new FileModel(DBName);

								if (child != null) {
									// set that the item is a database
									child.setIsDatabase(true);
									child.setDBInfo(DBInfo);

									// check print
									// rootLogger.log(Level.INFO,
									// "DatabaseType: " + DatabaseType);

									// set the database type considering the
									// driver information
									child.setDatabaseType(DatabaseType);

									if (DatabaseType
											.equals(ConstantsPortlet.MYSQL)) {
										child.setDatabaseType(ConstantsPortlet.MYSQL);
										child.setIsExpanded(false);
										treePanel.setExpanded(child, false);
									}
									if (DatabaseType
											.equals(ConstantsPortlet.POSTGRES)) {
										child.setDatabaseType(ConstantsPortlet.POSTGRES);
									}
									children.add(child);
								}
							}
							addChildren(element, children);
						}

						rootLogger.log(
								Level.INFO,
								"children number: "
										+ store.getChildCount(element));

						element.setIsLoaded(true);

						if (result.size() == 0) {
							element.setIsExpanded(false);
							treePanel.setExpanded(element, false);
						}

						if (treePanel.isMasked())
							treePanel.unmask();

						tree.enableEvents(true); // enable events on the tree
					}
				});
		// rootLogger.log(Level.SEVERE, "End RPC - getDBInfo");
	}

	// load schema
	private void loadSchema(final FileModel element) {
		rootLogger.log(Level.SEVERE, "Start RPC - getDBSchema");
		// recover data inputs for algorithm
		LinkedHashMap<String, String> dataInput = new LinkedHashMap<String, String>();
		// get information
		FileModel parent = store.getParent(element);
		dataInput.put("ResourceName", parent.getName());
		dataInput.put("DatabaseName", element.getName());

		// call remote service
		RPCservice.getDBSchema(dataInput, new AsyncCallback<List<FileModel>>() {
			@Override
			public void onFailure(Throwable caught) {
				// Window.alert(caught.getMessage());
				rootLogger.log(Level.SEVERE, "FAILURE RPC getDBSchema");

				if (caught instanceof SessionExpiredException) {
					rootLogger.log(Level.INFO, "Session expired");
					CheckSession.showLogoutDialog();
					return;
				}

				MessageBox.alert("Error ",
						"<br/>Message:" + caught.getMessage(), null);

				if (treePanel.isMasked())
					treePanel.unmask();

				treePanel.enableEvents(true);
			}

			@Override
			public void onSuccess(List<FileModel> result) {
				rootLogger.log(Level.SEVERE, "SUCCESS RPC getDBSchema");

				if (result.size() == 0) { // the database has not schema
					// element.setIsSchema(false);
					// element.setIsLoaded(false);
					// loadTables(element);
					rootLogger.log(Level.INFO, "the database has not schema");
					element.setIsExpanded(false);
					treePanel.setExpanded(element, false);
				} else {
					for (int i = 0; i < result.size(); i++) {
						// element.setIsSchema(true);
						result.get(i).setIsSchema(true);
						result.get(i).setIsExpanded(false);
						treePanel.setExpanded(result.get(i), false);
					}
					addChildren(element, result);
					// element.setIsLoaded(true);
				}
				element.setIsLoaded(true);

				if (treePanel.isMasked())
					treePanel.unmask();

				treePanel.enableEvents(true);
			}
		});
		// rootLogger.log(Level.SEVERE, "End RPC - getDBSchema");
	}

	// add children to a File Model element
	private void addChildren(FileModel parent, List<FileModel> children) {
		if (parent != null) {
			store.add(parent, children, true);
			rootLogger.log(Level.INFO, "Added children in store");
		}
	}

	private void removeChildren(FileModel parent) {

		if (parent != null) {
			store.removeAll(parent);
			rootLogger.log(Level.INFO, "children removed from the store");
		}
	}

	// set information (useful for the submitquery operation) for the selected
	// item. The selected item can be a schema, a table and a database.
	private void setInfoOnSelectedItem(FileModel element) {
		// the selected element is a schema
		if ((treeDepthSelectedItem == 4) && (element.isSchema() == true)) {
			rootLogger.log(Level.SEVERE, "setInfo for selected item schema");
			// recover database name
			FileModel database = store.getParent(element);
			String DatabaseName = database.getName();

			// recover resource name
			FileModel resource = store.getParent(database);
			String ResourceName = resource.getName();

			element.setResourceName(ResourceName);
			element.setDatabaseName(DatabaseName);
		}

		// the selected element is a table because the database is mysql type
		else if ((treeDepthSelectedItem == 4) && (element.isSchema() == false)) {
			rootLogger.log(Level.SEVERE, "setInfo for selected item table");

			// recover database name
			FileModel database = store.getParent(element);
			String DatabaseName = database.getName();

			// recover resource name
			FileModel resource = store.getParent(database);
			String ResourceName = resource.getName();

			element.setResourceName(ResourceName);
			element.setDatabaseName(DatabaseName);
		} else if (treeDepthSelectedItem == 5) { // the selected item is a table
													// so the database is
													// postgres type
			rootLogger.log(Level.SEVERE, "setInfo for selected item table");

			// recover schema name
			FileModel schema = store.getParent(element);

			// recover database name
			FileModel database = store.getParent(schema);
			String DatabaseName = database.getName();

			// recover resource name
			FileModel resource = store.getParent(database);
			String ResourceName = resource.getName();

			element.setResourceName(ResourceName);
			element.setDatabaseName(DatabaseName);
		} else if (treeDepthSelectedItem == 3) { // the selected item is a
													// database
			rootLogger.log(Level.SEVERE, "setInfo for selected item database");

			// recover database name
			String DatabaseName = element.getName();

			// recover resource name
			FileModel resource = store.getParent(element);
			String ResourceName = resource.getName();

			element.setResourceName(ResourceName);
			element.setDatabaseName(DatabaseName);
		}

	}

	// get the tree panel
	public TreePanel<FileModel> getTreePanel() {
		return this.treePanel;
	}

	// get the store
	public TreeStore<FileModel> getTreeStore() {
		return this.store;
	}

	// refresh data
	private void refreshData() {

		final GxtTreePanel tree = this;
		this.mask("Loading", "x-mask-loading");

		List<FileModel> items = treePanel.getSelectionModel()
				.getSelectedItems();

		final FileModel selectedItem = items.get(0);
		int Depth = store.getDepth(selectedItem);
		LinkedHashMap<String, String> inputData = new LinkedHashMap<String, String>();

		String elementType = "";
		String value;

		if (Depth == 1) { // root tree
			elementType = ConstantsPortlet.RESOURCESLIST;
			value = ConstantsPortlet.RESOURCESLIST;
			inputData.put(value, value);
		} else if (Depth == 2) { // resource
			elementType = ConstantsPortlet.RESOURCE;
			value = selectedItem.getName();
			inputData.put("ResourceName", value);

		} else if (Depth == 3) { // database
			elementType = ConstantsPortlet.DATABASE;
			String database = selectedItem.getName();
			// get the database type
			// String dbType = selectedItem.getDatabaseType();
			FileModel parent = store.getParent(selectedItem);
			String resource = parent.getName();

			if (selectedItem.getDatabaseType() != null
					&& (selectedItem.getDatabaseType()
							.equals(ConstantsPortlet.POSTGRES))) { // refresh
																	// schema
																	// list and
																	// query
																	// executed
				inputData.put("ResourceName", resource);
				inputData.put("DatabaseName", database);
			}
			if (selectedItem.getDatabaseType() != null
					&& (selectedItem.getDatabaseType()
							.equals(ConstantsPortlet.MYSQL))) { // refresh
																// tables list,
																// query
																// executed and
																// samplings
				inputData.put("ResourceName", resource);
				inputData.put("DatabaseName", database);
				inputData.put("SchemaName", "");
			}

		} else if (Depth == 4) { // Schema. Refresh tables list, query executed
									// and samplings
			elementType = ConstantsPortlet.SCHEMA;
			String schema = selectedItem.getName();
			FileModel db = store.getParent(selectedItem);
			String database = db.getName();
			FileModel rs = store.getParent(db);
			String resource = rs.getName();

			inputData.put("ResourceName", resource);
			inputData.put("DatabaseName", database);
			inputData.put("SchemaName", schema);

		}

		final String elemType = elementType;
		RPCservice.refreshDataTree(elemType, inputData, selectedItem,
				new AsyncCallback<GeneralOutputFromServlet>() {

					@Override
					public void onFailure(Throwable caught) {
						rootLogger.log(Level.SEVERE, "FAILURE refreshDataTree");

						if (caught instanceof SessionExpiredException) {
							rootLogger.log(Level.INFO, "Session expired");
							CheckSession.showLogoutDialog();
							return;
						}

						MessageBox.alert("Error ",
								"<br/>Message:" + caught.getMessage(), null);

						if (tree.isMasked()) {
							tree.unmask();
						}

						// fire an event in order to activate the refresh cache
						// button
						// eventBus.fireEvent(new RefreshDataFinishedEvent());
					}

					@Override
					public void onSuccess(GeneralOutputFromServlet result) {

						rootLogger.log(Level.SEVERE, "SUCCESS refreshDataTree");

						if (result != null) {
							// remove children

							removeChildren(selectedItem);
							tree.layout(true);

							if (elemType.equals(ConstantsPortlet.RESOURCESLIST)) {
								List<FileModel> output = new ArrayList<FileModel>();
								output = result.getListOutput();

								if (output.size() == 0) {
									selectedItem.setIsExpanded(false);
									treePanel.setExpanded(selectedItem, false);
								} else {
									// add the children
									addChildren(selectedItem, output);
									// System.out.println("added new data");
									treePanel.setExpanded(selectedItem, true);
								}

								selectedItem.setIsLoaded(true);

							} else if (elemType.equals(ConstantsPortlet.RESOURCE)) {
								LinkedHashMap<String, FileModel> output = new LinkedHashMap<String, FileModel>();
								output = result.getMapOutput();

								if (output.size() != 0) {
									// recover keys from the result
									Set<String> keys = output.keySet();
									Object[] array = keys.toArray();

									// recover information for each database
									List<FileModel> children = new ArrayList<FileModel>();
									int numIterations = (output.size()) / 5;

									int i = 0;
									int j = 0;
									for (i = 0; i < numIterations; i++) {

										String DatabaseType = null;
										// System.out.println("index i: " + i);
										String DBName = null;
										List<Result> DBInfo = new ArrayList<Result>();
										FileModel child = null;

										for (j = (i * 5); j < (i + 1) * 5; j++) {
											// System.out.println("index j: " +
											// j);
											if (array[j].toString().contains(
													"Database Name")) {
												// recover the database name
												DBName = output.get(
														array[j].toString())
														.getName();

												Result row = new Result(
														"Database Name",
														output.get(
																array[j].toString())
																.getName());

												child = output.get(array[j]
														.toString());
												DBInfo.add(row);
											}

											if (array[j].toString().contains(
													"URL")) {
												Result row = new Result(
														"URL",
														output.get(
																array[j].toString())
																.getName());
												DBInfo.add(row);
											}

											if (array[j].toString().contains(
													"Driver Name")) {
												Result row = new Result(
														"Driver Name",
														output.get(
																array[j].toString())
																.getName());

												String driver = output.get(
														array[j].toString())
														.getName();
												DBInfo.add(row);

												if (driver
														.toUpperCase()
														.contains(
																ConstantsPortlet.POSTGRES)) {
													DatabaseType = ConstantsPortlet.POSTGRES;
												}

												if (driver
														.toUpperCase()
														.contains(
																ConstantsPortlet.MYSQL)) {
													DatabaseType = ConstantsPortlet.MYSQL;
												}
											}

											if (array[j].toString().contains(
													"Dialect Name")) {
												Result row = new Result(
														"Dialect Name",
														output.get(
																array[j].toString())
																.getName());
												DBInfo.add(row);
											}

											if (array[j].toString().contains(
													"Platform Name")) {
												Result row = new Result(
														"Platform Name",
														output.get(
																array[j].toString())
																.getName());
												DBInfo.add(row);
											}
										}

										// FileModel child = new
										// FileModel(DBName);

										if (child != null) {
											// set that the item is a database
											child.setIsDatabase(true);
											child.setDBInfo(DBInfo);

											// check print
											// rootLogger.log(Level.INFO,
											// "DatabaseType: " + DatabaseType);

											// set the database type considering
											// the
											// driver information
											child.setDatabaseType(DatabaseType);

											if (DatabaseType
													.equals(ConstantsPortlet.MYSQL)) {
												child.setIsExpanded(false);
												treePanel.setExpanded(child,
														false);
											}
											children.add(child);
										}
									}
									addChildren(selectedItem, children);
									rootLogger
											.log(Level.INFO,
													"children number: "
															+ store.getChildCount(selectedItem));

									treePanel.setExpanded(selectedItem, true);
								} else if (output.size() == 0) {
									selectedItem.setIsExpanded(false);
									treePanel.setExpanded(selectedItem, false);
								}

								selectedItem.setIsLoaded(true);

							} else if (elemType.equals(ConstantsPortlet.DATABASE)) {
								List<FileModel> output = new ArrayList<FileModel>();
								output = result.getListOutput();

								if (output == null) {
									rootLogger.log(Level.INFO,
											"the database has not schema");
									selectedItem.setIsExpanded(false);
									treePanel.setExpanded(selectedItem, false);
								} else {
									for (int i = 0; i < output.size(); i++) {
										// element.setIsSchema(true);
										output.get(i).setIsSchema(true);
										output.get(i).setIsExpanded(false);
										treePanel.setExpanded(output.get(i),
												false);
									}
									addChildren(selectedItem, output);
									// element.setIsLoaded(true);
									treePanel.setExpanded(selectedItem, true);
								}
								selectedItem.setIsLoaded(true);
							}
						}
						if (tree.isMasked()) {
							tree.unmask();
						}
						// fire an event in order to activate the refresh cache
						// button
						// eventBus.fireEvent(new RefreshDataFinishedEvent());
					}
				});
	}
}
