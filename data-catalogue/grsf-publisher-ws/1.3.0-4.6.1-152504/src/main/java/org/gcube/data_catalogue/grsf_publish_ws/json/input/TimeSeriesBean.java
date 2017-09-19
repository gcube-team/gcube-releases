package org.gcube.data_catalogue.grsf_publish_ws.json.input;

import javax.validation.constraints.NotNull;

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

	public static final String YEAR_FIELD = "reference_year";
	public static final String VALUE_FIELD = "value";
	public static final String UNIT_FIELD = "unit";
	public static final String SOURCE_FIELD = "source";
	public static final String ASSESSMENT_FIELD = "reporting_year_or_assessment_id";

	@JsonProperty(YEAR_FIELD)
	@NotNull(message="reference_year of a time series cannot be null")
	private Long year;

	@JsonProperty(SOURCE_FIELD)
	private String source;

	@JsonProperty(ASSESSMENT_FIELD)
	private String assessment;

	@JsonProperty(VALUE_FIELD)
	private T value;

	@JsonProperty(UNIT_FIELD)
	private T1 unit;

	public TimeSeriesBean() {
		super();
	}

	/**
	 * @param year
	 * @param source
	 * @param assessment
	 * @param value
	 * @param unit
	 */
	public TimeSeriesBean(Long year, String source, String assessment, T value,
			T1 unit) {
		super();
		this.year = year;
		this.source = source;
		this.assessment = assessment;
		this.value = value;
		this.unit = unit;
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
		this.year = year;
	}

	public T1 getUnit() {
		return unit;
	}

	public void setUnit(T1 unit) {
		this.unit = unit;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
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
		return source != null && !source.isEmpty();
	}

	public boolean isAssessmentPresent(){
		return assessment != null && !assessment.isEmpty();
	}

	public boolean isValuePresent(){
		return value != null && !value.getClass().equals(Void.class);
	}

	@Override
	public int compareTo(TimeSeriesBean<T, T1> o) {
		return (int) (this.year - o.year); // ascending.. low to highest
	}

	@Override
	public String toString() {

		String value = "" + this.value;
		String unit = (this.unit != null ? " " + this.unit : "");
		String source = (this.source != null ? " (" + this.source + ")" : "");
		String referenceYear =  " Ref. Year " + year;
		String reportingYearOrAssessment = (assessment != null ? " and Rep. Year or Assessment Id " + assessment + "" : "");

		return value + unit + source + referenceYear + reportingYearOrAssessment;

	}

}
