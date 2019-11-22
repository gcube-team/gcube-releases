package org.gcube.portlets.user.td.expressionwidget.client;

import java.util.ArrayList;

import org.gcube.portlets.user.td.expressionwidget.client.notification.ExpressionWrapperNotification;
import org.gcube.portlets.user.td.expressionwidget.client.notification.ExpressionWrapperNotification.ExpressionWrapperNotificationListener;
import org.gcube.portlets.user.td.expressionwidget.client.notification.ExpressionWrapperNotification.HasExpressionWrapperNotificationListener;
import org.gcube.portlets.user.td.expressionwidget.client.resources.ExpressionResources;
import org.gcube.portlets.user.td.expressionwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnMockUp;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnTypeCode;

import com.google.gwt.user.client.ui.HTML;
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
public class TemplateMultiColumnExpressionDialog extends Window implements HasExpressionWrapperNotificationListener {
	private static final String WIDTH = "940px";
	private static final String HEIGHT = "388px";
	private ArrayList<ExpressionWrapperNotificationListener> listeners;
	private MultiColumnExpressionPanel multiColumnExpressionPanel;
	private ArrayList<ColumnData> columns;
	private EventBus eventBus;

	/**
	 * 
	 * @param columnsMockUp
	 *            Columns mockup
	 * @param eventBus
	 *            Event bus
	 */
	public TemplateMultiColumnExpressionDialog(ArrayList<ColumnMockUp> columnsMockUp, EventBus eventBus) {
		listeners = new ArrayList<ExpressionWrapperNotificationListener>();
		this.eventBus = eventBus;
		columns = new ArrayList<ColumnData>();
		for (ColumnMockUp columnMockUp : columnsMockUp) {
			ColumnData column = new ColumnData();
			column.setId(columnMockUp.getColumnId());
			column.setColumnId(columnMockUp.getColumnId());
			column.setLabel(columnMockUp.getLabel());
			column.setDataTypeName(columnMockUp.getColumnDataType().toString());
			column.setTypeCode(columnMockUp.getColumnType().toString());
			columns.add(column);
		}
		sanitizesColumns();
		initWindow();
		create();

	}

	protected void sanitizesColumns() {
		ArrayList<ColumnData> removableColumn = new ArrayList<ColumnData>();
		for (ColumnData c : columns) {
			if (c.getTypeCode().compareTo(ColumnTypeCode.DIMENSION.toString()) == 0
					|| c.getTypeCode().compareTo(ColumnTypeCode.TIMEDIMENSION.toString()) == 0) {
				removableColumn.add(c);
			} else {

			}
		}
		columns.removeAll(removableColumn);
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
		getHeader().setIcon(ExpressionResources.INSTANCE.ruleTableAdd());

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
		if (columns.size() > 0) {
			multiColumnExpressionPanel = new MultiColumnExpressionPanel(this, columns, eventBus);
			add(multiColumnExpressionPanel);
		} else {
			HTML errorMessage = new HTML(
					"Attention no columns with a type supported(Dimension and TimeDimension is not supported)!");
			add(errorMessage);
			close();
			UtilsGXT3.alert("Attention",
					"Attention no columns with a type supported(Dimension and TimeDimension is not supported)!");

		}
	}

	protected void onExpression(ExpressionWrapperNotification expressionWrapperNotification) {
		fireNotification(expressionWrapperNotification);
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

	@SuppressWarnings("unused")
	private void fireFailed(Throwable caught) {
		if (listeners != null) {
			for (ExpressionWrapperNotificationListener listener : listeners) {
				listener.failed(caught);
			}
		}
		hide();

	}

}
