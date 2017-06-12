package org.gcube.data_catalogue.grsf_publish_ws.json.input;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A time series bean that contains elements &lt year, T, T1 &gt.
 * Unit can be contained too.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 * @param <T> the first type value of the series
 * @param <T1> the second type value of the series (optional)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeSeriesBean<T, T1> implements Comparable<TimeSeriesBean<T, T1>>{

	@JsonProperty("year")
	@NotNull(message="year of a time series cannot be null")
	private Long year;

	@JsonProperty("value")
	@NotNull(message="value of a time series cannot be null")
	private T value;

	@JsonProperty("unit")
	private T1 unit;

	public TimeSeriesBean() {
		super();
	}

	public TimeSeriesBean(T value, Long year, T1 unit) {
		super();
		this.value = value;
		this.year = year;
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

	@Override
	public String toString() {

		if(this.unit != null && !this.unit.getClass().equals(Void.class)
				&& !this.unit.toString().isEmpty()) // e.g., catches and landings
			return year + "-" + value + "-" + unit;
		else
			return year + "-" + value;

	}

	@Override
	public int compareTo(TimeSeriesBean<T, T1> o) {
		return (int) (this.year - o.year); // ascending.. low to highest
	}
}
