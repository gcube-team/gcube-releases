/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator.view.external;

import org.gcube.portlets.user.td.expressionwidget.client.TemplateColumnExpressionDialog;
import org.gcube.portlets.user.td.expressionwidget.client.notification.ExpressionWrapperNotification;
import org.gcube.portlets.user.td.expressionwidget.client.notification.ExpressionWrapperNotification.ExpressionWrapperNotificationListener;
import org.gcube.portlets.user.td.widgetcommonevent.client.expression.C_ExpressionContainer;
import org.gcube.portlets.user.td.widgetcommonevent.client.expression.ExpressionWrapper;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnMockUp;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnTypeCode;
import org.gcube.portlets.user.tdtemplate.client.TdTemplateController;
import org.gcube.portlets.user.tdtemplate.client.TemplateRuleHandler;
import org.gcube.portlets.user.tdtemplate.client.event.ExpressionDialogOpenedEvent.ExpressionDialogType;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.ColumnDefinitionView;
import org.gcube.portlets.user.tdtemplate.shared.TemplateExpression;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;


/**
 * The Class ExpressionDialogCaller.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 17, 2015
 */
public class ExpressionDialogCaller implements ExpressionWrapperNotificationListener{
	
	private String columnId;
	private String columnType;
	private String dataType;
	private int columnIndex;
	private TemplateColumnExpressionDialog expressionDialog;
	private ConverterColumnMockupProperty converter;

	/**
	 * Instantiates a new expression dialog caller.
	 *
	 * @param columnId the column id
	 * @param columnType the column type
	 * @param dataType the data type
	 * @param columnIndex the column index
	 * @throws Exception the exception
	 */
	public ExpressionDialogCaller(String columnId, String columnType, String dataType, EventBus bus, int columnIndex, String columnLabel) throws Exception {
//		converColumn(columnId, columnType, dataType);
		this.converter = new ConverterColumnMockupProperty(columnId, columnType, dataType);
		this.columnIndex = columnIndex;
		ColumnMockUp cmk = new ColumnMockUp(columnIndex+"", columnId, columnLabel,converter.getColumnTypeCode(), converter.getColumnDataType(), "");
		this.expressionDialog = new TemplateColumnExpressionDialog(cmk, bus);
		this.expressionDialog.addExpressionWrapperNotificationListener(this);
	}

	/**
	 * Gets the column id.
	 *
	 * @return the column id
	 */
	public String getColumnId() {
		return columnId;
	}

	/**
	 * Gets the column type.
	 *
	 * @return the column type
	 */
	public String getColumnType() {
		return columnType;
	}

	/**
	 * Gets the data type.
	 *
	 * @return the data type
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * Gets the column type code.
	 *
	 * @return the column type code
	 */
	public ColumnTypeCode getColumnTypeCode() {
		return converter.getColumnTypeCode();
	}

	/**
	 * Gets the column data type.
	 *
	 * @return the column data type
	 */
	public ColumnDataType getColumnDataType() {
		return converter.getColumnDataType();
	}

	/**
	 * Gets the expression dialog.
	 *
	 * @return the expression dialog
	 */
	public TemplateColumnExpressionDialog getExpressionDialog() {
//		expressionDialog.setSize("660px", "550px");
		expressionDialog.setResizable(true);
		return expressionDialog;
	}
	

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.td.expressionwidget.client.notification.ExpressionWrapperNotification.ExpressionWrapperNotificationListener#onExpression(org.gcube.portlets.user.td.expressionwidget.client.notification.ExpressionWrapperNotification)
	 */
	@Override
	public void onExpression(ExpressionWrapperNotification expressionWrapperNotification) {
		
		ExpressionWrapper exWrapper = expressionWrapperNotification.getExpressionWrapper();
//		ExpressionWrapper exWrapper=event.getExpressionWrapper();
		C_ExpressionContainer ruleContainer = exWrapper.getConditionExpressionContainer();
		
		ColumnDefinitionView col = TdTemplateController.getTdGeneretor().getListColumnDefinition().get(columnIndex);
		
		if(ruleContainer!=null && ruleContainer.getExp()!=null){
			
			if(col!=null){
				TemplateRuleHandler templateRuleHandler = TdTemplateController.getTemplateRuleHandler();
				GWT.log("Added rule to: "+templateRuleHandler);
				TemplateExpression te = new TemplateExpression(ruleContainer.getExp(), ruleContainer.getExp().getReadableExpression());
				
				if(templateRuleHandler.getType().equals(ExpressionDialogType.NEW))
					col.addRule(te, true, true);
				else if(templateRuleHandler.getType().equals(ExpressionDialogType.UPDATE)){
					col.updateRule(templateRuleHandler.getIndexer().getExpressionIndex(), te, true, true);
				}
			}
				
		}
		else{
			GWT.log("Expression dialog closed with container as null");
//			col.deleteExpressionCaller(templateRuleHandler.getIndexer());
//			templateRuleHandler = null;
			TdTemplateController.setTemplateRuleHandler(null);
//			MessageBox.alert("Expression event", event.getC_ExpressionContainer().toString(), null);
//			event.getC_ExpressionContainer();
		}
		
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.td.expressionwidget.client.notification.ExpressionWrapperNotification.ExpressionWrapperNotificationListener#aborted()
	 */
	@Override
	public void aborted() {
		TdTemplateController.setTemplateRuleHandler(null);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.td.expressionwidget.client.notification.ExpressionWrapperNotification.ExpressionWrapperNotificationListener#failed(java.lang.Throwable)
	 */
	@Override
	public void failed(Throwable throwable) {
		TdTemplateController.setTemplateRuleHandler(null);
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ExpressionDialogCaller [columnId=");
		builder.append(columnId);
		builder.append(", columnType=");
		builder.append(columnType);
		builder.append(", dataType=");
		builder.append(dataType);
		builder.append(", columnIndex=");
		builder.append(columnIndex);
		builder.append(", expressionDialog=");
		builder.append(expressionDialog);
		builder.append(", converter=");
		builder.append(converter);
		builder.append("]");
		return builder.toString();
	}


	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		try {
//			System.out.println(new ExpressionDialogCaller("2", "CODENAME", "DateType"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
