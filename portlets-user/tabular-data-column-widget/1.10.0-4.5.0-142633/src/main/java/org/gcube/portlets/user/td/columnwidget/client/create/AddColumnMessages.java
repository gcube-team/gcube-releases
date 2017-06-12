package org.gcube.portlets.user.td.columnwidget.client.create;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface AddColumnMessages extends Messages {
	
	@DefaultMessage("Add Column")
	String dialogHead();
	
	@DefaultMessage("Add expression")
	String btnAddExpressionToolTip();
	
	@DefaultMessage("Remove expression")
	String btnRemoveExpressionToolTip();
	
	@DefaultMessage("Expression")
	String defaultStringLabel();
	
	@DefaultMessage("Select a column type...")
	String comboColumnTypeCodeEmptyText();
	
	@DefaultMessage("Select a measure type...")
	String comboMeasureTypeEmptyText();
	
	@DefaultMessage("Measure Type")
	String comboMeasureTypeLabel();
	
	@DefaultMessage("Label")
	String columnLabelFieldLabel();
	
	@DefaultMessage("Select a column type...")
	String comboAttributeTypeEmptyText();
	
	@DefaultMessage("Column Type")
	String comboColumnTypeCodeLabel();
	
	@DefaultMessage("Attribute Type")
	String comboAttributeTypeLabel();
	
	@DefaultMessage("Select a locale type...")
	String comboLocaleTypeEmptyText();
	
	@DefaultMessage("Locale")
	String comboLocaleTypeLabel();
	
	@DefaultMessage("Select a Codelist...")
	String comboDimensionTypeEmptyText();
	
	@DefaultMessage("Codelist")
	String comboDimensionTypeLabel();
	
	@DefaultMessage("Select a Column Reference...")
	String comboColumnReferenceTypeEmptyText();
	
	@DefaultMessage("Column")
	String comboColumnReferenceTypeLabel();
	
	@DefaultMessage("Select a time dimension type...")
	String comboTimeDimensionTypeEmptyText();

	@DefaultMessage("Time Type")
	String comboTimeDimensionTypeLabel();
	
	@DefaultMessage("Add")
	String btnAddColumnText();
	
	@DefaultMessage("Add Column")
	String btnAddColumnToolTip();
	
	@DefaultMessage("Column data type not selected!")
	String columnDataTypeNotSelected();
	
	@DefaultMessage("No locale selected!")
	String noLocaleSelected();
	
	@DefaultMessage("No column reference selected!")
	String noColumnReferenceSelected();
	
	@DefaultMessage("Time Dimension type not selected!")
	String timeDimensionTypeNotSelected();
	
	@DefaultMessage("This column type is not supported now!")
	String thisColumnTypeIsNotSupported();
	
	@DefaultMessage("Select a column type!")
	String selectAColumnType();
	
	@DefaultMessage("Insert a valid label!")
	String insertAValidLabel();
	
	@DefaultMessage("Error in invocation of add column operation!")
	String errorInInvocationOfAddColumnOperation();
	
	@DefaultMessage("Error retrieving columns")
	String errorRetrievingColumnsHead();
	
	@DefaultMessage("Error retrieving columns on server!")
	String errorRetrievingColumns();
	
	@DefaultMessage("Error retrieving locales")
	String errorRetrievingLocales();
	
	@DefaultMessage("Error retrieving period type")
	String errorRetrievingPeriodTypeHead();
	
	
	
}
