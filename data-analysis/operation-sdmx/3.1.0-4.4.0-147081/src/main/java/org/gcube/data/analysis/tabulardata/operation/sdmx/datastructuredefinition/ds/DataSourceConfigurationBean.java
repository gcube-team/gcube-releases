package org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition.ds;

public class DataSourceConfigurationBean 
{
	private String 	dsdId,
					observationTime,
					seriesKey,
					observationValue,
					table_id;
	
	private StringBuilder dimensions,
					attributes;

	public DataSourceConfigurationBean ()
	{
		this.dimensions = new StringBuilder();
		this.attributes = new StringBuilder();
	}
	
	public String getDsdId() {
		return dsdId;
	}

	public void setDsdId(String dsdId) {
		this.dsdId = dsdId;
	}

	public String getObservationTime() {
		return observationTime;
	}

	public void setObservationTime(String observationTime) {
		this.observationTime = observationTime;
	}

	public String getSeriesKey() {
		return seriesKey;
	}

	public void setSeriesKey(String seriesKey) {
		this.seriesKey = seriesKey;
	}

	public String getTable_id() {
		return table_id;
	}

	public void setTable_id(String table_id) {
		this.table_id = table_id;
	}

	public String getDimensions() {
		return dimensions.toString();
	}

	public void addDimension(String concept,String dimension) {
		this.dimensions.append(concept).append("=").append(dimension).append(";");
	}

	public String getAttributes() {
		return attributes.toString();
	}

	public void addAttributes(String concept,String attribute) {
		this.attributes.append(concept).append("=").append(attribute).append(";");
	}

	public String getObservationValue() {
		return observationValue;
	}

	public void setObservationValue(String observationValue) {
		this.observationValue = observationValue;
	}
	
	

}
