/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator.view;

import java.util.List;

import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.tdtemplate.client.TemplateIndexes;
import org.gcube.portlets.user.tdtemplate.client.event.ExpressionDialogOpenedEvent;
import org.gcube.portlets.user.tdtemplate.client.event.ExpressionDialogOpenedEvent.ExpressionDialogType;
import org.gcube.portlets.user.tdtemplate.client.resources.TdTemplateAbstractResources;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.ColumnDefinition;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.SetColumnTypeDialogManager;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.smart.SmartAddRuleButton;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.smart.SmartButtonDescription;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.external.ExpressionDialogCaller;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.external.ExpressionsDialogMng;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.rule.RulesDescriptionViewerMng;
import org.gcube.portlets.user.tdtemplate.shared.SPECIAL_CATEGORY_TYPE;
import org.gcube.portlets.user.tdtemplate.shared.TdTColumnCategory;
import org.gcube.portlets.user.tdtemplate.shared.TdTDataType;
import org.gcube.portlets.user.tdtemplate.shared.TdTFormatReference;
import org.gcube.portlets.user.tdtemplate.shared.TemplateExpression;

import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Label;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 16, 2014
 *
 */
public class ColumnDefinitionView implements ColumnDefinition, UserActionInterface, ColumnElement{
	
	private int columnIndex;
//	private Text columnHeaderValue = new Text();
	private String columnLabel = "";
	private boolean isValid = false;
	
	private Label columnDescription = new Label("Not defined");
	private SetColumnTypeDialogManager setColumnTypeDialogManager;

	private TemplatePanel templatePanel;
	
	private ColumnDefinitionView INSTANCE = this;
	
	private SmartAddRuleButton buttonSmartAddRule = new SmartAddRuleButton();
	
	private ExpressionsDialogMng expressionsDialogMng;
	private RulesDescriptionViewerMng rulesDescriptionMng = null;
	
	private SPECIAL_CATEGORY_TYPE selectedCategoryType = SPECIAL_CATEGORY_TYPE.NONE;
	private EditableLabelColumnDefinitionView editTableLabel;
	private EventBus controllerInternalBus;
	
	/**
	 * @param templatePanel 
	 * @param setColumnTypeDialogManager 
	 * @param setColumnTypeDefinition 
	 * 
	 */
	public ColumnDefinitionView(TemplatePanel templatePanel, int index, SetColumnTypeDialogManager setColumnTypeDialogManager) {
		this.templatePanel = templatePanel;
		this.setColumnTypeDialogManager = setColumnTypeDialogManager;
		this.controllerInternalBus = setColumnTypeDialogManager.getTemplateController().getInternalBus();
//		this.columnHeaderValue = new Text("Column "+(index+1));
		this.columnLabel = "Column "+(index+1);
		this.columnIndex = index;
		initListners();
		setCommandAddRule();
	}

	/**
	 * 
	 */
	private void initListners() {
		
		setColumnTypeDialogManager.getScbCategory().addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> se) {
				
				GWT.log("ScbCategory changed");
				
				setIsValid(false);
				resetRules();
				setSpecialCategory();
				validateColumCategoryAndDataType();
				setColumnTypeDialogManager.selectDataTypeIfIsSingle();
				templatePanel.validateTemplate();

			}
			
			private void setSpecialCategory(){
				
				if(setColumnTypeDialogManager.getCategorySelected().getName().compareTo(SPECIAL_CATEGORY_TYPE.DIMENSION.getLabel())==0){
					setSpecialCategoryType(SPECIAL_CATEGORY_TYPE.DIMENSION);
				}else if(setColumnTypeDialogManager.getCategorySelected().getName().compareTo(SPECIAL_CATEGORY_TYPE.TIMEDIMENSION.getLabel())==0){
					setSpecialCategoryType(SPECIAL_CATEGORY_TYPE.TIMEDIMENSION);
				}else if(setColumnTypeDialogManager.getCategorySelected().getName().compareTo(SPECIAL_CATEGORY_TYPE.CODENAME.getLabel())==0){
					setSpecialCategoryType(SPECIAL_CATEGORY_TYPE.CODENAME);
				}else if(setColumnTypeDialogManager.getCategorySelected().getName().compareTo(SPECIAL_CATEGORY_TYPE.ANNOTATION.getLabel())==0){
					setSpecialCategoryType(SPECIAL_CATEGORY_TYPE.ANNOTATION);
				}else if(setColumnTypeDialogManager.getCategorySelected().getName().compareTo(SPECIAL_CATEGORY_TYPE.CODE.getLabel())==0){
					setSpecialCategoryType(SPECIAL_CATEGORY_TYPE.CODE);
				}else if(setColumnTypeDialogManager.getCategorySelected().getName().compareTo(SPECIAL_CATEGORY_TYPE.CODEDESCRIPTION.getLabel())==0){
						setSpecialCategoryType(SPECIAL_CATEGORY_TYPE.CODEDESCRIPTION);
				}else{ 
					setSpecialCategoryType(SPECIAL_CATEGORY_TYPE.NONE);
				}
			}
		});
		
		setColumnTypeDialogManager.getScbDataType().addSelectionChangedListener(new SelectionChangedListener<TdTDataType>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<TdTDataType> se) {
				
				GWT.log("ScbDataType changed: "+selectedCategoryType);
				setColumnTypeDialogManager.selectDataTypeFormatIfIsSingle();
				
				switch (selectedCategoryType) {
					case DIMENSION:
//						templatePanel.setWidgetIntoTable(2, columnIndex, new Html("Choose Reference"));
						templatePanel.setWidgetIntoTable(2, columnIndex, new Html("Rule (unavailable)"));
						break;
						
					case TIMEDIMENSION:
						templatePanel.setWidgetIntoTable(2, columnIndex, new Html("Rule (unavailable)"));
//						buttonSmartAddRule.update("Rule", TdTemplateAbstractResources.dataType());
//						templatePanel.setWidgetIntoTable(2, columnIndex, buttonSmartAddRule);
						break;
						
					default: //ADD EDIT RULE IS DEFAULT BEHAVIOR
						buttonSmartAddRule.update("Add Rule", TdTemplateAbstractResources.ruleColumnAdd());
						templatePanel.setWidgetIntoTable(2, columnIndex, buttonSmartAddRule);
						break;
				}
				
				updateValidateTemplate();
				
			}
		});

		setColumnTypeDialogManager.getScbReference().addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> se) {
				updateValidateTemplate();
			}
		});
		
		setColumnTypeDialogManager.getScbLocales().addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> se) {
				updateValidateTemplate();
			}
		});
		
		setColumnTypeDialogManager.getScbPeriodTypes().addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> se) {
				setColumnTypeDialogManager.selectPeriodTypeFormatIfIsSingle();
				updateValidateTemplate();
			}
		});
		
		setColumnTypeDialogManager.getScbPeriodTypeValueFormats().addSelectionChangedListener(new SelectionChangedListener<TdTFormatReference>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<TdTFormatReference> se) {
				updateValidateTemplate();
				
			}
		});
		
		setColumnTypeDialogManager.getScbDataTypeFormat().addSelectionChangedListener(new SelectionChangedListener<TdTFormatReference>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<TdTFormatReference> se) {
				updateValidateTemplate();
				
			}
		});
	}
	
	private void updateValidateTemplate(){
		validateColumCategoryAndDataType();
		templatePanel.validateTemplate();
	}
	
	/**
	 * @return the setColumnTypeDialogManager
	 */
	public SetColumnTypeDialogManager getSetColumnTypeDialogManager() {
		return setColumnTypeDialogManager;
	}

	
	/**
	 * Column Data Type Validating
	 */
	private void validateColumCategoryAndDataType(){
		setIsValid(false);
		
//		boolean validating = setColumnTypeDialogManager.getDataTypeSelected()!=null?true:false;
		boolean validating;
		
		if(setColumnTypeDialogManager.getCategorySelected().getName().compareTo(SPECIAL_CATEGORY_TYPE.DIMENSION.getLabel())==0){
			ColumnData sel = setColumnTypeDialogManager.getReferenceSelected();
//			validating = setColumnTypeDialogManager.getReferenceSelected()!=null?true:false;
//			ColumnData sel = setColumnTypeDialogManager.getReferenceSelected();
			
			validating = sel!=null?true:false;
			if(validating){ //IF COLUMN DATA IS ALREADY SELECTED

				String  trName = setColumnTypeDialogManager.getReferenceTabularResourceName();
				
				String descr = "";
				if(trName!=null){
					descr = trName + " -> ";
				}
				descr+=sel.getLabel();
				
//				templatePanel.clearCell(2, columnIndex); //REMOVE Choose Reference
				SmartButtonDescription button = new SmartButtonDescription(0, "Added reference:", descr, INSTANCE, false, false);
				LayoutContainer lc = new LayoutContainer();
				lc.add(button);
				updateColumnDescription(lc);
			}
			
			
		}else if(setColumnTypeDialogManager.getCategorySelected().getName().compareTo(SPECIAL_CATEGORY_TYPE.TIMEDIMENSION.getLabel())==0){
//			validating = setColumnTypeDialogManager.getDataTypeSelected()!=null?true:false;
			validating = setColumnTypeDialogManager.getSelectedPeriodType()!=null?true:false;
			
			//ONLY IF PERIOD TYPE IS VALID
			if(validating)
				validating = setColumnTypeDialogManager.getSelectedPeriodValueFormat()!=null?true:false;
			
		}else if(setColumnTypeDialogManager.getCategorySelected().getName().compareTo(SPECIAL_CATEGORY_TYPE.CODENAME.getLabel())==0){
				validating = setColumnTypeDialogManager.getSelectedLocale()!=null?true:false;
		}else{ //NORMAL BEHAVIOUR
			
			validating = setColumnTypeDialogManager.getSelectedDataType()!=null?true:false;
			
			//ONLY IF DATA TYPE IS VALID
			if(validating){
				TdTDataType dt = setColumnTypeDialogManager.getSelectedDataType();
				
				//ONLY IF DATA TYPE HAVE TYPE FORMAT REFERENCE
				if(dt.getFormatReferenceIndexer()!=null){
					validating = setColumnTypeDialogManager.getSelectedDataTypeFormat()!=null?true:false;
				}
			}
		}
		
		setIsValid(validating);
	}
	
	/**
	 * 
	 */
	private void setCommandAddRule(){
		
		buttonSmartAddRule.setCommand(new Command() {
			@Override
			public void execute() {
				
				try {
				
					initExpressionDialogMng();
					initRulesDescriptionViewerMng();
					
					int lastExpressionIndex = rulesDescriptionMng.size(); //Number of rule already inserted
					expressionsDialogMng.updateExpressionCaller(getSelectedColumnCategory().getId(), getSelectedDataType().getName(), lastExpressionIndex);
					
					controllerInternalBus.fireEvent(new ExpressionDialogOpenedEvent(ExpressionDialogType.NEW, columnIndex, lastExpressionIndex));
					expressionsDialogMng.getEDCaller(lastExpressionIndex).getExpressionDialog().show();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	public void setVisibleAddRule(boolean b){
		GWT.log("set visible add rule: "+b);
		buttonSmartAddRule.setVisible(b);
	}
	
	private void initRulesDescriptionViewerMng(){
		
		if(this.rulesDescriptionMng==null)
			this.rulesDescriptionMng = new RulesDescriptionViewerMng(columnIndex);
	}
	
	private void initExpressionDialogMng(){
		
		if(expressionsDialogMng==null){
//			String columnId = "Column "+(columnIndex+1);
			expressionsDialogMng = new ExpressionsDialogMng((ColumnElement) this);
		}
	}
	
	public void resetRules(){
		templatePanel.clearCell(2, columnIndex); //IF EXISTS REMOVE EDIT RULE
		templatePanel.clearCell(3, columnIndex); //IF EXISTS REMOVE DESCRIPTION ROW
		expressionsDialogMng = null; //RESET RULE DIALOG
		rulesDescriptionMng = null;
	}
	
	/*public void resetDescriptionCell(){
		templatePanel.clearCell(3, columnIndex); //IF EXISTS REMOVE DESCRIPTION ROW
		expressionDialogMng = null; //RESET RULE DIALOG
		resetRulesExpressionsView();
	}*/
	
	public void deleteRule(int index){
		try {
			
			if(expressionsDialogMng!=null){ //THIS VALIDATION IS IMPORTANT TO TEMPLATE UPDATER
				expressionsDialogMng.deleteExpressionCaller(index);
			}
			rulesDescriptionMng.deleteRule(index);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*templatePanel.clearCell(3, columnIndex); //IF EXISTS REMOVE DESCRIPTION ROW
		expressionsDialogMng = null; //RESET RULE DIALOG
		resetRulesExpressionsView();
		*/
	}
	
	/**
	 * @param index
	 */
	private void editRule(int index) {
		try {
			
			ExpressionDialogCaller caller = expressionsDialogMng.getEDCaller(index);
			
			if(caller!=null){
				caller.getExpressionDialog().show();
				controllerInternalBus.fireEvent(new ExpressionDialogOpenedEvent(ExpressionDialogType.UPDATE, columnIndex, index));
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 
	 * @param expression
	 * @param isEditable
	 * @param isDeletable
	 */
	public void addRule(TemplateExpression expression, boolean isEditable, boolean isDeletable){

		initExpressionDialogMng();
		initRulesDescriptionViewerMng();
			
//		resetRulesExpressions();
		
		this.rulesDescriptionMng.addRule("Added Rule:", expression, this, isEditable, isDeletable);
		
		updateColumnDescription(this.rulesDescriptionMng.getPanel());
		
	}
	
	public void updateRule(int index, TemplateExpression expression, boolean isEditable, boolean isDeletable){

		initRulesDescriptionViewerMng();
			
		try {
			this.rulesDescriptionMng.updateRule(index, "Added Rule:", expression, this, isDeletable, isEditable);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		updateColumnDescription(this.rulesDescriptionMng.getPanel());
		
	}
	
	/**
	 * @param panel
	 */
	public void updateColumnDescription(LayoutContainer panel) {
		templatePanel.setWidgetIntoTable(3, columnIndex, panel);
	}


	public int getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	public Label getColumnDescription() {
		return columnDescription;
	}

	public void setColumnDescription(Label columnDescription) {
		this.columnDescription = columnDescription;
	}

//	public Text getColumnHeaderValue() {
//		return columnHeaderValue;
//	}

	public void setColumnHeaderValue(String columnHeaderValue) {
		GWT.log("Set column header value: "+columnHeaderValue);
		this.columnLabel = columnHeaderValue;
		this.editTableLabel.updateTextLabel(columnHeaderValue);
	}

	public SetColumnTypeDialogManager getSetColumnTypeViewManager() {
		return setColumnTypeDialogManager;
	}


	@Override
	public String getColumnName() {
		return columnLabel;
	}

	@Override
	public TdTDataType getSelectedDataType() {
		return setColumnTypeDialogManager.getSelectedDataType();
	}
	
	@Override
	public TdTFormatReference getSelectedDataTypeFormat() {
		return setColumnTypeDialogManager.getSelectedDataTypeFormat();
	}


	@Override
	public boolean isValid() {
		return isValid;
	}
	
	private void setIsValid(boolean bool){
		isValid = bool;
	}

	@Override
	public TdTColumnCategory getSelectedColumnCategory() {
		return setColumnTypeDialogManager.getCategorySelected();
	}

	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.template.ColumnDefinition#setCategoryType(org.gcube.portlets.user.tdtemplate.shared.CATEGORY_TYPE)
	 */
	@Override
	public void setSpecialCategoryType(SPECIAL_CATEGORY_TYPE category) {
		GWT.log("SPECIAL_CATEGORY_TYPE setted as: "+category);
		this.selectedCategoryType = category;
		
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.template.ColumnDefinition#getCategoryType()
	 */
	@Override
	public SPECIAL_CATEGORY_TYPE getSpecialCategoryType() {
		return this.selectedCategoryType;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.template.ColumnDefinition#getColumnData()
	 */
	@Override
	public ColumnData getReferenceColumnData() {
		return setColumnTypeDialogManager.getReferenceSelected();
	}

	/**
	 * 
	 */
	@Override
	public String getSelectedLocale() {
		return setColumnTypeDialogManager.getSelectedLocale();
		
	}

	/**
	 * 
	 */
	@Override
	public String getTimePeriod() {
		return setColumnTypeDialogManager.getSelectedPeriodType();
	}
	
	/**
	 * 
	 */
	@Override
	public TdTFormatReference getTimePeriodFormat() {
		return setColumnTypeDialogManager.getSelectedPeriodValueFormat();
	}

	/**
	 * 
	 * @return null if the column has not rules, the list of TemplateExpression otherwise
	 */
	public List<TemplateExpression> getRulesExpressions() {
		if(rulesDescriptionMng==null)
			return null;
		return rulesDescriptionMng.getTemplateColumnExpressions();
	}
	/*

	public void setRulesExpression(List<TemplateExpression> rulesExpression) {
		this.rulesDescriptionMng = rulesExpression;
	}*/
	
	@Override
	public String getColumnId() {
		return columnIndex+"";
	}
	
	public void setAsReadOnly(boolean enabled){
		
		setColumnTypeDialogManager.getScbCategory().setReadOnly(enabled);
		setColumnTypeDialogManager.getScbDataType().setReadOnly(enabled);
		setColumnTypeDialogManager.getScbLocales().setReadOnly(enabled);
		setColumnTypeDialogManager.getScbPeriodTypes().setReadOnly(enabled);
		setColumnTypeDialogManager.getScbReference().setReadOnly(enabled);
		
//		buttonSmartAddRule.enable(!enabled);
		
	}
	
	
	public void setColumnTypeAsReadOnly(boolean enabled){
		setColumnTypeDialogManager.getScbCategory().setReadOnly(enabled);
		setColumnTypeDialogManager.getScbDataType().setReadOnly(enabled);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.template.view.ActionInterface#editClicked()
	 */
	@Override
	public void editClicked(int index) {
		editRule(index);
	}

	

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.template.view.ActionInterface#cancelClicked()
	 */
	@Override
	public void deleteClicked(int index) {
		deleteRule(index);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.template.view.ColumnElement#getColumnDataType()
	 */
	@Override
	public String getColumnDataType() {
		return this.getSelectedColumnCategory().getName();
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.template.view.ColumnElement#getColumnType()
	 */
	@Override
	public String getColumnType() {
		return this.getSelectedColumnCategory().getId();
	}

	/**
	 * @param expDlgTemplateIndexUpdate
	 */
	public void deleteExpressionCaller(TemplateIndexes expDlgTemplateIndexUpdate) {
		
		try {
			boolean deleted = expressionsDialogMng.deleteExpressionCaller(expDlgTemplateIndexUpdate.getExpressionIndex());
			GWT.log("Expression caller is deleted: "+deleted);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * @param editableLabel
	 */
	public void setEditableLabel(EditableLabelColumnDefinitionView editableLabel) {
			this.editTableLabel = editableLabel;
	}

	/**
	 * @return the ediTableLabel
	 */
	public EditableLabelColumnDefinitionView getEdiTableLabel() {
		return editTableLabel;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.templatecreator.view.ColumnElement#getColumnLabel()
	 */
	@Override
	public String getColumnLabel() {
		return columnLabel;
	}
	
	/**
	 * @param expDlgTemplateIndexUpdate
	 */
	/*public void addExpressionCaller(TemplateIndexes expDlgTemplateIndexUpdate) {
		
		if(expressionsDialogMng==null){
			String columnId = "Column "+(columnIndex+1);
			expressionsDialogMng = new ExpressionsDialogMng(columnId, columnIndex);
		}
		
		try {
			if(expDlgTemplateIndexUpdate==null){
				GWT.log("TemplateIndexes is null, adding fake expression caller");
				expressionsDialogMng.addExpressionCaller(null, null);
			}
			else
				expressionsDialogMng.addExpressionCaller(getSelectedColumnCategory().getId(), getSelectedDataType().getName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}*/

}
