package org.gcube.portlets.user.td.widgetcommonevent.shared.operations;


/**
 * 
 * Tabular Data Service User Interface Operations Id
 * 
 *
 *	@author "Giancarlo Panichi" 
 *  <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 *
 */

public enum UIOperationsId {
	CSVImport("CSV Import"), 
	CSVExport("CSV Export"),
	Clone("Clone"),
	SDMXImport("SDMX Import"),	
	SDMXExport("SDMX Export"),
	JSONImport("JSON Import"),	
  	JSONExport("JSON Export"),
  	ChangeTableType("Change Table Type"), 
	DeleteColumn("Delete Column"), 
	AddColumn("Add Column"),
	ChangeColumnLabel("Change Column Label"),
	ChangeColumnType("Change Column Type"), 
	ChangeColumnsPosition("Change Columns Position"),
	EditRow("Edit Row"),
	DeleteRow("Delete Row"),
	Denormalize("Denormalize"),
	GroupBy("Group By"),
	MergeColumn("Merge Column"),
	SplitColumn("Split Column"),
	Normalize("Normalize"),
	TimeAggregation("Time Aggregation"),
	ReplaceValue("Replace Value"), 
	ReplaceByExpression("Replace By Expression"),
	ReplaceByExternal("Replace By External"),
	ReplaceBatch("Replace Batch"),
	FilterColumn("Filter Column"),
	Union("Union"),
	DuplicateTuples("Duplicate Tuples"),
	ExtractCodelist("Extract Codelist"),
	CodelistMappingImport("Codelist Mapping Import"), 
	ResumeTask("Resume Task"),
	ResubmitTask("ResubmitTask"),
	RollBack("Roll Back"),
	ApplyTemplate("Apply Template"),
	GenerateMap("Generate Map"),
	StatisticalOperation("Statistical Operation"),
	ChartTopRating("Top Rating Chart"),
	GeometryCreatePoint("Geometry Create Point"),
	GeospatialCreateCoordinates("Geospatial Create Coordinates"),
	DownscaleCSquare("Downscale C-Square"),
	Pending("Pending"),
	RuleOnColumnApply("Apply Rule On Column"),
	RuleOnColumnDetach("Detach Rule On Column"),
	RuleOnTableApply("Apply Rule On Table"),
	ValidationsDelete("Delete Validations");

	
	/**
	 * @param text
	 */
	private UIOperationsId(final String id) {
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


