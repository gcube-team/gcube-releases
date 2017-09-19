/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator.view.updater;

import java.util.List;

import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.tdtemplate.client.TdTemplateConstants;
import org.gcube.portlets.user.tdtemplate.client.TdTemplateControllerUpdater;
import org.gcube.portlets.user.tdtemplate.client.event.TemplateCompletedEvent;
import org.gcube.portlets.user.tdtemplate.client.resources.TdTemplateAbstractResources;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.SetColumnTypeDialogManager;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.ColumnDefinitionView;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.TemplatePanel;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.external.updater.UpdateColumnDataByReference;
import org.gcube.portlets.user.tdtemplate.shared.TdColumnDefinition;
import org.gcube.portlets.user.tdtemplate.shared.TdTColumnCategory;
import org.gcube.portlets.user.tdtemplate.shared.TdTDataType;
import org.gcube.portlets.user.tdtemplate.shared.TdTFormatReference;
import org.gcube.portlets.user.tdtemplate.shared.TemplateExpression;

import com.google.gwt.core.shared.GWT;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Mar 25, 2014
 *
 */
public class TemplatePanelUpdater extends TemplatePanel {

	
	/**
	 * @param templateSwitcherInteface
	 * @param controller
	 * @param startIndex 
	 */
	public TemplatePanelUpdater(TemplateUpdaterSwitcherInteface templateSwitcherInteface, TdTemplateControllerUpdater controller) {
		super(templateSwitcherInteface, controller);
	}
	
	/**
	 * USED TO CREATE ONLY POST-OPERATIONS
	 */
	public TemplatePanelUpdater() {
		super();
	}

	/**
	 * 
	 * @param columns
	 */
	public void setColumns(List<TdColumnDefinition> columns){
		for (TdColumnDefinition colm : columns) {
			updateColumnByTdColumnDefinition(colm);
		}
	}
	
	/**
	 * 
	 * @param colm
	 */
	public void updateColumnByTdColumnDefinition(TdColumnDefinition colm){
		
		if(colm.getIndex()>=0 && colm.getIndex()<columnsDefined.size()){
			
			ColumnDefinitionView colDefView = columnsDefined.get(colm.getIndex());
			
			if(colm.getColumnName()!=null && !colm.getColumnName().isEmpty())
				colDefView.setColumnHeaderValue(colm.getColumnName());
			
			TdTColumnCategory tdCategory = colm.getCategory();
			ColumnData columnData = colm.getColumnDataReference();
			TdTDataType tdDataType = colm.getDataType();
//			SPECIAL_CATEGORY_TYPE spc = colm.getSpecialCategoryType();
			
			SetColumnTypeDialogManager colTypeDialogMng = colDefView.getSetColumnTypeViewManager();
			
//			colDefView.setSpecialCategoryType(spc); //SETTING SPECIAL CATEGORY
			
			colTypeDialogMng.getScbCategory().setSimpleValue(tdCategory.getName());
			colTypeDialogMng.getScbDataType().setValue(tdDataType);
			
			//HAVE A REFERENCE?
			if(tdDataType.getFormatReference()!=null){
				TdTFormatReference format = tdDataType.getFormatReference();
				colTypeDialogMng.getScbDataTypeFormat().setValue(format);
			}
			
			GWT.log("ColumnData found: "+columnData);
			if(columnData!=null){
				new UpdateColumnDataByReference(colTypeDialogMng, columnData);
			}

			if(colm.getLocale()!=null){
				colTypeDialogMng.setSelectedLocale(colm.getLocale(), true);
			}
			
			if(colm.getTimePeriod()!=null){
				
				//TODO ON UPDATE??
				colTypeDialogMng.setSelectTimePeriod(colm.getTimePeriod());
			}
			
			GWT.log("Rule Extends found: "+colm.getRulesExtends());
			if(colm.getRulesExtends()!=null){
				for (TemplateExpression expres : colm.getRulesExtends()) {
					colDefView.addRule(expres, false, true);
				}
			}
			
			//TODO ADD LOCALE
			
			/*if(spc.equals(SPECIAL_CATEGORY_TYPE.NONE)){
				
			}else if(spc.equals(SPECIAL_CATEGORY_TYPE.DIMENSION)){
				
			}else if(spc.equals(SPECIAL_CATEGORY_TYPE.TIMEDIMENSION)){
				
			}*/
		}
	}
	
	@Override
	public void validateTemplate() {
		
		boolean isValid = true;
		for (ColumnDefinitionView col : columnsDefined) {
			if(!col.isValid()){
				isValid = false;
				refreshSuggestion(TdTemplateConstants.SUGGESTION, TdTemplateConstants.PLEASE_SET_TYPE_TO_COLUMN_NUMBER+(col.getColumnIndex()+1));
				controller.getInternalBus().fireEvent(new TemplateCompletedEvent(false));
//				enableFilter(false);
//				resetFilterPanel();
				break;
			}
		}
		
		if(isValid){
			refreshSuggestion(TdTemplateConstants.TEMPLATE_COMPLETED, TdTemplateConstants.NOW_IS_POSSIBLE_TO_UPDATE_THE_TEMPLATE_CREATED, TdTemplateAbstractResources.handsUP());
			controller.getInternalBus().fireEvent(new TemplateCompletedEvent(true));
			/*enableFilter(true);
			enanbleColumnTypes(false);
			BaloonPanel baloonPanel = new BaloonPanel("Do you want add filters?", true);
			int zIndex = controller.getWindowZIndex();
			int zi = zIndex+1;
			baloonPanel.getElement().getStyle().setZIndex(zi);
			baloonPanel.showRelativeTo(southContainer);
			*/
//			setCheckColumnsEnabled(true);
		}
	}

}
