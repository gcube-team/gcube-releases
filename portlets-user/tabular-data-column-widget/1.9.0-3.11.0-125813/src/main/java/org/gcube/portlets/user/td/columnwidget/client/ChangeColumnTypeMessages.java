package org.gcube.portlets.user.td.columnwidget.client;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface ChangeColumnTypeMessages extends Messages {

	@DefaultMessage("Change Column Type")
	String dialogHeadingText();

	@DefaultMessage("Select a column...")
	String comboColumnEmptyText();
	
	@DefaultMessage("Column")
	String comboColumnLabel();

	@DefaultMessage("Select a column type...")
	String comboColumnTypeCodeEmptyText();

	@DefaultMessage("Column Type")
	String comboColumnTypeCodeLabel();

	@DefaultMessage("Select a measure type...")
	String comboMeasureTypeEmptyText();

	@DefaultMessage("Measure Type")
	String comboMeasureTypeLabel();

	@DefaultMessage("Select a column type...")
	String comboAttributeTypeEmptyText();

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
	
	@DefaultMessage("Select optional mapping...")
	String comboColumnMappingEmptyText();

	@DefaultMessage("Mapping")
	String comboColumnMappingLabel();
	
	@DefaultMessage("Select a period type...")
	String comboPeriodTypeEmptyText();

	@DefaultMessage("Period Type")
	String comboPeriodTypeLabel();
	
	@DefaultMessage("Select a format...")
	String comboValueDataFormatEmptyText();

	@DefaultMessage("Format")
	String comboValueDataFormatLabel();
	
	//Apply Button
	@DefaultMessage("Apply")
	String applyBtnText();
	
	@DefaultMessage("Apply Column Type")
	String applyBtnToolTip();
	
	//Error
	@DefaultMessage("Error retrieving value data formats map!")
	String errorRetrievingValueDataFormatsMap();

	@DefaultMessage("Error retrieving column")
	String errorRetrievingColumnOnServerHead();
	
	@DefaultMessage("Error retrieving column on server!")
	String errorRetrievingColumnOnServer();
	
	@DefaultMessage("The requested column is null!")
	String requestColumnIsNull();
	
	@DefaultMessage("Error retrieving columns of tabular resource:")
	String errorRetrievingColumnsOfTabularResource();
	
	@DefaultMessage("Error retrieving connection")
	String errorRetrievingConnectionHead();

	@DefaultMessage("Error retrieving connection column!")
	String errorRetrievingConnection();
	
	@DefaultMessage("Type format not selected!")
	String typeFormatNotSelected();
	
	@DefaultMessage("Column data type not selected!")
	String columnDataTypeNotSelected();

	@DefaultMessage("No locale selected!")
	String noLocaleSelected();
	
	@DefaultMessage("Time Format not selected!")
	String timeFormatNotSelected();
	
	@DefaultMessage("Period Type not selected!")
	String periodTypeNotSelected();
	
	@DefaultMessage("This column type is not supported now!")
	String thisColumnTypeIsNotSupportedForNow();
	
	@DefaultMessage("Select a column type!")
	String selectAColumnType();
	
	@DefaultMessage("Select a column!")
	String selectAColumn();
	
	@DefaultMessage("Change Column Type Error")
	String changeColumnTypeErrorHead();

	@DefaultMessage("Error in invocation of  change column type operation!")
	String changeColumnTypeError();
	
	@DefaultMessage("Error retrieving tabular resource")
	String errorRetrievingTabularResource();
	
	@DefaultMessage("Error retrieving locales")
	String errorRetrievingLocales();
	
	@DefaultMessage("Error retrieving period type")
	String errorRetrievingPeriodType();
	
	@DefaultMessage("Column not selected!")
	String columnNotSelected();
	
	@DefaultMessage("Codelist not selected!")
	String codelistNotSelected();

	@DefaultMessage("Reference column not selected!")
	String referenceColumnNotSelected();
	

}
