package org.gcube.portlets.user.td.widgetcommonevent.shared.operations;


/**
 * 
 * Tabular Data Service Operations Id
 * 
 * <table summary="Operations Id">
 * <tr><td>CSVImport</td><td>100</td></tr> 
 * <tr><td>CSVExport</td><td>101</td></tr>
 * <tr><td>SDMXCodelistImport</td><td>200</td></tr>	
 * <tr><td>SDMXCodelistExport</td><td>201</td></tr>
 * <tr><td>SDMXDatasetImport</td><td>202</td></tr> 
 * <tr><td>SDMXDatasetExport</td><td>203</td></tr>
 * <tr><td>JSONImport</td><td>300</td></tr>	
 * <tr><td>JSONExport</td><td>301</td></tr>
 * <tr><td>ValidateCodelist</td><td>1000</td></tr>
 * <tr><td>ValidateDataset</td><td>1001</td></tr> 
 * <tr><td>ChangeTableType</td><td>1002</td></tr> 
 * <tr><td>CreateDatasetView</td><td>1003</td></tr> 
 * <tr><td>RemoveColumn</td><td>1004</td></tr> 
 * <tr><td>AddsAColumn</td><td>1005</td></tr>
 * <tr><td>ColumnNameAdd</td><td>1006</td></tr>
 * <tr><td>ColumnNameRemove</td><td>1007</td></tr>
 * <tr><td>TableNameAdd</td><td>1008</td></tr>
 * <tr><td>TableNameRemove</td><td>1009</td></tr>
 * <tr><td>ChangeToAnnotationColumn</td><td>2000</td></tr> 
 * <tr><td>ChangeToAttributeColumn</td><td>2001</td></tr> 
 * <tr><td>ChangeToMeasureColumn</td><td>2002</td></tr> 
 * <tr><td>ChangeToCodeColumn</td><td>2003</td></tr>
 * <tr><td>ChangeToCodeName</td><td>2004</td></tr> 
 * <tr><td>ChangeToCodeDescription</td><td>2005</td></tr> 
 * <tr><td>ChangeToDimensionColumn</td><td>2006</td></tr> 
 * <tr><td>ChangeToTimeDimensionColumn</td><td>2007</td></tr>
 * <tr><td>ModifyTuplesValuesByExpression</td><td>3000</td></tr>
 * <tr><td>ModifyTuplesValuesById</td><td>3001</td></tr>
 * <tr><td>ModifyTuplesValuesByValidation</td><td>3002</td></tr>
 * <tr><td>AddRow</td><td>3004</td></tr>
 * <tr><td>Denormalize</td><td>3005</td></tr>
 * <tr><td>GroupBy</td><td>3006</td></tr>
 * <tr><td>RemoveDuplicateTuples</td><td>3007</td></tr>
 * <tr><td>Normalize</td><td>3008</td></tr>
 * <tr><td>TimeAggregation</td><td>3009</td></tr>
 * <tr><td>DownscaleCSquare</td><td>3010</td></tr>
 * <tr><td>ReplaceColumnByExpression</td><td>3101</td></tr>
 * <tr><td>ReplaceById</td><td>3102</td></tr> 
 * <tr><td>FilterByExpression</td><td>3201</td></tr>
 * <tr><td>RemoveRowById</td><td>3202</td></tr>
 * <tr><td>Union</td><td>3208</td></tr>
 * <tr><td>CodelistValidation</td><td>5001</td></tr>
 * <tr><td>ColumnTypeCastCheck</td><td>5002</td></tr>
 * <tr><td>DuplicateTupleValidation</td><td>5003</td></tr>
 * <tr><td>DuplicateValuesInColumnValidator</td><td>5004</td></tr>
 * <tr><td>PeriodFormatCheck</td><td>5005</td></tr>
 * <tr><td>ExpressionValidation</td><td>5006</td></tr>
 * <tr><td>AmbiguousExternalReferenceCheck</td><td>5007</td></tr>
 * <tr><td>DimensionColumnValidator</td><td>5010</td></tr>
 * <tr><td>ValidateTable</td><td>5011</td></tr>
 * <tr><td>ValidateDataSet</td><td>5012</td></tr>
 * <tr><td>ValidateGeneric</td><td>5013</td></tr>
 * <tr><td>ExtractCodelist</td><td>11001</td></tr>
 * </table>
 *
 *	@author "Giancarlo Panichi" 
 *  <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 *
 */

public enum OperationsId {
	CSVImport("100"), 
	CSVExport("101"),
	CLONE("102"),
	SDMXCodelistImport("200"),	
	SDMXCodelistExport("201"),
	SDMXDatasetImport("202"), 
  	SDMXDatasetExport("203"),
  	SDMXTemplateExport("204"),
  	SDMXTemplateImport("205"),
  	JSONImport("300"),	
  	JSONExport("301"),
  	ValidateCodelist("1000"),
	ValidateDataset("1001"), 
	ChangeTableType("1002"), 
	CreateDatasetView("1003"), 
	RemoveColumn("1004"), 
	AddColumn("1005"),
	ColumnNameAdd("1006"),
	ColumnNameRemove("1007"),
	TableNameAdd("1008"),
	TableNameRemove("1009"),
	GenerateMap("1010"),
	ChangeColumnPosition("1011"),
	ChangeToAnnotationColumn("2000"), 
	ChangeToAttributeColumn("2001"), 
	ChangeToMeasureColumn("2002"), 
	ChangeToCodeColumn("2003"),
	ChangeToCodeName("2004"), 
	ChangeToCodeDescription("2005"), 
	ChangeToDimensionColumn("2006"), 
	ChangeToTimeDimensionColumn("2007"),
	ModifyTuplesValuesByExpression("3000"),
	ModifyTuplesValuesById("3001"),
	ModifyTuplesValuesByValidation("3002"),
	AddRow("3004"),
	Denormalize("3005"),
	GroupBy("3006"),
	RemoveDuplicateTuples("3007"),
	Normalize("3008"),
	TimeAggregation("3009"),
	DownscaleCSquare("3010"),
	ReplaceColumnByExpression("3101"),
	ReplaceById("3102"), 
	FilterByExpression("3201"),
	RemoveRowById("3202"),
	Union("3208"),
	CodelistValidation("5001"),
	ColumnTypeCastCheck("5002"),
	DuplicateTupleValidation("5003"),
	DuplicateValuesInColumnValidator("5004"),
	PeriodFormatCheck("5005"),
	ExpressionValidation("5006"),
	AmbiguousExternalReferenceCheck("5007"),
	DimensionColumnValidator("5010"),
	ValidateTable("5011"),
	ValidateDataSet("5012"),
	ValidateGeneric("5013"),
	TopRatingChart("9000"),
	StatisticalOperation("10001"),
	ExportToStatisticalOperation("10002"),
	ImportFromStatistical("10003"),
	EnhanceLatLong("10101"),
	ExtractCodelist("11001"),
	GuessCodelist("11002"),
	CodelistMappingImport("12001"),
	RuleOnColumnApply("1000000000"),
	ValidationsDelete("");
	
	
	
	/**
	 * @param text
	 */
	private OperationsId(final String id) {
		this.id = id;
	}

	private final String id;
	
	@Override
	public String toString() {
		return id;
	}
	
	public Long toLong(){
		return Long.valueOf(id);
	}
	

}


