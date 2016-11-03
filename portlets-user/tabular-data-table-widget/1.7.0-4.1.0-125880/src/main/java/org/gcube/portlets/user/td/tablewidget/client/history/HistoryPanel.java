package org.gcube.portlets.user.td.tablewidget.client.history;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsFinalException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.history.OpHistory;
import org.gcube.portlets.user.td.gwtservice.shared.history.RollBackSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TableData;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialog;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialogListener;
import org.gcube.portlets.user.td.tablewidget.client.custom.ActionButtonCellNoFirst;
import org.gcube.portlets.user.td.tablewidget.client.properties.OpHistoryProperties;
import org.gcube.portlets.user.td.tablewidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.tablewidget.client.util.UtilsGXT3;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.ChangeTableRequestEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableRequestType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableWhy;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.OperationResult;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.ListLoadConfig;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.data.shared.loader.ListLoadResultBean;
import com.sencha.gxt.data.shared.loader.ListLoader;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.RowExpander;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class HistoryPanel extends FramedPanel implements MonitorDialogListener {
	protected String WIDTH = "298px";
	protected String HEIGHT = "520px";

	protected TRId trId;
	protected TableData table;
	protected String headingTitle;
	protected VerticalLayoutContainer vl;
	protected EventBus eventBus;

	protected ListStore<OpHistory> store;
	protected ListLoader<ListLoadConfig, ListLoadResult<OpHistory>> loader;
	protected Grid<OpHistory> grid;

	protected OpHistory currentOpHistory;
	protected int currentRowIndex;
	protected RollBackSession rollBackSession;

	private boolean drawed = false;

	public HistoryPanel(EventBus eventBus) {
		super();
		this.eventBus = eventBus;
		forceLayoutOnResize = true;
		retrieveCurrentTR();
	}

	public HistoryPanel(TRId trId, EventBus eventBus) {
		super();
		this.trId = trId;
		this.eventBus = eventBus;
		forceLayoutOnResize = true;
		draw();
	}

	protected void draw() {
		drawed = true;
		init();
		create();
	}

	protected void init() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setHeaderVisible(false);
		setBodyBorder(false);
		setResize(true);

	}

	protected void create() {
		OpHistoryProperties props = GWT.create(OpHistoryProperties.class);

		IdentityValueProvider<OpHistory> identityProvider = new IdentityValueProvider<OpHistory>();
		RowExpander<OpHistory> expander = new RowExpander<OpHistory>(
				identityProvider, new AbstractCell<OpHistory>() {
					@Override
					public void render(Context context, OpHistory value,
							SafeHtmlBuilder sb) {
						sb.appendHtmlConstant("<p style='margin: 5px 5px 10px'><b>Date:</b>"
								+ SafeHtmlUtils.htmlEscape(value.getDate()) + "</p>");
						sb.appendHtmlConstant("<p style='margin: 5px 5px 10px'><b>Description:</b>"
								+ SafeHtmlUtils.htmlEscape(value
										.getDescription()) + "</p>");
					}
				});

		ColumnConfig<OpHistory, String> nameCol = new ColumnConfig<OpHistory, String>(
				props.name(), 132, "Step");

		nameCol.setCell(new AbstractCell<String>() {

			@Override
			public void render(Context context, String value, SafeHtmlBuilder sb) {
				sb.appendHtmlConstant("<span title='"
						+ SafeHtmlUtils.htmlEscape(value) + "'>"
						+ SafeHtmlUtils.htmlEscape(value) + "</span>");

			}

		});

		ColumnConfig<OpHistory, String> dateCol = new ColumnConfig<OpHistory, String>(
				props.date(), 106, "Date");

		ColumnConfig<OpHistory, String> rollBackColumn = new ColumnConfig<OpHistory, String>(
				props.date(), 40, "Undo");

		ActionButtonCellNoFirst button = new ActionButtonCellNoFirst();
		button.setIcon(ResourceBundle.INSTANCE.undo());
		button.setTitle("Undo");
		button.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				Log.debug("Button Undo Pressed");
				Context c = event.getContext();
				int rowIndex = c.getIndex();
				startSearchRollBackId(rowIndex);
			}
		});

		rollBackColumn.setCell(button);

		List<ColumnConfig<OpHistory, ?>> l = new ArrayList<ColumnConfig<OpHistory, ?>>();
		l.add(expander);
		l.add(nameCol);
		l.add(dateCol);
		l.add(rollBackColumn);

		ColumnModel<OpHistory> cm = new ColumnModel<OpHistory>(l);

		store = new ListStore<OpHistory>(props.id());

		RpcProxy<ListLoadConfig, ListLoadResult<OpHistory>> proxy = new RpcProxy<ListLoadConfig, ListLoadResult<OpHistory>>() {

			public void load(ListLoadConfig loadConfig,
					final AsyncCallback<ListLoadResult<OpHistory>> callback) {
				loadData(loadConfig, callback);
			}

		};

		loader = new ListLoader<ListLoadConfig, ListLoadResult<OpHistory>>(
				proxy);

		loader.setRemoteSort(false);
		loader.addLoadHandler(new LoadResultListStoreBinding<ListLoadConfig, OpHistory, ListLoadResult<OpHistory>>(
				store) {
		});

		grid = new Grid<OpHistory>(store, cm) {
			@Override
			protected void onAfterFirstAttach() {
				super.onAfterFirstAttach();
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {

					public void execute() {
						loader.load();
					}
				});
			}
		};

		grid.setLoader(loader);
		grid.setSize("200px", "300px");

		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.getView().setAutoFill(true);
		grid.setBorders(false);
		grid.setLoadMask(true);
		grid.setColumnReordering(true);
		grid.setColumnResize(true);
		grid.getView().setAutoExpandColumn(nameCol);
		grid.getView().setEmptyText("Empty");
		expander.initPlugin(grid);

		/*
		 * VerticalLayoutContainer v = new VerticalLayoutContainer();
		 * v.setScrollMode(ScrollMode.AUTO); v.add(grid, new
		 * VerticalLayoutData(1, 1, new Margins(0))); v.forceLayout();
		 */
		add(grid, new MarginData(0));

		onResize();
	}

	protected void startSearchRollBackId(int rowIndex) {
		currentRowIndex = rowIndex;
		currentOpHistory = store.get(rowIndex - 1);
		Log.debug(currentOpHistory.toString() + " was clicked.[rowIndex="
				+ currentRowIndex + " ]");
		callRollBack();

	}

	protected void callRollBack() {
		rollBackSession = new RollBackSession(trId,
				currentOpHistory.getHistoryId());
		TDGWTServiceAsync.INSTANCE.startRollBack(rollBackSession,
				new AsyncCallback<String>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert("Error Locked",
										caught.getLocalizedMessage());
							} else {
								if (caught instanceof TDGWTIsFinalException) {
									Log.error(caught.getLocalizedMessage());
									UtilsGXT3.alert("Error Final",
											caught.getLocalizedMessage());
								} else {
									Log.error("Error in rollback: "
											+ caught.getLocalizedMessage());
									UtilsGXT3
											.alert("Error in rollback",
													"Error: "
															+ caught.getLocalizedMessage());
								}
							}
						}
					}

					public void onSuccess(String taskId) {
						Log.debug("Rollback started");
						openMonitorDialog(taskId);

					}

				});
	}

	protected void loadData(ListLoadConfig loadConfig,
			final AsyncCallback<ListLoadResult<OpHistory>> callback) {

		TDGWTServiceAsync.INSTANCE.getHistory(trId,
				new AsyncCallback<ArrayList<OpHistory>>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert("Error Locked",
										caught.getLocalizedMessage());
							} else {
								Log.error("Error Retrieving History: "
										+ caught.getLocalizedMessage());
								UtilsGXT3.alert("Error retrieving history",
										"Error retrieving history");
							}
						}
						callback.onFailure(caught);
					}

					public void onSuccess(ArrayList<OpHistory> result) {
						Log.debug("loaded " + result.size());
						callback.onSuccess(new ListLoadResultBean<OpHistory>(
								result));
					}

				});

	}

	public void update() {
		retrieveCurrentTR();
		loader.load();
		forceLayout();
	}

	protected void retrieveCurrentTR() {
		TDGWTServiceAsync.INSTANCE.getCurrentTRId(new AsyncCallback<TRId>() {

			public void onFailure(Throwable caught) {
				if (caught instanceof TDGWTSessionExpiredException) {
					eventBus.fireEvent(new SessionExpiredEvent(
							SessionExpiredType.EXPIREDONSERVER));
				} else {
					if (caught instanceof TDGWTIsLockedException) {
						Log.error(caught.getLocalizedMessage());
						UtilsGXT3.alert("Error Locked",
								caught.getLocalizedMessage());
					} else {
						Log.error("Error retrieving current TRId: "
								+ caught.getLocalizedMessage());
						UtilsGXT3.alert("Error",
								"Error retrieving current tabular resource id");
					}
				}
			}

			public void onSuccess(TRId result) {
				Log.debug("retrieved " + result);
				trId = result;
				if (!drawed) {
					draw();
				}

			}

		});
	}

	protected void close() {
		/*
		 * if (parent != null) { parent.close(); }
		 */
	}

	// /
	protected void openMonitorDialog(String taskId) {
		MonitorDialog monitorDialog = new MonitorDialog(taskId, eventBus);
		monitorDialog.addProgressDialogListener(this);
		monitorDialog.show();
	}

	@Override
	public void operationComplete(OperationResult operationResult) {
		ChangeTableWhy why = ChangeTableWhy.TABLEUPDATED;
		ChangeTableRequestEvent changeTableRequestEvent = new ChangeTableRequestEvent(
				ChangeTableRequestType.ROLLBACK, operationResult.getTrId(), why);
		eventBus.fireEvent(changeTableRequestEvent);
		close();
	}

	@Override
	public void operationFailed(Throwable caught, String reason, String details) {
		UtilsGXT3.alert(reason, details);
		close();

	}

	@Override
	public void operationStopped(OperationResult operationResult, String reason, String details) {
		ChangeTableWhy why = ChangeTableWhy.TABLECURATION;
		ChangeTableRequestEvent changeTableRequestEvent = new ChangeTableRequestEvent(
				ChangeTableRequestType.ROLLBACK, operationResult.getTrId(), why);
		eventBus.fireEvent(changeTableRequestEvent);
		close();

	}

	@Override
	public void operationAborted() {
		close();

	}

	@Override
	public void operationPutInBackground() {
		close();

	}

}
