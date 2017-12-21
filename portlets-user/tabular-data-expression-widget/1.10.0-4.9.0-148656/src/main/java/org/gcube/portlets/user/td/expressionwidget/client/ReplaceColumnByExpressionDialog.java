package org.gcube.portlets.user.td.expressionwidget.client;

import java.util.ArrayList;

import org.gcube.portlets.user.td.expressionwidget.client.resources.ExpressionResources;
import org.gcube.portlets.user.td.expressionwidget.client.rpc.ExpressionServiceAsync;
import org.gcube.portlets.user.td.expressionwidget.client.type.ReplaceExpressionType;
import org.gcube.portlets.user.td.expressionwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsFinalException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.ReplaceColumnByExpressionSession;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialog;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialogListener;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.ChangeTableRequestEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableRequestType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableWhy;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.OperationResult;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnMockUp;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnTypeCode;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class ReplaceColumnByExpressionDialog extends Window implements MonitorDialogListener {
	private static final String WIDTH = "900px";
	private static final String HEIGHT = "490px";
	// private static final String HEIGHT_REDUCE = "404px";

	private ReplaceColumnByExpressionPanel replaceColumnByExpressionPanel;
	private C_Expression cConditionExpression = null;
	private ColumnData column = null;
	private ArrayList<ColumnData> columns;

	private EventBus eventBus;
	private ReplaceExpressionType replaceExpressionType;
	// private String columnLocalId;
	private TRId trId;

	/**
	 * 
	 * Columns must have set columnId, label, columnTypeCode and ColumnDataType
	 * 
	 * 
	 * @param columnMockUp
	 *            column mockup
	 * @param columnMockUpList
	 *            columns mockup list
	 * @param eventBus
	 *            event bus
	 */
	public ReplaceColumnByExpressionDialog(ColumnMockUp columnMockUp, ArrayList<ColumnMockUp> columnMockUpList,
			EventBus eventBus) {
		this.eventBus = eventBus;
		column = new ColumnData();
		column.setColumnId(columnMockUp.getColumnId());
		column.setLabel(columnMockUp.getLabel());
		column.setDataTypeName(columnMockUp.getColumnDataType().toString());
		column.setTypeCode(columnMockUp.getColumnType().toString());

		columns = new ArrayList<ColumnData>();
		for (ColumnMockUp colMock : columnMockUpList) {
			ColumnData col = new ColumnData();
			col.setColumnId(colMock.getColumnId());
			col.setLabel(colMock.getLabel());
			col.setDataTypeName(colMock.getColumnDataType().toString());
			col.setTypeCode(colMock.getColumnType().toString());
			columns.add(col);
		}

		replaceExpressionType = ReplaceExpressionType.Template;
		initWindow();
		sanitizesColumns();
		create();

	}

	/**
	 * 
	 * @param trId
	 *            tabular resource id
	 * @param columnId
	 *            column id
	 * @param eventBus
	 *            event bus
	 */
	public ReplaceColumnByExpressionDialog(TRId trId, String columnId, EventBus eventBus) {
		this.eventBus = eventBus;
		this.trId = trId;
		if (columnId == null) {
			column = null;
		} else {
			column = new ColumnData();
			column.setColumnId(columnId);
		}
		replaceExpressionType = ReplaceExpressionType.Replace;
		initWindow();
		load();

	}

	protected void initWindow() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setModal(true);
		setBodyBorder(false);
		setResizable(false);
		setHeadingText("Replace Column By Expression");
		setClosable(true);
		getHeader().setIcon(ExpressionResources.INSTANCE.columnReplaceByExpression());

	}

	
	@Override
	protected void initTools() {
		super.initTools();

		closeBtn.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				close();
			}
		});

	}

	protected void create() {
		replaceColumnByExpressionPanel = new ReplaceColumnByExpressionPanel(this, column, columns, eventBus,
				replaceExpressionType);
		add(replaceColumnByExpressionPanel);
	}

	public C_Expression getExpression() {
		return cConditionExpression;
	}

	protected void setExpression(C_Expression exp) {
		Log.debug("New Expression set:" + exp.toString());
		this.cConditionExpression = exp;
	}

	protected void applyReplaceColumnByExpression(ColumnData column, boolean allRows, C_Expression cConditionExpression,
			String replaceValue) {
		this.column = column;
		this.cConditionExpression = cConditionExpression;

		ReplaceColumnByExpressionSession replaceColumnByExpressionSession = new ReplaceColumnByExpressionSession(column,
				allRows, cConditionExpression, replaceValue);
		callApplyReplaceByExpression(replaceColumnByExpressionSession);
	}

	protected void applyReplaceColumnByExpression(ColumnData column, boolean allRows, C_Expression cConditionExpression,
			C_Expression cReplaceExpression) {
		this.column = column;
		this.cConditionExpression = cConditionExpression;

		ReplaceColumnByExpressionSession replaceColumnByExpressionSession = new ReplaceColumnByExpressionSession(column,
				allRows, cConditionExpression, cReplaceExpression);
		callApplyReplaceByExpression(replaceColumnByExpressionSession);
	}

	protected void load() {
		TDGWTServiceAsync.INSTANCE.getColumns(trId, new AsyncCallback<ArrayList<ColumnData>>() {

			public void onFailure(Throwable caught) {
				if (caught instanceof TDGWTSessionExpiredException) {
					eventBus.fireEvent(new SessionExpiredEvent(SessionExpiredType.EXPIREDONSERVER));
				} else {
					if (caught instanceof TDGWTIsLockedException) {
						Log.error(caught.getLocalizedMessage());
						UtilsGXT3.alert("Error Locked", caught.getLocalizedMessage());
					} else {
						Log.error("Error retrieving column: " + caught.getMessage());
						UtilsGXT3.alert("Error retrieving column", caught.getMessage());
					}
				}
			}

			public void onSuccess(ArrayList<ColumnData> result) {
				Log.debug("Retrived columns: " + result);
				columns = result;
				sanitizesColumns();
				create();

			}

		});

	}

	protected void sanitizesColumns() {
		ArrayList<ColumnData> removableColumn = new ArrayList<ColumnData>();
		for (ColumnData c : columns) {
			if (c.getTypeCode().compareTo(ColumnTypeCode.DIMENSION.toString()) == 0
					|| c.getTypeCode().compareTo(ColumnTypeCode.TIMEDIMENSION.toString()) == 0) {
				removableColumn.add(c);
			} else {
				if (c.getColumnId() != null && column != null && column.getColumnId() != null
						&& !column.getColumnId().isEmpty() && c.getColumnId().compareTo(column.getColumnId()) == 0) {
					column = c;
				}
			}
		}
		columns.removeAll(removableColumn);
	}

	protected void callApplyReplaceByExpression(ReplaceColumnByExpressionSession replaceColumnByExpressionSession) {
		Log.debug("Replace Column By Expression Session " + replaceColumnByExpressionSession);

		ExpressionServiceAsync.INSTANCE.startReplaceColumnByExpression(replaceColumnByExpressionSession,
				new AsyncCallback<String>() {

					@Override
					public void onSuccess(String taskId) {
						Log.debug("Submitted replace column by expression");
						openMonitorDialog(taskId);

					}

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert("Error Locked", caught.getLocalizedMessage());
							} else {
								if (caught instanceof TDGWTIsFinalException) {
									Log.error(caught.getLocalizedMessage());
									UtilsGXT3.alert("Error Final", caught.getLocalizedMessage());
								} else {
									Log.error("Error submitting replace column by expression: "
											+ caught.getLocalizedMessage());
									caught.printStackTrace();
									UtilsGXT3.alert("Error submitting replace column by expression",
											caught.getLocalizedMessage());
								}
							}
						}

					}
				});

	}

	protected void close() {
		hide();
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
				ChangeTableRequestType.COLUMNFILTER, operationResult.getTrId(), why);
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
				ChangeTableRequestType.COLUMNFILTER, operationResult.getTrId(), why);
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
