package org.gcube.portlets.user.td.expressionwidget.client;

import java.util.ArrayList;

import org.gcube.portlets.user.td.expressionwidget.client.notification.ExpressionWrapperNotification;
import org.gcube.portlets.user.td.expressionwidget.client.notification.ExpressionWrapperNotification.ExpressionWrapperNotificationListener;
import org.gcube.portlets.user.td.expressionwidget.client.notification.ExpressionWrapperNotification.HasExpressionWrapperNotificationListener;
import org.gcube.portlets.user.td.expressionwidget.client.resources.ExpressionResources;
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
import com.google.gwt.user.client.ui.HTML;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class TemplateColumnExpressionDialog extends Window implements HasExpressionWrapperNotificationListener {
	private static final String WIDTH = "670px";
	private static final String HEIGHT = "426px";
	private ArrayList<ExpressionWrapperNotificationListener> listeners;
	private ColumnExpressionPanel columnExpressionPanel;
	private ColumnData column = null;
	private TRId trId;
	private String columnId = null;
	private EventBus eventBus;

	/**
	 * 
	 * @param columnMockUp
	 *            Column Mockup
	 * @param eventBus
	 *            Event bus
	 */
	public TemplateColumnExpressionDialog(ColumnMockUp columnMockUp, EventBus eventBus) {
		listeners = new ArrayList<ExpressionWrapperNotificationListener>();
		this.eventBus = eventBus;
		column = new ColumnData();
		column.setColumnId(columnMockUp.getColumnId());
		column.setLabel(columnMockUp.getLabel());
		column.setDataTypeName(columnMockUp.getColumnDataType().toString());
		column.setTypeCode(columnMockUp.getColumnType().toString());
		initWindow();
		create();

	}

	/**
	 * 
	 * @param trId
	 *            Tabular Resource id
	 * @param columnId
	 *            Column id
	 * @param eventBus
	 *            Event bus
	 */
	public TemplateColumnExpressionDialog(TRId trId, String columnId, EventBus eventBus) {
		listeners = new ArrayList<ExpressionWrapperNotificationListener>();
		this.eventBus = eventBus;
		this.trId = trId;
		this.columnId = columnId;
		initWindow();
		load();

	}

	protected void initWindow() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setModal(true);
		setBodyBorder(false);
		setResizable(false);
		setHeadingText("New Rule");
		setClosable(true);
		forceLayoutOnResize = true;
		getHeader().setIcon(ExpressionResources.INSTANCE.ruleColumnAdd());

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

	protected void close() {
		fireAborted();
	}

	protected void create() {
		if (column.getTypeCode().compareTo(ColumnTypeCode.ANNOTATION.toString()) == 0
				|| column.getTypeCode().compareTo(ColumnTypeCode.ATTRIBUTE.toString()) == 0
				|| column.getTypeCode().compareTo(ColumnTypeCode.CODE.toString()) == 0
				|| column.getTypeCode().compareTo(ColumnTypeCode.CODEDESCRIPTION.toString()) == 0
				|| column.getTypeCode().compareTo(ColumnTypeCode.CODENAME.toString()) == 0
				|| column.getTypeCode().compareTo(ColumnTypeCode.MEASURE.toString()) == 0) {
			columnExpressionPanel = new ColumnExpressionPanel(this, column, eventBus);
			add(columnExpressionPanel);
		} else {
			HTML errorMessage = new HTML("This type of column is not supported for now!");
			add(errorMessage);
			AlertMessageBox d = new AlertMessageBox("Error", "This type of column is not supported for now!");
			d.addHideHandler(new HideHandler() {
				public void onHide(HideEvent event) {
					hide();
				}
			});
			d.show();
		}
	}

	protected void onExpression(ExpressionWrapperNotification expressionWrapperNotification) {
		fireNotification(expressionWrapperNotification);
	}

	protected void load() {
		TDGWTServiceAsync.INSTANCE.getColumn(columnId, trId, new AsyncCallback<ColumnData>() {

			public void onFailure(Throwable caught) {
				if (caught instanceof TDGWTSessionExpiredException) {
					eventBus.fireEvent(new SessionExpiredEvent(SessionExpiredType.EXPIREDONSERVER));
				} else {
					if (caught instanceof TDGWTIsLockedException) {
						Log.error(caught.getLocalizedMessage());
						UtilsGXT3.alert("Error Locked", caught.getLocalizedMessage());
					} else {
						Log.error("Error retrieving column: " + caught.getMessage());
						UtilsGXT3.alert("Error", "Error retrieving column: " + caught.getMessage());
					}
				}
				fireFailed(caught);

			}

			public void onSuccess(ColumnData result) {
				Log.debug("Retrived column: " + result);
				column = result;
				create();
			}

		});

	}

	@Override
	public void addExpressionWrapperNotificationListener(ExpressionWrapperNotificationListener handler) {
		listeners.add(handler);

	}

	@Override
	public void removeExpressionWrapperNotificationListener(ExpressionWrapperNotificationListener handler) {
		listeners.remove(handler);

	}

	private void fireNotification(ExpressionWrapperNotification expressionWrapperNotification) {
		if (listeners != null) {
			for (ExpressionWrapperNotificationListener listener : listeners) {
				listener.onExpression(expressionWrapperNotification);
			}
		}
		hide();
	}

	private void fireAborted() {
		if (listeners != null) {
			for (ExpressionWrapperNotificationListener listener : listeners) {
				listener.aborted();
			}
		}
		hide();
	}

	private void fireFailed(Throwable caught) {
		if (listeners != null) {
			for (ExpressionWrapperNotificationListener listener : listeners) {
				listener.failed(caught);
			}
		}
		hide();

	}

}
