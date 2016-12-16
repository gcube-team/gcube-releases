package org.gcube.portlets.d4sreporting.common.shared;

import java.io.Serializable;

/**
 * 
 * @author massi
 *
 */
public class RepTimeSeries implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7950417545919272419L;
	private TimeSeriesinfo tsMetadata;
	private TimeSeriesFilter filter;
	private String csvFile;
	
	/**
	 * 
	 */
	public RepTimeSeries() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param filter .
	 * @param tsMetadata .
	 * @param csvFile .
	 */
	public RepTimeSeries(TimeSeriesFilter filter,	TimeSeriesinfo tsMetadata, String csvFile) {
		super();
		this.filter = filter;
		this.tsMetadata = tsMetadata;
		this.csvFile = csvFile;
	}
	
	/**
	 * 
	 * @param filter .
	 * @param tsMetadata .
	 */
	public RepTimeSeries(TimeSeriesFilter filter,	TimeSeriesinfo tsMetadata) {
		super();
		this.filter = filter;
		this.tsMetadata = tsMetadata;
		this.csvFile = "";
	}


	/**
	 * 
	 * @return .
	 */
	public TimeSeriesinfo getTsMetadata() {
		return tsMetadata;
	}

	/**
	 * 
	 * @param tsMetadata .
	 */
	public void setTsMetadata(TimeSeriesinfo tsMetadata) {
		this.tsMetadata = tsMetadata;
	}
	
	/**
	 * 
	 * @return .
	 */
	public TimeSeriesFilter getFilter() {
		return filter;
	}
	/**
	 * 
	 * @param filter .
	 */
	public void setFilter(TimeSeriesFilter filter) {
		this.filter = filter;
	}

	/**
	 * 
	 * @return .
	 */
	public String getCsvFile() {
		return csvFile;
	}

	/**
	 * 
	 * @param csvFile .
	 */
	public void setCsvFile(String csvFile) {
		this.csvFile = csvFile;
	}
	
	
	
}
