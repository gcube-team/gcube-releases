package org.gcube.data_catalogue.grsf_publish_ws.json.input.others;

import org.gcube.datacatalogue.common.Constants;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A time series bean that contains elements &lt year, T, T1 , source &gt.
 * Year is the only required element.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 * @param <T> the first type value of the series
 * @param <T1> the second type value of the series (optional)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeSeriesBean<T, T1> implements Comparable<TimeSeriesBean<T, T1>>{

	@JsonProperty(Constants.TIME_SERIES_YEAR_FIELD)
	private Long year;

	@JsonProperty(Constants.TIME_SERIES_DB_SOURCE_FIELD)
	private String databaseSource;

	@JsonProperty(Constants.TIME_SERIES_ASSESSMENT_FIELD)
	private String assessment;

	@JsonProperty(Constants.TIME_SERIES_DATA_OWNER_FIELD)
	private String dataOwner;

	@JsonProperty(Constants.TIME_SERIES_VALUE_FIELD)
	private T value;

	@JsonProperty(Constants.TIME_SERIES_UNIT_FIELD)
	private T1 unit;

	public TimeSeriesBean() {
		super();
	}

	/**
	 * @param year
	 * @param databaseSource
	 * @param assessment
	 * @param dataOwner
	 * @param value
	 * @param unit
	 */
	public TimeSeriesBean(Long year, String databaseSource, String assessment,
			String dataOwner, T value, T1 unit) {
		super();
		this.year = year;
		this.databaseSource = databaseSource;
		this.assessment = assessment;
		this.dataOwner = dataOwner;
		this.value = value;
		this.unit = unit;
	}

	public String getDataOwner() {
		return dataOwner;
	}

	public void setDataOwner(String dataOwner) {
		this.dataOwner = dataOwner;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public Long getYear() {
		return year;
	}

	public void setYear(Long year) {
		// Check year 
		this.year = (year == null? -1 : year);
	}

	public T1 getUnit() {
		return unit;
	}

	public void setUnit(T1 unit) {
		this.unit = unit;
	}

	public String getDatabaseSource() {
		return databaseSource;
	}

	public void setDatabaseSource(String databaseSource) {
		this.databaseSource = databaseSource;
	}

	public String getAssessment() {
		return assessment;
	}

	public void setAssessment(String assessment) {
		this.assessment = assessment;
	}

	public boolean isUnitPresent(){
		return unit != null && !unit.getClass().equals(Void.class);
	}

	public boolean isSourcePresent(){
		return databaseSource != null && !databaseSource.isEmpty();
	}

	public boolean isAssessmentPresent(){
		return assessment != null && !assessment.isEmpty();
	}

	public boolean isValuePresent(){
		return value != null && !value.getClass().equals(Void.class);
	}

	public boolean isDataOwnerPresent(){
		return dataOwner != null && !dataOwner.isEmpty();
	}


	@Override
	public int compareTo(TimeSeriesBean<T, T1> o) {
		return Long.compare(this.year, o.year);
	}

	@Override
	public String toString() {

		String value = "" + this.value;
		String unit = (this.unit != null && !this.unit.toString().isEmpty() ? "Unit: " + this.unit : "");
		String databaseSource = (this.databaseSource != null && !this.databaseSource.toString().isEmpty()? " - DB Source: " + this.databaseSource : "");
		String dataOwner = (this.dataOwner != null && !this.dataOwner.toString().isEmpty()? " - Data Owner: " + this.dataOwner : "");
		String referenceYear =  year > 0 ? " - Ref. Year: " + year : "";
		String reportingYearOrAssessment = (assessment != null && !this.assessment.toString().isEmpty()? " - Rep. Year or Assessment Id: " + assessment + "" : "");
		String partial = value + " [" + 
				unit + 
				reportingYearOrAssessment + 
				referenceYear + 
				dataOwner + 
				databaseSource + 
				"]";

		return partial.replace("[ - ", "[").replace("[]", "").replace("&nbsp;", " ");

	}

	//	public static void main(String[] args) {
	//
	//		TimeSeriesBean<String, String> t = new TimeSeriesBean<String, String>();
	//		t.setYear(-1L);
	//		t.setDatabaseSource("tttt");
	//		t.setDataOwner("zzzz");
	//		t.setUnit("uuuu");
	//		t.setValue("vvvv");
	//		t.setAssessment("2015");
	//		System.err.println("Result is \n" + t.toString());
	//
	//	}

}
