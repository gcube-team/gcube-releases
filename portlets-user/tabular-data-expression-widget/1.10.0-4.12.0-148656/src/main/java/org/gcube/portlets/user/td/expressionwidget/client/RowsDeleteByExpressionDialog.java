package org.gcube.portlets.user.td.expressionwidget.client;

import java.util.ArrayList;

import org.gcube.portlets.user.td.expressionwidget.client.resources.ExpressionResources;
import org.gcube.portlets.user.td.expressionwidget.client.rpc.ExpressionServiceAsync;
import org.gcube.portlets.user.td.expressionwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsFinalException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.FilterColumnSession;
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
public class RowsDeleteByExpressionDialog extends Window implements MonitorDialogListener {
	private static final String WIDTH = "660px";
	private static final String HEIGHT = "400px";
	private ColumnExpressionPanel columnExpressionPanel;
	private C_Expression exp = null;
	private TRId trId;
	private ColumnData column = null;
	private String columnLocalId = null;
	private ArrayList<ColumnData> columns=null;
	private ArrayList<ColumnData> removableColumn=null;
	private EventBus eventBus;
	private FilterColumnSession filterColumnSession;

	

	public RowsDeleteByExpressionDialog(TRId trId, String columnLocalId, EventBus eventBus) {
		initWindow();
		this.eventBus = eventBus;
		this.trId = trId;
		this.columnLocalId = columnLocalId;
		loadColumns();

	}

	protected void initWindow() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setModal(true);
		setBodyBorder(false);
		setResizable(false);
		setHeadingText("Delete Rows By Expression");
		setClosable(true);
		getHeader().setIcon(ExpressionResources.INSTANCE.tableRowDeleteByExpression());

	}

	/**
	 * {@inheritDoc}
	 */
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
		columnExpressionPanel = new ColumnExpressionPanel(this, column,columns,
				eventBus);
		add(columnExpressionPanel);
	
	}
	
	
	

	public C_Expression getExpression() {
		return exp;
	}

	protected void setExpression(C_Expression exp) {
		Log.debug("New Expression set:" + exp.toString());
		this.exp = exp;
	}

	protected void deleteRowsByExpression(ColumnData column,C_Expression exp) {
		this.exp = exp;
		this.column= column;
		callDeleteRows();
	}

	protected void loadColumns() {
		TDGWTServiceAsync.INSTANCE.getColumns(trId,
				new AsyncCallback<ArrayList<ColumnData>>() {

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
								Log.error("Error retrieving column: "
										+ caught.getMessage());
								UtilsGXT3.alert("Error retrieving column",
										caught.getMessage());
							}
						}
					}

					public void onSuccess(ArrayList<ColumnData> result) {
						Log.debug("Retrived column: " + result);
						columns = result;
						sanitizesColumns();
						create();
					}

				});

	}
	
	protected void sanitizesColumns() {
		removableColumn=new ArrayList<ColumnData>();
		for(ColumnData c:columns){
			if(c.getTypeCode().compareTo(ColumnTypeCode.DIMENSION.toString())==0||
					c.getTypeCode().compareTo(ColumnTypeCode.TIMEDIMENSION.toString())==0 ){
				removableColumn.add(c);
			} else {
				if(columnLocalId!=null && c.getColumnId().compareTo(columnLocalId)==0){
					column=c;
				}
			}
		}
		columns.removeAll(removableColumn);
	}
	

	protected void callDeleteRows() {
		filterColumnSession = new FilterColumnSession(trId, exp);
		Log.debug(filterColumnSession.toString());

		ExpressionServiceAsync.INSTANCE.startFilterColumn(filterColumnSession,
				new AsyncCallback<String>() {

					@Override
					public void onSuccess(String taskId) {
						Log.debug("Submitted column filter");
						openMonitorDialog(taskId);

					}

					@Override
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
									Log.error("Error submitting the column filter: "
											+ caught.getLocalizedMessage());
									caught.printStackTrace();
									UtilsGXT3
											.alert("Error submitting the column filter",
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
