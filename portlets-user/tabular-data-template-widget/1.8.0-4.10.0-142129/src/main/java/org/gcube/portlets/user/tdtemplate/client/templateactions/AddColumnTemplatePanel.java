/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templateactions;

import java.util.ArrayList;

import org.gcube.portlets.user.tdtemplate.client.TdTemplateController;
import org.gcube.portlets.user.tdtemplate.client.ZIndexReference;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.SetColumnTypeDefinition;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.ColumnDefinitionView;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.TemplatePanel;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.TemplateSwitcherInteface;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.suggestion.ConstraintSuggestionLabel;
import org.gcube.portlets.user.tdtemplate.shared.SPECIAL_CATEGORY_TYPE;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.web.bindery.event.shared.EventBus;

/**
 * The Class AddColumnTemplatePanel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 9, 2015
 */
public class AddColumnTemplatePanel extends TemplatePanel{
	
	
	/**
	 * 
	 */
	public static final String ACT_NEW_COLUMN = "Act: New Column";
	private boolean isValidTemplate;
	private EventBus actionBus;
	private AddColumnTemplatePanel INSTANCE = this;

//	/**
//	 * 
//	 */
//	public AddColumnTemplatePanel() {
//		super();
//		init();
//	}
	
	/**
 * Instantiates a new adds the column template panel.
 *
 * @param templateSwitcherInteface the template switcher inteface
 * @param controller the controller
 */
	public AddColumnTemplatePanel(TemplateSwitcherInteface templateSwitcherInteface, TdTemplateController controller) {
		this.numColumns = templateSwitcherInteface.getNumberOfColumns();
		this.controller = controller;
		this.templateSwitcherInteface = templateSwitcherInteface;
		this.zIndexReference = new ZIndexReference(controller);
		this.columnsDefined = new ArrayList<ColumnDefinitionView>(numColumns);
//		this.htmlTitleLabel = new HtmlLabel("", "", "");
		setColumnTypeDefinition = new SetColumnTypeDefinition(templateSwitcherInteface, false) {
			
			@Override
			public void updateListCategory() {
				GWT.log("Init table");
				tableContainer.setEnabled(true);
				initTableColumns(flexTableTemplate, 0, numColumns);
				setVisibleAddRule(0, false);
				setColumnHeaderValue(0, ACT_NEW_COLUMN);
				setVisibleTitle(false);
				setEditableHeaderValue(0, false);
				setCategoriesAsVisible(false, SPECIAL_CATEGORY_TYPE.DIMENSION, SPECIAL_CATEGORY_TYPE.TIMEDIMENSION);
				
				HTMLTable.CellFormatter cellF = flexTableTemplate.getCellFormatter();
				cellF.addStyleName(0, 0, "FlexTableTemplateActions-header-row-action"); //SINGLE CELL
			}
		};
		
		initContainers();
		String title = templateSwitcherInteface.getType() + " columns constraints";
		constraintSuggestionLabel = new ConstraintSuggestionLabel(title, templateSwitcherInteface.getTdTTemplateType().getConstraintDescription(), false);
		centralContainer.add(constraintSuggestionLabel);
		init();
	}
	
	
	/**
	 * Sets the categories as visible.
	 *
	 * @param b the b
	 * @param cat the cat
	 */
	protected void setCategoriesAsVisible(boolean b, SPECIAL_CATEGORY_TYPE... cat) {
		for (ColumnDefinitionView columnDefinitionView : columnsDefined) {
			SimpleComboBox<String> scbCategory = columnDefinitionView.getSetColumnTypeViewManager().getScbCategory();
			ListStore<SimpleComboValue<String>> store = scbCategory.getStore();
			for (SPECIAL_CATEGORY_TYPE specialCategory : cat) {
				for (SimpleComboValue<String> category : store.getModels()) {
					GWT.log("comparing "+category.getValue() + " with "+specialCategory.getLabel());
					if(category.getValue().compareToIgnoreCase(specialCategory.getLabel())==0){
						scbCategory.remove(category.getValue());
						break;
					}
				}
				
			}
		}
	}

	/**
	 * Inits the.
	 */
	private void init(){
		setVisibleToolbar(false);
		setVisibleSuggests(false);
		enableValidateTemplate(false);
//		setVisibleTitle(false);
//		setWidgetIntoTable(0, 0, new Text(ACT_NEW_COLUMN));
//		setColumnHeaderValue(0, ACT_NEW_COLUMN);
//		setVisibleAddRule(0, false);
	}
	
	/**
	 * Validate template.
	 */
	@Override
	public void validateTemplate() {

		boolean isValid = true;
		for (ColumnDefinitionView col : columnsDefined) {
			if(!col.isValid()){
				isValid = false;
//				enableFlow(false);
				break;
			}
		}
		
		isValidTemplate = isValid;
		if(isValid){	
//			enableFlow(true);
		}
	}

	/**
	 * Checks if is valid template.
	 *
	 * @return the isValidTemplate
	 */
	@Override
	public boolean isValidTemplate() {
		return isValidTemplate;
	}
}
