package org.gcube.portlets.widgets.netcdfbasicwidgets.client.widgets;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public interface NetCDFPreviewMessages extends Messages {

	@DefaultMessage("NetCDF Preview")
	String dialogTitle();

	@DefaultMessage("Variables")
	String variablesTab();

	@DefaultMessage("Detail")
	String detailTab();

	@DefaultMessage("Sample")
	String sampleTab();

	@DefaultMessage("Info")
	String infoTab();

	// Variable
	@DefaultMessage("Name")
	String columnFullname();

	@DefaultMessage("Units")
	String columnUnits();

	@DefaultMessage("DataType")
	String columnDataType();

	@DefaultMessage("Dimensions")
	String columnDimensionString();

	@DefaultMessage("Rank")
	String columnRank();

	@DefaultMessage("Coord.")
	String columnCoordinateVariable();

	@DefaultMessage("Scalar")
	String columnScalar();

	@DefaultMessage("Immutable")
	String columnImmutable();

	@DefaultMessage("Unlimited")
	String columnUnlimited();

	@DefaultMessage("Unsigned")
	String columnUnsigned();

	@DefaultMessage("VLenght")
	String columnVariableLenght();

	@DefaultMessage("Member")
	String columnMemberOfStructure();

	@DefaultMessage("Value")
	String columnValue();

	// Range
	@DefaultMessage("Name")
	String columnName();

	@DefaultMessage("N")
	String columnN();

	@DefaultMessage("First")
	String columnFirst();

	@DefaultMessage("Stride")
	String columnStride();
	
	//Table
	@DefaultMessage("List of variables in nectdf file")
	String dataGridVariablesDescription();

	@DefaultMessage("No Variable")
	String dataGridVariablesEmpty();

	@DefaultMessage("NetCDF Variables")
	String dataGridVariablesName();

	@DefaultMessage("List of global attributes in nectdf file")
	String dataGridGlobalAttributeDescription();

	@DefaultMessage("No Global Attribute")
	String dataGridGlobalAttributeEmpty();

	@DefaultMessage("NetCDF Global Attributes")
	String dataGridGlobalAttributeName();

	@DefaultMessage("List of attributes of variable")
	String dataGridAttributeOfVariableDescription();

	@DefaultMessage("No Attribute")
	String dataGridAttributeOfVariableEmpty();

	@DefaultMessage("Attributes")
	String dataGridAttributeOfVariableName();

	@DefaultMessage("List of range of variable")
	String dataGridRangeOfVariableDescription();

	@DefaultMessage("No Range")
	String dataGridRangeOfVariableEmpty();

	@DefaultMessage("Ranges")
	String dataGridRangeOfVariableName();

}
