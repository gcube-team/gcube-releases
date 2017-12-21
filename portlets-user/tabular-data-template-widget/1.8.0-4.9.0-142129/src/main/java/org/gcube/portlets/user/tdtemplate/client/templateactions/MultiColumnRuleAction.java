/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templateactions;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.td.expressionwidget.client.TemplateMultiColumnExpressionDialog;
import org.gcube.portlets.user.td.expressionwidget.client.notification.ExpressionWrapperNotification;
import org.gcube.portlets.user.td.expressionwidget.client.notification.ExpressionWrapperNotification.ExpressionWrapperNotificationListener;
import org.gcube.portlets.user.td.widgetcommonevent.client.expression.C_ExpressionContainer;
import org.gcube.portlets.user.td.widgetcommonevent.client.expression.ExpressionWrapper;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnMockUp;
import org.gcube.portlets.user.tdtemplate.client.event.operation.TableRuleOperationEvent;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.external.ConverterColumnMockupProperty;
import org.gcube.portlets.user.tdtemplate.shared.TdColumnDefinition;
import org.gcube.portlets.user.tdtemplate.shared.TemplateExpression;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;

/**
 * The Class MultiColumnRuleAction.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 25, 2015
 */
public class MultiColumnRuleAction implements ExpressionWrapperNotificationListener{
	private TemplateMultiColumnExpressionDialog multiExpressionDialog;
	private TemplateExpression templateExpression;
	private EventBus bus;
	
	/**
	 * Instantiates a new multi column rule action.
	 *
	 * @param columns the columns
	 * @param bus the bus
	 */
	public MultiColumnRuleAction(List<TdColumnDefinition> columns, EventBus bus) {
		this.bus = bus;
		try {		
			ArrayList<ColumnMockUp> columnMockUpList = new ArrayList<ColumnMockUp>();
			for (TdColumnDefinition ce : columns) {
				String columnId = ce.getServerId();
				GWT.log("Column ID: "+columnId);
				ConverterColumnMockupProperty convert = new ConverterColumnMockupProperty(columnId, ce.getCategory().getId(),ce.getDataType().getName());
				columnMockUpList.add(new ColumnMockUp(columnId, columnId, ce.getColumnName(),convert.getColumnTypeCode(), convert.getColumnDataType(), ""));
			}

			multiExpressionDialog=new TemplateMultiColumnExpressionDialog(columnMockUpList, bus);
			multiExpressionDialog.addExpressionWrapperNotificationListener(this);
			multiExpressionDialog.setModal(true);
			multiExpressionDialog.setResizable(true);
			
		} catch (Exception e) {
			MessageBox.alert("Error", "Expression not available", null);
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.td.expressionwidget.client.notification.ExpressionWrapperNotification.ExpressionWrapperNotificationListener#onExpression(org.gcube.portlets.user.td.expressionwidget.client.notification.ExpressionWrapperNotification)
	 */
	@Override
	public void onExpression(ExpressionWrapperNotification expressionWrapperNotification) {
		ExpressionWrapper exWrapper = expressionWrapperNotification.getExpressionWrapper();
		C_ExpressionContainer ruleContainer = exWrapper.getConditionExpressionContainer();
		
		if(ruleContainer!=null && ruleContainer.getExp()!=null){
				templateExpression = new TemplateExpression(ruleContainer.getExp(), ruleContainer.getExp().getReadableExpression());
				bus.fireEvent(new TableRuleOperationEvent(templateExpression));
		}
		else{
			GWT.log("Expression dialog closed with container as null");
		}
		
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.td.expressionwidget.client.notification.ExpressionWrapperNotification.ExpressionWrapperNotificationListener#aborted()
	 */
	@Override
	public void aborted() {

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.td.expressionwidget.client.notification.ExpressionWrapperNotification.ExpressionWrapperNotificationListener#failed(java.lang.Throwable)
	 */
	@Override
	public void failed(Throwable throwable) {
	}

	/**
	 * Gets the template expression.
	 *
	 * @return the templateExpression
	 */
	public TemplateExpression getTemplateExpression() {
		return templateExpression;
	}
	
	/**
	 * Gets the multi expression dialog.
	 *
	 * @return the multiExpressionDialog
	 */
	public TemplateMultiColumnExpressionDialog getMultiExpressionDialog() {
		return multiExpressionDialog;
	}
}
