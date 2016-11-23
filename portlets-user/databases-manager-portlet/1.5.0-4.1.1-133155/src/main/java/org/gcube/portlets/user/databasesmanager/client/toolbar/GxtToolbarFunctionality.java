package org.gcube.portlets.user.databasesmanager.client.toolbar;

import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.user.databasesmanager.client.GWTdbManagerServiceAsync;
import org.gcube.portlets.user.databasesmanager.client.datamodel.FileModel;
import org.gcube.portlets.user.databasesmanager.client.datamodel.SubmitQueryData;
import org.gcube.portlets.user.databasesmanager.client.events.LoadTablesEvent;
import org.gcube.portlets.user.databasesmanager.client.events.RandomSamplingEvent;
import org.gcube.portlets.user.databasesmanager.client.events.RefreshDataEvent;
import org.gcube.portlets.user.databasesmanager.client.events.SamplingEvent;
import org.gcube.portlets.user.databasesmanager.client.events.SelectedItemEvent;
import org.gcube.portlets.user.databasesmanager.client.events.SelectedTableEvent;
import org.gcube.portlets.user.databasesmanager.client.events.ShowCreateTableEvent;
import org.gcube.portlets.user.databasesmanager.client.events.SmartSamplingEvent;
import org.gcube.portlets.user.databasesmanager.client.events.SubmitQueryEvent;
import org.gcube.portlets.user.databasesmanager.client.events.interfaces.SelectedItemEventHandler;
import org.gcube.portlets.user.databasesmanager.client.events.interfaces.SelectedTableEventHandler;
import org.gcube.portlets.user.databasesmanager.client.form.GxtFormSubmitQuery;
import org.gcube.portlets.user.databasesmanager.client.utils.UIDGenerator;
import org.gcube.portlets.user.databasesmanager.shared.ConstantsPortlet;
import org.gcube.portlets.user.databasesmanager.shared.SessionExpiredException;
import org.gcube.portlets.widgets.sessionchecker.client.CheckSession;

import com.extjs.gxt.ui.client.Style.ButtonArrowAlign;
import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;

//toolbar to contain buttons
public class GxtToolbarFunctionality {
	// event bus
	private HandlerManager eventBus = null;
	// buttons
	private Button btnTablesList;
	private Button btnSubmitQuery;
	// private Button btnGetInfo;
	private Button btnShowCreateTable;
	private Button btnSimpleSample;
	private Button btnSmartSample;
	private Button btnRandomSample;
	private Button btnRefresCachedData;
	// toolbar
	private ToolBar toolBar;
	// dialog list. Each dialog contains a form
	private LinkedHashMap<Integer, Dialog> dialogList = new LinkedHashMap<Integer, Dialog>();
	private LinkedHashMap<Integer, SubmitQueryData> submitQueryDataList = new LinkedHashMap<Integer, SubmitQueryData>();
	private static int ID = 0; // ID associated to a dialog form
	// uid list related to submit query operations
	private LinkedHashMap<Integer, String> uidSubmitQueryList = new LinkedHashMap<Integer, String>();
	// GWT logger
	private static Logger rootLogger = Logger
			.getLogger("GxtToolbarFunctionality");
	// RPC service
	private GWTdbManagerServiceAsync RPCservice = null;
	// selected table
	private String selectedTable;
	// databse information related to the selected table
	private FileModel tableInfo;
	// proposed query displayed in the submit query form
	private String queryForSubmitOperation = "select * from  %1$s limit 10";
	private boolean isTableSelected = false;
	// variable to control the deactivate/activate of the refresh data button
	private boolean treeLoaded = false;

	// Constructor
	// public GxtToolbarFunctionality(HandlerManager eBus) {
	// eventBus = eBus;
	// toolBar = new ToolBar();
	// initToolBar();
	// addHandler();
	// addSelectionListenersOnToolBar();
	// }

	// Constructor with GWTdbManagerServiceAsync service parameter
	public GxtToolbarFunctionality(HandlerManager eBus,
			GWTdbManagerServiceAsync service) {
		eventBus = eBus;
		RPCservice = service;
		toolBar = new ToolBar();
		initToolBar();
		addHandler();
		addSelectionListenersOnToolBar();
	}

	private void initToolBar() {
		// setLayout(new FlowLayout(10));

		// Button for tables list
		btnTablesList = new Button(ConstantsPortlet.TABLESLIST);
		// btnSubmitQuery.setIcon(Resources.ICONS.text());
		btnTablesList.setScale(ButtonScale.SMALL);
		btnTablesList.setArrowAlign(ButtonArrowAlign.BOTTOM);
		btnTablesList
				.setToolTip("returns the list of tables contained in the database schema");
		toolBar.add(btnTablesList);

		// Button for Submit Query
		btnSubmitQuery = new Button(ConstantsPortlet.SUBMITQUERY);
		// btnSubmitQuery.setIcon(Resources.ICONS.text());
		btnSubmitQuery.setScale(ButtonScale.SMALL);
		btnSubmitQuery.setArrowAlign(ButtonArrowAlign.BOTTOM);
		btnSubmitQuery
				.setToolTip("allows to submit a query to the selected database");
		toolBar.add(btnSubmitQuery);

		// Button to get the "show create table"
		btnShowCreateTable = new Button(ConstantsPortlet.TABLEDETAILS);
		btnShowCreateTable.setScale(ButtonScale.SMALL);
		btnShowCreateTable.setArrowAlign(ButtonArrowAlign.BOTTOM);
		btnShowCreateTable
				.setToolTip("gets information about the selected table, e.g. create statement, number of rows, columns names");
		toolBar.add(btnShowCreateTable);
		toolBar.add(new SeparatorToolItem());

		// Button for Simple Sample
		btnSimpleSample = new Button(ConstantsPortlet.SAMPLING);
		btnSimpleSample.setScale(ButtonScale.SMALL);
		btnSimpleSample.setArrowAlign(ButtonArrowAlign.BOTTOM);
		btnSimpleSample.setToolTip("retrieves the first 100 rows of the table");
		toolBar.add(btnSimpleSample);

		// Button for Smart Sample
		btnSmartSample = new Button(ConstantsPortlet.SMARTSAMPLING);
		btnSmartSample.setScale(ButtonScale.SMALL);
		btnSmartSample.setArrowAlign(ButtonArrowAlign.BOTTOM);
		btnSmartSample
				.setToolTip("retrieves the first 100 rows of the table, maximising the number of non empty columns");
		toolBar.add(btnSmartSample);

		// button for Random Sample
		btnRandomSample = new Button(ConstantsPortlet.RANDOMSAMPLING);
		btnRandomSample.setScale(ButtonScale.SMALL);
		btnRandomSample.setArrowAlign(ButtonArrowAlign.BOTTOM);
		btnRandomSample
				.setToolTip("retrieves 100 randomly picked rows from the table");
		toolBar.add(btnRandomSample);
		toolBar.add(new SeparatorToolItem());

		// button for Random Sample
		btnRefresCachedData = new Button(ConstantsPortlet.REFRESHCACHEDDATA);
		btnRefresCachedData.setScale(ButtonScale.SMALL);
		btnRefresCachedData.setArrowAlign(ButtonArrowAlign.BOTTOM);
		btnRefresCachedData.setToolTip("refreshes data");
		toolBar.add(btnRefresCachedData);

		// add(toolBar, new FlowData(10));

		// disable buttons
		btnTablesList.disable();
		btnSubmitQuery.disable();
		// btnGetInfo.disable();
		btnShowCreateTable.disable();
		btnSimpleSample.disable();
		btnSmartSample.disable();
		btnRandomSample.disable();
		// btnRefresCachedData.disable();
		btnRefresCachedData.enable();
	}

	private void addHandler() {

		eventBus.addHandler(SelectedTableEvent.TYPE,
				new SelectedTableEventHandler() {
					@Override
					public void onSelectedTable(
							SelectedTableEvent selectedTableEvent) {
						// enable button for table details and sampling
						// operations
						btnTablesList.enable();
						btnSubmitQuery.enable();
						btnShowCreateTable.enable();
						btnSimpleSample.enable();
						btnSmartSample.enable();
						btnRandomSample.enable();

						// get selected table name and related database
						// information
						String tableName = selectedTableEvent
								.getSelectedTable();
						FileModel tableInfo = selectedTableEvent.getTableInfo();
						setInfoOnSelectedTable(tableName, tableInfo);
						isTableSelected = true;
						// rootLogger.info("table clicked: " + isTableSelected);
					}
				});

		eventBus.addHandler(SelectedItemEvent.TYPE,
				new SelectedItemEventHandler() {

					@Override
					public void onSelectedItem(
							SelectedItemEvent selectedItemEvent) {

						isTableSelected = false;
						// rootLogger.info("item clicked: " + isTableSelected);

					}

				});

		// eventBus.addHandler(RefreshDataFinishedEvent.TYPE,
		// new RefreshDataFinishedEventHandler() {
		//
		// @Override
		// public void onRefreshDataFinished(
		// RefreshDataFinishedEvent refreshDataFinishedEvent) {
		// // in order to activate the button
		// if (!btnRefresCachedData.isEnabled()) {
		// btnRefresCachedData.enable();
		// }
		// }
		// });

		// eventBus.addHandler(LoadingTreeFinishedEvent.TYPE,
		// new LoadingTreeFinishedEventHandler() {
		// @Override
		// public void onLoadingTreeFinished(
		// LoadingTreeFinishedEvent loadingTreeEvent) {
		// treeLoaded = true;
		// if (!btnRefresCachedData.isEnabled()) {
		// btnRefresCachedData.enable();
		// }
		// }
		// });
	}

	private void addSelectionListenersOnToolBar() {

		btnSubmitQuery
				.addSelectionListener(new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						// dialog to insert inputs in order to submit a query
						final Dialog dialog = new Dialog() {
							// override the maximize event modifying it with a
							// different behavior if the mimimize event occurs
							public void maximize() {
								if (isCollapsed()) {
									expand();
								} else {
									super.maximize();
								}
							}
						};

						ID++;

						final int dialogID = ID;
						dialog.setLayout(new FitLayout());
						// dialog.setModal(true);
						// dialog.setBlinkModal(true);
						dialog.setButtons(Dialog.OKCANCEL);
						// dialog.setPlain(true);
						// dialog.setCollapsible(false);
						dialog.setResizable(false);
						dialog.setMaximizable(true);
						dialog.setMinimizable(true);
						dialog.setHeading("Submit Query " + dialogID);
						dialog.setSize(600, 500);
						// dialog.setConstrain(false);
						// dialog.setTitleCollapse(true);
						// dialog.setWidth(290);
						// dialog.setHeight(250);

						// create form to submit a query
						final GxtFormSubmitQuery form;
						// get query for submit operation
						String query = null;

						if (isTableSelected == true) {
							query = getQueryForSubmitOperation();
						}
						if ((query != null) && (!query.equals(""))) {
							form = new GxtFormSubmitQuery(query);
						} else {
							form = new GxtFormSubmitQuery();
						}
						dialog.add(form);
						dialog.show();

						// minimize event handled
						dialog.addListener(Events.Minimize,
								new Listener<WindowEvent>() {
									@Override
									public void handleEvent(WindowEvent be) {
										// collapse the dialog
										be.getWindow().collapse();
									}
								});

						// override maximize event
						dialog.addListener(Events.Maximize,
								new Listener<WindowEvent>() {
									@Override
									public void handleEvent(WindowEvent be) {
										// expand the dialog
										if (be.getWindow().isCollapsed()) {
											be.getWindow().expand();
										}
									}
								});

						// listener on the dialog "ok" button
						dialog.getButtonById(Dialog.OK).addSelectionListener(
								new SelectionListener<ButtonEvent>() {
									@Override
									public void componentSelected(ButtonEvent ce) {
										// deactivate the button
										dialog.getButtonById(Dialog.OK)
												.disable();
										// recover info from dialog
										setInfoOnSubmitQuery(form, dialog,
												dialogID);
										rootLogger.log(Level.SEVERE,
												"query submitted");
									}
								});

						// listener on the dialog "cancel" button
						dialog.getButtonById(Dialog.CANCEL)
								.addSelectionListener(
										new SelectionListener<ButtonEvent>() {
											@Override
											public void componentSelected(
													ButtonEvent ce) {
												rootLogger.log(Level.INFO,
														"button Cancel event");

												if (uidSubmitQueryList
														.get(dialogID) != null) {

													// remove computation
													RPCservice.removeComputation(
															uidSubmitQueryList
																	.get(dialogID),
															new AsyncCallback<Boolean>() {
																@Override
																public void onSuccess(
																		Boolean result) {
																	rootLogger
																			.log(Level.SEVERE,
																					"SUCCESS RPC removeComputation");
																	if (result
																			.booleanValue() == true) {
																		rootLogger
																				.log(Level.INFO,
																						"computation removed with uid: "
																								+ uidSubmitQueryList
																										.get(dialogID));
																	} else {
																		rootLogger
																				.log(Level.INFO,
																						"computation can not be removed");
																	}
																}

																@Override
																public void onFailure(
																		Throwable caught) {
																	rootLogger
																			.log(Level.SEVERE,
																					"FAILURE RPC removeComputation");

																	if (caught instanceof SessionExpiredException) {
																		rootLogger
																				.log(Level.INFO,
																						"Session expired");
																		CheckSession
																				.showLogoutDialog();
																		return;
																	}

																}
															});
												}
												dialog.hide();
											}
										});
					}
				});

		btnSimpleSample
				.addSelectionListener(new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						// fire event
						eventBus.fireEvent(new SamplingEvent());
					}
				});

		btnSmartSample
				.addSelectionListener(new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						// fire event
						eventBus.fireEvent(new SmartSamplingEvent());
					}
				});

		btnRandomSample
				.addSelectionListener(new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						// fire event
						eventBus.fireEvent(new RandomSamplingEvent());
					}
				});

		btnShowCreateTable
				.addSelectionListener(new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						// fire event
						eventBus.fireEvent(new ShowCreateTableEvent());
					}
				});

		btnTablesList
				.addSelectionListener(new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						// fire event
						eventBus.fireEvent(new LoadTablesEvent());
					}
				});

		btnRefresCachedData
				.addSelectionListener(new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {

						rootLogger.log(Level.INFO, "REFRESH BUTTON");
						// deactivate the button
						// btnRefresCachedData.disable();
						// fire event
						eventBus.fireEvent(new RefreshDataEvent());
					}
				});
	}

	// buttons enable/disable operation depending from the item selected in the
	// tree
	public void enableButtonOnToolbar(int infoTreeDepthSelectedItem,
			boolean infoSelectedItemIsSchema, String databaseType) {

		rootLogger.log(Level.INFO, "selectedItem depth: "
				+ infoTreeDepthSelectedItem);

		switch (infoTreeDepthSelectedItem) {

		case 1:
			// if (treeLoaded == true) {
			// btnRefresCachedData.enable();
			// } else {
			// btnRefresCachedData.disable();
			// }
			btnTablesList.disable();
			btnSubmitQuery.disable();
			// btnGetInfo.disable();
			btnShowCreateTable.disable();
			btnSimpleSample.disable();
			btnSmartSample.disable();
			btnRandomSample.disable();

			break;

		case 2:
			btnRefresCachedData.enable();
			btnTablesList.disable();
			btnSubmitQuery.disable();
			// btnGetInfo.enable();
			btnShowCreateTable.disable();
			btnSimpleSample.disable();
			btnSmartSample.disable();
			btnRandomSample.disable();

			break;

		case 3:
			if ((databaseType != null)
					&& (databaseType.equals(ConstantsPortlet.MYSQL))) {
				btnTablesList.enable();
				btnSubmitQuery.enable();
				btnRefresCachedData.enable();
				// btnGetInfo.disable();
				btnShowCreateTable.disable();
				btnSimpleSample.disable();
				btnSmartSample.disable();
				btnRandomSample.disable();

			}
			if ((databaseType != null)
					&& (databaseType.equals(ConstantsPortlet.POSTGRES))) {
				btnTablesList.disable();
				btnSubmitQuery.enable();
				btnRefresCachedData.enable();
				// btnGetInfo.disable();
				btnShowCreateTable.disable();
				btnSimpleSample.disable();
				btnSmartSample.disable();
				btnRandomSample.disable();

			}
			break;

		case 4: // check to verify that this level refers to schema or table
				// and manage it differently
			if (infoSelectedItemIsSchema == true) {
				// this tree level is a schema
				btnTablesList.enable();
				btnSubmitQuery.enable();
				btnRefresCachedData.enable();
				// btnShowCreateTable.enable();
				btnShowCreateTable.disable();
				btnSimpleSample.disable();
				btnSmartSample.disable();
				btnRandomSample.disable();

			} else {
				// this tree level is a table
				btnTablesList.enable();
				btnSubmitQuery.enable();
				btnShowCreateTable.enable();
				btnSimpleSample.enable();
				btnSmartSample.enable();
				btnRandomSample.enable();
				btnRefresCachedData.disable();

			}
			break;

		case 5: // if there is the schema this level refers to table
			btnTablesList.enable();
			btnSubmitQuery.enable();
			btnShowCreateTable.enable();
			btnSimpleSample.enable();
			btnSmartSample.enable();
			btnRandomSample.enable();
			btnRefresCachedData.disable();
			break;
		}
	}

	public void disableButtonsOperationsOnTable() {
		btnShowCreateTable.disable();
		btnSimpleSample.disable();
		btnSmartSample.disable();
		btnRandomSample.disable();
	}

	public ToolBar getToolBar() {
		return this.toolBar;
	}

	private void setInfoOnSubmitQuery(GxtFormSubmitQuery form,
			final Dialog SubmtQueryDialog, int dialogID) {

		SubmitQueryData data = form.getSubmitQueryData();
		// data = form.getSubmitQueryData();

		String query = data.getQuery();

		Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {
			public void handleEvent(MessageBoxEvent ce) {
				SubmtQueryDialog.getButtonById(Dialog.OK).enable();
			}
		};

		if ((query == null) || (query.equals(""))) {
			MessageBox.alert("Warning", "Query field null", l);
		} else {
			dialogList.put(new Integer(dialogID), SubmtQueryDialog);
			submitQueryDataList.put(new Integer(dialogID), data);

			// generate a UID for this request
			UIDGenerator generator = new UIDGenerator();
			String uidSubmitQuery = generator.get();
			// add uid for the submit query operation
			uidSubmitQueryList.put(new Integer(dialogID), uidSubmitQuery);
			// System.out.println("UID: " + uidSubmitQuery);
			SubmtQueryDialog.getBody().mask("Loading", "x-mask-loading");

			// fire event
			eventBus.fireEvent(new SubmitQueryEvent(dialogID));
		}
	}

	// public SubmitQueryData getSubmitQueryData() {
	// return data;
	// }

	// public LinkedHashMap<Dialog, ArrayList<String>> getDialogForm() {
	// // return dialog;
	// return dialogList;
	// }

	public LinkedHashMap<Integer, Dialog> getDialogFormList() {
		return dialogList;
	}

	public LinkedHashMap<Integer, SubmitQueryData> getSubmitQueryDataList() {
		return submitQueryDataList;
	}

	public LinkedHashMap<Integer, String> getUIDSubmitQueryList() {
		return uidSubmitQueryList;
	}

	private void setInfoOnSelectedTable(String table, FileModel info) {
		selectedTable = table;
		tableInfo = info;

	}

	private String getQueryForSubmitOperation() {
		String query = "";

		if (tableInfo.isDatabase()) {
			if (tableInfo.getDatabaseType().equals(ConstantsPortlet.MYSQL)) {

				String dbName = tableInfo.getDatabaseName();
				// the full name equal to "dbname.tablename"
				String tableName = dbName + "." + selectedTable;
				// query = String.format(queryForSubmitOperation, tableName);
				query = "select * from " + tableName + " limit 10";

				// System.out.println("query mysql: " + query);
			}
		}
		if (tableInfo.isSchema()) { // database postgres
			String schemaName = tableInfo.getName();
			// the full name equal to "schemaname.tablename"
			String tableName = schemaName + "." + "\"" + selectedTable + "\"";
			// query = String.format(queryForSubmitOperation, tableName);
			query = "select * from " + tableName + " limit 10";
			// System.out.println("query postgres: " + query);
		}

		return query;

	}

	// public Integer getDialogID() {
	// return new Integer(ID);
	// }
}
