/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator.view.external;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.td.expressionwidget.client.ReplaceExpressionDialog;
import org.gcube.portlets.user.td.expressionwidget.client.notification.ExpressionWrapperNotification;
import org.gcube.portlets.user.td.expressionwidget.client.notification.ExpressionWrapperNotification.ExpressionWrapperNotificationListener;
import org.gcube.portlets.user.td.expressionwidget.shared.model.leaf.TD_Value;
import org.gcube.portlets.user.td.widgetcommonevent.client.expression.C_ExpressionContainer;
import org.gcube.portlets.user.td.widgetcommonevent.client.expression.ExpressionWrapper;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnMockUp;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.TemplatePanel;
import org.gcube.portlets.user.tdtemplate.shared.TdColumnDefinition;
import org.gcube.portlets.user.tdtemplate.shared.TemplateExpression;
import org.gcube.portlets.user.tdtemplate.shared.util.CutStringUtil;

import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;

/**
 * The Class AddExpressionDialogManager.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 20, 2015
 */
public class AddExpressionDialogManager implements ExpressionWrapperNotificationListener {
	
	private ReplaceExpressionDialog replaceExpressionDlg;
	private EventBus bus;
	private TdColumnDefinition column;
	private List<TdColumnDefinition> otherColumns;
	private TemplatePanel templatePanel;
	private TemplateExpression replaceExpression;

	/**
	 * Instantiates a new adds the expression dialog manager.
	 *
	 * @param col the column
	 * @param otherColumns the other columns
	 * @param bus the bus
	 * @param templatePanel 
	 */
	public AddExpressionDialogManager(TdColumnDefinition col, List<TdColumnDefinition> otherColumns, EventBus bus, TemplatePanel templatePanel){
		this.column = col;
		this.otherColumns = otherColumns;
		this.templatePanel = templatePanel;
		this.bus = bus;
		try {		
			String columnId = col.getIndex()+col.getColumnName();
			ConverterColumnMockupProperty convert = new ConverterColumnMockupProperty(columnId, col.getCategory().getId(), col.getDataType().getName());
			ColumnMockUp columnMockUp = new ColumnMockUp(columnId, columnId, col.getColumnName(),convert.getColumnTypeCode(), convert.getColumnDataType(), "");
			ArrayList<ColumnMockUp> columnMockUpList = new ArrayList<ColumnMockUp>();
			for (TdColumnDefinition ce : otherColumns) {
				columnId = ce.getServerId();
				GWT.log("Other column serverId: "+columnId);
				convert = new ConverterColumnMockupProperty(columnId, ce.getCategory().getId(),ce.getDataType().getName());
				columnMockUpList.add(new ColumnMockUp(columnId, columnId, ce.getColumnName(),convert.getColumnTypeCode(), convert.getColumnDataType(), ""));
			}
//			GWT.log("Adding: "+columnMockUp);
//			for (ColumnMockUp columnMockUp2 : columnMockUpList) {
//				GWT.log("Adding: "+columnMockUp2);
//			}
			replaceExpressionDlg = new ReplaceExpressionDialog(columnMockUp, columnMockUpList, bus);
			replaceExpressionDlg.addExpressionWrapperNotificationListener(this);
			replaceExpressionDlg.setResizable(true);
			replaceExpressionDlg.setModal(true);
		} catch (Exception e) {
			MessageBox.alert("Error", "Expression not available", null);
			e.printStackTrace();
		}
//	
//		replaceExpressionDlg = new ReplaceExpressionDialog(column, trId, eventBus)
	}
	
	/**
	 * @return the column
	 */
	public TdColumnDefinition getColumn() {
		return column;
	}
	
	/**
	 * @return the replaceExpressionDlg
	 */
	public ReplaceExpressionDialog getReplaceExpressionDlg() {
		return replaceExpressionDlg;
	}

	public void showDialog(){
		replaceExpressionDlg.show();
	}

	/**
	 * @return the bus
	 */
	public EventBus getBus() {
		return bus;
	}

	/**
	 * @return the otherColumns
	 */
	public List<TdColumnDefinition> getOtherColumns() {
		return otherColumns;
	}

	


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.td.expressionwidget.client.notification.ExpressionWrapperNotification.ExpressionWrapperNotificationListener#onExpression(org.gcube.portlets.user.td.expressionwidget.client.notification.ExpressionWrapperNotification)
	 */
	@Override
	public void onExpression(ExpressionWrapperNotification expressionWrapperNotification) {
		GWT.log("onExpression fired");
		ExpressionWrapper exWrapper = expressionWrapperNotification.getExpressionWrapper();
		
		 
		if (exWrapper.isReplaceByValue()) {
			String exp = exWrapper.getReplaceValue();
			if(exp!=null){
				TD_Value value = new TD_Value(ColumnDataType.getColumnDataTypeFromId(column.getDataType().getName()), exp);
				replaceExpression = new TemplateExpression(value, value.getReadableExpression());
				Label text = new Label(CutStringUtil.cutString("Init: "+value.getReadableExpression(), 15));
				text.setTitle(value.getReadableExpression());
				templatePanel.setWidgetIntoTable(2, 0, text);	
			}else{
				GWT.log("Expression dialog closed with container as null");
			}	
		} else {
			 C_ExpressionContainer ruleContainer = exWrapper.getReplaceExpressionContainer();
			 if(ruleContainer!=null && ruleContainer.getExp()!=null){
				 replaceExpression = new TemplateExpression(ruleContainer.getExp(), ruleContainer.getExp().getReadableExpression());
				 Label text = new Label(CutStringUtil.cutString("Init: "+ruleContainer.getExp().getReadableExpression(), 15));
				 text.setTitle(ruleContainer.getExp().getReadableExpression());
				 templatePanel.setWidgetIntoTable(2, 0, text);	
			 }else{
					GWT.log("Expression dialog closed with container as null");
			}
		}
	}



	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.td.expressionwidget.client.notification.ExpressionWrapperNotification.ExpressionWrapperNotificationListener#aborted()
	 */
	@Override
	public void aborted() {
		// TODO Auto-generated method stub
		
	}



	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.td.expressionwidget.client.notification.ExpressionWrapperNotification.ExpressionWrapperNotificationListener#failed(java.lang.Throwable)
	 */
	@Override
	public void failed(Throwable throwable) {
		// TODO Auto-generated method stub
		
	}
	
	
	/**
	 * @return the replaceExpression
	 */
	public TemplateExpression getReplaceExpression() {
		return replaceExpression;
	}
	
	public void resetTemplateExpression(){
		replaceExpression = null;
		templatePanel.setWidgetIntoTable(2, 0, new Label(""));	
	}


}
