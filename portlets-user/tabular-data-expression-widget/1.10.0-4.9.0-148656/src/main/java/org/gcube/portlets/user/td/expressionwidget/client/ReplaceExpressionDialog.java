package org.gcube.portlets.user.td.expressionwidget.client;

import java.util.ArrayList;

import org.gcube.portlets.user.td.expressionwidget.client.notification.ExpressionWrapperNotification;
import org.gcube.portlets.user.td.expressionwidget.client.notification.ExpressionWrapperNotification.ExpressionWrapperNotificationListener;
import org.gcube.portlets.user.td.expressionwidget.client.notification.ExpressionWrapperNotification.HasExpressionWrapperNotificationListener;
import org.gcube.portlets.user.td.expressionwidget.client.resources.ExpressionResources;
import org.gcube.portlets.user.td.expressionwidget.client.type.ReplaceExpressionType;
import org.gcube.portlets.user.td.expressionwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
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
public class ReplaceExpressionDialog extends Window implements HasExpressionWrapperNotificationListener {
	private static final String WIDTH = "900px";
	private static final String HEIGHT = "360px";
	private ArrayList<ExpressionWrapperNotificationListener> listeners;
	private ColumnData column;
	private ArrayList<ColumnData> columns;
	// private String columnLocalId;
	private TRId trId;
	private EventBus eventBus;

	private ReplaceExpressionPanel replaceExpressionPanel;
	private ReplaceExpressionType type;

	/**
	 * 
	 * Column must have set columnId, label, columnTypeCode and ColumnDataType.
	 * Column is not used to construct the expression. Columns are retrieved
	 * from TRId.
	 * 
	 * 
	 * @param column
	 *            Column
	 * @param trId
	 *            Tabular Resource id
	 * @param eventBus
	 *            Event bus
	 */
	public ReplaceExpressionDialog(ColumnData column, TRId trId, EventBus eventBus) {
		listeners = new ArrayList<ExpressionWrapperNotificationListener>();
		this.eventBus = eventBus;
		this.trId = trId;
		this.column = column;
		type = ReplaceExpressionType.Replace;
		initWindow();
		load();

	}

	/**
	 * 
	 * ColumnMockUp must have set columnId, label, columnTypeCode and
	 * ColumnDataType. Column is not used to construct the expression. Columns
	 * are retrieved from TRId.
	 * 
	 * 
	 * @param columnMockUp
	 *            Column mockup
	 * @param trId
	 *            Tabular Resource id
	 * @param eventBus
	 *            Event bus
	 */
	public ReplaceExpressionDialog(ColumnMockUp columnMockUp, TRId trId, EventBus eventBus) {
		listeners = new ArrayList<ExpressionWrapperNotificationListener>();
		this.eventBus = eventBus;
		this.trId = trId;
		column = new ColumnData();
		column.setId(columnMockUp.getId());
		column.setColumnId(columnMockUp.getColumnId());
		column.setLabel(columnMockUp.getLabel());
		column.setDataTypeName(columnMockUp.getColumnDataType().toString());
		column.setTypeCode(columnMockUp.getColumnType().toString());
		type = ReplaceExpressionType.AddColumn;
		initWindow();
		load();

	}

	/**
	 * 
	 * ColumnMockUp and ColumnMockUpList must have set columnId, label,
	 * columnTypeCode and ColumnDataType. Column is not used to construct the
	 * expression.
	 * 
	 * 
	 * 
	 * @param columnMockUp
	 *            Column mockup
	 * @param columnMockUpList
	 *            List of column mockup
	 * @param eventBus
	 *            Event bus
	 */
	public ReplaceExpressionDialog(ColumnMockUp columnMockUp, ArrayList<ColumnMockUp> columnMockUpList,
			EventBus eventBus) {
		listeners = new ArrayList<ExpressionWrapperNotificationListener>();
		this.eventBus = eventBus;
		column = new ColumnData();
		column.setId(columnMockUp.getId());
		column.setColumnId(columnMockUp.getColumnId());
		column.setLabel(columnMockUp.getLabel());
		column.setDataTypeName(columnMockUp.getColumnDataType().toString());
		column.setTypeCode(columnMockUp.getColumnType().toString());
		Log.debug("ReplaceExpressionDialog: Column: " + column);

		columns = new ArrayList<ColumnData>();
		Log.debug("ReplaceExpressionDialog Columns:");
		for (ColumnMockUp colMock : columnMockUpList) {
			ColumnData col = new ColumnData();
			col.setId(colMock.getId());
			col.setColumnId(colMock.getColumnId());
			col.setLabel(colMock.getLabel());
			col.setDataTypeName(colMock.getColumnDataType().toString());
			col.setTypeCode(colMock.getColumnType().toString());
			Log.debug("Columns: " + col);
			columns.add(col);

		}

		type = ReplaceExpressionType.Template;
		initWindow();
		sanitizesColumns();
		create();

	}

	protected void initWindow() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setModal(true);
		setBodyBorder(false);
		setResizable(false);
		setHeadingText("Replace Expression");
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
		replaceExpressionPanel = new ReplaceExpressionPanel(this, column, columns, type, eventBus);
		add(replaceExpressionPanel);
	}

	protected void applyReplaceColumnByExpression(ExpressionWrapperNotification expressionWrapperNotification) {
		fire(expressionWrapperNotification);
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
				if (column.getColumnId() != null && c.getColumnId().compareTo(column.getColumnId()) == 0) {
					removableColumn.add(c);
				}
			}
		}
		columns.removeAll(removableColumn);
	}

	protected void close() {
		fireAborted();
	}

	@Override
	public void addExpressionWrapperNotificationListener(ExpressionWrapperNotificationListener handler) {
		listeners.add(handler);

	}

	@Override
	public void removeExpressionWrapperNotificationListener(ExpressionWrapperNotificationListener handler) {
		listeners.remove(handler);

	}

	protected void fire(ExpressionWrapperNotification notification) {
		for (ExpressionWrapperNotificationListener listener : listeners) {
			listener.onExpression(notification);
		}
		hide();

	}

	protected void fireAborted() {
		for (ExpressionWrapperNotificationListener listener : listeners) {
			listener.aborted();
		}
		hide();
	}

	protected void fireFailed(Throwable throwable) {
		for (ExpressionWrapperNotificationListener listener : listeners) {
			listener.failed(throwable);
			;
		}
		hide();

	}

}
